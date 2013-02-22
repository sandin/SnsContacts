package com.lds.snscontacts;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.MenuItem;
import com.lds.snscontacts.service.ContactsService;
import com.lds.snscontacts.service.ContactsService.ServiceCallback;
import com.weibo.sdk.android.Weibo;
import com.weibo.sdk.android.WeiboAuthListener;
import com.weibo.sdk.android.WeiboDialogError;
import com.weibo.sdk.android.WeiboException;

public class MainActivity extends SherlockActivity implements OnClickListener {

    private Button authBtn;
    private Button getFriendsBtn;
    private Button showWindowBtn;
    private Button startBtn;
    private Button stopBtn;
    private EditText incomingNumberInput;

    ContactsService mService;
    boolean mBound = false;

    private SharedPreferences mPref;
    
    private Handler mHandler;
    private static final int MSG_TOAST = 1;
    protected static final int MSG_GET_FRIENDS = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                case MSG_TOAST:
                    Toast.makeText(getApplicationContext(), (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case MSG_GET_FRIENDS:
                    getFirends();
                    break;
                default:
                    break;
                }
            }
        };

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.title_account);

        authBtn = (Button) findViewById(R.id.auth_btn);
        getFriendsBtn = (Button) findViewById(R.id.get_friends_btn);
        showWindowBtn = (Button) findViewById(R.id.show_window_btn);
        startBtn = (Button) findViewById(R.id.start_btn);
        stopBtn = (Button) findViewById(R.id.stop_btn);
        incomingNumberInput = (EditText) findViewById(R.id.incoming_number_input);

        authBtn.setOnClickListener(this);
        getFriendsBtn.setOnClickListener(this);
        showWindowBtn.setOnClickListener(this);
        startBtn.setOnClickListener(this);
        stopBtn.setOnClickListener(this);

        mPref = getSharedPreferences("user", Context.MODE_PRIVATE);

        // startService(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case android.R.id.home:
            Intent intent = new Intent(this, ContactsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent intent = new Intent(this, ContactsService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mBound) {
            unbindService(mConnection);
        }

        Intent intent = new Intent(this, ContactsService.class);
        startService(intent);
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get
            // LocalService instance
            ContactsService.LocalBinder binder = (ContactsService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.auth_btn:
            authorize();
            break;
        case R.id.get_friends_btn:
            getFirends();
            break;
        case R.id.show_window_btn:
            findFirends(incomingNumberInput.getText().toString().trim()
                    .replace(" ", ""));
            break;
        case R.id.start_btn:
            listenTelephone();
            break;
        case R.id.stop_btn:
            unlistenTelephone();
            break;
        default:
            break;
        }
    }

    private void findFirends(String incomingNumber) {
        if (TextUtils.isEmpty(incomingNumber))
            return;

        if (mBound) {
            mService.findFirends(incomingNumber);
        } else {
            Toast.makeText(getApplicationContext(), "服务未开启", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void getFirends() {
        if (mBound) {
            toast("正在请求您的微博好友列表...");
            mService.getAllFirends(new ServiceCallback() {
                @Override
                public void onSuccess(Object data) {
                    if (data != null) {
                        toast(String.format("成功获取%s名好友", ((List)data).size()));
                    }
                }
                @Override
                public void onError(String error) {
                    toast(error);
                }
            });
        } else {
            Toast.makeText(getApplicationContext(), "服务未开启", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private void listenTelephone() {
        if (mBound) {
            mService.listenTelephone();
        } else {
            Toast.makeText(getApplicationContext(), "服务未开启", Toast.LENGTH_SHORT)
                    .show();
        }
        Toast.makeText(getApplicationContext(), "开启监听", Toast.LENGTH_SHORT)
                .show();
    }

    private void unlistenTelephone() {
        if (mBound) {
            mService.unlistenTelephone();
        } else {
            Toast.makeText(getApplicationContext(), "服务未开启", Toast.LENGTH_SHORT)
                    .show();
        }
        Toast.makeText(getApplicationContext(), "关闭监听", Toast.LENGTH_SHORT)
                .show();
    }

    private void authorize() {
        Utils.clearCookie(this);
        final Weibo weibo = Weibo.getInstance(Configs.WEIBO_APP_KEY,
                Configs.WEIBO_REDIRECT_URL);
        weibo.authorize(this, new WeiboAuthListener() {

            @Override
            public void onWeiboException(WeiboException e) {
                e.printStackTrace();
                toast(e.getMessage());
            }

            @Override
            public void onError(WeiboDialogError e) {
                e.printStackTrace();
                toast(e.getMessage());
            }

            @Override
            public void onComplete(Bundle values) {
                String uid = values.getString("uid");
                long id = Long.parseLong(uid);

                // store accessToken
                Editor editor = mPref.edit();
                editor.putString("expires_in", values.getString("expires_in"));
                editor.putString("remind_in", values.getString("remind_in"));
                editor.putString("access_token",
                        values.getString("access_token"));
                editor.putString("uid", values.getString("uid"));
                editor.commit();
                
                toast("授权成功");
                mHandler.sendEmptyMessage(MSG_GET_FRIENDS);
            }

            @Override
            public void onCancel() {
                // TODO Auto-generated method stub
            }
        });

    }
    
    private void toast(String text) {
        final Message msg = mHandler.obtainMessage(MSG_TOAST, text);
        msg.sendToTarget();
    }

}
