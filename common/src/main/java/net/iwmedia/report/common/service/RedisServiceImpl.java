package net.iwmedia.report.common.service;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisServiceImpl implements RedisService {
    private final JedisPool pool;

    public RedisServiceImpl(String host, int port) {
        this.pool = new JedisPool(new JedisPoolConfig(), host, port);
    }

    @Override
    public JedisPool getPool() {
        return pool;
    }

    @Override
    public void close() {
        pool.close();
    }
}
