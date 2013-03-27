package com.github.riotopsys.malforandroid2.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.google.inject.Inject;
import com.jakewharton.DiskLruCache;
import com.jakewharton.DiskLruCache.Editor;
import com.jakewharton.DiskLruCache.Snapshot;

public class LazyLoader {
    
    private static final String TAG = LazyLoader.class.getSimpleName();
	private volatile static LruCache<String, Bitmap> memoryCache;
    private volatile static DiskLruCache fileCache;
    
    private Map<ImageView, String> imageViews=Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService;
    Handler handler=new Handler();//handler to display images in UI thread
    
    @Inject
    public LazyLoader(Context context){
    	
    	if (memoryCache == null) {
    		memoryCache = new LruCache<String, Bitmap>((int) ((Runtime.getRuntime().maxMemory() / 1024) /8));
		}

		if (fileCache == null) {
			File cache = new File(context.getCacheDir(), "images");
			cache.mkdir();
			try {
				fileCache = DiskLruCache.open(cache, 1, 1, 1024 * 1024 );
			} catch (IOException e) {
				Log.e(TAG, "", e);
			}
		}
		
        executorService=Executors.newFixedThreadPool(5);
    }
    
    public void DisplayImage(String url, ImageView imageView, int fail)
    {
        imageViews.put(imageView, url);
        Bitmap bitmap=memoryCache.get(url);
        if(bitmap!=null)
            imageView.setImageBitmap(bitmap);
        else
        {
            queuePhoto(url, imageView, fail);
            imageView.setImageResource(fail);
        }
    }
        
    private void queuePhoto(String url, ImageView imageView, int fail)
    {
        PhotoToLoad p=new PhotoToLoad(url, imageView, fail);
        executorService.submit(new PhotosLoader(p));
    }
    
    private Bitmap getBitmap(String url) 
    {
    	Snapshot snapshot;
		String key = url2Key(url);
		try {
			snapshot = fileCache.get(key);
		} catch (IOException e) {
			snapshot = null;
		}

		// from SD cache
		if (snapshot != null) {
			Bitmap b = BitmapFactory.decodeStream(snapshot.getInputStream(0));
			if (b != null)
				return b;
		}
        
        //from web
        try {
            Bitmap bitmap=null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is=conn.getInputStream();
           
            bitmap = BitmapFactory.decodeStream(is);
            
            if ( bitmap != null ){
				Editor entry = fileCache.edit(key);
				if ( entry != null ){
					OutputStream os = entry.newOutputStream(0);
					bitmap.compress(CompressFormat.PNG, 100, os);
					os.close();
					entry.commit();
				}
			}
            
            return bitmap;
        } catch (Throwable ex){
           ex.printStackTrace();
           if(ex instanceof OutOfMemoryError)
               memoryCache.evictAll();
           return null;
        }
    }

    
    //Task for the queue
    private class PhotoToLoad
    {
        public String url;
        public ImageView imageView;
		public int fail;
        public PhotoToLoad(String u, ImageView i, int f){
            url=u; 
            imageView=i;
            fail = f;
        }
    }
    
    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;
        PhotosLoader(PhotoToLoad photoToLoad){
            this.photoToLoad=photoToLoad;
        }
        
        @Override
        public void run() {
            try{
                if(imageViewReused(photoToLoad))
                    return;
                Bitmap bmp=getBitmap(photoToLoad.url);
                memoryCache.put(photoToLoad.url, bmp);
                if(imageViewReused(photoToLoad))
                    return;
                BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad);
                handler.post(bd);
            }catch(Throwable th){
                Log.e(TAG, "", th);
            }
        }
    }
    
    boolean imageViewReused(PhotoToLoad photoToLoad){
        String tag=imageViews.get(photoToLoad.imageView);
        if(tag==null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }
    
    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        public BitmapDisplayer(Bitmap b, PhotoToLoad p){bitmap=b;photoToLoad=p;}
        public void run()
        {
            if(imageViewReused(photoToLoad))
                return;
            if(bitmap!=null)
                photoToLoad.imageView.setImageBitmap(bitmap);
            else
                photoToLoad.imageView.setImageResource(photoToLoad.fail);
        }
    }

    public void clearCache() {
        memoryCache.evictAll();
    }
    
    private String url2Key(String url){
		String hash = "";
	    try
	    {
	        MessageDigest digest = MessageDigest.getInstance( "SHA-1" );
	        byte[] bytes = url.getBytes("UTF-8");
	        digest.update(bytes, 0, bytes.length);
	        bytes = digest.digest();
	        StringBuilder sb = new StringBuilder();
	        for( byte b : bytes )
	        {
	            sb.append( String.format("%02x", b) );
	        }
	        hash = sb.toString();
	    }
	    catch( NoSuchAlgorithmException e ){
	        e.printStackTrace();
	    }
	    catch( UnsupportedEncodingException e ){
	        e.printStackTrace();
	    }
	    return hash;
	}

}