package zx.soft.storm.core.redis;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.storm.redis.cache.dao.Cache;
import zx.soft.storm.redis.cache.factory.CacheFactory;

/**
 * 持久化到Redis的封装类
 *
 * @author wanggang
 *
 */
public class RedisPersist {

	private static Logger logger = LoggerFactory.getLogger(RedisPersist.class);

	private final Cache cache;

	private final String key;

	public RedisPersist(String key) {
		logger.info("Start reids-cluster connecting ...");
		cache = CacheFactory.getInstance();
		this.key = key;
		logger.info("Start reids-cluster CRUD ...");
	}

	/**
	 * 无重复插入一组数据
	 */
	public void addValues(String... values) {
		cache.sadd(key, values);
	}

	/**
	 * 关闭连接
	 */
	public void close() {
		cache.close();
	}

	/**
	 * 输出多条数据，并从集合中删除
	 */
	public List<String> getMultiValues(int N) {
		List<String> values = new ArrayList<>();
		String str = null;
		while (((str = getOneValue()) != null) && (values.size() <= N)) {
			values.add(str);
		}
		return values;
	}

	/**
	 * 输出一条数据，并从集合中删除
	 */
	public String getOneValue() {
		return cache.spop(key);
	}

	/**
	 * 输出集合大小
	 */
	public long getSetSize() {
		Long size = cache.scard(key);
		return size.longValue();
	}

	/**
	 * 插入可重复的数据
	 * @param values
	 */
	public void laddValuesToList(String... values) {
		cache.lpush(key, values);
	}

	/**
	 * 从list中获取一条数据，并从list中删除
	 * @return
	 */
	public String rgetOneValueFromList() {
		return cache.rpop(key);
	}

}
