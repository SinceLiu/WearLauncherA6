package com.readboy.wearlauncher.contact;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Nickname;
import android.provider.ContactsContract.CommonDataKinds.Note;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.Data;
import android.provider.ContactsContract.RawContacts;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class RBContactUtil {

    private static final String TAG = "RBContactUtil";
    private static final String ICON_URL="http://img.readboy.com/avatar/";
    private static final int IMAGE_WIDTH=200;
    private static final boolean DEBUG = true;
    private static final String FAMILY_NAME = "家庭圈";

    public static void saveBitmap(Bitmap bmp, final String name) {
        if (Environment.getExternalStorageState().equals( Environment.MEDIA_MOUNTED)) {
            String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + "/personal/";
            File fileDir = new File(dir);
            if (!fileDir.exists()) {
                fileDir.mkdirs();
            }

            File file = new File(dir, name + ".png");
            try {
                FileOutputStream fos = new FileOutputStream(file);
                bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);

                fos.flush();
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static int insertFamily(Context context){
        return 0;
    }



    /**
     * 获取所有联系人
     * @param context
     * @return
     */
    public static List<RBContact> getContactInfo(Context context){
        Cursor cursor = context.getContentResolver().query(Phone.CONTENT_URI,
                null, null, null, Data.RAW_CONTACT_ID);
        List<RBContact> list = new ArrayList<RBContact>();
        Stack<Integer> rawIdStack = new Stack<Integer>();
        if(cursor!=null && cursor.moveToFirst()){
            do {
                int rawId = cursor.getInt(cursor.getColumnIndex("raw_contact_id"));
                if(rawIdStack.contains(rawId)){
                    String number = cursor.getString(cursor.getColumnIndex("data1"));
                    if(TextUtils.isEmpty(number)) {
                        continue;
                    }
                    for(int i = 0;i<list.size();i++){
                        if(list.get(i).getRawContactId() == rawId){
                            RBContact contact = list.get(i);
                            String lastNumber = contact.getTphone();
                            if(TextUtils.isEmpty(lastNumber)){
                                contact.setTphone(number);
                            }else if(number.length() > lastNumber.length()){
                                contact.setTsphone(lastNumber);
                                contact.setTphone(number);
                            }else{
                                contact.setTsphone(number);
                            }
                            break;
                        }
                    }
                }
                else{
                    String number = cursor.getString(cursor.getColumnIndex("data1"));
                    if(TextUtils.isEmpty(number)) {
                        continue;
                    }
                    rawIdStack.push(rawId);
                    RBContact contact = new RBContact();
                    contact.setRawContactId(rawId);
                    contact.setName(cursor.getString(cursor.getColumnIndex("display_name")));
                    contact.setIconUri(cursor.getString(cursor.getColumnIndex("photo_uri")));

                    if(TextUtils.isEmpty(contact.getIconUri())){
                        contact.setRel(RBContactUtil.getRelByRawId(context, rawId));
                    }

                    contact.setTphone(number);


                    Cursor c = context.getContentResolver().query(Data.CONTENT_URI,
                            new String[]{"data8"},Data.MIMETYPE
                                    + "=? AND raw_contact_id=? AND data8 not null",
                            new String[]{StructuredPostal.CONTENT_ITEM_TYPE,rawId+""}, null);
                    if(c!=null && c.moveToFirst()){
                        contact.setUuid(c.getString(0));
                        c.close();
                    }
                    if(!TextUtils.isEmpty(contact.getUuid())) {
                        list.add(contact);
                    }
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }


    /**
     * 插入联系人(包含头像)，耗时操作，不要在UI线程里调用
     * @param context
     * @param sid
     * @param tid
     * @param tphone
     * @param tsphone
     * @param name
     * @param rel
     * @param tname
     * @param sname
     * @param important
     * @param unread
     * @param bmp  头像Bitmap
     */

    public static void insert(Context context,
                              String sid, String tid, String tphone, String tsphone,
                              String name, int rel, String tname, String sname,
                              int important, int unread, Bitmap bmp){
        ContentResolver cr = context.getContentResolver();
        ContentValues values = new ContentValues();
        Uri rawContentUri = cr.insert(RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContentUri);

        insertInfo(context, rawContactId, sid, tid, tphone, tsphone, name, rel, tname, sname, important, unread);
        insertContactPhoto(context,rawContactId,bmp);
    }

    /**
     * 插入头像
     * @param context
     * @param rawContactId
     * @param bmp
     */
    private static void insertContactPhoto(Context context, long rawContactId, Bitmap bmp){
        if(bmp==null){
            return;
        }
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, os);
        byte[] avatar = os.toByteArray();
        ContentValues cv = new ContentValues();
        cv.put(Data.RAW_CONTACT_ID,rawContactId);
        cv.put(Data.MIMETYPE, Photo.CONTENT_ITEM_TYPE);
        cv.put(Photo.PHOTO, avatar);
        context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, cv);
    }


    private static void insertInfo(Context context, long rawContactId,
                                   String sid, String tid, String tphone, String tsphone,
                                   String name, int rel, String tname, String sname,
                                   int important, int unread){
        ContentValues values = new ContentValues();
        ContentResolver cr = context.getContentResolver();

        if(!TextUtils.isEmpty(name)){
            values.put(Data.RAW_CONTACT_ID, rawContactId);
            values.put(Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE);
            values.put(StructuredName.DISPLAY_NAME, name);
            cr.insert(ContactsContract.Data.CONTENT_URI, values);
        }
        if(!TextUtils.isEmpty(sname)){
            values.clear();
            values.put(Data.RAW_CONTACT_ID, rawContactId);
            values.put(Data.MIMETYPE, Note.CONTENT_ITEM_TYPE);
            values.put(Note.NOTE, sname);
            cr.insert(ContactsContract.Data.CONTENT_URI, values);
        }
        if(!TextUtils.isEmpty(tname)){
            values.clear();
            values.put(Data.RAW_CONTACT_ID, rawContactId);
            values.put(Data.MIMETYPE,Nickname.CONTENT_ITEM_TYPE);
            values.put(Nickname.NAME, tname);
            cr.insert(ContactsContract.Data.CONTENT_URI, values);
        }

        values.clear();
        values.put(Data.RAW_CONTACT_ID, rawContactId);
        values.put(Data.MIMETYPE, StructuredPostal.CONTENT_ITEM_TYPE);
        values.put(StructuredPostal.TYPE, StructuredPostal.TYPE_WORK);
        values.put(StructuredPostal.CITY, sid);
        values.put(StructuredPostal.REGION, tid);
        values.put(StructuredPostal.POSTCODE, rel);
        values.put(StructuredPostal.COUNTRY, important);
        values.put("data6", unread);
        cr.insert(ContactsContract.Data.CONTENT_URI, values);

        if(!TextUtils.isEmpty(tphone)){
            values.clear();
            values.put(Data.RAW_CONTACT_ID, rawContactId);
            values.put(Data.MIMETYPE,Phone.CONTENT_ITEM_TYPE);
            values.put(Phone.TYPE, Phone.TYPE_MOBILE);
            values.put(Phone.NUMBER, tphone);
            cr.insert(ContactsContract.Data.CONTENT_URI, values);
        }
        if(!TextUtils.isEmpty(tsphone)){
            values.clear();
            values.put(Data.RAW_CONTACT_ID, rawContactId);
            values.put(Data.MIMETYPE,Phone.CONTENT_ITEM_TYPE);
            values.put(Phone.TYPE, Phone.TYPE_HOME);
            values.put(Phone.NUMBER, tsphone);
            cr.insert(ContactsContract.Data.CONTENT_URI, values);
        }
    }

    /**
     * 删除所有联系人，慎用
     * @param context
     * @return
     */
    public static int deleteAll(Context context){
        return context.getContentResolver().delete(ContactsContract.RawContacts.CONTENT_URI, null, null);
    }

    public static int getRelByRawId(Context context, long rawId){
        if(rawId<=0){
            return 12;
        }
        int rel = 12;
        Cursor relCursor = context.getContentResolver().query(Data.CONTENT_URI,
                new String[]{"data9"},
                "raw_contact_id=? AND mimetype=? AND data2=?",
                new String[]{rawId+"",StructuredPostal.CONTENT_ITEM_TYPE,"2"}, null);
        if(relCursor != null && relCursor.moveToFirst()){
            rel = relCursor.getInt(0);
            relCursor.close();
        }
        return rel;
    }

}
