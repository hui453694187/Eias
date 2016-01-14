package com.yunfang.framework.chart;

import java.util.LinkedList;

import org.xclcharts.common.IFormatterDoubleCallBack;
import org.xclcharts.common.IFormatterTextCallBack;
import org.xclcharts.renderer.XEnum;
import org.xclcharts.renderer.XEnum.AxisLocation;
import org.xclcharts.renderer.XEnum.HorizontalAlign;
import org.xclcharts.renderer.XEnum.LegendType;
import org.xclcharts.renderer.XEnum.VerticalAlign;

import android.graphics.Color;

/**
 * 图数据加载对象(通用)
 * 
 * @author gorson
 * 
 */
public class ChartDataObject<T> {
	
	/**
	 * 图表左边（Y轴）标签值
	 */
	public String LeftAxisTitle;

	/**
	 * 图表右边（Y轴）标签值
	 */
	public String RightAxisTitle;

	/**
	 * 图表底部（X轴）标签值
	 */
	public String BottomAxisTitle;

	/**
	 * Y轴的最大值
	 */
	public double AxisMax;

	/**
	 * Y轴的最小值
	 */
	public double AxisMin = 0;

	/**
	 * 顶部的标题
	 */
	public String Title;

	/**
	 * 顶部的标题字体颜色
	 */
	public Integer TitleColor;

	/**
	 * 顶部的子标题
	 */
	public String SubTitle;

	/**
	 * 顶部的子标题字体颜色
	 */
	public Integer SubTitleColor;

	/**
	 * 定义数据轴标签显示格式
	 */
	public IFormatterTextCallBack YLableFormatter;

	/**
	 * 数据源显示格式
	 */
	public IFormatterDoubleCallBack XLableFormatter;

	/**
	 * 指隔多少个轴刻度(即细刻度)后为主刻度
	 */
	public double DetailModeSteps = 5;

	/**
	 * 扩展横向显示范围,当数据太多时可用这个扩展实际绘图面积
	 */
	public float ExtWidth = 200f;

	/**
	 * 数据
	 */
	public LinkedList<T> Data;

	/**
	 * X轴显示的标签值
	 */
	public LinkedList<String> XLabels;

	/**
	 * Y轴的颜色
	 */
	public Integer YAxisColor = Color.BLUE;

	/**
	 * Y轴刻度的颜色
	 */
	public Integer YAxisTickMarksColor;

	/**
	 * X轴的颜色
	 */
	public Integer XAxisColor = Color.BLUE;

	/**
	 * X轴刻度的颜色
	 */
	public Integer XAxisTickMarksColor;

	/**
	 * 数据轴位置显示，左中右等位置选择
	 */
	public AxisLocation DataAxisLocation = XEnum.AxisLocation.LEFT;

	/**
	 * 是否在图表中显示每个数据值
	 */
	public Boolean ShowItemLabel = true;

	/**
	 * X轴的刻度值
	 */
	public double XAxisSteps = 10;

	/**
	 * 是否在表头显示有多少个数据源属性
	 */
	public Boolean ShowPlotLegend = true;

	/**
	 * 是否在图表的右上角显示数据源列表
	 */
	public Boolean ShowDyLegend = false;

	/**
	 * 图表数据源列表的纵向位置
	 */
	public VerticalAlign DyLegendVertiaclAlign = XEnum.VerticalAlign.TOP;

	/**
	 * 图表数据源列表的横向位置
	 */
	public HorizontalAlign DyLegendHorizontalAlign = XEnum.HorizontalAlign.RIGHT;

	public LegendType PlotLegendType = XEnum.LegendType.COLUMN;

	/**
	 * 在图表的右上角显示数据源列表的位置等相关参数，必须在ShowDyLegend为True时才起作用
	 * 第1个值为图表显示在X轴的位置，0.8表示从左边开始，在距离整个图表控件左边的80%的位置，默认值为0.8
	 * 第2个值为图表显示在Y轴的位置，0.2表示从顶部开始，在距离整个图表控件顶部下20%的位置，默认值为0.2
	 * 第3个值为图表中方块颜色标识和文字之间的距离，值越大距离越大，默认值为10 第4个值为图表中每一行之间的距离，值越大，行距越大，默认值为10
	 * 第5个值为图表中Margin的值，默认为10
	 */
	public float[] DyLegendProperties = { 0.8f, 0.2f, 10.f, 10.f, 10.f };

	/**
	 * 数据源列表的背景颜色
	 */
	public Integer DyLegendBackgroundColor;

	/**
	 * 数据源列表的背景透明度
	 */
	public int DyLegendBackgroundAlpha = 100;

	/**
	 * 图表默认的位置
	 */
	public int[] DefaultSpadding;

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

	/**
	 * 禁止双指缩放
	 */
	public boolean DisableScale = true;
	
	/**
	 * 散点图 点的大小 半径大小  一般是3～10
	 */
	public Float SctterPieSize;
	
	/**
	 * X轴、Y轴和图例字体大小 一般是18～40
	 */
	public Float ChartTextSize = 21f;
	
	/**
	 * 是否显示虚线
	 */
	public boolean ShowDottedLines  = false;
	
	/**
	 * 是否显示水平线（X轴）
	 */
	public boolean ShowHorizontalLines = true;
	
	/**
	 * 是否显示垂直线（Y轴）
	 */
	public boolean showVerticalLines = true;
	
	/**
	 * 显示数值上浮百分比
	 */
	public double cMaxValue = 0.95;
	
	/**
	 * 显示数值下浮百分比
	 */
	public double cMinValue = 1.05;
	
	/**
	 * 需要显示的间隔值，例如  0.2 显示的是 5份 0.3 显示 4份
	 */
	public double cStepValue = 0.3;
}
