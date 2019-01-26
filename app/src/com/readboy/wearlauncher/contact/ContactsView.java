package com.readboy.wearlauncher.contact;

import android.app.Activity;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SimpleItemAnimator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.readboy.recyclerview.wrapper.HeaderAndFooterWrapper;
import com.readboy.wearlauncher.R;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;


public class ContactsView extends FrameLayout {
    private static final String TAG = "ContactsView";
    private Context mContext;
    private static final String[] CONTACTS_PROJECTION = {
            "raw_contact_id", "data1", "display_name", "photo_uri"
    };
    public static final int CONTACTS_UPDATE = 1;

    private ContactListAdapter adapter;
    private HeaderAndFooterWrapper mWrapperAdapter;
    private View mHeaderView;
    private RecyclerView rv;
    private TextView emptyView;
    private final List<RBContact> datas = new ArrayList<RBContact>();
    private ExecutorService executorService;
    private Runnable getContact;
    private final Object mClock = new Object();

    public ContactsView(@NonNull Context context) {
        this(context, null);
    }

    public ContactsView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mContext.getContentResolver().registerContentObserver(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                true, new ContentObserver(new Handler()) {
                    @Override
                    public void onChange(boolean selfChange, Uri uri) {
                        executorService.execute(getContact);
                    }
                });
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        rv = (RecyclerView) findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        rv.setVisibility(View.VISIBLE);
        OverScrollDecoratorHelper.setUpOverScroll(rv,0);
        emptyView =(TextView)findViewById(R.id.empty_tv);
        executorService = Executors.newSingleThreadExecutor();
        getContact = new Runnable() {
            @Override
            public void run() {
                getContact();
            }
        };
        executorService.execute(getContact);
    }


    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONTACTS_UPDATE:
                    if(datas.isEmpty()){
                        emptyView.setVisibility(View.VISIBLE);
                    }else {
                        emptyView.setVisibility(View.GONE);
                    }
                    if (adapter == null) {
                        adapter = new ContactListAdapter(mContext, datas);
                        adapter.setActivity((Activity) mContext);
                        if (mHeaderView != null) {
                            addHeader(mHeaderView);
                        }
                    } else {
                        synchronized (mClock) {
                            adapter.notifyDataSetChanged();
                        }
                    }
                    break;
                default:
                    break;
            }
        }

    };

    private void getContact() {
        List<RBContact> contacts = RBContactUtil.getContactInfo(mContext);
        sortContact(contacts);
        synchronized (mClock) {
            datas.clear();
            datas.addAll(contacts);
        }
        mHandler.sendMessage(mHandler.obtainMessage(CONTACTS_UPDATE));
    }

    private void sortContact(List<RBContact> datas) {
        try {
            List<RBContact> userContact = new ArrayList<>();
            List<RBContact> deviceContact = new ArrayList<>();
            List<RBContact> phoneContact = new ArrayList<>();
            for (RBContact contact : datas) {
                if (contact.getUuid().startsWith("U")) {
                    userContact.add(contact);
                } else if (contact.getUuid().startsWith("D")) {
                    deviceContact.add(contact);
                } else if (contact.getUuid().startsWith("M")) {
                    phoneContact.add(contact);
                }
            }
            datas.clear();
            datas.addAll(userContact);
            datas.addAll(deviceContact);
            datas.addAll(phoneContact);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addHeader(int resId) {
        View view = LayoutInflater.from(mContext).inflate(resId, null, false);
        addHeader(view);
    }

    public void addHeader(View view) {
        mHeaderView = view;
        if (adapter == null) {
            Log.e(TAG, "addHeader: ");
            return;
        }
        if (mWrapperAdapter == null) {
            mWrapperAdapter = new HeaderAndFooterWrapper(adapter);
        }
        mWrapperAdapter.addHeaderView(view);
//        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//            @Override
//            public int getSpanSize(int i) {
//                return i == 0 ? 2 : 1;
//            }
//        });
        rv.setAdapter(mWrapperAdapter);
        mWrapperAdapter.notifyDataSetChanged();
    }
}
