package zx.soft.storm.redis.shard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;

import redis.clients.jedis.AdvancedJedisCommands;
import redis.clients.jedis.BasicCommands;
import redis.clients.jedis.BinaryClient.LIST_POSITION;
import redis.clients.jedis.BitOP;
import redis.clients.jedis.ClusterCommands;
import redis.clients.jedis.DebugParams;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCommands;
import redis.clients.jedis.JedisPubSub;
import redis.clients.jedis.JedisShardInfo;
import redis.clients.jedis.MultiKeyCommands;
import redis.clients.jedis.ScanResult;
import redis.clients.jedis.ScriptingCommands;
import redis.clients.jedis.SortingParams;
import redis.clients.jedis.Tuple;
import redis.clients.jedis.ZParams;
import redis.clients.jedis.exceptions.JedisDataException;
import redis.clients.util.Hashing;
import redis.clients.util.Pool;
import redis.clients.util.Sharded;
import redis.clients.util.Slowlog;

public class ValueShardedJedis extends Sharded<Jedis, JedisShardInfo> implements JedisCommands, MultiKeyCommands,
		AdvancedJedisCommands, ScriptingCommands, BasicCommands, ClusterCommands {

	private final Random random = new Random();

	private final Jedis[] allShards;

	protected Pool<ValueShardedJedis> dataSource = null;

	private static final Map<String, String> scripts = new HashMap<String, String>();

	public ValueShardedJedis(List<JedisShardInfo> shards) {
		super(shards);
		allShards = getAllShards().toArray(new Jedis[0]);
	}

	public ValueShardedJedis(List<JedisShardInfo> shards, Hashing algo) {
		super(shards, algo);
		allShards = getAllShards().toArray(new Jedis[0]);
	}

	public void setDataSource(Pool<ValueShardedJedis> jedisPool) {
		this.dataSource = jedisPool;
	}

	@Override
	public Long del(String... keys) {
		long result = 0;
		for (Jedis jedis : allShards) {
			result |= jedis.del(keys);
		}
		return result;
	}

	public void eval(String script, String[] keys, String... members) {
		if (members.length == 1) {
			eval(getShard(members[0]), script, keys, members);
		}

		for (Entry<Jedis, List<String>> entry : getShards(members)) {
			eval(entry.getKey(), script, keys, entry.getValue().toArray(new String[entry.getValue().size()]));
		}
	}

	@Override
	public Long sadd(String key, String... members) {
		if (members.length == 1) {
			return getShard(members[0]).sadd(key, members[0]);
		}

		long result = 0;
		for (Entry<Jedis, List<String>> entry : getShards(members)) {
			result += entry.getKey().sadd(key, entry.getValue().toArray(new String[entry.getValue().size()]));
		}
		return result;
	}

	@Override
	public Long scard(String key) {
		long result = 0;
		for (Jedis jedis : allShards) {
			result += jedis.scard(key);
		}
		return result;
	}

	@Override
	public String spop(String key) {
		Jedis jedis = allShards[random.nextInt(allShards.length)];
		String result = jedis.spop(key);
		if (result != null) {
			return result;
		}

		// 如果碰巧随机到的库没有数据，则继续随机剩下所有的库
		List<Jedis> js = new ArrayList<Jedis>();
		for (Jedis j : allShards) {
			js.add(j);
		}
		js.remove(jedis);
		return spop(key, js);
	}

	@Override
	public String srandmember(String key) {
		Jedis jedis = allShards[random.nextInt(allShards.length)];
		String result = jedis.srandmember(key);
		if (result != null) {
			return result;
		}

		// 如果碰巧随机到的库没有数据，则继续随机剩下所有的库
		List<Jedis> js = new ArrayList<Jedis>();
		for (Jedis j : allShards) {
			js.add(j);
		}
		js.remove(jedis);
		return srandmember(key, js);
	}

	@Override
	public Long srem(String key, String... members) {
		if (members.length == 1) {
			return getShard(members[0]).srem(key, members[0]);
		}

		long result = 0;
		for (Entry<Jedis, List<String>> entry : getShards(members)) {
			result += entry.getKey().srem(key, entry.getValue().toArray(new String[entry.getValue().size()]));
		}
		return result;
	}

	private Object eval(Jedis jedis, String script, String[] keys, String... members) {
		String[] params = new String[keys.length + members.length];
		System.arraycopy(keys, 0, params, 0, keys.length);
		System.arraycopy(members, 0, params, keys.length, members.length);
		String sha = scripts.get(script);
		if (sha == null) {
			sha = DigestUtils.shaHex(script);
			scripts.put(script, sha);
		}
		try {
			return jedis.evalsha(sha, keys.length, params);
		} catch (JedisDataException e) {
			return jedis.eval(script, keys.length, params);
		}
	}

	private Set<Entry<Jedis, List<String>>> getShards(String[] members) {
		Map<Jedis, List<String>> jedisMembersMap = new HashMap<Jedis, List<String>>();
		for (String member : members) {
			Jedis jedis = getShard(member);
			List<String> ms = jedisMembersMap.get(jedis);
			if (ms == null) {
				ms = new ArrayList<String>();
				jedisMembersMap.put(jedis, ms);
			}
			ms.add(member);
		}
		return jedisMembersMap.entrySet();
	}

	private String spop(String key, List<Jedis> jedises) {
		if (jedises.isEmpty()) {
			return null;
		}
		int index = random.nextInt(jedises.size());
		Jedis jedis = jedises.get(index);
		String result = jedis.spop(key);
		if (result != null) {
			return result;
		}
		jedises.remove(index);
		return spop(key, jedises);
	}

	private String srandmember(String key, List<Jedis> jedises) {
		if (jedises.isEmpty()) {
			return null;
		}
		int index = random.nextInt(jedises.size());
		Jedis jedis = jedises.get(index);
		String result = jedis.srandmember(key);
		if (result != null) {
			return result;
		}
		jedises.remove(index);
		return srandmember(key, jedises);
	}

	@Override
	public Long append(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long bitcount(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long bitcount(String arg0, long arg1, long arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> blpop(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> brpop(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long decr(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long decrBy(String arg0, long arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long del(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String echo(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean exists(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long expire(String arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long expireAt(String arg0, long arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String get(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getSet(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean getbit(String arg0, long arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getrange(String arg0, long arg1, long arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long hdel(String arg0, String... arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean hexists(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String hget(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, String> hgetAll(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long hincrBy(String arg0, String arg1, long arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> hkeys(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long hlen(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> hmget(String arg0, String... arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String hmset(String arg0, Map<String, String> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScanResult<Entry<String, String>> hscan(String arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScanResult<Entry<String, String>> hscan(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long hset(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long hsetnx(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> hvals(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long incr(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long incrBy(String arg0, long arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String lindex(String arg0, long arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long linsert(String arg0, LIST_POSITION arg1, String arg2, String arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long llen(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String lpop(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long lpush(String arg0, String... arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long lpushx(String arg0, String... arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> lrange(String arg0, long arg1, long arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long lrem(String arg0, long arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String lset(String arg0, long arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String ltrim(String arg0, long arg1, long arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long move(String arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long persist(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long pfadd(String arg0, String... arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long pfcount(String arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String rpop(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long rpush(String arg0, String... arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long rpushx(String arg0, String... arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String set(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String set(String arg0, String arg1, String arg2, String arg3, long arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean setbit(String arg0, long arg1, boolean arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean setbit(String arg0, long arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String setex(String arg0, int arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long setnx(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long setrange(String arg0, long arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean sismember(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> smembers(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> sort(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> sort(String arg0, SortingParams arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScanResult<String> sscan(String arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScanResult<String> sscan(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long strlen(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String substr(String arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long ttl(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String type(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zadd(String arg0, Map<String, Double> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zadd(String arg0, double arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zcard(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zcount(String arg0, double arg1, double arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zcount(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double zincrby(String arg0, double arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrange(String arg0, long arg1, long arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrangeByScore(String arg0, double arg1, double arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrangeByScore(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrangeByScore(String arg0, double arg1, double arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrangeByScore(String arg0, String arg1, String arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String arg0, double arg1, double arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String arg0, double arg1, double arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Tuple> zrangeByScoreWithScores(String arg0, String arg1, String arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Tuple> zrangeWithScores(String arg0, long arg1, long arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zrank(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zrem(String arg0, String... arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zremrangeByRank(String arg0, long arg1, long arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zremrangeByScore(String arg0, double arg1, double arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zremrangeByScore(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrevrange(String arg0, long arg1, long arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrevrangeByScore(String arg0, double arg1, double arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrevrangeByScore(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrevrangeByScore(String arg0, double arg1, double arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> zrevrangeByScore(String arg0, String arg1, String arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String arg0, double arg1, double arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String arg0, String arg1, String arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String arg0, double arg1, double arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Tuple> zrevrangeByScoreWithScores(String arg0, String arg1, String arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Tuple> zrevrangeWithScores(String arg0, long arg1, long arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zrevrank(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScanResult<Tuple> zscan(String arg0, int arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScanResult<Tuple> zscan(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Double zscore(String arg0, String arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String clusterNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String clusterMeet(String ip, int port) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String clusterAddSlots(int... slots) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String clusterDelSlots(int... slots) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String clusterInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> clusterGetKeysInSlot(int slot, int count) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String clusterSetSlotNode(int slot, String nodeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String clusterSetSlotMigrating(int slot, String nodeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String clusterSetSlotImporting(int slot, String nodeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String clusterSetSlotStable(int slot) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String clusterForget(String nodeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String clusterFlushSlots() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long clusterKeySlot(String key) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long clusterCountKeysInSlot(int slot) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String clusterSaveConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String clusterReplicate(String nodeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> clusterSlaves(String nodeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String clusterFailover() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String ping() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String quit() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String flushDB() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long dbSize() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String select(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String flushAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String auth(String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String save() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String bgsave() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String bgrewriteaof() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long lastsave() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String shutdown() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String info() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String info(String section) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String slaveof(String host, int port) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String slaveofNoOne() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getDB() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String debug(DebugParams params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String configResetStat() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long waitReplicas(int replicas, long timeout) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object eval(String script, int keyCount, String... params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object eval(String script, List<String> keys, List<String> args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object eval(String script) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object evalsha(String script) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object evalsha(String sha1, List<String> keys, List<String> args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object evalsha(String sha1, int keyCount, String... params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Boolean scriptExists(String sha1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Boolean> scriptExists(String... sha1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String scriptLoad(String script) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> configGet(String pattern) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String configSet(String parameter, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String slowlogReset() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long slowlogLen() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Slowlog> slowlogGet() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Slowlog> slowlogGet(long entries) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long objectRefcount(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String objectEncoding(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long objectIdletime(String string) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> blpop(int timeout, String... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> brpop(int timeout, String... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> blpop(String... args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> brpop(String... args) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> keys(String pattern) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> mget(String... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String mset(String... keysvalues) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long msetnx(String... keysvalues) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String rename(String oldkey, String newkey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long renamenx(String oldkey, String newkey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String rpoplpush(String srckey, String dstkey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> sdiff(String... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long sdiffstore(String dstkey, String... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> sinter(String... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long sinterstore(String dstkey, String... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long smove(String srckey, String dstkey, String member) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long sort(String key, SortingParams sortingParameters, String dstkey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long sort(String key, String dstkey) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> sunion(String... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long sunionstore(String dstkey, String... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String watch(String... keys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String unwatch() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zinterstore(String dstkey, String... sets) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zinterstore(String dstkey, ZParams params, String... sets) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zunionstore(String dstkey, String... sets) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long zunionstore(String dstkey, ZParams params, String... sets) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String brpoplpush(String source, String destination, int timeout) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long publish(String channel, String message) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void subscribe(JedisPubSub jedisPubSub, String... channels) {
		// TODO Auto-generated method stub

	}

	@Override
	public void psubscribe(JedisPubSub jedisPubSub, String... patterns) {
		// TODO Auto-generated method stub

	}

	@Override
	public String randomKey() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long bitop(BitOP op, String destKey, String... srcKeys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScanResult<String> scan(int cursor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ScanResult<String> scan(String cursor) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String pfmerge(String destkey, String... sourcekeys) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long pfcount(String... keys) {
		// TODO Auto-generated method stub
		return 0;
	}

}
