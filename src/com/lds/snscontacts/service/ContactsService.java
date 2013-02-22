package com.lds.snscontacts.service;

import java.io.IOException;
import java.util.List;

import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.lds.snscontacts.Configs;
import com.lds.snscontacts.EmptyActivity;
import com.lds.snscontacts.dao.WeiboUserDao;
import com.lds.snscontacts.db.ContactsDatabase;
import com.lds.snscontacts.db.ContactsDatabase.ContactsCols;
import com.lds.snscontacts.model.Status;
import com.lds.snscontacts.model.WeiboUser;
import com.lds.snscontacts.model.WeiboUsers;
import com.lds.snscontacts.widget.FloatingWindow;
import com.weibo.sdk.android.Oauth2AccessToken;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboException;
import com.weibo.sdk.android.api.FriendshipsAPI;
import com.weibo.sdk.android.net.RequestListener;

public class ContactsService extends Service {
    private ContactsDatabase db;
    private WeiboUserDao dao;
    
    private Handler mHandler;

    @Override
    public void onCreate() {
        super.onCreate();
        
        db = ContactsDatabase.getInstance(getApplicationContext());
        dao = new WeiboUserDao(db.getOpenHelper(), ContactsDatabase.CONTACTS_TABLE_NAME);
        
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                WeiboUser user = (WeiboUser) msg.obj;
                if (user != null) {
                    String photoUrl = user.getProfile_image_url();
                    String name = user.getName();
                    Status status = user.getStatus();
                    if (status != null) {
                        name = status.getText();
                    }
                    if (photoUrl != null && name != null) {
                        Intent intent = new Intent(getBaseContext(), EmptyActivity.class);
                        intent.putExtra("name", name);
                        intent.putExtra("photo", photoUrl);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getApplication().startActivity(intent);
                    }
                }
            }
        };
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        getAllFirends(null); // refresh data
        listenTelephone();
        return super.onStartCommand(intent, flags, startId);
    }
    
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    
    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public ContactsService getService() {
            // Return this instance of LocalService so clients can call public methods
            return ContactsService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        
        unlistenTelephone();
    }
    
    public void listenTelephone() {
        TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mTelephonyManager.listen(mPhoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);
    }
    
    public void unlistenTelephone() {
        TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        mTelephonyManager.listen(mPhoneStateListener,
                PhoneStateListener.LISTEN_NONE);
    }
    
    private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);

            switch (state) {
            case TelephonyManager.CALL_STATE_OFFHOOK:
                System.out.println("offhook");
                FloatingWindow.close(getApplicationContext());
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                System.out.println("ringing: " + incomingNumber);
                findFirends(incomingNumber);
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                System.out.println("idle");
                break;
            }
        }
    };
    
    /**
     * find a friend by phone number
     * 
     * @param incomingNumber
     */
    public void findFirends(String incomingNumber) {
        if (TextUtils.isEmpty(incomingNumber)) {
            return;
        }
        String name = getContactDisplayNameByNumber(incomingNumber);
        if (name == null) {
            return;
        }
        System.out.println("name: " + name);
        
        String selection = ContactsCols.REMARK + " = ? ";
        String[] selectionArgs = new String[] { name };
        List<WeiboUser> users = dao.findListByFields(null, selection, selectionArgs, null);
        if (users != null && users.size() > 0) {
            WeiboUser user =  users.get(0);
            System.out.println("Found User: " + user);
            Message msg = mHandler.obtainMessage(0);
            msg.obj = user;
            msg.sendToTarget();
        }
    }
    
    /**
     * search contact name by photo number
     * 
     * @param number
     * @return
     */
    public String getContactDisplayNameByNumber(String number) {
        String name = null;
        if (TextUtils.isEmpty(number)) {
            return name;
        }

        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        ContentResolver contentResolver = getContentResolver();
        Cursor contactLookup = contentResolver.query(uri, new String[] {BaseColumns._ID,
                ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);

        try {
            if (contactLookup != null && contactLookup.getCount() > 0) {
                contactLookup.moveToNext();
                name = contactLookup.getString(contactLookup.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
                //String contactId = contactLookup.getString(contactLookup.getColumnIndex(BaseColumns._ID));
            }
        } finally {
            if (contactLookup != null) {
                contactLookup.close();
            }
        }

        return name;
    }
    
    public interface ServiceCallback {
        void onSuccess(Object data);
        void onError(String error);
    }
    
    /**
     * Get All your friends on WEIBO
     */
    public void getAllFirends(final ServiceCallback callback) {
        System.out.println("getGirends");
        final Weibo weibo = Weibo.getInstance(Configs.WEIBO_APP_KEY, Configs.WEIBO_REDIRECT_URL);
        
        SharedPreferences pref = getSharedPreferences("user", Context.MODE_PRIVATE);
        String accessToken = pref.getString("access_token", null);
        String expires_in = pref.getString("expires_in", null);
        String uid = pref.getString("uid", null);
        if (accessToken == null || uid == null) {
            return;
        }
        
        weibo.accessToken = new Oauth2AccessToken(accessToken, expires_in);
        weibo.setupConsumerConfig(Configs.WEIBO_APP_KEY, Configs.WEIBO_APP_SECRET);
        // 不写这句会报WeiboException: auth faild! 21301 
//        Utility.setAuthorization(new Oauth2AccessTokenHeader());
        
        FriendshipsAPI api = new FriendshipsAPI(weibo.accessToken);
        long id = Long.parseLong(uid);
        api.friends(id, 50, 0, false, new RequestListener() {

            @Override
            public void onComplete(String response) {
                System.out.println("friends: " + response);
                if (!TextUtils.isEmpty(response)) {
                    final WeiboUsers users = JSON.parseObject(response,
                            WeiboUsers.class);
                    if (users != null) {
                        new Thread() {
                            public void run() {
                                db = ContactsDatabase.getInstance(getApplicationContext());
                                dao = new WeiboUserDao(db.getOpenHelper(), ContactsDatabase.CONTACTS_TABLE_NAME);
                                List<WeiboUser> list = users.getUsers();
                                if (list != null) {
                                    dao.getDb(true).execSQL("DELETE FROM " + ContactsDatabase.CONTACTS_TABLE_NAME);
                                    for (WeiboUser user : list) {
                                        if (user != null && user.isFollow_me()) { // 互粉的
                                            dao.insert(user);
                                        }
                                    }
                                    if (callback != null) callback.onSuccess(list);
                                    return;
                                }
                                
                            };
                        }.start();
                       
                    }
                }
                if (callback != null) callback.onError("没有获取到任何朋友");
            }

            @Override
            public void onIOException(IOException e) {
                e.printStackTrace();
                if (callback != null) callback.onError(e.getMessage());
            }

            @Override
            public void onError(WeiboException e) {
                e.printStackTrace();
                if (callback != null) callback.onError(e.getMessage());
            }

        });

    }


}
