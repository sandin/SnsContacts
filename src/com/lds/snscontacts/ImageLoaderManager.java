package com.lds.snscontacts;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap.CompressFormat;

import com.ldsuniversalimageloader.cache.memory.impl.SoftMemoryCache;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.download.URLConnectionImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

/**
 * Manger for Image Loader
 * 
 * @see https://github.com/nostra13/Android-Universal-Image-Loader
 * @author lds
 */
public class ImageLoaderManager {
	
//	public static DisplayImageOptions defaultDisplayImageOptions =
//	        createDisplayImageOptions(R.drawable.default_goods_img, ImageScaleType.EXACT);
	
	public static File cacheDir;
	
	private static ImageLoader sImageLoader;
	
	public static ImageLoader getImageLoader(Context context) {
		if (sImageLoader == null) {
			sImageLoader = createImageLoader(context);
		}
		return sImageLoader;
	}
	
	private static File ensureCacheDir(Context context) {
	    if (cacheDir == null) {
	        cacheDir = StorageUtils.getOwnCacheDirectory(context, "ldscache");
	    }
	    return cacheDir;
	}
	
	/**
	 * Factory of ImageLoader 
	 * 
	 * @param context
	 * @return
	 */
	public static ImageLoader createImageLoader(Context context) {
	    ensureCacheDir(context);
		
		ImageLoader imageloader = ImageLoader.getInstance();
		// Create configuration for ImageLoader (all options are optional)
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
		            .memoryCacheExtraOptions(480, 800)
		            .discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75) // Can slow ImageLoader, use it carefully (Better don't use it)
		            .threadPoolSize(5)
		            .threadPriority(Thread.MIN_PRIORITY + 2)
		            .denyCacheImageMultipleSizesInMemory()
		            //.offOutOfMemoryHandling()
		            //.memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // You can pass your own memory cache implementation
		            .memoryCache(new SoftMemoryCache()) // You can pass your own memory cache implementation
		            .discCache(new UnlimitedDiscCache(cacheDir)) // You can pass your own disc cache implementation
		            .discCacheFileNameGenerator(new HashCodeFileNameGenerator())
		            .imageDownloader(new URLConnectionImageDownloader(5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)
		            .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
		            .enableLogging()
		            .build();
		// Initialize ImageLoader with created configuration. Do it once.
		imageloader.init(config);
		return imageloader;
	}
	
	/**
	 * Factory of Display Image Options 
	 * @param defaultImageResId
	 * @param imageScaleType
	 * @return
	 */
	public static DisplayImageOptions createDisplayImageOptions(int defaultImageResId, ImageScaleType imageScaleType) {
	    return new DisplayImageOptions.Builder()
	            .showImageForEmptyUri(defaultImageResId)
	            .showStubImage(defaultImageResId)
	            .cacheInMemory()
	            .cacheOnDisc()
	            .imageScaleType(imageScaleType)
	            .build();
	}
	
	/**
	 * 删除所有文件缓存（直接删除缓存目录）
	 * 
	 * @param context
	 * @return
	 */
	public static boolean clearCache(Context context) {
	    ImageLoader loader = getImageLoader(context);
	    loader.clearDiscCache();
	    loader.clearMemoryCache();
	    return true;
	}
	
}
