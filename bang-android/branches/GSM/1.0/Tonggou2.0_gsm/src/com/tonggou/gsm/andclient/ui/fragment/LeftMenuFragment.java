package com.tonggou.gsm.andclient.ui.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.tonggou.gsm.andclient.App;
import com.tonggou.gsm.andclient.R;
import com.tonggou.gsm.andclient.UserBaseInfo;
import com.tonggou.gsm.andclient.db.dao.TonggouMessageDao;
import com.tonggou.gsm.andclient.service.MessageBroadcastReceiver;
import com.tonggou.gsm.andclient.service.MessageBroadcastReceiver.OnGotNewMessageListener;
import com.tonggou.gsm.andclient.ui.AppointmentActivity;
import com.tonggou.gsm.andclient.ui.DTCManagerActivity;
import com.tonggou.gsm.andclient.ui.MainActivity;
import com.tonggou.gsm.andclient.ui.MessageManagerActivity;
import com.tonggou.gsm.andclient.ui.ShopNoticeActivity;
import com.tonggou.gsm.andclient.ui.VehicleDataStatisticActivity;
import com.tonggou.gsm.andclient.ui.ViolationQueryActivity;
import com.tonggou.gsm.andclient.ui.ViolationQuerySettingActivity;
import com.tonggou.gsm.andclient.ui.view.AbsViewHolderAdapter;
import com.tonggou.gsm.andclient.ui.view.ImageWithIndicatorView;
import com.tonggou.gsm.andclient.util.ContextUtil;

/**
 * 左边菜单栏
 * @author lwz
 *
 */
public class LeftMenuFragment extends AbsMenuAdapterViewFragment implements OnItemClickListener, OnGotNewMessageListener {
	
	private final int ITEM_POS_MESSAGE = 1; 
	private final int ITEM_POS_VIOLATION = 4;
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		MessageBroadcastReceiver.register(mActivity, this);
		
		findViewById(R.id.one_key_sos_container).setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				doSOS();
			}
		});
	}
	
	@Override
	int getLayoutId() {
		return R.layout.fragment_menu_left;
	}

	@Override
	int getAdapterViewId() {
		return R.id.left_menu_list;
	}
	
	@Override
	int getAdapterItemLayoutId() {
		return R.layout.item_list_left_menu;
	}
	
	@Override
	int getItemCount() {
		return getDestActivitys().length;
	}

	@Override
	Class<?>[] getDestActivitys() {
		return new Class<?>[]{
                AppointmentActivity.class, MessageManagerActivity.class, DTCManagerActivity.class,
                ShopNoticeActivity.class, ViolationQueryActivity.class, VehicleDataStatisticActivity.class
			};
	}

	@Override
	int getContentsArrayRes() {
		return R.array.left_menu_arr;
	}

	@Override
	int getIconsTypeArrayRes() {
		return R.array.left_menu_icon;
	}

	@Override
	void setMenuAdapterViewData( AbsViewHolderAdapter<MenuItem> adapter,
			int pos, MenuItem itemData) {
		ImageWithIndicatorView numberImage = adapter.getViewFromHolder(R.id.number_icon);
		TextView txtContent = adapter.getViewFromHolder(R.id.menu_item_content);
		
		numberImage.setImageDrawable(itemData.icon);
		numberImage.setIndicator(itemData.indicator);
		txtContent.setText(itemData.content);
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if( position == ITEM_POS_VIOLATION 
				&& !UserBaseInfo.getVehicleInfo().isCanQueryViolation()) {
			Bundle args = new Bundle();
			args.putSerializable(ViolationQuerySettingActivity.EXTRA_DEST_ACTIVITY_CLASS, ViolationQueryActivity.class);
			ContextUtil.startActivity(mActivity, ViolationQuerySettingActivity.class, args);
			return;
		}
		super.onItemClick(parent, view, position, id);
		if( position == ITEM_POS_MESSAGE ) {
			updateMessageCount(0);
		}
	}
	
	/**
	 * 一键救援，拨打电话
	 */
	private void doSOS() {
		String mobile = UserBaseInfo.getShopInfo().getAccidentMobile();
		if( TextUtils.isEmpty(mobile) ) {
			App.showShortToast(getString(R.string.menu_left_shop_mobile_empty));
			return;
		}		
		ContextUtil.phoneCall(mActivity, mobile);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		onGotNewMessage();
	}

	@Override
	public void onGotNewMessage() {
		updateMessageCount(
				(int)TonggouMessageDao.getUnreadMessageCount( 
						mActivity, UserBaseInfo.getUserInfo().getUserNo()));
	}
	
	private void updateMessageCount(int count) {
		if( count > 0 ) {
			((MainActivity)getActivity()).getTitleBar().showIndicator(true, false);
		} else {
			((MainActivity)getActivity()).getTitleBar().hideIndicator(true, false);
		}
		mMenuAdapter.getData().get(ITEM_POS_MESSAGE).indicator = count <= 0 ? null : String.valueOf(count);
		mMenuAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onDestroyView() {
		MessageBroadcastReceiver.unregister(mActivity);
		super.onDestroyView();
	}

}
