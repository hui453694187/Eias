package com.yunfang.framework.chart;

import java.util.ArrayList;
import java.util.List;

import org.xclcharts.chart.BarChart;
import org.xclcharts.chart.BarData;
import org.xclcharts.event.click.BarPosition;
import org.xclcharts.renderer.XChart;
import org.xclcharts.renderer.XEnum;
import org.xclcharts.renderer.XEnum.LineStyle;
import org.xclcharts.renderer.info.DyLine;
import org.xclcharts.renderer.info.Legend;
import org.xclcharts.renderer.line.PlotDot;

import com.yunfang.framework.utils.NumberUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 柱状图视图
 * 
 * @author gorson
 * 
 */
@SuppressLint("InlinedApi")
public class BarChartView extends BaseChart implements Runnable {

	// {{ 构造函数
	public BarChartView(Context context, ChartDataObject<BarData> data) {
		super(context);
		initView(context, data);
	}

	public BarChartView(Context context, AttributeSet attrs,
			ChartDataObject<BarData> data) {
		super(context, attrs);
		initView(context, data);
	}

	public BarChartView(Context context, AttributeSet attrs, int defStyle,
			ChartDataObject<BarData> data) {
		super(context, attrs, defStyle);
		initView(context, data);
	}

	// }}

	// {{ 控件
	/**
	 * 柱状图控件
	 */
	BarChart chart = new BarChart();
	// }}

	// {{ 全局参数
	/**
	 * 柱状图数据对象列表
	 */
	ChartDataObject<BarData> chartData = null;

	/**
	 * 数据源
	 */
	List<BarData> chartAxisData = new ArrayList<BarData>();

	Paint mPaintToolTip = new Paint(Paint.ANTI_ALIAS_FLAG);
	PlotDot mDotToolTip = new PlotDot();

	// }}

	// {{ 函数
	/**
	 * 初始化视图
	 */
	private void initView(Context context, ChartDataObject<BarData> data) {
		currentContext = context;
		chartData = data;
		fillData();
		chartRender();
		new Thread(this).start();
	}

	/**
	 * 绘制图表
	 */
	private void chartRender() {
		try {
			// 设置绘图区默认缩进px值,留置空间显示Axis,Axistitle....
			spaddingValue = chartData.DefaultSpadding != null ? chartData.DefaultSpadding
					: getBarLnDefaultSpadding();
			chart.setPadding(spaddingValue[0], spaddingValue[1],
					spaddingValue[2], spaddingValue[3]);

			// 数据源
			chart.setDataSource(chartAxisData);
			chart.setCategories(chartLabels);

			// 轴标题
			chart.getAxisTitle().setLeftTitle(chartData.LeftAxisTitle);
			chart.getAxisTitle().setLowerTitle(chartData.BottomAxisTitle);

			// Y数据轴
			chart.getDataAxis().setAxisMax(maxValue);
			chart.getDataAxis().setAxisMin(minValue);
			chart.getDataAxis().setAxisSteps(YSteps);

			// X数据轴
			chart.getCategoryAxis().setAxisSteps(chartData.XAxisSteps);

			// 定义Y数据轴标签显示格式
			chart.getDataAxis().setLabelFormatter(chartData.YLableFormatter);

			// 是否在图表控件中显示每个数据值
			chart.getBar().setItemLabelVisible(chartData.ShowItemLabel);

			// 定义X数据轴标签显示格式
			chart.setItemLabelFormatter(chartData.XLableFormatter);

			// 让柱子间没空白
			// chart.getBar().setBarInnerMargin(0d);

			// Y轴的颜色和刻度颜色设置
			if (NumberUtil.Greater0(chartData.YAxisColor)) {
				chart.getDataAxis().getAxisPaint()
						.setColor(chartData.YAxisColor);
			}
			if (NumberUtil.Greater0(chartData.YAxisTickMarksColor)) {
				chart.getDataAxis().getTickMarksPaint()
						.setColor(chartData.YAxisTickMarksColor);
			}

			// X轴的颜色和刻度颜色设置
			if (NumberUtil.Greater0(chartData.XAxisColor)) {
				chart.getCategoryAxis().getAxisPaint()
						.setColor(chartData.XAxisColor);
			}
			if (NumberUtil.Greater0(chartData.XAxisTickMarksColor)) {
				chart.getCategoryAxis().getTickMarksPaint()
						.setColor(chartData.XAxisTickMarksColor);
			}

			// 指隔多少个轴刻度(即细刻度)后为主刻度
			chart.getDataAxis().setDetailModeSteps(3);

			// 扩展横向显示范围,当数据太多时可用这个扩展实际绘图面积
			chart.getPlotArea().extWidth(chartData.ExtWidth);

			// 显示十字交叉线
			chart.showDyLine();
			DyLine dyl = chart.getDyLine();
			if (null != dyl) {
				dyl.setDyLineStyle(XEnum.DyLineStyle.Horizontal);
				dyl.setLineDrawStyle(XEnum.LineStyle.DASH);
			}

			// 数据轴居中显示
			chart.setDataAxisLocation(chartData.DataAxisLocation);

			// 忽略Java的float计算误差，提高性能chartAllDatadataSeriesD.add(35d + i);
			chart.disableHighPrecision();
			
			//是否显示虚线
			if(chartData.ShowDottedLines){
				chart.getPlotGrid().setHorizontalLineStyle(LineStyle.DASH);
				chart.getPlotGrid().setVerticalLineStyle(LineStyle.DASH);
			}

			//是否显示水平线
			if(chartData.ShowHorizontalLines){
				chart.getPlotGrid().showHorizontalLines();	
			}
			else{
				chart.getPlotGrid().hideHorizontalLines();
			}
			//是否显示垂直线
			if(chartData.ShowHorizontalLines){
				chart.getPlotGrid().showVerticalLines();
			}else{
				chart.getPlotGrid().hideVerticalLines();
			}

			// 柱形和标签居中方式
			// chart.setBarCenterStyle(XEnum.BarCenterStyle.TICKMARKS);

		} catch (Exception e) {

		}
	}

