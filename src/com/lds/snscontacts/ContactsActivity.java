package com.lds.snscontacts;

import java.util.List;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.edroid.utils.TaskUtils;
import com.edroid.widget.BaseListAdapter;
import com.lds.snscontacts.dao.WeiboUserDao;
import com.lds.snscontacts.db.ContactsDatabase;
import com.lds.snscontacts.model.WeiboUser;
import com.lds.snscontacts.service.ContactsService;
import com.lds.snscontacts.service.ContactsService.ServiceCallback;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public class ContactsActivity extends SherlockActivity {
    
    private ListView list;
    private ListAdapter mAdapter;
    
    private ContactsDatabase db;
    private WeiboUserDao dao;
    
    private GetContactsTask getContactsTask;
    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        actionBar = getSupportActionBar();
        
        setContentView(R.layout.activity_contacts);
        
        list = (ListView) findViewById(android.R.id.list);
        mAdapter = new ListAdapter(this);
        list.setAdapter(mAdapter);
        
        db = ContactsDatabase.getInstance(getApplicationContext());
        dao = new WeiboUserDao(db.getOpenHelper(), ContactsDatabase.CONTACTS_TABLE_NAME);
        
        onRefresh();
    }
    
    private void onRefresh() {
        TaskUtils.cancelTaskInterrupt(getContactsTask);
        getContactsTask = new GetContactsTask();
        getContactsTask.execute();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getSupportMenuInflater();
        inflater.inflate(R.menu.contacts, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_refresh:
            getAllFirends();
            return true;
        case R.id.action_settings:
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    
    private void getAllFirends() {
        Toast.makeText(getApplicationContext(), "正在刷新...", Toast.LENGTH_SHORT).show();
        mService.getAllFirends(new ServiceCallback() {
            
            @Override
            public void onSuccess(Object data) {
                onRefresh();
            }
            
            @Override
            public void onError(String error) {
                System.out.println(error); // TODO
                
            }
        });
    }
    
    private boolean mBound;
    private ContactsService mService;
    
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

    class GetContactsTask extends AsyncTask<Void, Integer, List<WeiboUser>> {
        
        @Override
        protected List<WeiboUser> doInBackground(Void... params) {
            return dao.findAll();
        }
        
        @Override
        protected void onPostExecute(List<WeiboUser> result) {
            if (result != null) {
                mAdapter.refresh(result);
            }
        }
        
    }
    
    class ListAdapter extends BaseListAdapter<WeiboUser> {
        private ImageLoader imageLoader;
        private DisplayImageOptions options;

        public ListAdapter(Context context) {
            super(context);
            imageLoader = ImageLoaderManager.getImageLoader(getApplicationContext());
            options = ImageLoaderManager.createDisplayImageOptions(R.drawable.uc_default_avatar, ImageScaleType.EXACT);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parentView) {
            View view;
            if (convertView == null) {
                view = mInflater.inflate(R.layout.item_contact, null);
                ViewHolder holder = new ViewHolder();
                holder.name = (TextView) view.findViewById(R.id.name);
                holder.photo = (ImageView) view.findViewById(R.id.photo);
                view.setTag(holder);
            } else {
                view = convertView;
            }
            ViewHolder holder = (ViewHolder) view.getTag();
            WeiboUser bean = list.get(position);
            String realName = bean.getRemark();
            if (! TextUtils.isEmpty(realName)) {
                holder.name.setText(bean.getRemark());
            } else {
                holder.name.setText(bean.getName());
            }
            String photoUrl = bean.getProfile_image_url();
            if (! TextUtils.isEmpty(photoUrl)) {
                imageLoader.displayImage(photoUrl, holder.photo, options);
            }
            
            return view;
        }
        
        class ViewHolder {
            TextView name;
            ImageView photo;
        }
        
    }

}
