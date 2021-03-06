package com.tonggou.andclient;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.Symbol;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.tonggou.andclient.DrivingJournalCalendarLayout.DJCalendarListener;
import com.tonggou.andclient.myview.AbsViewHolderAdapter;
import com.tonggou.andclient.util.DJDatabase;
import com.tonggou.andclient.util.ScreenSizeUtil;
import com.tonggou.andclient.util.SomeUtil;
import com.tonggou.andclient.vo.DrivingJournalItem;

public class MyVehicleConditionActivity extends BDMapBaseActivity implements DJCalendarListener {
	public static final String TAG = "MyVehicleConditionActivity";
	private Resources mResources;
	private SharedPreferences mPreferences;
	private DJDatabase mDJDatabase;
	private MapView mMapView;
	private int mCcMapHeight, mDjMapHeight;
	private float mCcScaleRate, mDjScaleRate;
	private LocationClient mLocationClient;
	private MyLocationOverlay mMyLocationOverlay;
	private MKSearch mMKSearch;
	private Symbol mLineSymbol;
	private DrivingJournalPullLayout mDjPullLayout;
	private DrivingJournalCalendarLayout mDjCalendarLayout;
	private HorizontalScrollView mCcHSDatas;
	private ListView mDjListView;
	private LinearLayout mLlClip, mDjLlArrow, mLlOilPrice;
	private EditText mEtPrice;
	private ProgressBar mPbClip;
	private ImageView[] mDjIvCalendars;
	private TextView[] mTxtSubtitles;
	private TextView mTopTxtOilPrice, mCcTxtOilWear, mCcTxtOilLeft, mCcTxtTemperature, mCcTxtDistance,
			mCcTxtTime, mDjTxtDistance, mDjTxtTime, mDjTxtOilWear, mDjTxtOilCost;

	private DjitemUpdateTask mDjitemUpdateTask;
	private OnRoadQueryTask mOnRoadQueryTask;
	private AddressUpdateTask mAddrUpdateTask;
	private ArrayList<DrivingJournalItem> mAddrUpdateList;
	private DJItemAdapter mDJItemAdapter;
	private LocationData mCurrLocData;
	private String mDefVehiNo, mDefOilPrice;
	private PopupWindow mPopupOilPrice;
	private Dialog mDialogEnter;
	private int mSelectedIndex, mUpdateIndex, mStartAddrCount, mEndAddrCount;
	private int mCurrType = TYPE_CC;
	private static final double MIN_OIL_PRICE = 5d;
	protected static final long CLIP_TIME = 150;
	private static final int TYPE_CC = 0;
	private static final int TYPE_DJ = 1;

