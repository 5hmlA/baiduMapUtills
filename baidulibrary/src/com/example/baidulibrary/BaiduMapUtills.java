package com.example.baidulibrary;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationConfiguration.LocationMode;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;

/**
 * @author yun
 * @time 2015/03/08
 * 
百度地图工具类
public boolean toast = true;// 是否打印吐司 默认true 打印
setZoom(float zoom) 设置 地图的缩放级别
setCenter(LatLng latLng) 设置地图中心
setCenterAndZoom(LatLng latLng, float zoom)设置地图中心点以及缩放级别

路线检索
startRouteSearch(String city, String start, String end,TransPortation transPortation)路线检索
nextRoute() 返回下一组数据
preRoute() 前一组数据
setRouteNodeIndex(int setNodeIndex)显示 第几组 路线 从0开始
setOnRoutePlanSearchListener 路线检索 监听器

兴趣点检索poi
searchInCity(String city, String searchkey)全城搜索
NextPageInCity()全城搜索下一页
PrePageInCity()全城搜索上一页
searchNearBy(LatLng position, String searchkey, int radius) 中心半径搜索
NextPageNearBy()半径搜索下一页
PrePageNearBy()半径城搜索上一页
setOnPoiSearchListener(OnPoiSearchListener psl)兴趣点检索监听

百度定位
getLocation() 定位
setChangeAlways(boolean changeAlways)是否实时更新 位置true为实时更新 默认false
setCurrentMarker(int id)设置定位的显示图片
setLocationMode(LocationMode mode)设置定位模式
setOnlyGetzb(boolean onlyGetzb) true为只获取坐标信息不显示在地图上 默认为false
setFrequency(int frequency) 设置卫星扫面间隔默认1000毫秒 每隔1秒更新位置
stopLocation()  取消定位  destory的时候 调用
setOnLocationSearchListener(OnLocationSearchListener lsl)定位监听
 *
 */
public class BaiduMapUtills {
	private Context context;
	private MapView mMapView;
	private BaiduMap mBaidumap;
	// 浏览路线节点相关
	int nodeIndex = -1;// 节点索引,供浏览节点时使用
	RouteLine route = null;
	OverlayManager routeOverlay = null;
	// boolean useDefaultIcon = false;
	private TextView popupText = null;// 泡泡view

	// 搜索相关
	RoutePlanSearch mSearch = null; // 搜索模块，也可去掉地图模块独立使用

	public BaiduMapUtills(Context context, MapView mMapView) {
		this.context = context;
		this.mMapView = mMapView;
		mBaidumap = mMapView.getMap();
	}

	/**
	 * 开始检索 路线
	 * 
	 * @param city
	 *            城市
	 * @param start
	 *            起点
	 * @param end
	 *            终点
	 * @param TransPortation
	 *            交通方式(枚举 DRIVING,WALKINF,TRANSIT)
	 */
	public void startRouteSearch(String city, String start, String end,
			TransPortation transPortation) {
		// 设置起终点信息，对于tranist search 来说，城市名有意义
		PlanNode stNode = PlanNode.withCityNameAndPlaceName(city, start);
		PlanNode enNode = PlanNode.withCityNameAndPlaceName(city, end);
		doRouteSearch(city, stNode, enNode, transPortation);
	}

	/**
	 * 开始检索 路线
	 * 
	 * @param city
	 *            城市
	 * @param position
	 *            起点坐标
	 * @param end
	 *            终点
	 * @param TransPortation
	 *            交通方式(枚举 DRIVING,WALKINF,TRANSIT)
	 */
	public void startRouteSearch(String city, LatLng position, String end,
			TransPortation transPortation) {
		// 设置起终点信息，对于tranist search 来说，城市名有意义
		PlanNode stNode = PlanNode.withLocation(position);
		PlanNode enNode = PlanNode.withCityNameAndPlaceName(city, end);
		doRouteSearch(city, stNode, enNode, transPortation);
	}

