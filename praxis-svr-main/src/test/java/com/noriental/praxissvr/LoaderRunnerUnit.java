package com.noriental.praxissvr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by bluesky on 2016/8/27.
 */
public class LoaderRunnerUnit {
    private static final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public  static  void run(Runnable run ,int concurentCount,int seconds){
        ExecutorService threadPool = Executors.newFixedThreadPool(concurentCount);
//        ScheduledExecutorService executor = Executors.newScheduledThreadPool(concurentCount);
//        try {
//            threadPool.awaitTermination(seconds, TimeUnit.SECONDS);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        long s = System.currentTimeMillis();
        long milSec = seconds*1000;
        int count =0;
        while (true){
            long t = System.currentTimeMillis();
            if( (t-s) < (milSec)){
//                for(int i =0;i<concurentCount;i++){
                    threadPool.execute(run);
                    ++count;
//                }
            }else{
                break;
            }
        }
        logger.info("count="+ count);
        threadPool.shutdown();
    }
}
