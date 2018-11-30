package com.noriental.praxissvr.question.utils;

import com.noriental.praxissvr.exception.PraxisErrorCode;
import com.noriental.validate.exception.BizLayerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * Created by luozukai on 2016/11/25.
 */
@Component("ssdbLock")
public class SSDBLock {
    private static final Logger logger = LoggerFactory.getLogger(SSDBLock.class);

    @javax.annotation.Resource(name = "singleServerSsdbClient")
    protected com.hyd.ssdb.SsdbClient ssdbClient;

    private String lockKey = "ssdb:lock";
    private int ttl = 60;
    private int default_retry_lock_term = 100; // 100ms重试

    /**
     * 锁操作，默认key和过期时间
     *
     * @throws Exception
     */
    public void lock() throws BizLayerException {
        lock(lockKey, ttl);
    }

    /**
     * 锁操作，指定key和过期时间
     *
     * @param lockKey
     * @param ttl
     * @throws Exception
     */
    public void lock(String lockKey, int ttl) throws BizLayerException {
        boolean flag = getLock(lockKey, ttl);
        while (!flag) {
            try {
                Thread.sleep(default_retry_lock_term);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
                throw  new BizLayerException("", PraxisErrorCode.SSDB_ADD_LOCK_FAIL);

            }
            flag = getLock(lockKey, ttl);
        }
    }

    /**
     * 获取锁
     *
     * @param lockKey
     * @param ttl
     * @return
     * @throws Exception
     */
    private boolean getLock(String lockKey, int ttl) throws BizLayerException {
        boolean lock = false;
        Long result = ssdbClient.incr(lockKey);
        if (result == 1) {
            lock = true;
            ssdbClient.expire(lockKey, ttl);
        }
        return lock;
    }

    /**
     * 释放锁，默认key
     */
    public void unlock() {
        try {
            ssdbClient.del(lockKey);
        } catch (Exception e) {
            logger.error("", e);
        }
    }

    /**
     * 释放锁，指定key
     *
     * @param lockKey
     */
    public void unlock(String lockKey) {
        try {
            ssdbClient.del(lockKey);
        } catch (Exception e) {
            logger.error("", e);
        }
    }
}
