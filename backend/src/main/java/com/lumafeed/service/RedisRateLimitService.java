package com.lumafeed.service;

import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * Redisson-backed distributed rate limiter for write and read hotspots.
 *
 * @author qzh
 */
@Service
public class RedisRateLimitService {

    @Resource
    private RedissonClient redissonClient;

    /**
     * Acquire one permit from a distributed rate limiter.
     *
     * @param scope request scope, such as blog:like:user:1
     * @param permits permitted requests per interval
     * @param seconds interval length in seconds
     * @return true when one permit was acquired
     */
    public boolean tryAcquire(String scope, long permits, long seconds) {
        RRateLimiter limiter = redissonClient.getRateLimiter("rate:limit:" + scope);
        limiter.trySetRate(RateType.OVERALL, permits, seconds, RateIntervalUnit.SECONDS);
        return limiter.tryAcquire();
    }
}
