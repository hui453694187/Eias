package com.yunfang.framework.chart;

import java.util.ArrayList;
import java.util.List;

import org.xclcharts.chart.PointD;
import org.xclcharts.chart.ScatterChart;
import org.xclcharts.chart.ScatterData;
import org.xclcharts.renderer.XChart;
import org.xclcharts.renderer.XEnum;
import org.xclcharts.renderer.XEnum.LineStyle;
import org.xclcharts.renderer.line.PlotDot;

import com.yunfang.framework.R;
import com.yunfang.framework.utils.NumberUtil;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;

/**
 * 散点图
 * 
 * @author gorson
 * 
 */
@SuppressLint("InlinedApi")
public class ScatterChartView extends BaseChart implements Runnable {

	// {{ 构造函数
	public ScatterChartView(Context context, ChartDataObject<ScatterData> data) {
		super(context);
		initView(context, null, data);
	}

	public ScatterChartView(Context context, AttributeSet attrs,
			ChartDataObject<ScatterData> data) {
		super(context, attrs);
		initView(context, attrs, data);
	}

	public ScatterChartView(Context context, AttributeSet attrs, int defStyle,
			ChartDataObject<ScatterData> data) {
		super(context, attrs, defStyle);
		initView(context, attrs, data);
	}

	// }}

	// {{ 控件
	/**
	 * 柱状图控件
	 */
	ScatterChart chart = new ScatterChart();

	// }}

	// {{ 全局参数
	/**
	 * 柱状图数据对象列表
	 */
	ChartDataObject<ScatterData> chartData = null;

	/**
	 * 数据源
	 */
	List<ScatterData> chartAxisData = new ArrayList<ScatterData>();

	Paint mPaintToolTip = new Paint(Paint.ANTI_ALIAS_FLAG);
	PlotDot mDotToolTip = new PlotDot();

	private double xMaxValue = 0;

	private double xMinValue = 0;

	/**
	 * 标记点大小3~10
	 */
	private Float scatterPieSize = 1f;

	// }}

	// {{ 函数
	/**
	 * 初始化视图
	 */
	private void initView(Context context, AttributeSet attrs,
			ChartDataObject<ScatterData> data) {
		currentContext = context;
		chartData = data;
		getCustomerAttrs(attrs, data);
		fillData();
		chartRender();
		new Thread(this).start();
	}

	/**
	 * 响应自定义属性
	 * 
	 * @param attrs
	 */
	private void getCustomerAttrs(AttributeSet attrs,
			ChartDataObject<ScatterData> data) {
		if (attrs != null) {
			TypedArray customerAttrs = currentContext.obtainStyledAttributes(
					attrs, R.styleable.xclChartsAttr);
			scatterPieSize = customerAttrs.getFloat(
					R.styleable.xclChartsAttr_scatterPieSize, 3);
		} else {
			scatterPieSize = data.SctterPieSize;
		}
		// 设置标记点的半径大小
		mDotToolTip.setDotRadius(scatterPieSize);
		mDotToolTip.setColor(Color.RED);
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

			// Y轴的字体大小
			chart.getDataAxis().getTickLabelPaint()
					.setTextSize(chartData.ChartTextSize);
			// X轴的字体大小
			chart.getCategoryAxis().getTickLabelPaint()
					.setTextSize(chartData.ChartTextSize);
			// 图例
			chart.getPlotLegend().getPaint()
					.setTextSize(chartData.ChartTextSize);

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
			// 指隔多少个轴刻度(即细刻度)后为主刻度
			//chart.getDataAxis().setDetailModeSteps(3);
			
			// 定义数据轴标签显示格式
			chart.getDataAxis().setLabelFormatter(chartData.YLableFormatter);

			// 标签轴最大值
			chart.setCategoryAxisMax(xMaxValue);
			// 标签轴最小值
			chart.setCategoryAxisMin(xMinValue);

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
			
			// 扩展横向显示范围,当数据太多时可用这个扩展实际绘图面积
			// chart.getPlotArea().extWidth(chartData.ExtWidth);
		} catch (Exception e) {

		}
	}

	/**
	 * 将对象数据填充到各自参数中
	 */
	private void fillData() {
		if (chartData != null && chartData.Data != null
				&& chartData.Data.size() > 0) {
			chartAxisData = chartData.Data;
			chartLabels = chartData.XLabels;

			PointD tempPoint = chartAxisData.get(0).getDataSet().get(0); // .get(0).getDataSet().get(0);
			minValue = tempPoint.y;
			maxValue = tempPoint.y;
			xMaxValue = tempPoint.x;
			xMinValue = tempPoint.x;
			for (ScatterData scatterData : chartAxisData) {
				//scatterData.setDotStyle(DotStyle.RING);
				scatterData.setPlotDot(mDotToolTip);
				if (scatterData.getDataSet() != null
						&& scatterData.getDataSet().size() > 0) {
					for (PointD pointData : scatterData.getDataSet()) {
						xMaxValue = pointData.x > xMaxValue ? pointData.x
								: xMaxValue;
						xMinValue = pointData.x < xMinValue ? pointData.x
								: xMinValue;

						maxValue = pointData.y > maxValue ? pointData.y
								: maxValue;
						minValue = pointData.y < minValue ? pointData.y
								: minValue;
					}
				}
			}
			if (chartData.DisableScale) {
				chart.disableScale();
			}
			initBaseProperties(chartData);
		}
	}

	/**
	 * 加载控件
	 */
	private void chartAnimation() {
		try {
			chart.getPlotLegend().hide();

			// for (int i = 8; i > 0; i--) {
			// Thread.sleep(50);
			// chart.setPadding(spaddingValue[0], i * spaddingValue[1],
			// spaddingValue[2], spaddingValue[3]);
			// if (i == 1) {
			drawTitle();
			// }
			postInvalidate();
			// }

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
		chart.disableHighPrecision();

		if (chartData.ShowPlotLegend) {
			chart.getPlotLegend().show();
		}
	}

	/**
	 * 右上角来源的列表
	 */
	private void drawDyLegend() {
		// 图例
		chart.getPlotLegend().setType(chartData.PlotLegendType);
		chart.getPlotLegend().setVerticalAlign(chartData.DyLegendVertiaclAlign);
		chart.getPlotLegend().setHorizontalAlign(
				chartData.DyLegendHorizontalAlign);
		chart.getPlotLegend().getBox().setBorderRectType(XEnum.RectType.RECT);
		chart.getPlotLegend().showBox();
	}

	// }}

	// {{ Override
	@Override
	public void run() {
		try {
			chartAnimation();
		} catch (Exception e) {
			Thread.currentThread().interrupt();
		}
	}

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
	// }}
}
