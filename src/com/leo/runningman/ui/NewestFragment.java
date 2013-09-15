package com.leo.runningman.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.leo.runningman.provider.ImageProvider;
import com.leo.runningman.util.ImageCache;
import com.leo.runningman.util.ImageFetcher;
import com.leo.runningman.util.ImageResizer;
import com.leo.runningman.util.Network;
import com.leo.runningman.util.ImageCache.ImageCacheParams;

public class NewestFragment extends Fragment implements AdapterView.OnItemClickListener{
    private static final String TAG = "ImageGridFragment";
    private static final String IMAGE_CACHE_DIR = "thumbs";
    
    private int mImageThumbSize;
    private int mImageThumbSpacing;
    private ImageAdapter mAdapter;
    private ImageResizer mImageWorker;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
        mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);

        mAdapter = new ImageAdapter(getActivity());

        ImageCacheParams cacheParams = new ImageCacheParams(IMAGE_CACHE_DIR);
        
        mImageWorker = new ImageFetcher(getActivity(), mImageThumbSize);
        mImageWorker.setAdapter(ImageProvider.newThumbWorkerUrlsAdapter);
        mImageWorker.setLoadingImage(R.drawable.empty_photo);			//ÉèÖÃÄ¬ÈÏ¼ÓÔØ±³¾°
        mImageWorker.setImageCache(ImageCache.findOrCreateCache(getActivity(), cacheParams));
        
        new HttpGetTask(1000L).execute();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
    	Window window = getActivity().getWindow();
    	window.setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.layout_title);
        TextView txt_title = (TextView)this.getActivity().findViewById(R.id.txt_title);
        txt_title.setText(getActivity().getResources().getString(R.string.newest));
        Button btn_delete = (Button)this.getActivity().findViewById(R.id.btn_delete);
        TextView txt_delete_confirm = (TextView)this.getActivity().findViewById(R.id.txt_delete_confirm);
        btn_delete.setVisibility(View.GONE);
        txt_delete_confirm.setVisibility(View.GONE);
        
        final View v = inflater.inflate(R.layout.layout_newest, container, false);
        final GridView mGridView = (GridView) v.findViewById(R.id.grid_new);
        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);
        
        mGridView.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        if (mAdapter.getNumColumns() == 0) {
                            final int numColumns = (int) Math.floor(
                                    mGridView.getWidth() / (mImageThumbSize + mImageThumbSpacing));
                            if (numColumns > 0) {
                                final int columnWidth =
                                        (mGridView.getWidth() / numColumns) - mImageThumbSpacing;
                                mAdapter.setNumColumns(numColumns);
                                mAdapter.setItemHeight(columnWidth);
                                if (BuildConfig.DEBUG) {
                                    Log.d(TAG, "onCreateView - numColumns set to " + numColumns);
                                }
                            }
                        }
                    }
                });
		return v;
	}

   private class ImageAdapter extends BaseAdapter {

        private final Context mContext;
        private int mItemHeight = 0;
        private int mNumColumns = 0;
        private int mActionBarHeight = -1;
        private GridView.LayoutParams mImageViewLayoutParams;

        public ImageAdapter(Context context) {
            super();
            mContext = context;
            mImageViewLayoutParams = new GridView.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        }
		
		        @Override
        public int getCount() {
		            // Size of adapter + number of columns for top empty row
		    return mImageWorker.getAdapter().getSize() + mNumColumns;
		}
		
		@Override
		public Object getItem(int position) {
		    return position < mNumColumns ?
		            null : mImageWorker.getAdapter().getItem(position - mNumColumns);
		}
		
		@Override
		public long getItemId(int position) {
		    return position < mNumColumns ? 0 : position - mNumColumns;
		}
		
		@Override
		public int getViewTypeCount() {
		    // Two types of views, the normal ImageView and the top row of empty views
		    return 2;
		}
		
		@Override
		public int getItemViewType(int position) {
		    return (position < mNumColumns) ? 1 : 0;
		}
		
		@Override
		public boolean hasStableIds() {
		    return true;
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup container) {
		    // First check if this is the top row
		    if (position < mNumColumns) {
		        if (convertView == null) {
		            convertView = new View(mContext);
		        }
		        // Calculate ActionBar height
		//	                if (mActionBarHeight < 0) {
		//	                    TypedValue tv = new TypedValue();
		//	                    if (mContext.getTheme().resolveAttribute(
		//	                            android.R.attr.actionBarSize, tv, true)) {
		//	                        mActionBarHeight = TypedValue.complexToDimensionPixelSize(
		//	                                tv.data, mContext.getResources().getDisplayMetrics());
		//	                    } else {
		//	                        // No ActionBar style (pre-Honeycomb or ActionBar not in theme)
		//	                        mActionBarHeight = 0;
		//	                    }
		//	                }
		        mActionBarHeight = 0;
		        // Set empty view with height of ActionBar
		        convertView.setLayoutParams(new AbsListView.LayoutParams(
		                ViewGroup.LayoutParams.MATCH_PARENT, mActionBarHeight));
		        return convertView;
		    }
		
		    // Now handle the main ImageView thumbnails
		    ImageView imageView;
		    if (convertView == null) { // if it's not recycled, instantiate and initialize
		        imageView = new ImageView(mContext);
		        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		        imageView.setLayoutParams(mImageViewLayoutParams);
		    } else { // Otherwise re-use the converted view
		        imageView = (ImageView) convertView;
		    }
		
		    // Check the height matches our calculated column width
		    if (imageView.getLayoutParams().height != mItemHeight) {
		        imageView.setLayoutParams(mImageViewLayoutParams);
		    }
		
		    // Finally load the image asynchronously into the ImageView, this also takes care of
		    // setting a placeholder image while the background thread runs
		    mImageWorker.loadImage(position - mNumColumns, imageView);
		    return imageView;
		}
		
		/**
		 * Sets the item height. Useful for when we know the column width so the height can be set
		 * to match.
		 *
		 * @param height
		 */
        public void setItemHeight(int height) {
            if (height == mItemHeight) {
                return;
            }
            mItemHeight = height;
            mImageViewLayoutParams =
                    new GridView.LayoutParams(LayoutParams.MATCH_PARENT, mItemHeight);
            mImageWorker.setImageSize(height);
            notifyDataSetChanged();
        }
		
        public void setNumColumns(int numColumns) {
            mNumColumns = numColumns;
        }

        public int getNumColumns() {
            return mNumColumns;
        }
    }
   
	   @Override
	public void onResume() {
		super.onResume();
        mImageWorker.setExitTasksEarly(false);
        if(mAdapter != null && mAdapter.getCount()>0){
        	mAdapter.notifyDataSetChanged();
        }
	}
	   
    @Override
    public void onPause() {
        super.onPause();
        mImageWorker.setExitTasksEarly(true);
    }
    
    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
        final Intent i = new Intent(getActivity(), ImageDetailActivity.class);
        i.putExtra(ImageDetailActivity.EXTRA_IMAGE, (int) id);
        startActivity(i);
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
			Network.getInstance(getActivity()).getNewUrls(3,0,90);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mAdapter.notifyDataSetChanged();
		}
	}
}
