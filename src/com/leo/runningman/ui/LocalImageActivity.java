package com.leo.runningman.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.leo.runningman.util.Utils;

public class LocalImageActivity extends Activity implements OnClickListener{
	private Button btn_back,btn_delete;
	private ImageView img_nodata;
	private TextView txt_nodata,txt_delete_confirm;
	private GridView gridView;
	private boolean onDelete = false;
	private List<Integer> deletelist = new ArrayList<Integer>();
	private List<Bitmap> data = null;
	private ImageAdapter mAdapter = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.layout_local);
		
		btn_back = (Button)findViewById(R.id.btn_back);
		btn_delete = (Button)findViewById(R.id.btn_delete);
		txt_delete_confirm = (TextView)findViewById(R.id.txt_delete_confirm);
		
		img_nodata = (ImageView)findViewById(R.id.img_nodata);
		txt_nodata = (TextView)findViewById(R.id.txt_nodata);
		gridView = (GridView)findViewById(R.id.grid_local);
		
		btn_back.setOnClickListener(this);
		btn_delete.setOnClickListener(this);
		txt_delete_confirm.setOnClickListener(this);
		
		data = listSavedBitmap();
		mAdapter = new ImageAdapter(this, data);
		gridView.setAdapter(mAdapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				ImageView img_check = (ImageView)arg1.findViewById(R.id.img_unchecked);
				if(onDelete){
					if(deletelist.contains(arg2)){
						deletelist.remove((Integer)arg2);
//						img_check.setBackgroundResource(R.drawable.image_uncheck);
					}else{
						deletelist.add(arg2);
//						img_check.setBackgroundResource(R.drawable.check_delete);
						img_check.setPressed(true);
					}
				}
			}
		});
		if(mAdapter.getCount()==0){
			img_nodata.setVisibility(View.VISIBLE);
			txt_nodata.setVisibility(View.VISIBLE);
		}else{
			img_nodata.setVisibility(View.GONE);
			txt_nodata.setVisibility(View.GONE);
		}
	}
	
	private List<File> allSaveBitmapFile = null;
	private List<Bitmap> listSavedBitmap(){
		List<Bitmap> list = new ArrayList<Bitmap>();
		allSaveBitmapFile = new ArrayList<File>();
		File[] files = Utils.browseSavedBitmap();
		if(files != null && files.length>0){
			for (File file : files) {
				Bitmap bitmap = Utils.getBitmap(file);
				if(bitmap != null){
					list.add(bitmap);
					allSaveBitmapFile.add(file);
				}
			}
			return list;
		}
		return null;
	}
	
	public class ImageAdapter extends BaseAdapter{
		private Context mContext;
		public List<Bitmap> mList;
		private LayoutInflater inflater;  

		public ImageAdapter(Context mContext,List<Bitmap> list) {
			this.mContext = mContext;
			this.mList = list;
			inflater = LayoutInflater.from(mContext);
		}

		@Override
		public int getCount() {
			if(mList != null ){
				return mList.size();
			}
			return 0;
		}

		@Override
		public Object getItem(int arg0) {
			if(mList != null){
				return mList.get(arg0);
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
//			ImageView imageView;
			ViewHolder viewHolder;
			if(convertView == null){
//				imageView = new ImageView(mContext);
				convertView = inflater.inflate(R.layout.local_griditem, null);
				convertView.setMinimumWidth(MyApplication.SCREEN_WIDTH/2);
				viewHolder = new ViewHolder();
				viewHolder.img_item = (ImageView)convertView.findViewById(R.id.img_item);
				viewHolder.img_item.setLayoutParams(new LayoutParams(MyApplication.SCREEN_WIDTH/2-2, MyApplication.SCREEN_WIDTH/2-2));
				viewHolder.img_check = (ImageView)convertView.findViewById(R.id.img_unchecked);
				convertView.setTag(viewHolder);
//				imageView.setLayoutParams(new GridView.LayoutParams(MyApplication.SCREEN_WIDTH/2-10,MyApplication.SCREEN_WIDTH/2-10));
//				imageView.setScaleType(ImageView.ScaleType.FIT_XY);
//				imageView.setPadding(5, 5, 5, 5);
			}else{
//				imageView = (ImageView)convertView;
				viewHolder = (ViewHolder)convertView.getTag();
			}
			if(mList != null && mList.size()>0){
//				imageView.setImageBitmap(mList.get(position));
				viewHolder.img_item.setImageBitmap(mList.get(position));
				viewHolder.img_check.setBackgroundResource(R.drawable.image_uncheck);
				if(onDelete){
					viewHolder.img_check.setVisibility(View.VISIBLE);
				}else{
					viewHolder.img_check.setVisibility(View.GONE);
				}
			}
//			return imageView;
			return convertView;
		}
		
	}

	static class ViewHolder   
	{   
		public ImageView img_item;   
		public ImageView img_check;  
	}  
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.btn_back:
			this.finish();
			break;
		case R.id.btn_delete:
			if(data != null && data.size()>0){
				if(!onDelete){
					onDelete = true;
				}
				btn_delete.setVisibility(View.GONE);
				txt_delete_confirm.setVisibility(View.VISIBLE);
				mAdapter.notifyDataSetChanged();
			}
			break;
		case R.id.txt_delete_confirm:
			onDelete = false;
			if(deletelist != null && deletelist.size() >0){
				for (Integer i : deletelist) {
					Utils.deleteSDCardFile(allSaveBitmapFile.get(i));
				}
				deletelist.clear();
				data = listSavedBitmap();
				mAdapter = new ImageAdapter(this, data);
				gridView.setAdapter(mAdapter);
				if(data == null || data.size() == 0){
					img_nodata.setVisibility(View.VISIBLE);
					txt_nodata.setVisibility(View.VISIBLE);
				}
			}
			if(mAdapter != null)mAdapter.notifyDataSetChanged();
			txt_delete_confirm.setVisibility(View.GONE);
			btn_delete.setVisibility(View.VISIBLE);
			break;
			default:
				break;
		}
	}

	@Override
	public void onBackPressed() {
		if(onDelete){
			txt_delete_confirm.setVisibility(View.GONE);
			btn_delete.setVisibility(View.VISIBLE);
			onDelete = false;
			if(mAdapter != null)mAdapter.notifyDataSetChanged();
		}else{
			super.onBackPressed();
		}
	}
	
	
}
