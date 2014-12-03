package zx.soft.storm.redis.cache.factory;

import java.lang.reflect.Proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.exceptions.JedisConnectionException;
import zx.soft.storm.redis.cache.dao.Cache;
import zx.soft.storm.redis.shard.RedisCache;
import zx.soft.storm.redis.utils.Config;
import zx.soft.utils.retry.RetryHandler;

/**
 * 缓存工厂类
 * 
 * @author wanggang
 *
 */
public class CacheFactory {

	private static Logger logger = LoggerFactory.getLogger(CacheFactory.class);

	private static Cache instance;

	static {
		try {
			instance = (Cache) Proxy.newProxyInstance(
					Cache.class.getClassLoader(),
					new Class[] { Cache.class },
					new RetryHandler<Cache>(new RedisCache(Config.get("redis.servers"), Integer.parseInt(Config
							.get("redis.port")), Config.get("redis.password")), 5000, 10) {
						@Override
						protected boolean isRetry(Throwable e) {
							return e instanceof JedisConnectionException;
						}
					});
		} catch (Exception e) {
			logger.error("CacheFactory Exception is " + e);
			throw new RuntimeException(e);
		}
	}

	public static Cache getInstance() {
		return instance;
	}

}
