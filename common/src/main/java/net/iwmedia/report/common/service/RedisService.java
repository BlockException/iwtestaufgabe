package net.iwmedia.report.common.service;

import redis.clients.jedis.JedisPool;

public interface RedisService {
    JedisPool getPool();
    void close();
}
