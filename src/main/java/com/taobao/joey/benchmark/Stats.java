package com.taobao.joey.benchmark;

/**
 * taobao.com Inc. Copyright (c) 1998-2101 All Rights Reserved.
 * <p/>
 * Project: java-utils
 * User: qiaoyi.dingqy
 * Date: 13-6-14
 * Time: 下午1:51
 */
public class Stats {
    public long start;// 起始时间
    public long finish;// 结束时间
    public double elapsedSecs;// 总耗时单位seconds
    /**
     * 统计完成的所有operations设计的bytes的count值
     */
    public long bytes;
    /**
     * 用于统计每个操作的耗时的柱状图数据
     */
    public Histogram hist = new Histogram();
    /**
     * 统计完成的operations的count值
     */
    public long lastOpFinished; //上一个操作完成时间
    public long done;
    public long nextReport;
    public String opName;

    public Stats(String opName) {
        this.opName = opName;
    }

    public Stats() {
    }

    public void finishSingleOp() {
        long now = System.currentTimeMillis();
        long consume = now - lastOpFinished;
        hist.sample(consume);

        lastOpFinished = now;

        done++;
        if (done >= nextReport) {
            if (nextReport < 1000) nextReport += 100;
            else if (nextReport < 5000) nextReport += 500;
            else if (nextReport < 10000) nextReport += 1000;
            else if (nextReport < 50000) nextReport += 5000;
            else if (nextReport < 100000) nextReport += 10000;
            else if (nextReport < 500000) nextReport += 50000;
            else nextReport += 100000;

            System.err.println("... fininshed " + done + " ops.");
            System.err.flush();
        }

    }

    public void sampleBytes(long bytesSize) {
        bytes += bytesSize;
    }

    public void start() {
        start = System.currentTimeMillis();
        finish = 0l;
        elapsedSecs = 0l;
        elapsedSecs = 0l;
        lastOpFinished = start;
        done = 0l;
        nextReport = 100l;
        bytes = 0l;
        hist.clear();
    }

    public void stop() {
        finish = System.currentTimeMillis();
        elapsedSecs = (finish - start) * 1e-6;
    }

    public void merge(Stats s) {
        this.hist.merge(s.hist);
        this.done += s.done;
        this.bytes += s.bytes;
        this.elapsedSecs += s.elapsedSecs;
        if (this.start > s.start) this.start = s.start;
        if (this.finish < s.finish) this.finish = s.finish;
    }

    public void report() {
        if (elapsedSecs <= 0) {
            System.err.println("no enough time to bench");
            return;
        }

        // 平均每秒操作的数据量  单位  MB/s
        double rate = (bytes / 1048576.0) / elapsedSecs;
        System.out.println(String.format("%6.1f MB/s", rate));
        // 平均每个操作的耗时 单位 ms/op
        double ms = elapsedSecs * 1e6 / done;
        System.out.println(String.format("%-12s : %11.3f micros/op", opName, ms));

        // 进一步的打印每个操作的耗时柱状图统计
        System.out.println(String.format("Microseconds per op:\n%s\n", hist.toString()));
        System.out.flush();
    }


}
