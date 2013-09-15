package com.leo.runningman.util;

import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

public class ImageCrop {
	private Activity mActivity;
	private static ImageCrop instance = null;
	
	private ImageCrop(Activity mContext) {
		this.mActivity = mContext;
	}
	
	public static ImageCrop getInstance(Activity context){
		if(instance == null){
			instance = new ImageCrop(context);
		}
		return instance;
	}
	
	/**
	 * 从相册截大图：通过Uri来传递和解析图片
	 * @param outputX
	 * @param outputY
	 * @param uri
	 * @param imageFormat
	 */
	public void startCrop(int outputX,int outputY,Uri uri,String imageFormat,int requestCode){
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
		intent.setType("image/*");
		intent.putExtra("crop", "true");
		intent.putExtra("aspectX", 2);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", outputX);
		intent.putExtra("outputY", outputY);
		intent.putExtra("scale", true);
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("outputFormat",imageFormat);   //Bitmap.CompressFormat.JPEG.toString()
		intent.putExtra("noFaceDetection", true); 	   // no face detection
		mActivity.startActivityForResult(intent, requestCode);
	}
	
	/**
	 * 通过Uri来解析图片
	 * @param uri
	 * @return
	 */
	public Bitmap decodeUriAsBitmap(Uri uri){
	    Bitmap bitmap = null;
	    try {
	        bitmap = BitmapFactory.decodeStream(mActivity.getContentResolver().openInputStream(uri));
	    } catch (FileNotFoundException e) {
	        e.printStackTrace();
	        return null;
	    }
	    return bitmap;
	}

}
