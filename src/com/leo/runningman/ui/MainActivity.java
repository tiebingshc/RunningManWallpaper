package com.leo.runningman.ui;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabWidget;
import android.widget.TextView;

import com.leo.runningman.util.DummyTabContent;

public class MainActivity extends FragmentActivity implements OnTabChangeListener{
	private TabHost tabHost;
	private TabWidget tabWidget;
	private LinearLayout layout_bottom;
	private FragmentTransaction ft;        //android.suport.v4.app.FragmentTransaction
	private RelativeLayout tabHome,tabNew,tabLocal,tabSetting;
	private ImageGridFragment homeFragment;
	private NewestFragment newestFragment;
	private LocalImageFragment localFragment;
	private SettingFragment settingFragment;
	private int CURRENT_TAB = 0;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_main);
		initTabView();
		tabHost.setup();
		
        tabHost.setOnTabChangedListener(this);
        initTab();
        tabHost.setCurrentTab(0);
	}

	private void initTabView() {
		tabHost = (TabHost)findViewById(android.R.id.tabhost);
		tabWidget = (TabWidget)findViewById(android.R.id.tabs);	
		LinearLayout layout = (LinearLayout)tabHost.getChildAt(0);
		TabWidget tw = (TabWidget)layout.getChildAt(1);
		
	     tabHome = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tab_indicator, tw, false);
	     TextView txt_home = (TextView)tabHome.getChildAt(1);
	     ImageView img_home = (ImageView)tabHome.getChildAt(0);
	     txt_home.setText(R.string.home);
	     img_home.setBackgroundResource(R.drawable.selector_home);
	     
	     tabNew = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tab_indicator, tw, false);
	     TextView txt_new = (TextView)tabNew.getChildAt(1);
	     ImageView img_new = (ImageView)tabNew.getChildAt(0);
	     txt_new.setText(R.string.newest);
	     img_new.setBackgroundResource(R.drawable.selector_newest);
	     
	     tabLocal = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tab_indicator, tw, false);
	     TextView txt_local = (TextView)tabLocal.getChildAt(1);
	     ImageView img_local = (ImageView)tabLocal.getChildAt(0);
	     txt_local.setText(R.string.local);
	     img_local.setBackgroundResource(R.drawable.btn_download);   //TODO
	     
	     tabSetting = (RelativeLayout) LayoutInflater.from(this).inflate(R.layout.tab_indicator, tw, false);
	     TextView txt_setting = (TextView)tabSetting.getChildAt(1);
	     ImageView img_setting = (ImageView)tabSetting.getChildAt(0);
	     txt_setting.setText(R.string.setting);
	     img_setting.setBackgroundResource(R.drawable.btn_wallpaper);   //TODO
	}

	@Override
	public void onTabChanged(String tabId) {
		FragmentManager fm = getSupportFragmentManager();
		homeFragment = (ImageGridFragment) fm.findFragmentByTag("home");
		newestFragment = (NewestFragment) fm.findFragmentByTag("newest");
		localFragment = (LocalImageFragment) fm.findFragmentByTag("local");
		settingFragment = (SettingFragment) getFragmentManager().findFragmentByTag("setting");
		ft = fm.beginTransaction();
		
		if(homeFragment!=null){
			ft.detach(homeFragment);
		}
		
		if(newestFragment!=null){
			ft.detach(newestFragment);
		}
		
		if(localFragment != null){
			ft.detach(localFragment);
		}
		
		if(settingFragment != null){
			getFragmentManager().beginTransaction().detach(settingFragment);
//			ft.detach(settingFragment);
		}
		
		if(tabId.equalsIgnoreCase("home")){
			isTabHome();
			CURRENT_TAB = 1;
		}else if(tabId.equalsIgnoreCase("newest")){
			isTabNewest();
			CURRENT_TAB = 2;
		}else if(tabId.equalsIgnoreCase("local")){
			isTabLocal();
			CURRENT_TAB = 3;
		}else if(tabId.equalsIgnoreCase("setting")){
			isTabSetting();
			CURRENT_TAB = 4;
		}else{
			switch(CURRENT_TAB){
			case 1:
				isTabHome();
				break;
			case 2:
				isTabNewest();
				break;
			case 3:
				isTabLocal();
				break;
			case 4:
				isTabSetting();
				break;
			default:
				isTabHome();
				break;
			}
		}
		ft.commit();
	}
	
    private void isTabHome(){
    	if(homeFragment==null){		
			ft.add(R.id.realtabcontent,new ImageGridFragment(), "home");						
		}else{
			ft.attach(homeFragment);						
		}
    }
    
    private void isTabNewest(){
    	if(newestFragment==null){		
			ft.add(R.id.realtabcontent,new NewestFragment(), "newest");						
		}else{
			ft.attach(newestFragment);						
		}
    }
    
    private void isTabLocal(){
    	if(localFragment==null){		
			ft.add(R.id.realtabcontent,new LocalImageFragment(), "local");						
		}else{
			ft.attach(localFragment);						
		}
    }
 
    private void isTabSetting(){
    	if(settingFragment==null){		
//			ft.add(R.id.realtabcontent,new SettingFragment(), "setting");	
			getFragmentManager().beginTransaction().replace(R.id.realtabcontent, new SettingFragment());
		}else{
//			ft.attach(settingFragment);		
			getFragmentManager().beginTransaction().detach(settingFragment);
		}
    }
    
	private void initTab() {
		TabHost.TabSpec tSpecHome = tabHost.newTabSpec("home");
        tSpecHome.setIndicator(tabHome);        
        tSpecHome.setContent(new DummyTabContent(getBaseContext()));
        tabHost.addTab(tSpecHome);
        
        TabHost.TabSpec tSpecNew = tabHost.newTabSpec("newest");
        tSpecNew.setIndicator(tabNew);        
        tSpecNew.setContent(new DummyTabContent(getBaseContext()));
        tabHost.addTab(tSpecNew);
        
        TabHost.TabSpec tSpecLocal = tabHost.newTabSpec("local");
        tSpecLocal.setIndicator(tabLocal);        
        tSpecLocal.setContent(new DummyTabContent(getBaseContext()));
        tabHost.addTab(tSpecLocal);
        
        TabHost.TabSpec tSpecSetting = tabHost.newTabSpec("setting");
        tSpecSetting.setIndicator(tabSetting);        
        tSpecSetting.setContent(new DummyTabContent(getBaseContext()));
        tabHost.addTab(tSpecSetting);
	}

	@Override
	public void onBackPressed() {
		dialog();
	}
	
	protected void dialog() {
		  AlertDialog.Builder builder = new Builder(MainActivity.this);
		  builder.setMessage(getResources().getString(R.string.confirm_exit));

		  builder.setTitle(getResources().getString(R.string.app_name));

		  builder.setPositiveButton(getResources().getString(R.string.confirm), new OnClickListener() {

			   @Override
			   public void onClick(DialogInterface dialog, int which) {
				   dialog.dismiss();
				   MainActivity.this.finish();
				   android.os.Process.killProcess(android.os.Process.myPid());
			   }
		  });

		  builder.setNegativeButton(getResources().getString(R.string.cancel), new OnClickListener() {

			   @Override
			   public void onClick(DialogInterface dialog, int which) {
			    dialog.dismiss();
			   }
		  });

		  builder.create().show();
	}
}
