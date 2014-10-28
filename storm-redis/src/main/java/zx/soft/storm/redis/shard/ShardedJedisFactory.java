package zx.soft.storm.redis.shard;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.util.Hashing;

public class ShardedJedisFactory extends BasePooledObjectFactory<ValueShardedJedis> {

	private static Logger logger = LoggerFactory.getLogger(ShardedJedisFactory.class);

	private final List<JedisShardInfo> shards;
	private final Hashing algo;

	public ShardedJedisFactory(List<JedisShardInfo> shards, Hashing algo, Pattern keyTagPattern) {
		this.shards = shards;
		this.algo = algo;
	}

	@Override
	public void activateObject(PooledObject<ValueShardedJedis> pooledJedis) throws Exception {
		final ValueShardedJedis jedis = pooledJedis.getObject();
		if (jedis.getDB() != 1) {
			jedis.select(1);
		}
	}

	@Override
	public void destroyObject(PooledObject<ValueShardedJedis> obj) throws Exception {
		if ((obj != null) && (obj.getObject() != null) && (obj.getObject() instanceof ValueShardedJedis)) {
			ValueShardedJedis shardedJedis = obj.getObject();
			for (Jedis jedis : shardedJedis.getAllShards()) {
				if (jedis.isConnected()) {
					try {
						try {
							jedis.quit();
						} catch (Exception e) {
							logger.error("Exception: " + e);
						}
						jedis.disconnect();
					} catch (final Exception e) {
						logger.error("Exception: " + e);
					}
				}
			}
		}
	}

	@Override
	public PooledObject<ValueShardedJedis> makeObject() throws Exception {
		//		return wrap(create());
		final ValueShardedJedis shardedJedis = new ValueShardedJedis(shards, algo);
		return new DefaultPooledObject<ValueShardedJedis>(shardedJedis);
	}

	@Override
	public void passivateObject(PooledObject<ValueShardedJedis> pooledJedis) throws Exception {
		// TODO maybe should select db 0? Not sure right now.
	}

	@Override
	public boolean validateObject(PooledObject<ValueShardedJedis> obj) {
		final ValueShardedJedis shardedJedis = obj.getObject();
		try {
			for (Jedis jedis : shardedJedis.getAllShards()) {
				return jedis.isConnected() && jedis.ping().equals("PONG");
			}
			return true;
		} catch (final Exception e) {
			return false;
		}
	}

	@Override
	public ValueShardedJedis create() throws Exception {
		return new ValueShardedJedis(shards, algo);
	}

	@Override
	public PooledObject<ValueShardedJedis> wrap(ValueShardedJedis obj) {
		try {
			return new DefaultPooledObject<ValueShardedJedis>(create());
		} catch (final Exception e) {
			logger.error("Wrap Exception: " + e);
			return null;
		}
	}

}
