package com.yunfang.framework.chart;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;

import org.xclcharts.common.DensityUtil;
import org.xclcharts.common.IFormatterDoubleCallBack;
import org.xclcharts.common.IFormatterTextCallBack;
import org.xclcharts.renderer.XChart;
import org.xclcharts.view.ChartView;

import com.yunfang.framework.utils.NumberUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

@SuppressLint("InlinedApi")
public class BaseChart extends ChartView {

	// {{ 构造函数
	/**
	 * 构造函数
	 * 
	 * @param context
	 */
	public BaseChart(Context context) {
		super(context);
		currentContext = context;
	}

	/**
	 * 构造函数
	 * 
	 * @param context
	 * @param attrs
	 */
	public BaseChart(Context context, AttributeSet attrs) {
		super(context, attrs);
		currentContext = context;
	}

	/**
	 * 构造函数
	 * 
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public BaseChart(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		currentContext = context;
	}

	// }}

	Context currentContext;

	/**
	 * 数据源中最大值
	 */
	double maxValue = 0d;

	/**
	 * 数据源中最小值
	 */
	double minValue = 0d;

	/**
	 * 最大值与最小值之间的间隔值
	 */
	double YSteps = 0d;

	/**
	 * 控件的Spadding值
	 */
	int[] spaddingValue;

	/**
	 * 图表控件的宽度
	 */
	public Integer Width;

	/**
	 * 图表控件的高度
	 */
	public Integer Height;

	/**
	 * 图表控件的底色
	 */
	public Integer BackgroundColor;

	// 标签轴
	LinkedList<String> chartLabels = new LinkedList<String>();

	@Override
	public List<XChart> bindChart() {
		return null;
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}

	/**
	 * 数值标准格式
	 */
	public IFormatterTextCallBack textFormatterCallBack = new IFormatterTextCallBack() {
		@Override
		public String textFormatter(String value) {
			Double tmp = Double.parseDouble(value);
			DecimalFormat df = new DecimalFormat("#0");
			String label = df.format(tmp).toString();
			return (label);
		}
	};

	/**
	 * 数值标准格式
	 */
	public IFormatterDoubleCallBack doubleFormatterCallBack = new IFormatterDoubleCallBack() {
		@Override
		public String doubleFormatter(Double value) {
			DecimalFormat df = new DecimalFormat("#0");
			String label = df.format(value).toString();
			return label;
		}
	};

	/**
	 * 柱状图chart所使用的默认偏移值。Pad版本要另外兼容 偏移出来的空间用于显示图表左右上下的标签值
	 * 
	 * @return
	 */
	protected int[] getBarLnDefaultSpadding() {
		int[] result = new int[4];
		result[0] = DensityUtil.dip2px(getContext(), 40); // left
		result[1] = DensityUtil.dip2px(getContext(), 60); // top
		result[2] = DensityUtil.dip2px(getContext(), 20); // right
		result[3] = DensityUtil.dip2px(getContext(), 40); // bottom
		return result;
	}

	protected int[] getPieDefaultSpadding() {
		int[] ltrb = new int[4];
		ltrb[0] = DensityUtil.dip2px(getContext(), 20); // left
		ltrb[1] = DensityUtil.dip2px(getContext(), 65); // top
		ltrb[2] = DensityUtil.dip2px(getContext(), 20); // right
		ltrb[3] = DensityUtil.dip2px(getContext(), 20); // bottom
		return ltrb;
	}

	/**
	 * 获取图表控件，直接加入某个视图即可
	 * 
	 * @return
	 */
	public FrameLayout getChartView() {
		// 缩放控件放置在FrameLayout的上层，用于放大缩小图表
		FrameLayout.LayoutParams frameParm = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		frameParm.gravity = Gravity.BOTTOM | Gravity.END;
		// 图表显示范围在占屏幕大小的90%的区域内
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				Width, Height);
		// 居中显示
		layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
		// 图表view放入布局中，也可直接将图表view放入Activity对应的xml文件中
		final RelativeLayout chartLayout = new RelativeLayout(currentContext);
		if (NumberUtil.Greater0(BackgroundColor)) {
			chartLayout.setBackgroundColor(BackgroundColor);
		}
		chartLayout.addView(this, layoutParams);

		FrameLayout frameLayout = new FrameLayout(currentContext);
		if (NumberUtil.Greater0(BackgroundColor)) {
			frameLayout.setBackgroundColor(BackgroundColor);
		}
		// 增加控件
		((ViewGroup) frameLayout).addView(chartLayout);
		return frameLayout;
	}

	/**
	 * 填充公共属性
	 * 
	 * @param chartData
	 */
	protected <T> void initBaseProperties(ChartDataObject<T> chartData) {
		NumberFormat nf = NumberFormat.getNumberInstance();
		nf.setMaximumFractionDigits(0);		
		minValue = Double.parseDouble(nf.format(minValue * chartData.cMaxValue)
				.replace(",", ""));
		maxValue = Double.parseDouble(nf.format(maxValue * chartData.cMinValue)
				.replace(",", ""));		
		YSteps = Double.parseDouble(nf.format((maxValue - minValue) * chartData.cStepValue)
				.replace(",", ""));
		YSteps = YSteps < 1 ? 1 : YSteps;

		DisplayMetrics dm = getResources().getDisplayMetrics();
		Width = NumberUtil.Greater0(chartData.Width) ? chartData.Width
				: (int) (dm.widthPixels * 0.9);
		Height = NumberUtil.Greater0(chartData.Height) ? chartData.Height
				: (int) (dm.heightPixels * 0.9);
		BackgroundColor = chartData.BackgroundColor;
	}
}
