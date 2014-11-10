package zx.soft.storm.web.demo;

import zx.soft.storm.redis.cache.dao.Cache;
import zx.soft.storm.redis.cache.factory.CacheFactory;

public class RedisDemo {

	public static void main(String[] args) {

		Cache cache = CacheFactory.getInstance();
		String key = "stream:word:count";
		System.out.println(cache.smembers(key));

	}

}
