package com.leo.runningman.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;

import com.leo.runningman.util.Network;

public class LoaddingActivity extends Activity {
	private ImageView img_splash_anim;
	AnimationDrawable anim_drawable;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_loadding);
		
		new HttpGetTask(-1L).execute();
		
		img_splash_anim = (ImageView)findViewById(R.id.img_splash_anim);
		img_splash_anim.setBackgroundResource(R.drawable.anim_spalsh);
		anim_drawable = (AnimationDrawable)img_splash_anim.getBackground();

		AlphaAnimation alpha = new AlphaAnimation(1.0f, 0.1f);
		alpha.setDuration(4000L);
		img_splash_anim.startAnimation(alpha);
		
		alpha.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				anim_drawable.setAlpha(90);
				anim_drawable.start();
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				anim_drawable.stop();
			}
		});
	}

	class HttpGetTask extends AsyncTask<String, Void, Void>{
		private Long time = -1L;
		
		public HttpGetTask(Long time){
			this.time = time;
		}

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

		@Override
		protected Void doInBackground(String... params) {
			Network.getInstance(LoaddingActivity.this).getUrls(1,0,90);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			Intent intent = new Intent(LoaddingActivity.this,MainActivity.class);
			startActivity(intent);
			LoaddingActivity.this.finish();
		}
	}
	
}
