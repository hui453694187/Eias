package com.yunfang.framework.view;

import com.yunfang.framework.R;
import com.yunfang.framework.base.BaseApplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * 下拉列表
 * 
 * @author 贺隽
 * 
 */
@SuppressLint("InflateParams")
public class ComboBox extends LinearLayout {

	/**
	 * 当前类标识
	 */
	public final static String TAG = "ComboBox";

	/**
	 * 派发事件用的Action
	 */
	public final static String ACTION = "com.yunfang.eias.ui.ComboBox";

	/**
	 * 当前用的列表点击的监控事件
	 */
	private ListViewItemClickListener m_listener;

	/**
	 * 显示的视图
	 */
	private View m_view;

	/**
	 * 下拉列表的数据视图
	 */
	private ListView m_listView;

	/**
	 * 用于显示下拉列表的视图
	 */
	private PopupWindow m_popupwindow;

	/**
	 * 下拉列表的数据适配
	 */
	private ListViewAdapter m_adapter_listview;

	/**
	 * 显示的数据源
	 */
	private String[] m_data;

	/**
	 * 所属的界面
	 */
	private Context mContext;

	/**
	 * 查询按钮
	 */
	private Button m_Button;

	/**
	 * 输入的信息
	 */
	private EditText m_EditText;

	/**
	 * 显示出来下拉列表的高度
	 */
	private int m_popupwindowHeight = 300;

	/**
	 * 下拉列表构成韩城
	 * 
	 * @param context
	 */
	public ComboBox(Context context) {
		super(context);
		mContext = context;
		init(null);

	}

	/**
	 * 构造函数
	 * 
	 * @param context
	 * @param attrs
	 */
	public ComboBox(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init(attrs);
	}

	/**
	 * 设置禁用状态
	 * 
	 * @param isEnabled
	 */
	public void setEditTextEnabled(Boolean isEnabled) {
		m_EditText.setEnabled(isEnabled);
	}

	/**
	 * 设置显示的高度
	 * 
	 * @param height
	 */
	public void setPopupwindowHeight(int height) {
		m_popupwindowHeight = height;
	}

	public void setPosition(Integer position) {
		m_EditText.setText(m_data[position]);
	}

	@SuppressLint("InflateParams")
	private void init(AttributeSet attrs) {
		View newView = LayoutInflater.from(mContext).inflate(R.layout.combobox,
				this, true);
		m_Button = (Button) newView.findViewById(R.id.comboButton);
		m_EditText = (EditText) newView.findViewById(R.id.comboEditText);

		m_adapter_listview = new ListViewAdapter(mContext);
		m_view = LayoutInflater.from(mContext).inflate(
				R.layout.combobox_listview, null);

		m_listView = (ListView) m_view.findViewById(R.id.id_listview);
		m_listView.setAdapter(m_adapter_listview);
		m_listView.setClickable(true);
		m_listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				m_popupwindow.dismiss();
				m_EditText.setText(m_data[position]);

				if (m_listener != null) {
					m_listener.onItemClick(position);
				}

				Intent intent = new Intent();
				intent.setAction(ACTION);
				BaseApplication.getInstance().getApplicationContext()
						.sendBroadcast(intent);
			}
		});

		setListeners();
		initAttrs(attrs);
	}

	private void initAttrs(AttributeSet attrs) {
		if (attrs != null) {
			TypedArray customerAttrs = mContext.obtainStyledAttributes(attrs,
					R.styleable.comboboxAttr);
			if (m_EditText != null) {
				m_EditText.setEnabled(customerAttrs.getBoolean(
						R.styleable.comboboxAttr_editText, true));
			}
		}
	}

	public void setData(String[] data) {
		if (null == data || data.length <= 0) {
			return;
		}
		m_data = data;
	}

	public void setListViewOnClickListener(ListViewItemClickListener listener) {
		m_listener = listener;
	}

	private void setListeners() {
		m_Button.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return false;
			}
		});

		m_Button.setOnClickListener(new OnClickListener() {
			@SuppressWarnings("deprecation")
			@Override
			public void onClick(View v) {

				Log.d(TAG, "Click......");
				if (m_popupwindow == null) {
					m_popupwindow = new PopupWindow(m_view, ComboBox.this
							.getWidth(), m_popupwindowHeight);// LayoutParams.WRAP_CONTENT);

					// 点击PopUpWindow外面的控件也可以使得PopUpWindow dimiss。
					// 需要顺利让PopUpWindow dimiss；PopUpWindow的背景不能为空。
					m_popupwindow.setBackgroundDrawable(new BitmapDrawable());

					// 获得焦点，并且在调用setFocusable（true）方法后，可以通过Back(返回)菜单使PopUpWindow
					// dimiss
					// pop.setFocusable(true)
					m_popupwindow.setFocusable(true);
					m_popupwindow.setOutsideTouchable(true);
					m_popupwindow.showAsDropDown(ComboBox.this, 0, 0);

				} else if (m_popupwindow.isShowing()) {
					m_popupwindow.dismiss();
				} else {
					m_popupwindow.showAsDropDown(ComboBox.this);
				}
			}

		});
	}

	class ListViewAdapter extends BaseAdapter {
		private LayoutInflater m_inflate;

		public ListViewAdapter(Context context) {
			m_inflate = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return m_data == null ? 0 : m_data.length;
		}

		@Override
		public Object getItem(int position) {
			return m_data[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView textview = null;

			if (convertView == null) {
				convertView = m_inflate.inflate(R.layout.combobox_item, null);
				textview = (TextView) convertView.findViewById(R.id.id_txt);

				convertView.setTag(textview);
			} else {
				textview = (TextView) convertView.getTag();
			}

			textview.setText(m_data[position]);

			return convertView;
		}
	}

	public String getText() {
		return m_EditText.getText().toString();
	}

	public void setText(String text) {
		m_EditText.setText(text);
	}

	public interface ListViewItemClickListener {
		void onItemClick(int position);
	}

}
