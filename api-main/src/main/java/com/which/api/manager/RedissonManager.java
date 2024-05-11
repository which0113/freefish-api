package com.which.api.manager;

import cn.hutool.core.bean.BeanUtil;
import com.which.apicommon.common.BusinessException;
import com.which.apicommon.common.ErrorCode;
import com.which.apicommon.common.ThrowUtils;
import com.which.apicommon.model.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static com.which.apicommon.constant.RedisConstant.*;

/**
 * RedissonManager
 *
 * @author which
 */
@Slf4j
@Component
public class RedissonManager {

    @Resource
    public RedissonClient redissonClient;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 限流操作
     *
     * @param key 区分不同的限流器，比如不同的用户 id 应该分别统计
     */
    public void doRateLimit(String key) {
        // 创建一个限流器
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        // 可以考虑设置过期时间，使得Redis自动清理不活跃用户的限流记录。
        // rateLimiter.expire(10, TimeUnit.DAYS);
        // 每秒最多访问 3 次
        // 参数1 type：限流类型，可以是自定义的任何类型，用于区分不同的限流策略。
        // 参数2 rate：限流速率，即单位时间内允许通过的请求数量。
        // 参数3 rateInterval：限流时间间隔，即限流速率的计算周期长度。
        // 参数4 unit：限流时间间隔单位，可以是秒、毫秒等。
        rateLimiter.trySetRate(RateType.OVERALL, RATE_LIMIT_NUM, 1, RateIntervalUnit.SECONDS);
        // 每当一个操作来了后，请求一个令牌
        boolean canOp = rateLimiter.tryAcquire(1);
        ThrowUtils.throwIf(!canOp, ErrorCode.REQUEST_ERROR);
    }

    /**
     * 限流操作（AI 分析）
     *
     * @param key 区分不同的限流器，比如不同的用户 id 应该分别统计
     */
    public void doRateLimitByAi(String key) {
        // 创建一个限流器
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        rateLimiter.trySetRate(RateType.OVERALL, RATE_LIMIT_NUM_AI, 1, RateIntervalUnit.SECONDS);
        // 每当一个操作来了后，请求一个令牌
        boolean canOp = rateLimiter.tryAcquire(1);
        ThrowUtils.throwIf(!canOp, ErrorCode.REQUEST_ERROR);
    }


    /**
     * redisson分布式锁
     *
     * @param lockName     锁名称
     * @param supplier     供应商
     * @param errorCode    错误代码
     * @param errorMessage 错误消息
     * @return
     */
    public <T> T redissonDistributedLocks(String lockName, Supplier<T> supplier, ErrorCode errorCode, String errorMessage) {
        RLock rLock = redissonClient.getLock(lockName);
        try {
            if (rLock.tryLock(WAIT_TIME, -1, TimeUnit.MILLISECONDS)) {
                return supplier.get();
            }
            throw new BusinessException(errorCode.getCode(), errorMessage);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, e.getMessage());
        } finally {
            if (rLock.isHeldByCurrentThread()) {
                log.info("unLock: " + Thread.currentThread().getId());
                rLock.unlock();
            }
        }
    }

    /**
     * redisson分布式锁
     *
     * @param lockName     锁名称
     * @param supplier     供应商
     * @param errorMessage 错误消息
     * @return
     */
    public <T> T redissonDistributedLocks(String lockName, Supplier<T> supplier, String errorMessage) {
        return redissonDistributedLocks(lockName, supplier, ErrorCode.OPERATION_ERROR, errorMessage);
    }

    /**
     * 根据 requset 获取 userVO
     *
     * @param tokenKey
     * @return
     */
    public UserVO getUserByTokenKey(String tokenKey) {
        if (StringUtils.isBlank(tokenKey)) {
            return null;
        }
        Map<Object, Object> userMap = redisTemplate.opsForHash().entries(tokenKey);
        if (userMap.isEmpty()) {
            return null;
        }
        return BeanUtil.fillBeanWithMap(userMap, new UserVO(), false);
    }

    /**
     * 根据 request 获取 userVO
     *
     * @param request
     * @return
     */
    public UserVO getUserByRequest(HttpServletRequest request) {
        return this.getUserByTokenKey(this.getTokenKeyByRequest(request));
    }

    /**
     * 根据 request 获取 tokenKey
     *
     * @param request
     * @return
     */
    public String getTokenKeyByRequest(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String token = request.getHeader("token");
        if (StringUtils.isBlank(token)) {
            return null;
        }
        return USER_LOGIN_KEY + token;
    }

}