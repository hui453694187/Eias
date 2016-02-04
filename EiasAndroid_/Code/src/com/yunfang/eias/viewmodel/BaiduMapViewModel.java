package com.yunfang.eias.viewmodel;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.yunfang.eias.R;
import com.yunfang.eias.dto.ResidentialDTO;
import com.yunfang.eias.model.MapPointObj;
import com.yunfang.framework.base.BaseBroadcastReceiver;
import com.yunfang.framework.model.ViewModelBase;

/**
 * 
 * @author 林豇平
 * 
 */
public class BaiduMapViewModel extends ViewModelBase {
	/**
	 * 第一次加载
	 */
	public boolean isFirstLoad = false;
	/**
	 * 当前 级别
	 */
	public float currentZoomLevel = 17.0f;
	/**
	 * 
	 */
	public float zoomForDistrictEnd = 17.0f;
	/**
	 * 
	 */
	public float zoomForDistrictStart = 17.0f;
	/**
	 * 大于或等于指定等级显示冒泡窗
	 */
	public float zoomForShowPop = 17;
	
	/**
	 * 
	 */
	public boolean loadDistrictCompele;
	/**
	 * 红色标记点
	 */
	public BitmapDescriptor redMarker = BitmapDescriptorFactory
			.fromResource(R.drawable.markpoint_red);

	/**
	 * 绿色标记点
	 */
	public BitmapDescriptor greenMarker = BitmapDescriptorFactory
			.fromResource(R.drawable.markpoint_green);
	/**
	 * 获取屏幕矩形缩进值
	 */
	public int screenSpan = 10;
	/**
	 * 首页用于切换到更多城市的判断
	 */
	public String more = "MORE";
	/**
	 * 转换成地图标记对象
	 * 
	 * @param rItem
	 *            小区信息
	 * @return
	 */
	public MapPointObj toMapPoint(ResidentialDTO rItem) {
		MapPointObj p = new MapPointObj(Double.parseDouble(rItem.Longitude),
				Double.parseDouble(rItem.Latitude), "");
		// 均价环比大于0就用红色图标 否则用绿色
		if (Double.parseDouble(rItem.AvgRelative) > 0) {
			p.Icon = redMarker;
		} else {
			p.Icon = greenMarker;
		}
		p.Address = rItem.Address;
		p.AvgPrice = rItem.AvgPrice;
		p.AvgRelative = rItem.AvgRelative;
		p.Rent = rItem.Rent;
		p.RentRelative = rItem.RentRelative;
		p.ResidentialName = rItem.Name;
		return p;
	}

	/*是*
	 * 通知
	 */
	public BaseBroadcastReceiver broadcastReceiver = null;

}
