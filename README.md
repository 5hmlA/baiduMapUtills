# baiduMapUtills
百度地图V3.3.3 



1，引用baidulibrary （library的清单文件中的权限 和assets资源文件 无法被项目引用 布局可以引用）

2，根据工程名称和开发者电脑的安全码 Android SDK安全码组成：数字签名+;+包名

申请百度map的key		
	申请地址：http://lbsyun.baidu.com/apiconsole/key
	
	在工程的清单文件中配置key信息
	 <meta-data
         android:name="com.baidu.lbsapi.API_KEY"
         android:value="百度地图key" />
3，权限配置

	<!-- 这个权限用于进行网络定位-->
	<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"></uses-permission>
	<!-- 这个权限用于访问GPS定位-->
	<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION"></uses-permission>
	<!-- 用于访问wifi网络信息，wifi信息会用于进行网络定位-->
	<uses-permission android:name="android.permission.ACCESS_WIFI_STATE"></uses-permission>
	<!-- 获取运营商信息，用于支持提供运营商信息相关的接口-->
	<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"></uses-permission>
	<!-- 这个权限用于获取wifi的获取权限，wifi信息会用来进行网络定位-->
	<uses-permission android:name="android.permission.CHANGE_WIFI_STATE"></uses-permission>
	<!-- 用于读取手机当前的状态-->
	<uses-permission android:name="android.permission.READ_PHONE_STATE"></uses-permission>
	<!-- 写入扩展存储，向扩展卡写入数据，用于写入离线定位数据-->
	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"></uses-permission>
	<!-- 访问网络，网络定位需要上网-->
	<uses-permission android:name="android.permission.INTERNET" />
	<uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS"></uses-permission>
	<!--允许应用读取低级别的系统日志文件 -->
	<uses-permission android:name="android.permission.READ_LOGS"></uses-permission>

##baidulibrary的使用文档

开发包 v3.3.0

	百度地图工具类
	setZoomShow(boolean show)是否显示缩放控件 
	setScaleControlShow(boolean show)是否显示比例尺控件
	public boolean toast = true;// 是否打印吐司 默认true 打印
	zoomOut() 缩小地图缩放级别
	zoomIn() 放大地图缩放级别
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
	PrePageInCity全城搜索上一页
	searchNearBy(LatLng position, String searchkey, int radius) 中心半径搜索
	NextPageNearBy()半径搜索下一页
	PrePageNearBy()半径城搜索上一页
	setOnPoiSearchListener(OnPoiSearchListener psl)兴趣点检索监听
	
	百度定位
	getLocation() 定位 默认显示 位置信息
	getLocation(boolean onlyGetzb) 定位 true为只获取坐标信息不显示在地图上 默认为false
	setChangeAlways(boolean changeAlways)是否实时更新位置 true为实时更新 默认false
	setCurrentMarker(int id)设置定位的显示图片
	setLocationMode(LocationMode mode)设置定位模式
	setFrequency(int frequency) 设置卫星扫面间隔默认1000毫秒 每隔1秒更新位置
	stopLocation()  取消定位  destory的时候 调用
	setOnLocationSearchListener(OnLocationSearchListener lsl)定位监听


