package zx.soft.storm.redis.shard.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisShardInfo;
import zx.soft.storm.redis.shard.ValueShardedJedis;
import zx.soft.storm.redis.shard.ValueShardedJedisPool;

public class RedisShardClient {

	private static Logger logger = LoggerFactory.getLogger(RedisShardClient.class);

	private final ValueShardedJedisPool pool;

	/**
	 * redisServers、port和password需要根据实际情况设置。
	 * @param redisServers: "host1,host2,host3"
	 * @param port: 6397
	 * @param password: xxxx
	 */
	public RedisShardClient(String redisServers, int port, String password) {
		List<JedisShardInfo> shards = new ArrayList<JedisShardInfo>();
		for (String server : redisServers.split(",")) {
			server = server.trim();
			// 设置访问端口
			if (server.indexOf(":") != -1) {
				String[] hostAndPort = server.split(":");
				server = hostAndPort[0];
				port = Integer.parseInt(hostAndPort[1]);
			}
			logger.info("Add redis server: {}:{}", server, port);
			JedisShardInfo jsi = new JedisShardInfo(server, port, 600_000);
			// 设置访问密码
			jsi.setPassword(password);
			shards.add(jsi);
		}
		JedisPoolConfig config = new JedisPoolConfig();
		pool = new ValueShardedJedisPool(config, shards);
	}

	public void close() {
		pool.destroy();
	}

	public Long del(String... keys) {
		ValueShardedJedis jedis = pool.getResource();
		try {
			return jedis.del(keys);
		} finally {
			pool.returnResource(jedis);
		}
	}

	public boolean exists(String key) {
		ValueShardedJedis jedis = pool.getResource();
		try {
			return jedis.exists(key);
		} finally {
			pool.returnResource(jedis);
		}
	}

	public void eval(String script, String[] keys, String... members) {
		ValueShardedJedis jedis = pool.getResource();
		try {
			jedis.eval(script, keys, members);
		} finally {
			pool.returnResource(jedis);
		}
	}

	public void sadd(String key, String... members) {
		ValueShardedJedis jedis = pool.getResource();
		try {
			jedis.sadd(key, members);
		} finally {
			pool.returnResource(jedis);
		}
	}

	public Long scard(String key) {
		ValueShardedJedis jedis = pool.getResource();
		try {
			return jedis.scard(key);
		} finally {
			pool.returnResource(jedis);
		}
	}

	public boolean sismember(String key, String member) {
		ValueShardedJedis jedis = pool.getResource();
		try {
			return jedis.sismember(key, member);
		} finally {
			pool.returnResource(jedis);
		}
	}

	public Set<String> smembers(String key) {
		ValueShardedJedis jedis = pool.getResource();
		try {
			return jedis.smembers(key);
		} finally {
			pool.returnResource(jedis);
		}
	}

	public String spop(String key) {
		ValueShardedJedis jedis = pool.getResource();
		try {
			return jedis.spop(key);
		} finally {
			pool.returnResource(jedis);
		}
	}

	public String srandmember(String key) {
		ValueShardedJedis jedis = pool.getResource();
		try {
			return jedis.srandmember(key);
		} finally {
			pool.returnResource(jedis);
		}
	}

	public Set<String> srandmember(String key, int count) {
		throw new UnsupportedOperationException();
	}

	public Long srem(String key, String... members) {
		ValueShardedJedis jedis = pool.getResource();
		try {
			return jedis.srem(key, members);
		} finally {
			pool.returnResource(jedis);
		}
	}

}
