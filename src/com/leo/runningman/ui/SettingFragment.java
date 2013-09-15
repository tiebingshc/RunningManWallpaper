package com.leo.runningman.ui;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class SettingFragment extends PreferenceFragment{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
     	Window window = getActivity().getWindow();
     	window.setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.layout_title);
    	
        TextView txt_title = (TextView)this.getActivity().findViewById(R.id.txt_title);
        txt_title.setText(getActivity().getResources().getString(R.string.setting));
        Button btn_delete = (Button)this.getActivity().findViewById(R.id.btn_delete);
        TextView txt_delete_confirm = (TextView)this.getActivity().findViewById(R.id.txt_delete_confirm);
        btn_delete.setVisibility(View.GONE);
        txt_delete_confirm.setVisibility(View.GONE);
		return inflater.inflate(R.layout.layout_setting, container, false);
	}
}
