/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.leo.runningman.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.widget.Toast;

/**
 * Class containing some static utility methods.
 */
public class Utils {
    public static final int IO_BUFFER_SIZE = 8 * 1024;

    private Utils() {};

    /**
     * Workaround for bug pre-Froyo, see here for more info:
     * http://android-developers.blogspot.com/2011/09/androids-http-clients.html
     */
    public static void disableConnectionReuseIfNecessary() {
        // HTTP connection reuse which was buggy pre-froyo
        if (hasHttpConnectionBug()) {
            System.setProperty("http.keepAlive", "false");
        }
    }

    /**
     * Get the size in bytes of a bitmap.
     * @param bitmap
     * @return size in bytes
     */
    @SuppressLint("NewApi")
    public static int getBitmapSize(Bitmap bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
            return bitmap.getByteCount();
        }
        // Pre HC-MR1
        return bitmap.getRowBytes() * bitmap.getHeight();
    }

    /**
     * Check if external storage is built-in or removable.
     *
     * @return True if external storage is removable (like an SD card), false
     *         otherwise.
     */
    @SuppressLint("NewApi")
    public static boolean isExternalStorageRemovable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }

    /**
     * Get the external app cache directory.
     *
     * @param context The context to use
     * @return The external cache dir
     */
    @SuppressLint("NewApi")
    public static File getExternalCacheDir(Context context) {
        if (hasExternalCacheDir()) {
            return context.getExternalCacheDir();
        }

        // Before Froyo we need to construct the external cache dir ourselves
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }

    /**
     * Check how much usable space is available at a given path.
     *
     * @param path The path to check
     * @return The space available in bytes
     */
    @SuppressLint("NewApi")
    public static long getUsableSpace(File path) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            return path.getUsableSpace();
        }
        final StatFs stats = new StatFs(path.getPath());
        return (long) stats.getBlockSize() * (long) stats.getAvailableBlocks();
    }

    /**
     * Get the memory class of this device (approx. per-app memory limit)
     *
     * @param context
     * @return
     */
    public static int getMemoryClass(Context context) {
        return ((ActivityManager) context.getSystemService(
                Context.ACTIVITY_SERVICE)).getMemoryClass();
    }

    /**
     * Check if OS version has a http URLConnection bug. See here for more information:
     * http://android-developers.blogspot.com/2011/09/androids-http-clients.html
     *
     * @return
     */
    public static boolean hasHttpConnectionBug() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.FROYO;
    }

    /**
     * Check if OS version has built-in external cache dir method.
     *
     * @return
     */
    public static boolean hasExternalCacheDir() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    /**
     * Check if ActionBar is available.
     *
     * @return
     */
    public static boolean hasActionBar() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }
    
    /**
     * Save bitmap to SDCard  /rm_img/*.png
     * @param bitmap
     * @param name
     */
    public static boolean saveBitmapToSDCard(Bitmap bitmap,String name,Context context) {
    	boolean saveSuccess = false;
    	boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
    	String state = Environment.getExternalStorageState();
    	if (state.equals(Environment.MEDIA_MOUNTED)) {  				//拥有可读写权限
    		mExternalStorageAvailable = mExternalStorageWriteable = true;
    		FileOutputStream fos = null;
    		// 获取扩展存储设备的文件目录
    		File sdFile = Environment.getExternalStorageDirectory();
    		//创建文件存储目录
    		File baseDir = new File(sdFile.getAbsolutePath()+File.separator+"rm_img"+File.separator);
            if(!baseDir.exists()){
            	baseDir.mkdirs();
            }
            //创建新文件
            File newFile = new File(baseDir.getAbsolutePath(),name);
            try {
            	if(!newFile.exists()){
            		newFile.createNewFile();
            	}
            	fos = new FileOutputStream(newFile);
            	saveSuccess = bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            	fos.flush();
            	fos.close();
            	fos = null;
            	
//            	bitmap.recycle();
//            	System.gc();
//            	bitmap = null;
            } catch (Exception e) {
            	e.printStackTrace();
            }
    	}else if(state.equals(Environment.MEDIA_MOUNTED_READ_ONLY)){	//拥有只读权限
    	    // We can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
            if(context != null){
            	Toast.makeText(context, "Your SDcared is read only.",Toast.LENGTH_LONG).show();
            }
    		saveSuccess = false;
    	}else{
    	    // Something else is wrong. It may be one of many other states, but
            // all we need
            // to know is we can neither read nor write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
            if(context != null){
            	Toast.makeText(context, "Your SDcared is unmounted.",
            			Toast.LENGTH_LONG).show();
            }
    		saveSuccess = false;
    	}
        return saveSuccess;
    }
    
   public static boolean deleteSDCardFile(File file) {
        File baseDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/rm_img/");
        if(!baseDir.exists()){
        	return false;
        }
        if(file.exists()){
        	return file.delete();
        }
        return false;
    }
    
    public static File[] browseSavedBitmap(){
        File baseDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/rm_img/");
        if(!baseDir.exists()){
        	return null;
        }else{
        	FilenameFilter filter = new FilenameFilter() {
				
				@Override
				public boolean accept(File dir, String filename) {
					if(filename.endsWith(".png")){
						return true;
					}
					return false;
				}
			};
        	return baseDir.listFiles(filter);
        }
    }
    
	public static Bitmap getBitmap(File file) {
		Bitmap bitmap = null;
		FileInputStream fis = null;
		if (file.exists()) {
			try {
				fis = new FileInputStream(file);
				bitmap = BitmapFactory.decodeStream(fis);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}finally{
				if(fis != null){
					try {
						fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return bitmap;
	}
}