	private int[] imgCalendarSelected = { R.drawable.icon_calendar_day_selected,
			R.drawable.icon_calendar_week_selected, R.drawable.icon_calendar_month_selected };
	private int[] imgCalendarNormal = { R.drawable.icon_calendar_day_normal,
			R.drawable.icon_calendar_week_normal, R.drawable.icon_calendar_month_normal };

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		initBMap(mMKGeneralListener);
		setContentView(R.layout.my_vehicle_condition);
		initCommon();
		initAllViews();
		initBMapParams();
		registerReceiver();
		dependToShowDialog();
	}

	private void initCommon() {
		mDJDatabase = DJDatabase.getInstance(this);
		mPreferences = getSharedPreferences(BaseActivity.SETTING_INFOS, Context.MODE_PRIVATE);
		mDefVehiNo = mPreferences.getString(BaseActivity.VEHICLENUM, "");
		mDefOilPrice = mPreferences.getString(BaseActivity.EXTRA_DEFAULT_OIL_PRICE, "0.0");
	}

	private void initBMapParams() {
		initMapController(mMapView);
		initMyLocationOverlay();
		initLineSymbol();
		initMKSearch();
		initLocationClient();
		mMapView.setBuiltInZoomControls(false);
		mMapView.post(new Runnable() {
			@Override
			public void run() {
				ScreenSizeUtil screenUtil = new ScreenSizeUtil(getApplicationContext());
				int screenHeight = screenUtil.getScreenHeight();
				mCcMapHeight = mMapView.getHeight();
				mDjMapHeight = (int) mResources.getDimension(R.dimen.vehicle_condition_mapview_height);
				mCcScaleRate = ((float) mCcMapHeight) / ((float) screenHeight);
				mDjScaleRate = ((float) mDjMapHeight) / ((float) screenHeight);
			}
		});
	}

	private void initAllViews() {
		mResources = getResources();
		initTopViews();
		initMyViews();
		initDrivingJournalViews();
		initCurrConditionViews();
	}

	private void initMyViews() {
		mMapView = (MapView) findViewById(R.id.mv_vehicle_condition);
		mLlClip = (LinearLayout) findViewById(R.id.ll_vehicle_condition_clip);
		mPbClip = (ProgressBar) findViewById(R.id.pb_vehicle_condition_clip);
	}

	private void registerReceiver() {
		IntentFilter filter = new IntentFilter(DrivingJournalService.ACTION_DRAW_ROUTE_ON_MAP);
		filter.addAction(DrivingJournalService.ACTION_UPDATE_VEHICLE_CONDITION);
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mBroadcastReceiver, filter);
	}

	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (DrivingJournalService.ACTION_DRAW_ROUTE_ON_MAP.equals(action)) {
				if (mCurrType == TYPE_CC
						&& (mOnRoadQueryTask == null || AsyncTask.Status.FINISHED.equals(mOnRoadQueryTask
								.getStatus()))) {
					mOnRoadQueryTask = new OnRoadQueryTask();
					mOnRoadQueryTask.execute();
				}
			} else if (DrivingJournalService.ACTION_UPDATE_VEHICLE_CONDITION.equals(action)) {
				ccUpdate(intent);
			} else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
				ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
				if (activeNetworkInfo != null && activeNetworkInfo.isAvailable()) {
					NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
					NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
					if (mobileInfo != null && mobileInfo.isConnected()) {
						Log.d(TAG, "连接到移动网络");
					} else if (wifiInfo != null && wifiInfo.isConnected()) {
						Log.d(TAG, "连接到wifi");
						if (mAddrUpdateTask == null
								|| AsyncTask.Status.FINISHED.equals(mAddrUpdateTask.getStatus())) {
							mAddrUpdateTask = new AddressUpdateTask();
							mAddrUpdateTask.execute();
						}
					}
				} else {
					Log.d(TAG, "断开网络");
				}
			}
		}
	};

	private void ccUpdate(Intent intent) {
		String oilLeft = intent.getStringExtra(DrivingJournalService.EXTRA_CURR_OIL_LEFT);
		mCcTxtOilLeft.setText(TextUtils.isEmpty(oilLeft) ? "--" : oilLeft + "%");
		String temperature = intent.getStringExtra(DrivingJournalService.EXTRA_CURR_TANK_TEMPERATURE);
		mCcTxtTemperature.setText(TextUtils.isEmpty(temperature) ? "--" : temperature);
		mCcTxtOilWear.setText(intent.getDoubleExtra(DrivingJournalService.EXTRA_CURR_OIL_WEAR, 0d) + "l");
		mCcTxtDistance.setText(intent.getLongExtra(DrivingJournalService.EXTRA_CURR_ON_ROAD_TIME, 0)
				+ "km");
		mCcTxtTime.setText(SomeUtil.fmtMilliseconds(intent.getLongExtra(
				DrivingJournalService.EXTRA_CURR_ON_ROAD_TIME, 0l)));
	}

	private class AddressUpdateTask extends AsyncTask<Void, Void, Void> {
		@Override
		protected Void doInBackground(Void... params) {
			updateDJAddress();
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			if (mAddrUpdateList != null && mAddrUpdateList.size() > 0) {
				reverseAddr(mAddrUpdateList.get(mUpdateIndex));
			}
		}

		private void updateDJAddress() {
			ArrayList<DrivingJournalItem> djItems = mDJDatabase.queryDJItems();
			if (djItems != null) {
				mAddrUpdateList = new ArrayList<DrivingJournalItem>();
				mUpdateIndex = mStartAddrCount = mEndAddrCount = 0;
				ArrayList<DrivingJournalItem> startList = new ArrayList<DrivingJournalItem>();
				ArrayList<DrivingJournalItem> endList = new ArrayList<DrivingJournalItem>();
				for (DrivingJournalItem djItem : djItems) {
					if (djItem.getStartPlace() == null) {
						startList.add(djItem);
					}
					if (djItem.getEndPlace() == null) {
						endList.add(djItem);
					}
				}
				mStartAddrCount = startList.size();
				mEndAddrCount = endList.size();
				if (mStartAddrCount > 0) {
					mAddrUpdateList.addAll(startList);
				}
				if (mEndAddrCount > 0) {
					mAddrUpdateList.addAll(endList);
				}
			}
		}
	}

	private void reverseAddr(DrivingJournalItem djItem) {
		GeoPoint geoPoint;
		if (mUpdateIndex < mStartAddrCount) {
			geoPoint = SomeUtil.toGeoPointE6(djItem.getStartLat(), djItem.getStartLon());
		} else {
			geoPoint = SomeUtil.toGeoPointE6(djItem.getEndLat(), djItem.getEndLon());
		}
		mMKSearch.reverseGeocode(geoPoint);
	}

	private void showOilPriceWindow() {
		if (mPopupOilPrice == null) {
			View contentView = View.inflate(this, R.layout.popup_update_oil_price, null);
			mPopupOilPrice = new PopupWindow(contentView, WindowManager.LayoutParams.WRAP_CONTENT,
					WindowManager.LayoutParams.WRAP_CONTENT);
			mPopupOilPrice.setFocusable(true);
			mPopupOilPrice.setBackgroundDrawable(new BitmapDrawable());
			mPopupOilPrice.setOutsideTouchable(true);

			mEtPrice = (EditText) contentView.findViewById(R.id.et_popup_oil_price);
			mEtPrice.setText(mDefOilPrice);
			mEtPrice.requestFocus();
			mEtPrice.post(new Runnable() {
				@Override
				public void run() {
					mEtPrice.setWidth(mEtPrice.getWidth() + SomeUtil.Dp2Px(getApplicationContext(), 8));
					mEtPrice.setSelection(mEtPrice.getText().toString().length());
				}
			});

			mPopupOilPrice.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss() {
					double oilPrice = Double.parseDouble(mEtPrice.getText().toString());
					if (oilPrice > MIN_OIL_PRICE) {
						String strPrice = String.valueOf(oilPrice);
						mPreferences.edit().putString(BaseActivity.EXTRA_DEFAULT_OIL_PRICE, strPrice)
								.commit();
						mTopTxtOilPrice.setText(String.format(
								mResources.getString(R.string.title_myVehicleCondition_oil_price), strPrice));
					}
				}
			});
		}
		// 如果开启输入法，行车日志的记录很多时，界面显示会有BUG，所以只在当前车况显示
		mEtPrice.post(new Runnable() {
			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) getApplicationContext().getSystemService(
						Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			}
		});
		mPopupOilPrice.showAsDropDown(mLlOilPrice);
	}

	private OnClickListener mOnClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.rl_myVehicleCondition_back:
				onBackPressed();
				break;
			case R.id.ll_myVehicleCondition_oil_price:
				showOilPriceWindow();
				break;
			case R.id.iv_curr_condtion_dtc:
				startActivity(new Intent(MyVehicleConditionActivity.this, FaultCodeCenterActivity.class));
				break;
			case R.id.ll_myVehicleCondition_curr_condtion:
				setSubtitle(0);
				changePage(0);
				break;
			case R.id.ll_myVehicleCondition_driving_journal:
				setSubtitle(1);
				changePage(1);
				break;
			case R.id.imgView_driving_journal_calendar_day:
				setCalendarType(0);
				mDjCalendarLayout.updateDatas(SomeUtil.TYPE_DAY);
				djUpdate();
				break;
			case R.id.imgView_driving_journal_calendar_week:
				setCalendarType(1);
				mDjCalendarLayout.updateDatas(SomeUtil.TYPE_WEEK);
				djUpdate();
				break;
			case R.id.imgView_driving_journal_calendar_month:
				setCalendarType(2);
				mDjCalendarLayout.updateDatas(SomeUtil.TYPE_MONTH);
				djUpdate();
				break;
			}
		}
	};

	private void changePage(final int index) {
		switch (index) {
		case 0:
			mCurrType = TYPE_CC;
			setMainVisible(View.VISIBLE, View.GONE);
			setMapParams(0);
			ccDependToShow();
			break;
		case 1:
			mCurrType = TYPE_DJ;
			setMainVisible(View.GONE, View.VISIBLE);
			djUpdate();
			setMapParams(1);
			break;
		}
	}

	private void setMainVisible(int v1, int v2) {
		mCcHSDatas.setVisibility(v1);
		mDjPullLayout.setVisibility(v2);
		mDjListView.setVisibility(v2);
		mLlOilPrice.setVisibility(v1);
	}

	private void setMapParams(final int index) {
		if (index == 1) {
			mPbClip.setVisibility(View.VISIBLE);
		} else {
			mPbClip.setVisibility(View.GONE);
		}
		mLlClip.setVisibility(View.VISIBLE);

		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				SystemClock.sleep(CLIP_TIME);
				return null;
			}

			protected void onPostExecute(Void result) {
				mLlClip.setVisibility(View.GONE);
			};
		}.execute();

		mMapView.post(new Runnable() {
			@Override
			public void run() {
				if (index == 0) {
					LayoutParams layoutParams = (LayoutParams) mMapView.getLayoutParams();
					layoutParams.height = mCcMapHeight;
					layoutParams.setMargins(0, 0, 0, 0);
					mMapView.setLayoutParams(layoutParams);
					mMapView.showScaleControl(true);
					mMapView.refresh();
				} else {
					LayoutParams layoutParams = (LayoutParams) mMapView.getLayoutParams();
					layoutParams.height = mDjMapHeight;
					layoutParams.setMargins(0, mDjLlArrow.getHeight(), 0, 0);
					mMapView.setLayoutParams(layoutParams);
					mMapView.showScaleControl(false);
					mMapView.refresh();
				}
			}
		});
	}

	private void djUpdate() {
		if (mDjitemUpdateTask == null || AsyncTask.Status.FINISHED.equals(mDjitemUpdateTask.getStatus())) {
			mDjitemUpdateTask = new DjitemUpdateTask();
			mDjitemUpdateTask.execute();
		}
	}

	private class DjitemUpdateTask extends AsyncTask<Void, Void, ArrayList<DrivingJournalItem>> {
		@Override
		protected ArrayList<DrivingJournalItem> doInBackground(Void... params) {
			ArrayList<DrivingJournalItem> djItems = mDJDatabase.queryDJItems(
					mDjCalendarLayout.getSelectedType(), mDjCalendarLayout.getSelectedData());
			return djItems;
		}

		@Override
		protected void onPostExecute(ArrayList<DrivingJournalItem> result) {
			if (result != null) {
				mDJItemAdapter.update(result);
				calculateDjDatas(result);
			}
		}
	}

	private void calculateDjDatas(ArrayList<DrivingJournalItem> djItems) {
		double sumDistance = 0, sumOilWear = 0, sumOilCost = 0;
		long sumTravelTime = 0;
		for (DrivingJournalItem djItem : djItems) {
			sumDistance += djItem.getDistance();
			sumOilWear += djItem.getOilWear();
			sumOilCost += djItem.getTotalOilMoney();
			sumTravelTime += djItem.getTravelTime();
		}
		mDjTxtDistance.setText(sumDistance + "km");
		mDjTxtOilWear.setText(sumOilWear + "L");
		mDjTxtOilCost.setText("￥" + sumOilCost);
		mDjTxtTime.setText(SomeUtil.fmtMilliseconds(sumTravelTime));
	}

	private void setSubtitle(int postion) {
		mTxtSubtitles[postion].setTextColor(mResources.getColor(android.R.color.white));
		mTxtSubtitles[(postion + 1) % mTxtSubtitles.length].setTextColor(mResources
				.getColor(android.R.color.darker_gray));
	}

	private void setCalendarType(int postion) {
		for (int i = 0; i < mDjIvCalendars.length; i++) {
			if (postion == i) {
				mDjIvCalendars[i].setImageResource(imgCalendarSelected[i]);
			} else {
				mDjIvCalendars[i].setImageResource(imgCalendarNormal[i]);
			}
		}
	}

	private void initMyLocationOverlay() {
		mMyLocationOverlay = new MyLocationOverlay(mMapView);
		mMyLocationOverlay.enableCompass();
	}

	private void initLineSymbol() {
		mLineSymbol = new Symbol();
		Symbol.Color lineColor = mLineSymbol.new Color();
		lineColor.red = 38;
		lineColor.green = 131;
		lineColor.blue = 239;
		lineColor.alpha = 200;
		mLineSymbol.setLineSymbol(lineColor, 7);
	}

	private void initMKSearch() {
		mMKSearch = new MKSearch();
		mMKSearch.init(mBMapManager, new AddrSearchListener());
	}

	private void initLocationClient() {
		mLocationClient = new LocationClient(getApplicationContext());
		mLocationClient.registerLocationListener(mLocationListener);
		LocationClientOption clientOption = new LocationClientOption();
		clientOption.setOpenGps(true);
		clientOption.setAddrType("all");
		clientOption.setCoorType(BD09LL);
		clientOption.disableCache(true);
		clientOption.setPriority(LocationClientOption.NetWorkFirst);
		mLocationClient.setLocOption(clientOption);
		mLocationClient.start();
	}

	private BDLocationListener mLocationListener = new BDLocationListener() {
		@Override
		public void onReceiveLocation(BDLocation location) {
			int type = location.getLocType();
			if (location != null && (type == SUCCESS_FROM_GPS || type == SUCCESS_FROM_NETWORK)) {
				mCurrLocData = getLocData(location);
				ccDependToShow();
			}
		}

		@Override
		public void onReceivePoi(BDLocation location) {
		}
	};

	private void ccDependToShow() {
		if (mCurrLocData != null) {
			if (mOnRoadQueryTask == null || AsyncTask.Status.FINISHED.equals(mOnRoadQueryTask.getStatus())) {
				mOnRoadQueryTask = new OnRoadQueryTask();
				mOnRoadQueryTask.execute(mCurrLocData);
			} else {
				drawMylocation(mCurrLocData);
			}
		}
	}

	private class OnRoadQueryTask extends AsyncTask<LocationData, Void, GeoPoint[]> {
		private LocationData[] mParams;

		@Override
		protected GeoPoint[] doInBackground(LocationData... params) {
			mParams = params;
			return mDJDatabase.querySPArray();
		}

		@Override
		protected void onPostExecute(GeoPoint[] result) {
			if (result == null && mParams != null && mParams.length > 0) {
				drawMylocation(mParams[0]);
			} else {
				if (!SomeUtil.isServiceRunning(getApplicationContext(), DrivingJournalService.class)) {
					// TODO:如果正在行驶途中强杀掉Service，下次再次进入时应该开启Service应继续上次未完成的；这里先简单处理，删除上次的记录
					mDJDatabase.clearSamplePoints();
				}
				drawRoute(result, mCcScaleRate);
			}
		}
	}

	private synchronized void drawRoute(final GeoPoint[] samplePoints, final float scaleRate) {
		if (mMapView != null && mMapView.getOverlays() != null && samplePoints.length > 1) {
			mMapView.getOverlays().clear();
			GraphicsOverlay graphicsOverlay = new GraphicsOverlay(mMapView);
			mMapView.getOverlays().add(graphicsOverlay);
			Geometry lineGeometry = new Geometry();
			lineGeometry.setPolyLine(samplePoints);
			Graphic lineGraphic = new Graphic(lineGeometry, mLineSymbol);
			graphicsOverlay.setData(lineGraphic);

			if (mCurrType == TYPE_CC) {
				addPointOverlays(R.drawable.icon_cc_start, R.drawable.icon_cc_on_road, samplePoints);
			} else {
				addPointOverlays(R.drawable.icon_dj_start, R.drawable.icon_dj_end, samplePoints);
			}
			mMapView.refresh();

			final int[] spanParams = SomeUtil.getSpanParams(samplePoints);
			mMapView.post(new Runnable() {
				@Override
				public void run() {
					int maxLat = spanParams[SomeUtil.MAX_LAT];
					int minLat = spanParams[SomeUtil.MIN_LAT];
					int maxLon = spanParams[SomeUtil.MAX_LON];
					int minLon = spanParams[SomeUtil.MIN_LON];
					mMapController.setCenter(new GeoPoint((maxLat + minLat) / 2, (maxLon + minLon) / 2));
					mMapController.zoomToSpan((int) ((maxLat - minLat) * scaleRate),
							(int) ((maxLon - minLon) * scaleRate));
					mMapView.refresh();
				}
			});
		}
	}

	private void addPointOverlays(int startResId, int endResId, GeoPoint[] samplePoints) {
		PointOverlay startOverlay = getPointOverlay(startResId);
		PointOverlay endOverlay = getPointOverlay(endResId);
		OverlayItem startItem = new OverlayItem(samplePoints[0], "start", "start");
		OverlayItem endtItem = new OverlayItem(samplePoints[samplePoints.length - 1], "end", "end");
		startOverlay.addItem(startItem);
		endOverlay.addItem(endtItem);
		mMapView.getOverlays().add(startOverlay);
		mMapView.getOverlays().add(endOverlay);
	}

	private PointOverlay getPointOverlay(int resId) {
		return new PointOverlay(mResources.getDrawable(resId), mMapView);
	}

	private class PointOverlay extends ItemizedOverlay<OverlayItem> {
		public PointOverlay(Drawable mark, MapView mapView) {
			super(mark, mapView);
		}
	}

	private void drawMylocation(LocationData locData) {
		mMyLocationOverlay.setData(locData);
		mMapView.getOverlays().clear();
		mMapView.getOverlays().add(mMyLocationOverlay);
		mMapView.refresh();
		mMapController.setZoom(ZOOM_LEVEL);
		GeoPoint geoPoint = SomeUtil.toGeoPointE6(locData.latitude, locData.longitude);
		mMapController.animateTo(geoPoint);
	}

	private void dependToShowDialog() {
		mMapView.postDelayed(new Runnable() {
			@Override
			public void run() {
				if (TextUtils.isEmpty(mDefVehiNo)) {
					startToShowDialog(0);
					return;
				} else if (!SomeUtil.isDefVehiOBDBinded()) {
					startToShowDialog(1);
					return;
				}
			}
		}, 1000);
	}

	private void startToShowDialog(int type) {
		mDialogEnter = new Dialog(this, R.style.Dialog_style_dim2);
		View view = View.inflate(this, R.layout.dialog_my_vehicle_condition, null);
		TextView txt1 = (TextView) view.findViewById(R.id.txt_dialog_my_vehicle_condition_heading);
		TextView txt2 = (TextView) view.findViewById(R.id.txt_dialog_my_vehicle_condition_subheading);
		ImageView ivYes = (ImageView) view.findViewById(R.id.iv_dialog_my_vehicle_condition_yes);
		ImageView ivNo = (ImageView) view.findViewById(R.id.iv_dialog_my_vehicle_condition_no);

		if (type == 0) {
			txt1.setText(mResources.getString(R.string.myVehicleCondition_heading_no_vehicle));
			txt2.setText(mResources.getString(R.string.myVehicleCondition_subheading_no_vehicle));
			ivYes.setOnClickListener(new DialogOnClickListener(0));
			ivNo.setOnClickListener(new DialogOnClickListener(0));
		} else if (type == 1) {
			txt1.setText(mResources.getString(R.string.myVehicleCondition_heading_no_obd));
			txt2.setText(mResources.getString(R.string.myVehicleCondition_subheading_no_obd));
			ivYes.setOnClickListener(new DialogOnClickListener(1));
			ivNo.setOnClickListener(new DialogOnClickListener(1));
		}

		mDialogEnter.setContentView(view);
		mDialogEnter.setCanceledOnTouchOutside(true);
		// 当前方法是进入Activity启动1秒后执行，如果不加下面判断，做如下操作：秒进秒退,会BadTokenException
		if (SomeUtil.isActivityRunning(getApplicationContext(), MyVehicleConditionActivity.class)) {
			mDialogEnter.show();
		}
	}

	private class DialogOnClickListener implements OnClickListener {
		private int mType = -1;

		private DialogOnClickListener(int type) {
			mType = type;
		}

		@Override
		public void onClick(View view) {
			switch (view.getId()) {
			case R.id.iv_dialog_my_vehicle_condition_yes:
				if (mType == 0) {
					startActivity(new Intent(getApplicationContext(), AddBindCarActivity.class));
				} else if (mType == 1) {
					startActivity(new Intent(getApplicationContext(), ConnectOBDDialogActivity.class));
				}
				mDialogEnter.cancel();
				finish();
				break;
			case R.id.iv_dialog_my_vehicle_condition_no:
				mDialogEnter.cancel();
				break;
			}
		}
	};

	private class DJItemAdapter extends AbsViewHolderAdapter<DrivingJournalItem> {

		public DJItemAdapter(Context context, List<DrivingJournalItem> data, int layoutRes) {
			super(context, data, layoutRes);
		}

		@Override
		protected void setData(int pos, View convertView, final DrivingJournalItem itemData) {
			String startDate = getFmtDate(itemData.getStartTime());
			String endDate = getFmtDate(itemData.getEndTime());
			setTxtContent(convertView, R.id.txt_driving_journal_item_startTime, getTime(startDate));
			setTxtContent(convertView, R.id.txt_driving_journal_item_startDay, SomeUtil.getDay(startDate));
			setTxtContent(convertView, R.id.txt_driving_journal_item_startMonth, SomeUtil.getMonth(startDate));
			setTxtContent(convertView, R.id.txt_driving_journal_item_endTime, getTime(endDate));
			setTxtContent(convertView, R.id.txt_driving_journal_item_endDay, SomeUtil.getDay(endDate));
			setTxtContent(convertView, R.id.txt_driving_journal_item_endMonth, SomeUtil.getMonth(endDate));
			setTxtContent(convertView, R.id.txt_driving_journal_item_startAddr,
					getAddr(itemData.getStartPlace()));
			setTxtContent(convertView, R.id.txt_driving_journal_item_endAddr, getAddr(itemData.getEndPlace()));
			setTxtContent(convertView, R.id.txt_driving_journal_item_distance, itemData.getDistance() + "km");
			setTxtContent(convertView, R.id.txt_driving_journal_item_cost, itemData.getTotalOilMoney() + "");
			ImageView ivSpan = getViewFromHolder(convertView, R.id.iv_driving_journal_item_span);
			ImageView ivEdit = getViewFromHolder(convertView, R.id.iv_driving_journal_item_edit);
			ivEdit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getApplicationContext(), VehicleConditionAdjustActivity.class);
					intent.putExtra(VehicleConditionAdjustActivity.EXTRA_DJITEM, itemData);
					startActivity(intent);
				}
			});
			if (pos == mSelectedIndex) {
				ivSpan.setImageResource(R.drawable.icon_driving_journal_item_route_light);
				showJournal(itemData.getPlaceNotes());
			} else {
				ivSpan.setImageResource(R.drawable.icon_driving_journal_item_route_dim);
			}
		}

		private String getAddr(String srcAddr) {
			return TextUtils.isEmpty(srcAddr) ? mResources.getString(R.string.myVehicleCondition_no_address)
					: srcAddr;
		}

		private String getFmtDate(long time) {
			return new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date(time));
		}

		private String getTime(String strFmt) {
			return strFmt.substring(strFmt.lastIndexOf(" ") + 1);
		}

		private void setTxtContent(View convertView, int resId, String content) {
			TextView textView = getViewFromHolder(convertView, resId);
			textView.setText(content);
		}

		private void showJournal(String strSamplePoints) {
			GeoPoint[] result = SomeUtil.getSamplePointArray(strSamplePoints);
			if (result != null) {
				drawRoute(result, mDjScaleRate);
			}
		}
	}

	private void initTopViews() {
		RelativeLayout rlBack = (RelativeLayout) findViewById(R.id.rl_myVehicleCondition_back);
		LinearLayout llCurrCondition = (LinearLayout) findViewById(R.id.ll_myVehicleCondition_curr_condtion);
		LinearLayout llDrivingJournal = (LinearLayout) findViewById(R.id.ll_myVehicleCondition_driving_journal);
		mLlOilPrice = (LinearLayout) findViewById(R.id.ll_myVehicleCondition_oil_price);
		mTopTxtOilPrice = (TextView) findViewById(R.id.txt_myVehicleCondition_oil_price);

		mTxtSubtitles = new TextView[2];
		mTxtSubtitles[0] = (TextView) findViewById(R.id.txt_myVehicleCondition_curr_condtion);
		mTxtSubtitles[1] = (TextView) findViewById(R.id.txt_myVehicleCondition_driving_journal);

		rlBack.setOnClickListener(mOnClickListener);
		mLlOilPrice.setOnClickListener(mOnClickListener);
		llCurrCondition.setOnClickListener(mOnClickListener);
		llDrivingJournal.setOnClickListener(mOnClickListener);

		mTopTxtOilPrice.setText(String.format(
				mResources.getString(R.string.title_myVehicleCondition_oil_price), mDefOilPrice));
	}

	private void initCurrConditionViews() {
		mCcHSDatas = (HorizontalScrollView) findViewById(R.id.ll_curr_condtion_datas);
		mCcTxtOilLeft = (TextView) findViewById(R.id.txt_curr_condition_oil_left);
		mCcTxtTemperature = (TextView) findViewById(R.id.txt_curr_condition_tank_temperature);
		mCcTxtOilWear = (TextView) findViewById(R.id.txt_curr_condition_oil_wear);
		mCcTxtDistance = (TextView) findViewById(R.id.txt_curr_condition_distance);
		mCcTxtTime = (TextView) findViewById(R.id.txt_curr_condition_time);
		ImageView ccIvDTC = (ImageView) findViewById(R.id.iv_curr_condtion_dtc);
		ccIvDTC.setOnClickListener(mOnClickListener);

		initCCDatas();
	}

	private void initCCDatas() {
		String oilLeft = mPreferences.getString(BaseActivity.LAST_CC_OIL_LEFT, null);
		mCcTxtOilLeft.setText(TextUtils.isEmpty(oilLeft) ? "--" : oilLeft);
		String temperature = mPreferences.getString(BaseActivity.LAST_CC_TANK_TEMPERATURE, null);
		mCcTxtOilLeft.setText(TextUtils.isEmpty(temperature) ? "--" : temperature);
		float oilWear = mPreferences.getFloat(BaseActivity.LAST_CC_OIL_WEAR, 0f);
		mCcTxtOilWear.setText(oilWear + "L");
		float onRoadDistance = mPreferences.getFloat(BaseActivity.LAST_CC_ON_ROAD_DISTANCE, 0f);
		mCcTxtDistance.setText(onRoadDistance + "km");
		long onRoadTime = mPreferences.getLong(BaseActivity.LAST_CC_ON_ROAD_TIME, 0l);
		mCcTxtTime.setText(SomeUtil.fmtMilliseconds(onRoadTime));
	}

	private void initDrivingJournalViews() {
		mDjListView = (ListView) findViewById(R.id.lv_driving_journal);
		mDJItemAdapter = new DJItemAdapter(this, new ArrayList<DrivingJournalItem>(),
				R.layout.driving_journal_item);
		mDjListView.setAdapter(mDJItemAdapter);
		mDjListView.setOnItemClickListener(mDJItemClickListener);

		mDjCalendarLayout = (DrivingJournalCalendarLayout) findViewById(R.id.djcl_driving_journal_calendar);
		mDjCalendarLayout.setDJCalendarListener(this);

		mDjPullLayout = (DrivingJournalPullLayout) findViewById(R.id.djpl_journal_pull_layout);
		mDjLlArrow = (LinearLayout) findViewById(R.id.ll_driving_journal_arrow);
		mDjTxtDistance = (TextView) findViewById(R.id.txt_driving_distance);
		mDjTxtTime = (TextView) findViewById(R.id.txt_driving_time);
		mDjTxtOilWear = (TextView) findViewById(R.id.txt_driving_oil_wear);
		mDjTxtOilCost = (TextView) findViewById(R.id.txt_driving_oil_cost);

		mDjIvCalendars = new ImageView[3];
		mDjIvCalendars[0] = (ImageView) findViewById(R.id.imgView_driving_journal_calendar_day);
		mDjIvCalendars[1] = (ImageView) findViewById(R.id.imgView_driving_journal_calendar_week);
		mDjIvCalendars[2] = (ImageView) findViewById(R.id.imgView_driving_journal_calendar_month);

		for (ImageView imageView : mDjIvCalendars) {
			imageView.setOnClickListener(mOnClickListener);
		}
	}

	private OnItemClickListener mDJItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			mSelectedIndex = position;
			mDJItemAdapter.notifyDataSetChanged();
			dependToCollapse(position);
		}

		private void dependToCollapse(int pos) {
			if (mDjPullLayout.getStatus() != DrivingJournalPullLayout.PullLayoutStatus.ALL_COLLAPSED) {
				mDjPullLayout.collapseAll();
			}
		}
	};

	private class AddrSearchListener implements MKSearchListener {
		@Override
		public void onGetAddrResult(MKAddrInfo mkAddrInfo, int error) {
			if (error != 0)
				return;
			String strGeoPoint = SomeUtil.getStrGeoPoint(mkAddrInfo.geoPt);
			String strAddr = mkAddrInfo.strAddr;
			if (strAddr == null || strGeoPoint == null) {
				return;
			}
			DrivingJournalItem djItem = mAddrUpdateList.get(mUpdateIndex);
			if (mUpdateIndex < mStartAddrCount) {
				mDJDatabase.updateAdrr(djItem.getAppDriveLogId(), SomeUtil.TYPE_SA, strAddr);
			} else {
				mDJDatabase.updateAdrr(djItem.getAppDriveLogId(), SomeUtil.TYPE_EA, strAddr);
			}
			if (++mUpdateIndex < mAddrUpdateList.size()) {
				reverseAddr(mAddrUpdateList.get(mUpdateIndex));
			}
			if (mUpdateIndex == mAddrUpdateList.size()) {
				djUpdate();
			}
		}

		@Override
		public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
		}

		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
		}

		@Override
		public void onGetPoiDetailSearchResult(int arg0, int arg1) {
		}

		@Override
		public void onGetPoiResult(MKPoiResult arg0, int arg1, int arg2) {
		}

		@Override
		public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1, int arg2) {
		}

		@Override
		public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
		}

		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
		}

		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
		}
	}

	@Override
	protected void onResume() {
		if (mMapView != null) {
			mMapView.onResume();
		}
		super.onResume();
		if (mCurrType == TYPE_DJ) {
			djUpdate();
		}
	}

	@Override
	protected void onPause() {
		if (mMapView != null) {
			mMapView.onPause();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (mLocationClient != null) {
			mLocationClient.stop();
		}
		if (mMKSearch != null) {
			mMKSearch.destory();
		}
		if (mMapView != null) {
			mMapView.destroy();
		}
		unregisterReceiver(mBroadcastReceiver);
		super.onDestroy();
	}

	@Override
	public void onDateSelected() {
		djUpdate();
	}
}