	/**
	 * 将对象数据填充到各自参数中
	 */
	private void fillData() {
		if (chartData != null && chartData.Data != null
				&& chartData.Data.size() > 0) {
			minValue = chartData.Data.get(0).getDataSet().get(0);
			chartAxisData = chartData.Data;
			chartLabels = chartData.XLabels;
			for (BarData barData : chartAxisData) {
				List<Double> tempData = barData.getDataSet();
				if (tempData != null && tempData.size() > 0) {
					for (int i = 0; i < tempData.size(); i++) {
						maxValue = tempData.get(i) > maxValue ? tempData.get(i)
								: maxValue;
						minValue = tempData.get(i) < minValue ? tempData.get(i)
								: minValue;
					}
				}
			}
			if(chartData.DisableScale){
				chart.disableScale();
			}
			initBaseProperties(chartData);
		}
	}

	// }}

	//{{ Override
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// 图所占范围大小
		chart.setChartRange(w, h); // + w * 0.5f
	}

	@Override
	public void render(Canvas canvas) {
		try {
			chart.render(canvas);
		} catch (Exception e) {

		}
	}

	@Override
	public List<XChart> bindChart() {
		List<XChart> lst = new ArrayList<XChart>();
		lst.add(chart);
		return lst;
	}

	@Override
	public void run() {
		try {
			chartAnimation();
		} catch (Exception e) {
			Thread.currentThread().interrupt();
		}
	}
