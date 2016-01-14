package com.yunfang.framework.chart;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.xclcharts.chart.PieChart;
import org.xclcharts.chart.PieData;
import org.xclcharts.common.MathHelper;
import org.xclcharts.event.click.ArcPosition;
import org.xclcharts.renderer.XChart;
import org.xclcharts.renderer.XEnum;
import org.xclcharts.renderer.XEnum.LineStyle;
import org.xclcharts.renderer.line.PlotDot;
import org.xclcharts.renderer.plot.PlotLegend;

import com.yunfang.framework.utils.NumberUtil;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class PieChartView extends BaseChart implements Runnable {
	// {{ 构造函数
	public PieChartView(Context context, ChartDataObject<PieData> data) {
		super(context);
		initView(context, data);
	}

	public PieChartView(Context context, AttributeSet attrs,
			ChartDataObject<PieData> data) {
		super(context, attrs);
		initView(context, data);
	}

	public PieChartView(Context context, AttributeSet attrs, int defStyle,
			ChartDataObject<PieData> data) {
		super(context, attrs, defStyle);
		initView(context, data);
	}

	// }}

	// {{ 控件
	/**
	 * 柱状图控件
	 */
	PieChart chart = new PieChart();
	// }}

	// {{ 全局参数
	/**
	 * 柱状图数据对象列表
	 */
	ChartDataObject<PieData> chartData = null;

	/**
	 * 数据源
	 */
	List<PieData> chartAxisData = new ArrayList<PieData>();

	// 标签轴
	List<String> chartLabels = new ArrayList<String>();
	Paint mPaintToolTip = new Paint(Paint.ANTI_ALIAS_FLAG);
	PlotDot mDotToolTip = new PlotDot();

	private int mSelectedID = -1;

	// }}

	// {{ 函数
	/**
	 * 初始化视图
	 */
	private void initView(Context context, ChartDataObject<PieData> data) {
		currentContext = context;
		chartData = data;
		chartAxisData = chartData.Data;
		if(chartData.DisableScale){
			chart.disableScale();
		}
		initBaseProperties(chartData);
		chartRender();
		new Thread(this).start();
	}

	private void chartRender() {
		try {
			// 设置绘图区默认缩进px值,留置空间显示Axis,Axistitle....
			spaddingValue = chartData.DefaultSpadding != null ? chartData.DefaultSpadding
					: getPieDefaultSpadding();
			chart.setPadding(spaddingValue[0], spaddingValue[1],
					spaddingValue[2], spaddingValue[3]);

			// 设置起始偏移角度(即第一个扇区从哪个角度开始绘制)
			// chart.setInitialAngle(90);

			// 标签显示(隐藏，显示在中间，显示在扇区外面)
			chart.setLabelStyle(XEnum.SliceLabelStyle.INSIDE);
			chart.getLabelPaint().setColor(Color.WHITE);

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
			chart.setTitleVerticalAlign(XEnum.VerticalAlign.BOTTOM);

			// chart.setDataSource(chartAxisData);

			// 激活点击监听
			chart.ActiveListenItemClick();
			chart.showClikedFocus();

			// 设置允许的平移模式
			// chart.enablePanMode();
			// chart.setPlotPanMode(XEnum.PanMode.HORIZONTAL);

			if (chartData.ShowDyLegend) {
				drawDyLegend();
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
			
			
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 显示图例
	 */
	private void drawDyLegend() {
		// 显示图例
		PlotLegend legend = chart.getPlotLegend();
		legend.show();
		legend.setType(chartData.PlotLegendType);
		chart.getPlotLegend().setVerticalAlign(chartData.DyLegendVertiaclAlign);
		chart.getPlotLegend().setHorizontalAlign(chartData.DyLegendHorizontalAlign);
		legend.showBox();
	}

	private void chartAnimation() {
		try {
			float sum = 0.0f;
			int count = chartAxisData.size();
			for (int i = 0; i < count; i++) {
				Thread.sleep(50);
				LinkedList<PieData> animationData = new LinkedList<PieData>();

				sum = 0.0f;
				for (int j = 0; j <= i; j++) {
					animationData.add(chartAxisData.get(j));
					sum = (float) MathHelper.getInstance().add(sum,
							chartAxisData.get(j).getPercentage());
				}

				animationData.add(new PieData("", "", MathHelper.getInstance()
						.sub(100.0f, sum), (int) Color.argb(1, 0, 0, 0)));
				chart.setDataSource(animationData);

				// 激活点击监听
				if (count - 1 == i) {
					chart.ActiveListenItemClick();
					// 显示边框线，并设置其颜色
					chart.getArcBorderPaint().setColor(Color.YELLOW);
					chart.getArcBorderPaint().setStrokeWidth(3);
				}

				postInvalidate();
			}

		} catch (Exception e) {
			Thread.currentThread().interrupt();
		}
	}

	// 触发监听
	private void triggerClick(float x, float y) {
		if (!chart.getListenItemClickStatus())
			return;

		ArcPosition record = chart.getPositionRecord(x, y);
		if (null == record)
			return;
		/*
		 * PieData pData = chartData.get(record.getDataID());
		 * Toast.makeText(this.getContext(), " key:" + pData.getKey() +
		 * " Label:" + pData.getLabel() , Toast.LENGTH_SHORT).show();
		 */

		// 用于处理点击时弹开，再点时弹回的效果
		PieData pData = chartAxisData.get(record.getDataID());
		if (record.getDataID() == mSelectedID) {
			boolean bStatus = chartAxisData.get(mSelectedID).getSelected();
			chartAxisData.get(mSelectedID).setSelected(!bStatus);
		} else {
			if (mSelectedID >= 0)
				chartAxisData.get(mSelectedID).setSelected(false);
			pData.setSelected(true);
		}
		mSelectedID = record.getDataID();
		this.invalidate();

		/*
		 * boolean isInvaldate = true; for(int i=0;i < chartData.size();i++) {
		 * PieData cData = chartData.get(i); if(i == record.getDataID()) {
		 * if(cData.getSelected()) { isInvaldate = false; break; }else{
		 * cData.setSelected(true); } }else cData.setSelected(false); }
		 * if(isInvaldate)this.invalidate();
		 */

	}

	// }}

	// {{ Override
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
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		// 图所占范围大小
		chart.setChartRange(w, h);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		super.onTouchEvent(event);
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (chart.isPlotClickArea(event.getX(), event.getY())) {
				triggerClick(event.getX(), event.getY());
			}
		}
		return true;
	}

	// }}
}
