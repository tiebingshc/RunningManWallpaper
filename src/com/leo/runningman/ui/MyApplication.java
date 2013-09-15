package com.leo.runningman.ui;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;

public class MyApplication extends Application{

	public static int SCREEN_WIDTH;
	public static int SCREEN_HEIGHT;
	public static float SCREEN_DENSITY;
	public static Context mContext;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
		SCREEN_WIDTH = displayMetrics.widthPixels;
		SCREEN_HEIGHT = displayMetrics.heightPixels;
		SCREEN_DENSITY = displayMetrics.density;
		
		mContext = getApplicationContext();
	}
	
	public static final int getScaledSize(int size){
		
		return (int) (size*SCREEN_DENSITY + 0.5);
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		super.onLowMemory();
	}
}
