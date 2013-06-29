package com.taobao.joey.benchmark;

/**
 * taobao.com Inc. Copyright (c) 1998-2101 All Rights Reserved.
 * <p/>
 * Project: java-utils
 * User: qiaoyi.dingqy
 * Date: 13-6-14
 * Time: ����2:15
 */
public interface IHistogram {
    /**
     * �ϲ�ͳ������
     *
     * @param h
     */
    public void merge(Histogram h);

    /**
     * ���ͳ��������
     *
     * @param value
     */
    public void sample(double value);

    /**
     * ȡ��λ��
     * ��λ�������������б�־ֵ��������λ��ȷ����ȫ�嵥λ��־ֵ�Ĵ���ֵ�����ֲܷ����еļ����СֵӰ�죬
     * �Ӷ���һ���̶����������λ���Էֲ����еĴ����ԡ�
     *
     * @return
     */
    public double median();

    /**
     * ȡ�ٷֱ�λ�õ�������ֵ
     *
     * @param p
     * @return
     */
    public double percentile(double p);

    /**
     * ƽ��ֵ
     *
     * @return
     */
    public double average();

    /**
     * ��׼��  �ܷ�ӳһ�����ݼ�����ɢ�̶�
     *
     * @return
     */
    public double standardDeviation();

    public double sum();

    public double max();

    public double min();
}
