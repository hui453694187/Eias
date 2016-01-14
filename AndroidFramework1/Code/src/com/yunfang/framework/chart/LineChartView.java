package com.yunfang.framework.chart;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.xclcharts.chart.LineChart;
import org.xclcharts.chart.LineData;
import org.xclcharts.event.click.PointPosition;
import org.xclcharts.renderer.XChart;
import org.xclcharts.renderer.XEnum;
import org.xclcharts.renderer.XEnum.LineStyle;
import org.xclcharts.renderer.line.PlotDot;

import com.yunfang.framework.utils.NumberUtil;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class LineChartView extends BaseChart implements Runnable {
	// {{ 构造函数
	public LineChartView(Context context, ChartDataObject<LineData> data) {
		super(context);
		initView(context, data);
	}

	public LineChartView(Context context, AttributeSet attrs,
			ChartDataObject<LineData> data) {
		super(context);
		initView(context, data);
	}

	public LineChartView(Context context, AttributeSet attrs, int defStyle,
			ChartDataObject<LineData> data) {
		super(context);
		initView(context, data);
	}

	// }}

	// {{ 全局参数
	/**
	 * 折线图数据对象列表
	 */
	ChartDataObject<LineData> chartData = null;

	LinkedList<LineData> chartAxisData = new LinkedList<LineData>();
	// // 标签轴
	// List<String> chartLabels = new ArrayList<String>();
	Paint mPaintToolTip = new Paint(Paint.ANTI_ALIAS_FLAG);
	PlotDot mDotToolTip = new PlotDot();

	// }}

	// {{ 控件
	/**
	 * 折线图控件
	 */
	LineChart chart = new LineChart();

	// }}

	// {{ 函数
	/**
	 * 初始化视图
	 */
	private void initView(Context context, ChartDataObject<LineData> data) {
		currentContext = context;
		chartData = data;
		fillData();
		chartRender();
		new Thread(this).start();
	}

	/**
	 * 将对象数据填充到各自参数中
	 */
	private void fillData() {
		if (chartData != null && chartData.Data != null
				&& chartData.Data.size() > 0) {
			minValue = chartData.Data.get(0).getLinePoint().get(0);
			chartAxisData = chartData.Data;
			chartLabels = chartData.XLabels;
			for (LineData lineData : chartAxisData) {
				List<Double> tempData = lineData.getLinePoint();
				if (tempData != null && tempData.size() > 0)
					for (int i = 0; i < tempData.size(); i++) {
						maxValue = tempData.get(i) > maxValue ? tempData.get(i)
								: maxValue;
						minValue = tempData.get(i) < minValue ? tempData.get(i)
								: minValue;
					}
			}
			if(chartData.DisableScale){
				chart.disableScale();
			}
			initBaseProperties(chartData);
		}
	}

	/**
	 * 绘制图表
	 */
	private void chartRender() {
		try {

			// 设置绘图区默认缩进px值,留置空间显示Axis,Axistitle....
			// int [] ltrb = getBarLnDefaultSpadding();
			// chart.setPadding(DensityUtil.dip2px(getContext(), 45),ltrb[1],
			// ltrb[2], ltrb[3]);
			// 设置绘图区默认缩进px值,留置空间显示Axis,Axistitle....
			spaddingValue = chartData.DefaultSpadding != null ? chartData.DefaultSpadding
					: getBarLnDefaultSpadding();
			chart.setPadding(spaddingValue[0], spaddingValue[1],
					spaddingValue[2], spaddingValue[3]);

			// 设定数据源
			chart.setCategories(chartLabels);
			chart.setDataSource(chartAxisData);
			// chart.setCustomLines(mCustomLineDataset);

			for (LineData item : chartAxisData) {
				item.setLabelVisible(chartData.ShowItemLabel);
			}

			//Y轴的字体大小
			chart.getDataAxis().getTickLabelPaint().setTextSize(chartData.ChartTextSize);
			//X轴的字体大小
			chart.getCategoryAxis().getTickLabelPaint().setTextSize(chartData.ChartTextSize);
			//图例
			chart.getPlotLegend().getPaint().setTextSize(chartData.ChartTextSize);				
			
			// 轴标题
			chart.getAxisTitle().setLeftTitle(chartData.LeftAxisTitle);
			chart.getAxisTitle().setLowerTitle(chartData.BottomAxisTitle);

			// 数据轴最大值
			chart.getDataAxis().setAxisMax(maxValue);// 3500);
			chart.getDataAxis().setAxisMin(minValue);
			// 数据轴刻度间隔
			chart.getDataAxis().setAxisSteps(YSteps);// 100);
			// 指隔多少个轴刻度(即细刻度)后为主刻度
			//chart.getDataAxis().setDetailModeSteps(3);

			// 背景网格
			chart.getPlotGrid().showHorizontalLines();

			// 标题
			chart.setTitle(chartData.Title);
			if (NumberUtil.Greater0(chartData.TitleColor)) {
				chart.getPlotTitle().getTitlePaint()
						.setColor(chartData.TitleColor);
			}
			chart.addSubtitle(chartData.SubTitle);
			if (NumberUtil.Greater0(chartData.SubTitleColor)) {
				chart.getPlotTitle().getSubtitlePaint()
						.setColor(chartData.SubTitleColor);
			}

			// 隐藏顶轴和右边轴
			// chart.hideTopAxis();
			// chart.hideRightAxis();

			// 设置轴风格
			// chart.getDataAxis().setTickMarksVisible(false);
			chart.getDataAxis().getAxisPaint().setStrokeWidth(2);
			chart.getDataAxis().getTickMarksPaint().setStrokeWidth(2);
			chart.getDataAxis().showAxisLabels();

			chart.getCategoryAxis().getAxisPaint().setStrokeWidth(2);
			chart.getCategoryAxis().hideTickMarks();

			// 定义数据轴标签显示格式
			chart.getDataAxis().setLabelFormatter(chartData.YLableFormatter);

			// 定义线上交叉点标签显示格式
			chart.setItemLabelFormatter(chartData.XLableFormatter);

			// 允许线与轴交叉时，线会断开
			chart.setLineAxisIntersectVisible(false);

			// chart.setDataSource(chartData);
			// 动态线
			chart.showDyLine();

			// 不封闭
			chart.setAxesClosed(false);

			// 扩展绘图区右边分割的范围，让定制线的说明文字能显示出来
			//chart.getClipExt().setExtRight(150.f);
			
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

			// chart.getDataAxis().hide();
		} catch (Exception e) {

		}
	}

	private void chartAnimation() {
		try {
			int count = chartAxisData.size();
			for (int i = 0; i < count; i++) {
				Thread.sleep(50);
				LinkedList<LineData> animationData = new LinkedList<LineData>();
				for (int j = 0; j <= i; j++) {
					animationData.add(chartAxisData.get(j));
				}

				chart.setDataSource(animationData);
				if (i == count - 1) {
					chart.getDataAxis().show();
					chart.getDataAxis().showAxisLabels();

				}
				postInvalidate();
			}
		} catch (Exception e) {
			Thread.currentThread().interrupt();
		}
	}

	// }}

	// {{ Override
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// 图所占范围大小
		chart.setChartRange(w, h);
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

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		if (event.getAction() == MotionEvent.ACTION_UP) {
			// 交叉线
			if (chart.getDyLineVisible()) {
				chart.getDyLine().setCurrentXY(event.getX(), event.getY());
				if (chart.getDyLine().isInvalidate()) {
					this.invalidate();
					PointPosition record = chart.getPositionRecord(
							event.getX(), event.getY());
					if (record != null) {
						if (record.getDataID() < chartAxisData.size()) {
							LineData lData = chartAxisData.get(record
									.getDataID());
							if (record.getDataChildID() < lData.getLinePoint()
									.size()) {
								Double lValue = lData.getLinePoint().get(
										record.getDataChildID());

								// 显示选中框
								chart.showFocusRectF(record.getRectF());
								chart.getFocusPaint().setStyle(Style.STROKE);
								chart.getFocusPaint().setStrokeWidth(3);
								chart.getFocusPaint()
										.setColor(
												lData.getLineColor() != Color.GREEN ? Color.GREEN
														: Color.RED);
								// 在点击处显示tooltip
								mPaintToolTip.setColor(lData.getLineColor());
								mDotToolTip.setDotStyle(XEnum.DotStyle.RECT);
								chart.getToolTip().setCurrentXY(event.getX(),
										event.getY());
								chart.getToolTip().setStyle(
										XEnum.DyInfoStyle.ROUNDRECT);
								chart.getToolTip().addToolTip(mDotToolTip,
										lData.getLineKey(), mPaintToolTip);
								chart.getToolTip().addToolTip(
										"当前值:" + Double.toString(lValue),
										mPaintToolTip);
								chart.getToolTip().getBackgroundPaint()
										.setAlpha(100);
							}
						}
					}
				}
			}
		}
		return true;
	}
	// }}

}
