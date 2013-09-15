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

package com.leo.runningman.ui;

import java.io.IOException;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.leo.runningman.util.ImageCrop;
import com.leo.runningman.util.ImageWorker;
import com.leo.runningman.util.Network;
import com.leo.runningman.util.Utils;

/**
 * This fragment will populate the children of the ViewPager from {@link ImageDetailActivity}.
 */
public class ImageDetailFragment extends Fragment implements OnClickListener{
	public static final String TAG = "ImageDetailFragment";
	
    private static final String IMAGE_DATA_EXTRA = "resId";
    private static final String IMAGE_URL = "img_url";
    private int mImageNum;
    public ImageView img_wallpaper;
    private RelativeLayout layout_title;
    private Button btn_back,btn_wallpaper,btn_download;
    private ImageWorker mImageWorker;
    private FragmentActivity mActivity;   
    private Thread hideThread;
    
    /**
     * Factory method to generate a new instance of the fragment given an image number.
     *
     * @param imageNum The image number within the parent adapter to load
     * @return A new instance of ImageDetailFragment with imageNum extras
     */
    public static ImageDetailFragment newInstance(int imageNum) {
        final ImageDetailFragment f = new ImageDetailFragment();

        final Bundle args = new Bundle();
        args.putInt(IMAGE_DATA_EXTRA, imageNum);
        f.setArguments(args);

        return f;
    }
    
    public static ImageDetailFragment newInstance(int imageNum,String url) {
        final ImageDetailFragment f = new ImageDetailFragment();

        final Bundle args = new Bundle();
        args.putInt(IMAGE_DATA_EXTRA, imageNum);
        args.putString(IMAGE_URL, url);
        f.setArguments(args);
        return f;
    }

    /**
     * Empty constructor as per the Fragment documentation
     */
    public ImageDetailFragment() {}

    /**
     * Populate image number from extra, use the convenience factory method
     * {@link ImageDetailFragment#newInstance(int)} to create this fragment.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageNum = getArguments() != null ? getArguments().getInt(IMAGE_DATA_EXTRA) : -1;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate and locate the main ImageView
        final View v = inflater.inflate(R.layout.image_detail_fragment, container, false);
        img_wallpaper = (ImageView) v.findViewById(R.id.img_wallpaper);
        layout_title = (RelativeLayout)v.findViewById(R.id.layout_title);
        btn_back = (Button)v.findViewById(R.id.btn_back);
        btn_wallpaper = (Button) v.findViewById(R.id.btn_wallpaper);
        btn_download = (Button)v.findViewById(R.id.btn_download);
        
        autoHideview(3000);
        layout_title.setVisibility(View.GONE);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = this.getActivity();
        // Use the parent activity to load the image asynchronously into the ImageView (so a single
        // cache can be used over all pages in the ViewPager
        if (ImageDetailActivity.class.isInstance(getActivity())) {
            mImageWorker = ((ImageDetailActivity) getActivity()).getImageWorker();
            mImageWorker.loadImage(mImageNum, img_wallpaper);
        }
        // Pass clicks on the ImageView to the parent activity to handle
//        if (OnClickListener.class.isInstance(getActivity()) && Utils.hasActionBar()) {
//            img_wallpaper.setOnClickListener((OnClickListener) getActivity());
//        }
        img_wallpaper.setOnClickListener(this);
        btn_back.setOnClickListener(this);
        btn_wallpaper.setOnClickListener(this);
        btn_download.setOnClickListener(this);
    }

    /**
     * Cancels the asynchronous work taking place on the ImageView, called by the adapter backing
     * the ViewPager when the child is destroyed.
     */
    public void cancelWork() {
        ImageWorker.cancelWork(img_wallpaper);
        img_wallpaper.setImageDrawable(null);
        img_wallpaper = null;
        btn_wallpaper = null;
    }

	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_back:
			this.getActivity().finish();
			break;
		case R.id.img_wallpaper:
			if(layout_title.isShown()){
				layout_title.setVisibility(View.GONE);
				cancelAutoHideview();
			}else{
				layout_title.setVisibility(View.VISIBLE);
				autoHideview(4000);
			}
			break;
		case R.id.btn_wallpaper:
			if (ImageDetailActivity.class.isInstance(mActivity) && mImageWorker.getImageCache() != null){
				int currentItem = ((ImageDetailActivity) mActivity).mPager.getCurrentItem();
				Bitmap bitmap = mImageWorker.getImageCache().getBitmapFromMemCache(String.valueOf(this.mImageWorker.getAdapter().getItem(currentItem)));
				//1.download
				boolean isSaved = false;
				String imgName = Network.getInstance(mActivity).imgNames.get(currentItem);
				if(bitmap != null){
					isSaved = Utils.saveBitmapToSDCard(bitmap,imgName+".JPEG",getActivity());
					//TODO asynckTask
				}
				if(isSaved){
					Toast.makeText(mActivity, mActivity.getResources().getString(R.string.download_success), Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(mActivity,mActivity.getResources().getString(R.string.download_failure), Toast.LENGTH_SHORT).show();
				}
				
				//2.Crop (ËøÆÁ/±ÚÖ½)
			    String IMAGE_FILE_LOCATION = "file:///sdcard/rm_img/"+imgName+"/";
				Uri imageUri = Uri.parse(IMAGE_FILE_LOCATION);//The Uri to store the big bitmap

				ImageCrop.getInstance(mActivity).startCrop(MyApplication.SCREEN_WIDTH*2, MyApplication.SCREEN_HEIGHT, imageUri, Bitmap.CompressFormat.JPEG.toString(), 0);
				
				//3.setWallpaper
//				if(bitmap != null){
//					setWapaper(bitmap);
//				} 
			}
			break;
		case R.id.btn_download:
			if (ImageDetailActivity.class.isInstance(mActivity) && mImageWorker.getImageCache() != null){
				int currentItem = ((ImageDetailActivity) mActivity).mPager.getCurrentItem();
//				String data = String.valueOf(this.mImageWorker.getAdapter().getItem(currentItem));
				Bitmap bitmap = mImageWorker.getImageCache().getBitmapFromMemCache(String.valueOf(this.mImageWorker.getAdapter().getItem(currentItem)));
				boolean isSaved = false;
				if(bitmap != null){
					isSaved = Utils.saveBitmapToSDCard(bitmap,Network.getInstance(mActivity).imgNames.get(currentItem)+".png",getActivity());
				}
				if(isSaved){
					Toast.makeText(mActivity, mActivity.getResources().getString(R.string.download_success), Toast.LENGTH_SHORT).show();
				}else{
					Toast.makeText(mActivity,mActivity.getResources().getString(R.string.download_failure), Toast.LENGTH_SHORT).show();
				}
			}
			break;
			default:
				break;
			
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		hideTitleLayout();
	}

