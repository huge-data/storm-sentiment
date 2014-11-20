package zx.soft.storm.redis;

import zx.soft.storm.core.redis.RedisPersist;
import zx.soft.storm.redis.cache.dao.Cache;
import zx.soft.storm.redis.cache.factory.CacheFactory;

public class TestRedisPersist {
	private final Cache cache = CacheFactory.getInstance();
	public static RedisPersist redisPersist = new RedisPersist("test");

	/*	@Test
		public void addValuesToListTest() {
			redisPersist.addValuesToList("hello");
			System.out.println("add ok!!");
		}*/
	public static void main(String[] args) {
		redisPersist.laddValuesToList("hello");
		System.out.println("add ok!!");
	}

}
