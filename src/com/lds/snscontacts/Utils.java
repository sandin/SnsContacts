package com.lds.snscontacts;

import android.content.Context;
import android.database.sqlite.SQLiteDiskIOException;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

public class Utils {
	
	// 清除所有Cookie
	public static void clearCookie(Context context) {
	    try {
    		CookieSyncManager.createInstance(context); 
    		CookieManager cookieManager = CookieManager.getInstance();
    		cookieManager.removeAllCookie();
	    } catch (SQLiteDiskIOException e) {
	        e.printStackTrace();
	    } catch (Throwable e) {
	        e.printStackTrace();
	    }
	}

}
