package com.lds.snscontacts.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;

import com.edroid.dao.BaseDao;
import com.lds.snscontacts.db.ContactsDatabase.ContactsCols;
import com.lds.snscontacts.model.Status;
import com.lds.snscontacts.model.WeiboUser;

public class WeiboUserDao extends BaseDao<WeiboUser> {

    public WeiboUserDao(SQLiteOpenHelper mOpenHelper, String tableName) {
        super(mOpenHelper, tableName);
    }
    
    private int avatar_large_index;
    private int description_index;
    private int gender_index;
    private int location_index;
    private int name_index;
    private int profile_image_url_index;
    private int remark_index;
    private int screen_name_index;
    private int uid_index;
    private int status_text_index;
    private int follow_me_index;

    @Override
    public WeiboUser cursorToObject(Cursor cursor) {
        avatar_large_index = cursor.getColumnIndex(ContactsCols.AVATAR_LARGE);
        description_index = cursor.getColumnIndex(ContactsCols.DESCRIPTION);
        gender_index = cursor.getColumnIndex(ContactsCols.GENDER);
        location_index = cursor.getColumnIndex(ContactsCols.LOCATION);
        name_index = cursor.getColumnIndex(ContactsCols.NAME);
        profile_image_url_index = cursor.getColumnIndex(ContactsCols.PROFILE_IMAGE_URL);
        remark_index = cursor.getColumnIndex(ContactsCols.REMARK);
        screen_name_index = cursor.getColumnIndex(ContactsCols.SCREEN_NAME);
        uid_index = cursor.getColumnIndex(ContactsCols.UID);
        status_text_index = cursor.getColumnIndex(ContactsCols.STATUS_TEXT);
        follow_me_index = cursor.getColumnIndex(ContactsCols.FOLLOW_ME);
        
        WeiboUser user = new WeiboUser();
        user.setAvatar_large(cursor.getString(avatar_large_index));
        user.setDescription(cursor.getString(description_index));
        user.setGender(cursor.getString(gender_index));
        user.setLocation(cursor.getString(location_index));
        user.setName(cursor.getString(name_index));
        user.setProfile_image_url(cursor.getString(profile_image_url_index));
        user.setRemark(cursor.getString(remark_index));
        user.setScreen_name(cursor.getString(screen_name_index));
        Status status = new Status();
        status.setText(cursor.getString(status_text_index));
        user.setStatus(status);
        user.setFollow_me(cursor.getInt(follow_me_index) == 1);
//        String idstr = cursor.getString(uid_index);
//        long id = Long.parseLong(idstr);
//        user.setId(id);
//        user.setIdstr(idstr);
        return user;
    }

    @Override
    public ContentValues objectToValues(WeiboUser user) {
        ContentValues v = new ContentValues();
        v.put(ContactsCols.AVATAR_LARGE, user.getAvatar_large());
        v.put(ContactsCols.DESCRIPTION, user.getDescription());
        v.put(ContactsCols.GENDER, user.getGender());
        v.put(ContactsCols.LOCATION, user.getLocation());
        v.put(ContactsCols.NAME, user.getName());
        v.put(ContactsCols.PROFILE_IMAGE_URL, user.getProfile_image_url());
        v.put(ContactsCols.REMARK, user.getRemark());
        v.put(ContactsCols.SCREEN_NAME, user.getScreen_name());
        v.put(ContactsCols.UID, user.getId());
        v.put(ContactsCols.FOLLOW_ME, user.isFollow_me());
        Status status = user.getStatus();
        if (status != null) {
            v.put(ContactsCols.STATUS_TEXT, status.getText());
        }
        return v;
    }

}