	/**
	 * 开始检索 路线
	 * 
	 * @param city
	 *            城市
	 * @param position
	 *            起点坐标
	 * @param endposition
	 *            终点坐标
	 * @param TransPortation
	 *            交通方式(枚举 DRIVING,WALKINF,TRANSIT)
	 */
	public void startRouteSearch(String city, LatLng position,
			LatLng endposition, TransPortation transPortation) {
		// 设置起终点信息，对于tranist search 来说，城市名有意义
		PlanNode stNode = PlanNode.withLocation(position);
		PlanNode enNode = PlanNode.withLocation(endposition);
		doRouteSearch(city, stNode, enNode, transPortation);
	}

	private void doRouteSearch(String city, PlanNode stNode, PlanNode enNode,
			TransPortation transPortation) {
		// 初始化搜索模块，注册事件监听
		mSearch = RoutePlanSearch.newInstance();
		mSearch.setOnGetRoutePlanResultListener(new MyOnGetRoutePlanResultListener());
		// 重置浏览节点的路线数据
		route = null;
		mBaidumap.clear();
		// // 设置起终点信息，对于tranist search 来说，城市名有意义
		// PlanNode stNode = PlanNode.withCityNameAndPlaceName(city, start);
		// PlanNode enNode = PlanNode.withCityNameAndPlaceName(city, end);
		if (transPortation == TransPortation.DRIVING) {
			// 驾车检索
			mSearch.drivingSearch((new DrivingRoutePlanOption()).from(stNode)
					.to(enNode));
		} else if (transPortation == TransPortation.TRANSIT) {
			// 公交检索
			mSearch.transitSearch((new TransitRoutePlanOption()).from(stNode)
					.city(city).to(enNode));
		} else if (transPortation == TransPortation.WALKING) {
			// 步行检索
			mSearch.walkingSearch((new WalkingRoutePlanOption()).from(stNode)
					.to(enNode));
		}
	}

	private onRoutePlanSearchListener rpsl;

	public void setOnRoutePlanSearchListener(onRoutePlanSearchListener rpsl) {
		this.rpsl = rpsl;
	}

	public interface onRoutePlanSearchListener {
		public void getDrivingRouteSucceed(DrivingRouteResult result);

		/**
		 * 
		 * @param error
		 *            两种错误"未找到结果" ,"起终点或途经点地址有岐义"
		 */
		public void getRouteError(String error);

		public void getWalkingRouteSucceed(WalkingRouteResult result);

		public void getTransitRouteSucceed(TransitRouteResult result);
	}

