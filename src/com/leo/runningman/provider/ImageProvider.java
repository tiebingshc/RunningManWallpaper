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

package com.leo.runningman.provider;

import java.util.List;
import com.leo.runningman.util.ImageWorker.ImageWorkerAdapter;
import com.leo.runningman.util.Network;

/**
 * Image Data Provider
 */
public class ImageProvider {
	
	/**
	 * the main module's all urls
	 */
    public final static ImageWorkerAdapter imageWorkerUrlsAdapter = new ImageWorkerAdapter() {
    	List<String> imageUrls = Network.getInstance(null).getAllImageUrls();
        @Override
        public Object getItem(int num) {
        	  if(imageUrls != null && imageUrls.size()>0){
        		  return imageUrls.get(num);
        	  }else{
        		  return null;
        	  }
        }

        @Override
        public int getSize() {
        	if(imageUrls != null && imageUrls.size()>0){
        		return imageUrls.size();
        	}
        	return 0;
        }
    };

    public final static ImageWorkerAdapter imageThumbWorkerUrlsAdapter = new ImageWorkerAdapter() {
    		List<String> imageThumbUrls = Network.getInstance(null).getAllImageThumbUrls();
    		
        @Override
        public Object getItem(int num) {
        	if(imageThumbUrls != null && imageThumbUrls.size()>0){
        		return imageThumbUrls.get(num);
        	}
        	return null;
        }

        @Override
        public int getSize() {
        	if(imageThumbUrls != null && imageThumbUrls.size()>0){
        		return imageThumbUrls.size();
        	}
        	return 0;
        }
    };
    
    /**
     * The newest module's urls
     */
    public final static ImageWorkerAdapter newUrlsAdapter = new ImageWorkerAdapter() {
    	List<String> imageUrls = Network.getInstance(null).getNewImageUrls();
        @Override
        public Object getItem(int num) {
        	  if(imageUrls != null && imageUrls.size()>0){
        		  return imageUrls.get(num);
        	  }
    		  return null;
        }

        @Override
        public int getSize() {
        	if(imageUrls != null && imageUrls.size()>0){
        		return imageUrls.size();
        	}
        	return 0;
        }
    };

    public final static ImageWorkerAdapter newThumbWorkerUrlsAdapter = new ImageWorkerAdapter() {
    	List<String> imageThumbUrls = Network.getInstance(null).getNewImageThumbUrls();
        @Override
        public Object getItem(int num) {
        	if(imageThumbUrls != null && imageThumbUrls.size()>0){
        		return imageThumbUrls.get(num);
        	}
        	return null;
        }

        @Override
        public int getSize() {
        	if(imageThumbUrls != null && imageThumbUrls.size()>0){
        		return imageThumbUrls.size();
        	}
        	return 0;
        }
    };
}
