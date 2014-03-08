package com.example.mylistview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

public class PullUpAndDownListView extends ListView {

	/**
	 * 下拉的状态
	 */
	private static final int PULL_DOWN = 1;
	/**
	 * 上拉的状态
	 */
	private static final int PULL_UP = 2;
	/**
	 * 不是上拉也不是下拉
	 */
	private static final int NOTHING = 3;

	protected static final int REFRESHING = 4;
	/**
	 * 底部view
	 */
	private View mFootView;
	/**
	 * 头部view
	 */
	private View mHeaderView;
	/**
	 * 布局填充器
	 */
	private LayoutInflater mInflater;
	/**
	 * 记录献一次按下的坐标
	 */
	private int mLastMotionY;
	/**
	 * 记录头部的高
	 */
	private int mHeaderViewHeight;
	/**
	 * 记录头部原始离顶部的距离
	 */
	private int mHeaderOriginalTopPadding = 0;
	/**
	 * 
	 * 底部view距离底部的距离
	 */
	private int mFooterOriginalTopPadding = 0;
	/**
	 * 上拉和下拉的状态
	 */
	private int STATE = NOTHING;
	/**
	 * 刷新状态监听
	 */
	private RefreshListener mRefreshListener;
	/**
	 * 正常的头部显示
	 * 
	 * @param context
	 */
	private View headerOnNormal;
	/**
	 * 刷新状态的头部显示
	 */
	private View headerOnRefresh;
	/**
	 * 正常的底部显示
	 * 
	 * @param context
	 */
	private View footerOnNormal;
	/**
	 * 刷新状态的底部显示
	 */
	private View footerOnRefresh;

	public PullUpAndDownListView(Context context) {
		super(context);
		init(context);
	}

	public PullUpAndDownListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public PullUpAndDownListView(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		// TODO Auto-generated method stub
		mInflater = LayoutInflater.from(context);

		mHeaderView = mInflater.inflate(R.layout.header, null);
		headerOnNormal = mHeaderView.findViewById(R.id.pullDown);
		headerOnNormal.setVisibility(View.VISIBLE);
		headerOnRefresh = mHeaderView.findViewById(R.id.pullDown_pro_bar);
		headerOnRefresh.setVisibility(View.GONE);

		mFootView = mInflater.inflate(R.layout.footer, null);
		footerOnNormal = mFootView.findViewById(R.id.pullUp);
		footerOnNormal.setVisibility(View.VISIBLE);
		footerOnRefresh = mFootView.findViewById(R.id.pullUp_pro_bar);
		footerOnRefresh.setVisibility(View.GONE);

		addHeaderView(mHeaderView);
		mHeaderView.setMinimumHeight(50);
		mHeaderOriginalTopPadding = mHeaderView.getPaddingTop();

		addFooterView(mFootView);
		mFootView.setMinimumHeight(50);
		mFooterOriginalTopPadding = mFootView.getPaddingTop();

	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastMotionY = (int) ev.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			applyHeaderPadding(ev);
			break;

		case MotionEvent.ACTION_UP:

			resetHeaderPaddingOrFooterPadding();

			break;

		default:
			break;
		}
		// TODO Auto-generated method stub
		return super.onTouchEvent(ev);
	}

	@Override
	public void setAdapter(ListAdapter adapter) {
		// TODO Auto-generated method stub
		super.setAdapter(adapter);

	}

	private void applyHeaderPadding(MotionEvent ev) {
		// getHistorySize has been available since API 1
		int pointerCount = ev.getHistorySize();

		for (int p = 0; p < pointerCount; p++) {

			int historicalY = (int) ev.getHistoricalY(p);

			int topPadding = (int) (((historicalY - mLastMotionY)) / 1.7);

			if (topPadding > 0 && this.getFirstVisiblePosition() == 0) {
				STATE = PULL_DOWN;
				mHeaderView.setPadding(mHeaderView.getPaddingLeft(),
						topPadding, mHeaderView.getPaddingRight(),
						mHeaderView.getPaddingBottom());
			} else if (this.getLastVisiblePosition() == super.getAdapter()
					.getCount() - 1) {
				STATE = PULL_UP;
				int bottonPadding = Math.abs(topPadding);
				mFootView.setPadding(mFootView.getPaddingLeft(),
						mFootView.getPaddingTop(), mFootView.getPaddingRight(),
						bottonPadding);
			}

		}
	}

	/**
	 * 恢复与上下边的距离
	 */
	private void resetHeaderPaddingOrFooterPadding() {
		if (STATE == PULL_DOWN) {
			mHeaderView.setPadding(mHeaderView.getPaddingLeft(),
					mHeaderOriginalTopPadding, mHeaderView.getPaddingRight(),
					mHeaderView.getPaddingBottom());
			if (mRefreshListener != null) {
				mRefreshListener.pullDown();
			}
			changeHeaderView();

		} else if (STATE == PULL_UP) {
			mFootView.setPadding(mFootView.getPaddingLeft(),
					mFootView.getPaddingRight(), mFootView.getPaddingRight(),
					mFooterOriginalTopPadding);
			if (mRefreshListener != null) {
				mRefreshListener.pullUp();
			}
			changeFooterView();
		}

	}

	public void setRefreshComplete() {
		// TODO Auto-generated method stub
		if (STATE == PULL_DOWN) {
			STATE = NOTHING;
			changeHeaderView();

		} else if (STATE == PULL_UP) {
			STATE = NOTHING;
			changeFooterView();
		}

	}

	/**
	 * 改变头部的显示
	 */
	public void changeHeaderView() {
		if (STATE == PULL_DOWN) {
			headerOnNormal.setVisibility(View.GONE);
			headerOnRefresh.setVisibility(View.VISIBLE);
		} else if (STATE == NOTHING) {
			headerOnNormal.setVisibility(View.VISIBLE);
			headerOnRefresh.setVisibility(View.GONE);
		}

	};

	/**
	 * 改变底部的显示
	 */
	public void changeFooterView() {
		if (STATE == PULL_UP) {
			footerOnNormal.setVisibility(View.GONE);
			footerOnRefresh.setVisibility(View.VISIBLE);
		} else if (STATE == NOTHING) {
			footerOnNormal.setVisibility(View.VISIBLE);
			footerOnRefresh.setVisibility(View.GONE);
		}

	};

	/**
	 * 设置监听者
	 * 
	 * @param refreshListener
	 */
	public void setRefreshListener(RefreshListener refreshListener) {

		mRefreshListener = refreshListener;
	}

	/**
	 * 上拉和下拉的监听者
	 * 
	 * @author Administrator
	 * 
	 */
	interface RefreshListener {

		void pullUp();

		void pullDown();
	}

}
