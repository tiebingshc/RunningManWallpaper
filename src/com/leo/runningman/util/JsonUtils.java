package com.leo.runningman.util;

import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.leo.runningman.bean.Image;

/**
 * @author tiebing
 *
 */

//使用Google的Gson API来解析Json数据
public class JsonUtils {
	public static List<Image> parseJson(String jsonData){
		List<Image> list = new ArrayList<Image>();
		try {
			//如果需要解析Json数据，先生成一个JsonReader对象
			JsonReader reader = new JsonReader(new StringReader(jsonData));
			reader.beginArray();
			while(reader.hasNext()){		//判断有没有json对象
				Image image = new Image();
				reader.beginObject();		//开始解析json对象
				while(reader.hasNext()){	//判断有没有json键值对
					String tagName = reader.nextName();  //得到下一个键值对的键
					if(tagName.equals("thumb")){
						image.setThumbNailURL(reader.nextString());
					}else if(tagName.equals("path")){
						image.setPathURL(reader.nextString());
					}else if(tagName.equals("create_time")){
						image.setCreateTime(reader.nextString());
					}
				}
				reader.endObject();
				list.add(image);
			}
			reader.endArray();	
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public Image parseObjectFromJson(String jsonData){
		Gson gson = new Gson();
		Image image = gson.fromJson(jsonData, Image.class);
		return image;
	}
	
	public static LinkedList<Image> parseObjectArrayFromJson(String jsonData){
		Type listType = new TypeToken<LinkedList<Image>>(){}.getType();
		Gson gson = new Gson();
		LinkedList<Image> images = gson.fromJson(jsonData, listType);
		return images;
	}
	
}
