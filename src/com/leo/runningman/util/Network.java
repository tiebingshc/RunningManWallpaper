package com.leo.runningman.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.widget.Toast;

import com.leo.runningman.bean.Image;

public class Network {
	private static final String TAG = "Network";
	private static final String BASE_URL = "http://leosandy-meiupic.stor.sinaapp.com/";
	private Context mContext;
	private static Network instance = null;
	public List<String> imageUrls = new ArrayList<String>();
	public List<String> imageThumbUrls = new ArrayList<String>();
	public List<String> imgNames = new ArrayList<String>();
	public List<String> newImageUrls = new ArrayList<String>();
	public List<String> newImageThumbUrls = new ArrayList<String>();
	public List<String> newImgNames = new ArrayList<String>();
	
	private Network(Context mContext) {
		this.mContext = mContext;
	}

	public static Network getInstance(Context context){
		if(instance == null){
			instance = new Network(context);
		}
		return instance;
	}
	
	public void getUrls(int aid,int start,int count){
		String HTTP_GET_URL = "http://leosandy.sinaapp.com/api_photo.php?aid="+aid+"&start="+start+"&count="+count;
		try {
			AndroidHttpClient client = AndroidHttpClient.newInstance("RunningMan");
			HttpGet httpGet = new HttpGet(HTTP_GET_URL);
			HttpResponse response = client.execute(httpGet);
			
			StringBuilder builder = new StringBuilder(); 
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				Toast.makeText(mContext, "Network status isn't OK!", Toast.LENGTH_SHORT).show();	//TODO
			}else{
				BufferedReader reader = new BufferedReader(new InputStreamReader( response.getEntity().getContent())); 
				 for (String s = reader.readLine(); s != null; s = reader.readLine()) { 
					 builder.append(s); 
				 } 
				try {
					List<Image> list = JsonUtils.parseJson(builder.toString());
					for (Image image : list) {
						imageThumbUrls.add(BASE_URL+image.getThumbNailURL());
						imageUrls.add(BASE_URL+image.getPathURL());
						imgNames.add(image.getCreateTime());
					}
				} catch (Exception e) {
					e.printStackTrace();
				} 

			}
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void getNewUrls(int aid,int start,int count){
		String HTTP_GET_URL = "http://leosandy.sinaapp.com/api_photo.php?aid="+aid+"&start="+start+"&count="+count;
		try {
			AndroidHttpClient client = AndroidHttpClient.newInstance("RunningMan");
			HttpGet httpGet = new HttpGet(HTTP_GET_URL);
			HttpResponse response = client.execute(httpGet);
			
			StringBuilder builder = new StringBuilder(); 
			if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
				Toast.makeText(mContext, "Network status isn't OK!", Toast.LENGTH_SHORT).show();	//TODO
			}else{
				BufferedReader reader = new BufferedReader(new InputStreamReader( response.getEntity().getContent())); 
				 for (String s = reader.readLine(); s != null; s = reader.readLine()) { 
					 builder.append(s); 
				 } 
				try {
					List<Image> list = JsonUtils.parseJson(builder.toString());
					for (Image image : list) {
						newImageThumbUrls.add(BASE_URL+image.getThumbNailURL());
						newImageUrls.add(BASE_URL+image.getPathURL());
						newImgNames.add(image.getCreateTime());
					}
				} catch (Exception e) {
					e.printStackTrace();
				} 

			}
			client.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public List<String> getAllImageUrls(){
		return imageUrls;
	}
	
	public List<String> getAllImageThumbUrls(){
		return imageThumbUrls;
	}
	
	public List<String> getNewImageUrls(){
		return newImageUrls;
	}
	
	public List<String> getNewImageThumbUrls(){
		return newImageThumbUrls;
	}
}