//}}
	/**
	 * 加载控件
	 */
	private void chartAnimation() {
		try {
			chart.getDataAxis().hide();
			chart.getPlotLegend().hide();

			for (int i = 8; i > 0; i--) {
				Thread.sleep(50);
				chart.setPadding(spaddingValue[0], i * spaddingValue[1],
						spaddingValue[2], spaddingValue[3]);
				if (i == 1) {
					drawTitle();
				}
				postInvalidate();
			}

			if (chartData.ShowDyLegend) {
				drawDyLegend();
			}
		} catch (Exception e) {
			Thread.currentThread().interrupt();
		}
	}

	/**
	 * 设定图表标题
	 */
	private void drawTitle() {
		chart.setTitle(chartData.Title);
		if (NumberUtil.Greater0(chartData.TitleColor)) {
			chart.getPlotTitle().getTitlePaint().setColor(chartData.TitleColor);
		}
		chart.addSubtitle(chartData.SubTitle);
		if (NumberUtil.Greater0(chartData.SubTitleColor)) {
			chart.getPlotTitle().getSubtitlePaint()
					.setColor(chartData.SubTitleColor);
		}

		// 激活点击监听
		chart.ActiveListenItemClick();
		chart.showClikedFocus();

		// 禁用平移模式
		// chart.disablePanMode();
		// 限制只能左右滑动
		chart.setPlotPanMode(XEnum.PanMode.HORIZONTAL);

		// 禁用双指缩放
		// chart.disableScale();

		// 显示X和Y轴
		chart.getDataAxis().show();

		if (chartData.ShowPlotLegend) {
			chart.getPlotLegend().show();
		}

		// 当值与轴最小值相等时，不显示轴
		chart.hideBarEqualAxisMin();
	}

	/**
	 * 右上角来源的列表
	 */
	private void drawDyLegend() {
		Legend dyLegend = chart.getDyLegend();
		if (null == dyLegend || chartData.DyLegendProperties == null
				|| chartData.DyLegendProperties.length != 5) {
			return;
		}
		dyLegend.setPosition(chartData.DyLegendProperties[0],
				chartData.DyLegendProperties[1]);
		dyLegend.setColSpan(chartData.DyLegendProperties[2]);
		if (NumberUtil.Greater0(chartData.DyLegendBackgroundColor)) {
			dyLegend.getBackgroundPaint().setColor(
					chartData.DyLegendBackgroundColor);
		}
		dyLegend.getBackgroundPaint().setAlpha(
				chartData.DyLegendBackgroundAlpha);
		dyLegend.setRowSpan(chartData.DyLegendProperties[3]);
		dyLegend.setMargin(chartData.DyLegendProperties[4]);
		dyLegend.setStyle(XEnum.DyInfoStyle.ROUNDRECT);

		for (BarData item : chartData.Data) {
			Paint pDyLegend = new Paint(Paint.ANTI_ALIAS_FLAG);
			pDyLegend.setColor(item.getColor());
			PlotDot dotDyLegend = new PlotDot();
			dotDyLegend.setDotStyle(XEnum.DotStyle.RECT);
			dyLegend.addLegend(dotDyLegend, item.getKey(), pDyLegend);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);

		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			if (clickListener != null) {
				clickListener.triggerClick(event.getX(), event.getY());
			} else {
				triggerClick(event.getX(), event.getY());
			}
			break;
		default:
			break;
		}

		return true;
	}

	// {{ 点击事件
	/**
	 * 图表点击事件
	 */
	ClickListener clickListener;

	/**
	 * 设置点击后的响应事件
	 * 
	 * @param l
	 */
	public void setClickListener(ClickListener l) {
		this.clickListener = l;
	}

	// }}

	// 触发监听
	private void triggerClick(float x, float y) {

		// 交叉线
		if (chart.getDyLineVisible())
			chart.getDyLine().setCurrentXY(x, y);

		if (!chart.getListenItemClickStatus()) {
			// 交叉线
			if (chart.getDyLineVisible() && chart.getDyLine().isInvalidate())
				this.invalidate();
		} else {
			BarPosition record = chart.getPositionRecord(x, y);
			if (null == record) {
				if (chart.getDyLineVisible())
					this.invalidate();
				return;
			}

			if (record.getDataID() >= chartAxisData.size())
				return;
			BarData bData = chartAxisData.get(record.getDataID());

			if (record.getDataChildID() >= bData.getDataSet().size())
				return;
			Double bValue = bData.getDataSet().get(record.getDataChildID());

			// 显示选中框
			chart.showFocusRectF(record.getRectF());
			chart.getFocusPaint().setStyle(Style.STROKE);
			chart.getFocusPaint().setStrokeWidth(3);
			chart.getFocusPaint().setColor(
					bData.getColor() != Color.GREEN ? Color.GREEN : Color.RED);

			// 在点击处显示tooltip
			mPaintToolTip.setColor(bData.getColor());
			mDotToolTip.setDotStyle(XEnum.DotStyle.RECT);
			chart.getToolTip().setCurrentXY(x, y);
			chart.getToolTip().setStyle(XEnum.DyInfoStyle.ROUNDRECT);
			chart.getToolTip().addToolTip(mDotToolTip, bData.getKey(),
					mPaintToolTip);
			chart.getToolTip().addToolTip("当前值:" + Double.toString(bValue),
					mPaintToolTip);
			chart.getToolTip().getBackgroundPaint().setAlpha(100);
			this.invalidate();
		}
	}
}
