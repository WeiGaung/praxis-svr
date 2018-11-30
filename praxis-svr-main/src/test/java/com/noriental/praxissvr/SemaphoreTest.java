package com.noriental.praxissvr;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * Created by kate on 2017/9/7.
 */
public class SemaphoreTest {

    private static final int THREAD_COUNT = 30;

    private static ExecutorService threadPool = Executors
            .newFixedThreadPool(THREAD_COUNT);

    private static Semaphore s = new Semaphore(10);

    public static void main(String[] args) {
        /*for (int i = 0; i < THREAD_COUNT; i++) {
            threadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        s.acquire();
                        System.out.println("save data");
                        s.release();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        threadPool.shutdown();*/

        List<String> list = new ArrayList<String>();
        list.add("1");
        list.add("2");
        list.add("2");
        // 通过去重之后的HashSet长度来判断原list是否包含重复元素
        boolean isRepeat = list.size() != new HashSet<String>(list).size();
        System.out.println("list中包含重复元素：" + isRepeat);

    }

}
