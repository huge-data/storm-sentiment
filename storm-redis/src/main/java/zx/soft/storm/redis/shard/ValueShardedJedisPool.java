package zx.soft.storm.redis.shard;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import redis.clients.jedis.JedisShardInfo;
import redis.clients.util.Hashing;
import redis.clients.util.Pool;

public class ValueShardedJedisPool extends Pool<ValueShardedJedis> {

	public ValueShardedJedisPool(final GenericObjectPoolConfig poolConfig, List<JedisShardInfo> shards) {
		this(poolConfig, shards, Hashing.MURMUR_HASH);
	}

	public ValueShardedJedisPool(final GenericObjectPoolConfig poolConfig, List<JedisShardInfo> shards, Hashing algo) {
		this(poolConfig, shards, algo, null);
	}

	public ValueShardedJedisPool(final GenericObjectPoolConfig poolConfig, List<JedisShardInfo> shards, Hashing algo,
			Pattern keyTagPattern) {
		super(poolConfig, new ShardedJedisFactory(shards, algo, keyTagPattern));
	}

	public ValueShardedJedisPool(final GenericObjectPoolConfig poolConfig, List<JedisShardInfo> shards,
			Pattern keyTagPattern) {
		this(poolConfig, shards, Hashing.MURMUR_HASH, keyTagPattern);
	}

	@Override
	public ValueShardedJedis getResource() {
		ValueShardedJedis jedis = super.getResource();
		jedis.setDataSource(this);
		return jedis;
	}

	@Override
	public void returnBrokenResource(final ValueShardedJedis resource) {
		if (resource != null) {
			returnBrokenResourceObject(resource);
		}
	}

	@Override
	public void returnResource(final ValueShardedJedis resource) {
		if (resource != null) {
			returnResourceObject(resource);
		}
	}

}