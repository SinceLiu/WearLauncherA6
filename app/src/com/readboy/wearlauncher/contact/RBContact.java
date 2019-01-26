package com.readboy.wearlauncher.contact;

import android.graphics.Bitmap;
import android.text.TextUtils;

public class RBContact {

    public RBContact() {
        // TODO Auto-generated constructor stub
    }

    public RBContact(long rawContactId, String sid, String tid, String tphone,
                     String tsphone, String name, int rel, String tname, String sname,
                     int important, Bitmap icon, int unread_count) {
        super();
        this.rawContactId = rawContactId;
        this.sid = sid;
        this.tid = tid;
        this.tphone = tphone;
        this.tsphone = tsphone;
        this.name = name;
        this.rel = rel;
        this.tname = tname;
        this.sname = sname;
        this.important = important;
        this.unread_count = unread_count;
    }

    private String tname = "";
    private String sname = "";
    /**
     * 通讯录中的rawId
     */
    public long rawContactId;
    /**
     * 设备uuid
     * <P>Type: TEXT</P>
     */
    private String sid;
    /**
     * 联系人uuid
     * <P>Type: TEXT</P>
     */
    public String tid;
    /* 联系人手机
     * <P>Type: TEXT</P>
     */
    private String tphone = "";
    /**
     * 联系人短号
     * <P>Type: TEXT</P>
     */
    private String tsphone = "";

    /**
     * 联系人与设备的关系，相当于手表通讯录里的头像
     * <P>Type: INTEGER</P>
     */
    private int rel;

    private String name = "";
    /**
     * 头像
     * <P>Type: Bitmap</P>
     */
    public String photo_uri;
    /**
     * 是否为紧急联系人；0：不是	1：是
     * <P>Type: INTEGER</P>
     */
    private int important;
    /**
     * 头像
     * <P>Type: Bitmap</P>
     */
    private String iconUri;


    /**
     * 未计消息数量
     * <P>Type: INTEGER</P>
     */
    private int unread_count;

    /**
     * 未接电话数量
     * <P>Type: INTEGER</P>
     */
    private int missed_call_number;

    /**
     * 最近通话
     */
    private long MC_date;

    private String uuid = null;

    private int unread_wt_number;

    public int getUnread_wt_number() {
        return unread_wt_number;
    }

    public void setUnread_wt_number(int unread_wt_number) {
        this.unread_wt_number = unread_wt_number;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getTphone() {
        return tphone;
    }

    public void setTphone(String tphone) {
        this.tphone = tphone;
    }

    public String getTsphone() {
        return tsphone;
    }

    public void setTsphone(String tsphone) {
        this.tsphone = tsphone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getRawContactId() {
        return rawContactId;
    }

    public void setRawContactId(long rawContactId) {
        this.rawContactId = rawContactId;
    }

    public int getMissed_call_number() {
        return missed_call_number;
    }

    public void setMissed_call_number(int missed_call_number) {
        this.missed_call_number = missed_call_number;
    }

    public void missedCallIncrease() {
        missed_call_number++;
    }

    public long getMC_date() {
        return MC_date;
    }

    public String getTname() {
        return tname;
    }

    public void setTname(String tname) {
        this.tname = tname;
    }

    public void setSname(String name) {
        sname = name;
    }

    public String getSname() {
        return sname;
    }

    public void setMC_date(long mC_date) {
        if (mC_date < MC_date) return;
        MC_date = mC_date;
    }

    public int getImportant() {
        return important;
    }

    public void setImportant(int important) {
        this.important = important;
    }

    public String getIconUri() {
        return iconUri;
    }

    public void setIconUri(String iconUri) {
        this.iconUri = iconUri;
    }

    public boolean isInWeTalk() {
        return !TextUtils.isEmpty(uuid) && !uuid.startsWith("M");
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }

    public void setRel(int rel) {
        this.rel = rel;
    }

    public int getRel() {
        return rel;
    }

    public int getUnread_count() {
        return unread_count;
    }

    public void setUnread_count(int unread_count) {
        this.unread_count = unread_count;
    }

    @Override
    public String toString() {
        return new StringBuilder()
                .append("rawContactId = ").append(rawContactId)
                .append(",tphone = ").append(tphone)
                .append(",tsphone = ").append(tsphone)
                .append(",name = ").append(name)
                .append(",icon = ").append(iconUri)
                .append(",unread_count = ").append(unread_count)
                .toString();
    }


}

