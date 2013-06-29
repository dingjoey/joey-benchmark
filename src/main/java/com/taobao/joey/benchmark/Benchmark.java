package com.taobao.joey.benchmark;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * taobao.com Inc. Copyright (c) 1998-2101 All Rights Reserved.
 * <p/>
 * Project: java-utils
 * User: qiaoyi.dingqy
 * Date: 13-6-14
 * Time: ����4:36
 */
public class Benchmark {

    public void run(int numThreads, int cnt, String name, BenchMethodRunnable method) {
        State state = new State();
        state.total = numThreads;
        state.cnt = cnt;
        state.numInitialized = 0;
        state.numDone = 0;
        state.started = false;

        ThreadFactory factory = new ThreadFactory() {
            private int index = 0;

            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setName("bench mark thread index : " + index++);
                return thread;
            }
        };

        List<BenchThreadArg> args = new ArrayList<BenchThreadArg>(numThreads);
        //List<Thread> threads = new ArrayList<Thread>(numThreads);
        for (int i = 0; i < state.total; i++) {
            BenchThreadArg arg = new BenchThreadArg(state, name, method);
            Thread t = factory.newThread(new BenchThreadRunnable(arg));
            args.add(arg);
            //threads.add(t);
            t.start();
        }

        state.lock.lock();
        // �ȴ����в����̶߳���ʼ�����
        while (state.numInitialized < state.total) {
            try {
                state.initCondition.await();    // �����lock
            } catch (InterruptedException e) {
            }
        }
        state.started = true;
        state.startCondition.signalAll();

        // �ȴ����в����̶߳�ִ�����
        while (state.numDone < state.total) {
            try {
                state.doneCondition.await(); // �����lock
            } catch (InterruptedException e) {
            }
        }
        state.lock.unlock();

        // merge���������̵߳�stats���report
        for (int i = 1; i < args.size(); i++) {
            Stats stats = args.get(0).stats;
            stats.merge(args.get(i).stats);
        }
        args.get(0).stats.report();
    }

    public static interface BenchMethodRunnable {
        public void run(Stats stats);
    }

    private class State {
        ReentrantLock lock = new ReentrantLock();
        Condition initCondition = lock.newCondition();
        Condition startCondition = lock.newCondition();
        Condition doneCondition = lock.newCondition();
        int total; // �߳���
        int cnt;  // ÿ���߳�ִ�д���
        /**
         * Each thread goes through the following states:
         * (1) initializing
         * (2) waiting for others to be initialized
         * (3) running
         * (4) done
         */
        int numInitialized;
        int numDone;
        boolean started = false;   // ������bench�̳߳�ʼ����Ϻ�started == true
    }

    private class BenchThreadRunnable implements Runnable {
        BenchThreadArg arg;

        private BenchThreadRunnable(BenchThreadArg arg) {
            this.arg = arg;
        }

        public void run() {
            // initializing
            State state = arg.state;
            Stats stats = arg.stats;
            BenchMethodRunnable method = arg.method;

            state.lock.lock();
            state.numInitialized++;
            if (state.numInitialized >= state.total) {
                state.initCondition.signalAll();
            }

            //  waiting for others to be initialized
            while (!state.started) {
                try {
                    state.startCondition.await();
                } catch (InterruptedException e) {
                }
            }
            state.lock.unlock();

            // running
            stats.start();
            for (int i = 0; i < state.cnt; i++)
                method.run(stats);
            stats.stop();

            // done
            state.lock.lock();
            state.numDone++;
            if (state.numDone >= state.total) {
                state.doneCondition.signalAll();
            }
            state.lock.unlock();
        }
    }

    private class BenchThreadArg {
        State state;
        Stats stats;
        BenchMethodRunnable method;

        private BenchThreadArg(State state, String name, BenchMethodRunnable method) {
            this.state = state;
            this.stats = new Stats(name);
            this.method = method;
        }
    }

}
