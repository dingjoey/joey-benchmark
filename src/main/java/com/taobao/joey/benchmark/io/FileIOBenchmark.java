package com.taobao.joey.benchmark.io;

import com.taobao.joey.benchmark.Benchmark;
import com.taobao.joey.benchmark.Stats;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * taobao.com Inc. Copyright (c) 1998-2101 All Rights Reserved.
 * <p/>
 * Project: joey-benchmark
 * User: qiaoyi.dingqy
 * Date: 13-6-29
 * Time: обнГ2:20
 */
public class FileIOBenchmark {

    public static void main(String[] args) {
        int concurrentNum = 0;
        int runCnt = 0;
        String benchmarkName = "default benchmark";

        if (args.length < 3) {
            System.out.println("Usage:");
            System.out.println("./benchmark concurrentNum runCnt benchmarkName");
            return;
        } else {
            concurrentNum = Integer.valueOf(args[0]);
            runCnt = Integer.valueOf(args[1]);
            benchmarkName = args[2];
        }


        Benchmark.BenchMethodRunnable method = new Benchmark.BenchMethodRunnable() {
            String path = "./testfile";
            BufferedOutputStream out = null;
            byte[] writeContent = new byte[1024 * 1024];

            {
                try {
                    out = new BufferedOutputStream(new FileOutputStream(path, false));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                for (int i = 0; i < writeContent.length; i++) {
                    writeContent[i] = (byte) (i % 10);
                }
            }

            public void run(Stats stats) {
                if (out == null) return;

                try {
                    Thread.sleep(1);
                    out.write(writeContent);
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                stats.finishSingleOp();
                stats.sampleBytes(writeContent.length);
            }
        };

        Benchmark benchmark = new Benchmark();
        benchmark.run(concurrentNum, runCnt, benchmarkName, method);

    }

}