	public void hideTitleLayout() {
		if(layout_title != null){
			layout_title.setVisibility(View.GONE);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
//		hideTitleLayout();
		if(Utils.hasActionBar() &&  this.getActivity().getActionBar().isShowing()){
			this.getActivity().getActionBar().hide();
		}
	}
	
	private void setWapaper(final Bitmap bitmap) {
		final Context mContext= this.getActivity();
		new Handler().post(new Runnable() {
			
			@Override
			public void run() {
				try {
					WallpaperManager wpm = WallpaperManager.getInstance(mContext);
					float minH =  wpm.getDesiredMinimumHeight();
					float minW =  wpm.getDesiredMinimumWidth();
					Bitmap targetBitmap = getResizedBitmap(bitmap, (int)minH, (int)minW);
					wpm.setBitmap(targetBitmap);
					Toast.makeText(mContext, getActivity().getResources().getString(R.string.set_wallpaper_success), 200).show();
				} catch (IOException e) {
					e.printStackTrace();
					Toast.makeText(mContext, getActivity().getResources().getString(R.string.set_wallpaper_failure), 200).show();
				}
				
			}
		});
	}

    private Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);
        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }
    
    private static final int HIDE_TITLE = 2;
	public void autoHideview(long millions)
	{
		if(hideThread == null){
			hideThread = new Thread(){
				public void run(){
					sendHandlerMessage(HIDE_TITLE, 0, 0, null);
				}
			};
		}
		if(layout_title.getVisibility() == View.VISIBLE)
		{
			cancelAutoHideview();
			layout_title.postDelayed(hideThread, millions);
		}
	}
	
	public void cancelAutoHideview(){
		if(hideThread != null){
			layout_title.removeCallbacks(hideThread);
		}
	}
	
	public boolean sendHandlerMessage(int what,int arg1,int arg2,Object obj){
		Message msg = Message.obtain();
		if(msg != null){
			msg.what = what;
			msg.arg1 = arg1;
			msg.arg2 = arg2;
			msg.obj = obj;
			return mHandler.sendMessage(msg);
		}
		return false;
	}
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			switch(msg.what){
			case HIDE_TITLE:
				hideTitleLayout();
				break;
				default:
					break;
			}
		}
		
	};

}