	class MyOnGetRoutePlanResultListener implements
			OnGetRoutePlanResultListener {

		@Override
		public void onGetDrivingRouteResult(DrivingRouteResult result) {
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				toast("抱歉，未找到结果");
				if (rpsl != null) {
					rpsl.getRouteError("未找到结果");
				}
			}
			if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
				// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
				// result.getSuggestAddrInfo()
				toast("起终点或途经点地址有岐义");
				if (rpsl != null) {
					rpsl.getRouteError("起终点或途经点地址有岐义");
				}
				return;
			}
			if (result.error == SearchResult.ERRORNO.NO_ERROR) {
				if (rpsl != null) {
					rpsl.getDrivingRouteSucceed(result);
				}
				nodeIndex = -1;
				route = result.getRouteLines().get(0);
				DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(
						mBaidumap);
				routeOverlay = overlay;
				mBaidumap.setOnMarkerClickListener(overlay);
				overlay.setData(result.getRouteLines().get(0));
				overlay.addToMap();
				overlay.zoomToSpan();
			}
			mSearch.destroy();
		}

		@Override
		public void onGetTransitRouteResult(TransitRouteResult result) {
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				toast("抱歉，未找到结果");
				if (rpsl != null) {
					rpsl.getRouteError("抱歉，未找到结果");
				}
			}
			if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
				// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
				// result.getSuggestAddrInfo()
				toast("起终点或途经点地址有岐义");
				if (rpsl != null) {
					rpsl.getRouteError("起终点或途经点地址有岐义");
				}
				return;
			}
			if (result.error == SearchResult.ERRORNO.NO_ERROR) {
				if (rpsl != null) {
					rpsl.getTransitRouteSucceed(result);
				}
				nodeIndex = -1;
				route = result.getRouteLines().get(0);
				TransitRouteOverlay overlay = new MyTransitRouteOverlay(
						mBaidumap);
				mBaidumap.setOnMarkerClickListener(overlay);
				routeOverlay = overlay;
				overlay.setData(result.getRouteLines().get(0));
				overlay.addToMap();
				overlay.zoomToSpan();
			}
		}

		@Override
		public void onGetWalkingRouteResult(WalkingRouteResult result) {
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				toast("抱歉，未找到结果");
				if (rpsl != null) {
					rpsl.getRouteError("抱歉，未找到结果");
				}
			}
			if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
				// 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
				toast("起终点或途经点地址有岐义");
				// result.getSuggestAddrInfo()
				if (rpsl != null) {
					rpsl.getRouteError("起终点或途经点地址有岐义");
				}
				return;
			}
			if (result.error == SearchResult.ERRORNO.NO_ERROR) {
				if (rpsl != null) {
					rpsl.getWalkingRouteSucceed(result);
				}
				nodeIndex = -1;
				route = result.getRouteLines().get(0);
				WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(
						mBaidumap);
				mBaidumap.setOnMarkerClickListener(overlay);
				routeOverlay = overlay;
				overlay.setData(result.getRouteLines().get(0));
				overlay.addToMap();
				overlay.zoomToSpan();

			}
			// mSearch.destroy();
		}
	}

	// 定制RouteOverly
	private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

		public MyDrivingRouteOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public BitmapDescriptor getStartMarker() {
			return null;
		}

		@Override
		public BitmapDescriptor getTerminalMarker() {
			return null;
		}
	}

	private class MyTransitRouteOverlay extends TransitRouteOverlay {

		public MyTransitRouteOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public BitmapDescriptor getStartMarker() {
			return null;
		}

		@Override
		public BitmapDescriptor getTerminalMarker() {
			return null;
		}
	}

	private class MyWalkingRouteOverlay extends WalkingRouteOverlay {

		public MyWalkingRouteOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public BitmapDescriptor getStartMarker() {
			return null;
		}

		@Override
		public BitmapDescriptor getTerminalMarker() {
			return null;
		}
	}

	/**
	 * 返回 下一组路线
	 */
	public void nextRoute() {
		if (route == null || route.getAllStep() == null) {
			return;
		}
		if (nodeIndex < route.getAllStep().size() - 1) {
			nodeIndex++;
		} else {
			return;
		}
		nodeClick();
	}

	/**
	 * 返回 上一组路线
	 */
	public void preRoute() {
		if (route == null || route.getAllStep() == null) {
			return;
		}
		if (nodeIndex == -1) {
			return;
		}
		if (nodeIndex > 0) {
			nodeIndex--;
		} else {
			return;
		}
		nodeClick();
	}

	/**
	 * 显示 第几组 路线 从0开始
	 * 
	 * @param nodeIndex
	 */
	public void setRouteNodeIndex(int setNodeIndex) {
		if (route == null || route.getAllStep() == null) {
			return;
		}
		if (setNodeIndex == -1 || route.getAllStep().size() <= setNodeIndex) {
			return;
		}
		nodeIndex = setNodeIndex;
		nodeClick();
	}

	private void nodeClick() {
		// 获取节结果信息
		LatLng nodeLocation = null;
		String nodeTitle = null;
		Object step = route.getAllStep().get(nodeIndex);
		if (step instanceof DrivingRouteLine.DrivingStep) {
			nodeLocation = ((DrivingRouteLine.DrivingStep) step).getEntrace()
					.getLocation();
			nodeTitle = ((DrivingRouteLine.DrivingStep) step).getInstructions();
		} else if (step instanceof WalkingRouteLine.WalkingStep) {
			nodeLocation = ((WalkingRouteLine.WalkingStep) step).getEntrace()
					.getLocation();
			nodeTitle = ((WalkingRouteLine.WalkingStep) step).getInstructions();
		} else if (step instanceof TransitRouteLine.TransitStep) {
			nodeLocation = ((TransitRouteLine.TransitStep) step).getEntrace()
					.getLocation();
			nodeTitle = ((TransitRouteLine.TransitStep) step).getInstructions();
		}

		if (nodeLocation == null || nodeTitle == null) {
			return;
		}
		// 移动节点至中心
		mBaidumap.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
		popupText = new TextView(context);
		popupText.setBackgroundResource(R.drawable.popup);
		popupText.setTextColor(0xFF000000);
		popupText.setText(nodeTitle);
		mBaidumap.showInfoWindow(new InfoWindow(popupText, nodeLocation, 0));
	}

	public enum TransPortation {
		WALKING, DRIVING, TRANSIT
	}

	// TODO
	// ================================================================================
	// poi检索
	private PoiSearch mPoiSearch = null;
	private int load_Index = 0;
	private String city, searchkey;
	private int radius;
	private LatLng position;
	private SuggestionSearch mSuggestionSearch = null;

	private void poiSearch() {
		// 初始化搜索模块，注册搜索事件监听
		mPoiSearch = PoiSearch.newInstance();

		mPoiSearch
				.setOnGetPoiSearchResultListener(new MyOnGetPoiSearchResultListener());
	}

	/**
	 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
	 */
	public void poiRequestSuggestionIncity(String searchkey, String city) {
		mSuggestionSearch = SuggestionSearch.newInstance();
		mSuggestionSearch
				.setOnGetSuggestionResultListener(new MyOnGetSuggestionResultListener());
		/**
		 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
		 */
		mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
				.keyword(searchkey).city(city));
	}

	public void poiRequestSuggestion(String searchkey) {
		mSuggestionSearch = SuggestionSearch.newInstance();
		mSuggestionSearch
				.setOnGetSuggestionResultListener(new MyOnGetSuggestionResultListener());
		/**
		 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
		 */
		mSuggestionSearch.requestSuggestion((new SuggestionSearchOption())
				.keyword(searchkey));
	}

	class MyOnGetSuggestionResultListener implements
			OnGetSuggestionResultListener {

		@Override
		public void onGetSuggestionResult(SuggestionResult res) {

			if (res == null || res.getAllSuggestions() == null) {
				osrl.getSuggestionResultError();
				return;
			}
			if (osrl != null) {
				osrl.getSuggestionResultSucced(res);
			}
			// toast(res.getAllSuggestions().size()+"");
		}

	}

	/**
	 * 全城搜索
	 * 
	 * @param city
	 *            搜索城市
	 * @param searchkey
	 *            关键字
	 */
	public void searchInCity(String city, String searchkey) {
		this.city = city;
		this.searchkey = searchkey;
		poiSearch();
		mPoiSearch.searchInCity((new PoiCitySearchOption()).city(city)
				.keyword(searchkey).pageNum(load_Index));
	}

	/**
	 * 半径 搜索
	 * 
	 * @param position
	 *            中心位置（经纬度）
	 * @param searchkey
	 *            关键字
	 * @param radius
	 *            搜索半径 单位： m
	 */
	public void searchNearBy(LatLng position, String searchkey, int radius) {
		this.position = position;
		this.searchkey = searchkey;
		this.radius = radius;
		poiSearch();
		mPoiSearch.searchNearby((new PoiNearbySearchOption())
				.location(position).keyword(searchkey).pageNum(load_Index)
				.radius(radius));
	}

	private int totalPageNum;

	/**
	 * 全城搜索下一页
	 */
	public void nextPageInCity() {
		if (load_Index > totalPageNum) {
			toast("没有更多数据了");
			return;
		}
		load_Index++;
		searchInCity(city, searchkey);
	}

	/**
	 * 全城搜索上一页
	 */
	public void prePageInCity() {
		if (load_Index <= 0) {
			return;
		}
		load_Index--;
		searchInCity(city, searchkey);
	}

	/**
	 * 半径搜索下一页
	 */
	public void nextPageNearBy() {
		if (load_Index > totalPageNum) {
			toast("没有更多数据了");
			return;
		}
		load_Index++;
		searchNearBy(position, searchkey, radius);
	}

	/**
	 * 半径城搜索上一页
	 */
	public void prePageNearBy() {
		if (load_Index <= 0) {
			return;
		}
		load_Index--;
		searchNearBy(position, searchkey, radius);
	}

	class MyOnGetPoiSearchResultListener implements
			OnGetPoiSearchResultListener {

		@Override
		public void onGetPoiDetailResult(PoiDetailResult result) {

			if (result.error != SearchResult.ERRORNO.NO_ERROR) {
				toast("抱歉，未找到结果");
			} else {
				toast(result.getName() + ": " + result.getAddress());
			}

		}

		@Override
		public void onGetPoiResult(PoiResult result) {
			totalPageNum = result.getTotalPageNum();
			if (result == null
					|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
				toast("抱歉，未找到结果");
				if (psl != null) {
					psl.getPoiResultError("未找到结果");
				}
				return;
			}
			if (result.error == SearchResult.ERRORNO.NO_ERROR) {
				mBaidumap.clear();
				PoiOverlay overlay = new MyPoiOverlay(mBaidumap);
				mBaidumap.setOnMarkerClickListener(overlay);
				overlay.setData(result);
				overlay.addToMap();
				overlay.zoomToSpan();
				if (psl != null) {
					psl.getPoiResultSucced(result);
				}
				return;
			}
			if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

				// 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
				String strInfo = "在";
				for (CityInfo cityInfo : result.getSuggestCityList()) {
					strInfo += cityInfo.city;
					strInfo += ",";
				}
				strInfo += "找到结果";
				toast(strInfo);
				if (psl != null) {
					psl.getPoiResultError(strInfo);
				}
				// mPoiSearch.destroy();
			}
		}

	}

	private class MyPoiOverlay extends PoiOverlay {

		public MyPoiOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public boolean onPoiClick(int index) {
			super.onPoiClick(index);
			PoiInfo poi = getPoiResult().getAllPoi().get(index);
			LatLng location = poi.location;
			// setCenterAndZoom(location, 14);
			setCenter(location);
			// if (poi.hasCaterDetails) {
			mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
					.poiUid(poi.uid));
			// }
			return true;
		}
	}

	private OnPoiSearchListener psl;

	public void setOnPoiSearchListener(OnPoiSearchListener psl) {
		this.psl = psl;
	}

	public interface OnPoiSearchListener {
		public void getPoiResultSucced(PoiResult result);

		public void getPoiResultError(String error);
	}

	private OnSuggestionResultListener osrl;

	/**
	 * 建议 搜索结果监听
	 * 
	 * @param osrl
	 */
	public void setOnSuggestionResultListener(OnSuggestionResultListener osrl) {
		this.osrl = osrl;
	}

	public interface OnSuggestionResultListener {
		public void getSuggestionResultSucced(SuggestionResult res);

		public void getSuggestionResultError();
	}

	// TODO
	// ==============================================================================
	// 百度 定位
	LocationClient mLocClient;
	public MyLocationListenner myListener = new MyLocationListenner();
	// 定位模式
	private LocationMode mCurrentMode = LocationMode.NORMAL;
	private BitmapDescriptor mCurrentMarker = null;// 系统默认
	private boolean onlyGetzb = false;// 是否只获取坐标
	private int frequency = 1000;// 扫描间隔

	/**
	 * 资源释放
	 */
	public void destroyResource() {
		if (mPoiSearch != null) {
			mPoiSearch.destroy();
		}
		if (mSearch != null) {
			mSearch.destroy();
		}
		if (mSuggestionSearch != null) {
			mSuggestionSearch.destroy();
		}
		if (mLocClient != null) {
			// 退出时销毁定位
			mLocClient.stop();
			// 关闭定位图层
			// mBaidumap.setMyLocationEnabled(false);
		}
	}

	/**
	 * 设置定位模式
	 * 
	 * @param mode
	 *            枚举（LocationMode.NORMAL等）跟随 普通 罗盘
	 */
	public void setLocationMode(LocationMode mode) {
		mCurrentMode = mode;
	}

	/**
	 * 自定义 定位的图表
	 * 
	 * @param id
	 *            图片的id
	 */
	public void setCurrentMarker(int id) {
		mCurrentMarker = BitmapDescriptorFactory.fromResource(id);
	}

	/**
	 * 设置卫星扫面间隔
	 * 
	 * @param frequency
	 *            默认1000毫秒 每隔1秒更新位置
	 */
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	/**
	 * 定位 设置 是否只获取 坐标
	 * 
	 * @param onlyGetzb
	 *            true为只获取坐标信息不显示在地图上 默认为false 显示
	 */
	public void getLocation(boolean onlyGetzb) {
		this.onlyGetzb = onlyGetzb;
		getLocation();
	}

	/**
	 * 定位 默认显示 定位信息
	 */
	public void getLocation() {
		// 设置 地图的缩放等级
		// MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(19.0f);
		// mBaidumap.setMapStatus(u);
		setZoom(17);
		// 设置 定位的图片 以及 定位模式
		mBaidumap.setMyLocationConfigeration(new MyLocationConfiguration(
				mCurrentMode, true, mCurrentMarker));

		// 开启定位图层
		mBaidumap.setMyLocationEnabled(true);
		// 定位初始化
		mLocClient = new LocationClient(context);
		mLocClient.registerLocationListener(myListener);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.setScanSpan(frequency);// 设置卫星扫面间隔
		mLocClient.setLocOption(option);
		mLocClient.start();
	}

	private boolean isFirst = true;
	private boolean changeAlways = false;

	/**
	 * 是否实时更新 位置
	 * 
	 * @param changeAlways
	 *            true为实时更新 默认false
	 */
	public void setChangeAlways(boolean changeAlways) {
		this.changeAlways = changeAlways;
	}

	/**
	 * 定位SDK监听函数
	 */
	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// 1 数据 经度 维度
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();

			// map view 销毁后不在处理新接收的位置
			if (location == null || mMapView == null)
				return;
			if (!onlyGetzb) {
				if (isFirst) {
					if (!changeAlways) {
						isFirst = false;
					}
					MyLocationData locData = new MyLocationData.Builder()
							.accuracy(location.getRadius())
							// 此处设置开发者获取到的方向信息，顺时针0-360
							.direction(100).latitude(location.getLatitude())
							.longitude(location.getLongitude()).build();
					mBaidumap.setMyLocationData(locData);

					LatLng ll = new LatLng(location.getLatitude(),
							location.getLongitude());
					MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
					mBaidumap.animateMapStatus(u);
					toast("定位成功");
				}
			}
			if (lsl != null) {
				lsl.getLocationSucced(location);
			}
		}

		public void onReceivePoi(BDLocation poiLocation) {
			toast("onReceivePoi");
		}

	}

	private OnLocationSearchListener lsl;

	public void setOnLocationSearchListener(OnLocationSearchListener lsl) {
		this.lsl = lsl;
	}

	public interface OnLocationSearchListener {
		/**
		 * 会被多次调用 有频率的
		 * 
		 * @param location
		 */
		public void getLocationSucced(BDLocation location);
	}

	// =======================================================================
	/**
	 * 设置 地图的缩放级别
	 * 
	 * @param zoom
	 */
	public void setZoom(float zoom) {
		// 设置 地图的缩放等级
		MapStatusUpdate u = MapStatusUpdateFactory.zoomTo(zoom);
		mBaidumap.setMapStatus(u);
	}

	/**
	 * 放大地图缩放级别
	 */
	public void zoomIn() {
		MapStatusUpdate u = MapStatusUpdateFactory.zoomIn();
		mBaidumap.setMapStatus(u);
	}

	/**
	 * 缩小地图缩放级别
	 */
	public void zoomOut() {
		MapStatusUpdate u = MapStatusUpdateFactory.zoomOut();
		mBaidumap.setMapStatus(u);
	}

	/**
	 * 设置地图中心
	 * 
	 * @param latLng
	 *            坐标(经度，维度)
	 */
	public void setCenter(LatLng latLng) {
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
		mBaidumap.setMapStatus(u);
	}

	// static MapStatusUpdate newLatLngZoom(LatLng latLng, float zoom)
	/**
	 * 设置地图中心点以及缩放级别
	 * 
	 * @param latLng
	 *            中心
	 * @param zoom
	 *            缩放级别
	 */
	public void setCenterAndZoom(LatLng latLng, float zoom) {
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLngZoom(latLng, zoom);
		mBaidumap.setMapStatus(u);
	}

	/**
	 * 是否显示缩放控件
	 * 
	 * @param show
	 */
	public void setZoomShow(boolean show) {
		mMapView.showZoomControls(show);
	}

	/**
	 * 是否显示 比例尺控件
	 * 
	 * @param show
	 */
	public void setScaleControlShow(boolean show) {
		mMapView.showScaleControl(show);
	}

	public boolean toast = true;// 是否打印吐司 默认true 打印

	public void toast(String message) {
		if (toast) {
			Toast.makeText(context, message, 0).show();
		}
	}

}
