package com.taobao.joey.benchmark;

/**
 * taobao.com Inc. Copyright (c) 1998-2101 All Rights Reserved.
 * <p/>
 * Project: java-utils
 * User: qiaoyi.dingqy
 * Date: 13-6-14
 * Time: ����2:20
 * </p>
 * ���̰߳�ȫ��һ��һ�������̱߳���һ���������Ⲣ�����⣻
 * </p>
 * �������ʾʱmerge��������ܵĽṹ
 */

public class Histogram implements IHistogram {
    /**
     * ������ȡֵ��������
     * BUCKET_LIMIT[i - 1] <= value in Bucket i < BUCKET_LIMIT[i]
     * ������������Ч��ȡֵ�ռ䣺 0 --- 1e10(10��)
     */
    private static final double BUCKET_LIMIT[] = {
            1, 5, 10, 20, 50, 100, 200, 500, 1000, 2000, 3000,
            4000, 5000, 6000, 7000, 8000, 9000, 10000, 12000, 14000,
            16000, 18000, 20000, 25000, 30000, 35000, 40000, 45000, 50000, 60000,
            70000, 80000, 90000, 100000, 120000, 140000, 160000, 180000, 200000,
            250000, 300000, 350000, 400000, 450000, 500000, 600000, 700000, 800000,
            900000, 1000000, 1200000, 1400000, 1600000, 1800000, 2000000, 2500000,
            3000000, 3500000, 4000000, 4500000, 5000000, 1e10,
    };
    /**
     * �������������
     */
    private static final int BUCKET_SIZE = BUCKET_LIMIT.length;
    /**
     * ���������
     */
    public double count;
    /**
     * ������ֵ�ĺ�
     */
    public double sum;
    /**
     * ����������Сֵ
     */
    public double min;
    /**
     * ���������ֵ
     */
    public double max;
    /**
     * ������ֵ��ƽ����
     */
    public double squareSum;
    /**
     * ������ֵ����ͳ�Ƹ���
     */
    public double bucketCnt[] = new double[BUCKET_SIZE];

    public void clear() {
        count = 0.0;
        max = Double.MIN_VALUE;
        min = Double.MAX_VALUE;
        sum = 0.0;
        squareSum = 0.0;
        for (int i = 0; i < BUCKET_SIZE; i++) {
            bucketCnt[i] = 0.0;
        }
    }

    public void merge(Histogram h) {
        this.count += h.count;
        this.sum += h.sum;
        this.squareSum += h.squareSum;
        if (min > h.min) this.min = h.min;
        if (max < h.max) this.max = h.max;
        for (int i = 0; i < BUCKET_SIZE; i++) {
            bucketCnt[i] += h.bucketCnt[i];
        }
    }

    public void sample(double value) {
        count++;

        sum += value;
        squareSum += (value * value);

        if (min > value) min = value;
        if (max < value) max = value;

        int bucketIndex = 0;
        while (bucketIndex < BUCKET_SIZE) {
            if (value < BUCKET_LIMIT[bucketIndex]) {
                bucketCnt[bucketIndex]++;
                return;
            } else bucketIndex++;
        }
        bucketCnt[BUCKET_SIZE - 1]++;
    }

    public double median() {
        return percentile(50.0);
    }

    public double percentile(double p) {
        double threshold = count * (p / 100.0);
        double sum = 0;
        for (int i = 0; i < BUCKET_SIZE; i++) {
            sum += bucketCnt[i];
            // Scale linearly within this bucket
            if (sum >= threshold) {
                double left_point = (i == 0) ? 0 : BUCKET_LIMIT[i - 1];
                double right_point = BUCKET_LIMIT[i];
                double left_sum = sum - bucketCnt[i];
                double right_sum = sum;
                double pos = (threshold - left_sum) / (right_sum - left_sum);
                double r = left_point + (right_point - left_point) * pos;
                if (r < min) r = min;
                if (r > max) r = max;
                return r;
            }
        }
        return 0;
    }

    public double average() {
        if (count <= 0.0) return 0;
        return sum / count;
    }

    public double standardDeviation() {
        if (count <= 0.0) return 0;
        double variance = (squareSum * count - sum * sum) / (count * count);
        return Math.sqrt(variance);
    }

    public double sum() {
        return sum;
    }

    public double max() {
        return max;
    }

    public double min() {
        return min;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append(String.format("Count: %.0f  Average: %.4f  StdDev: %.2f\n", count, average(), standardDeviation()));

        sb.append(String.format("Min: %.4f  Median: %.4f  Max: %.4f\n", min, median(), max));
        sb.append("------------------------------------------------------------------\n");

        long cumulative = 0l;
        for (int i = 0; i < BUCKET_SIZE; i++) {
            if (bucketCnt[i] < 0.0) continue;

            cumulative += bucketCnt[i];
            sb.append(String.format("[ %7.0f, %7.0f ) %7.0f %7.3f%% %7.3f%% ",
                    i == 0 ? 0 : BUCKET_LIMIT[i - 1],         // left
                    BUCKET_LIMIT[i],                          // right
                    bucketCnt[i],                             // count
                    bucketCnt[i] / count * 100,               // percentage
                    cumulative / count * 100                  // cumulative percentage
            ));
            sb.append("\n");
        }

        return sb.toString();
    }


}
