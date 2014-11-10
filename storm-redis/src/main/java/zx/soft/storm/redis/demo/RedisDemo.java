package zx.soft.storm.redis.demo;

import zx.soft.storm.redis.cache.dao.Cache;
import zx.soft.storm.redis.cache.factory.CacheFactory;

public class RedisDemo {

	public static void main(String[] args) {

		Cache cache = CacheFactory.getInstance();
		System.out.println("ok");
		cache.sadd("test", "wgybzb");
		String data = cache.spop("test");
		System.out.println(data);

	}

}
