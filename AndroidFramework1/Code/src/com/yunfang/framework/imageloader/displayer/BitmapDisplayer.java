
package com.yunfang.framework.imageloader.displayer;

import android.graphics.Bitmap;
import android.widget.ImageView;

/** 
* @ClassName: BitmapDisplayer 
* @Description:  图片加载接口
* @author 贺隽
* @date 2015-7-8 19:02:25 
*  
*/
public interface BitmapDisplayer {
	
	void display(Bitmap bitmap, ImageView imageView);
	void display(int resouceID,ImageView imageView);
}
