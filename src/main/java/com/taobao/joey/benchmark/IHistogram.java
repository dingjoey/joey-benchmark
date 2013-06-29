package com.taobao.joey.benchmark;

/**
 * taobao.com Inc. Copyright (c) 1998-2101 All Rights Reserved.
 * <p/>
 * Project: java-utils
 * User: qiaoyi.dingqy
 * Date: 13-6-14
 * Time: 下午2:15
 */
public interface IHistogram {
    /**
     * 合并统计数据
     *
     * @param h
     */
    public void merge(Histogram h);

    /**
     * 添加统计样本点
     *
     * @param value
     */
    public void sample(double value);

    /**
     * 取中位数
     * 中位数是以它在所有标志值中所处的位置确定的全体单位标志值的代表值，不受分布数列的极大或极小值影响，
     * 从而在一定程度上提高了中位数对分布数列的代表性。
     *
     * @return
     */
    public double median();

    /**
     * 取百分比位置的样本点值
     *
     * @param p
     * @return
     */
    public double percentile(double p);

    /**
     * 平均值
     *
     * @return
     */
    public double average();

    /**
     * 标准差  能反映一个数据集的离散程度
     *
     * @return
     */
    public double standardDeviation();

    public double sum();

    public double max();

    public double min();
}
