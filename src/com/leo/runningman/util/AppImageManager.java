package com.leo.runningman.util;

public class AppImageManager {
	private static ImageFetcher mImageFetcher;

	public static ImageFetcher getImageFetcher(){
		
		if(mImageFetcher == null){
//			mImageFetcher = new ImageFetcher(MyApplication.mContext);
//			ImageCacheParams cacheParams = new ImageCacheParams(MyApplication.mContext,".image");
//			cacheParams.setMemCacheSizePercent(MyApplication.mContext, 0.25f);
//			mImageFetcher.addImageCache(new ImageCache(cacheParams));
		}
		
		return mImageFetcher;
	}
}
