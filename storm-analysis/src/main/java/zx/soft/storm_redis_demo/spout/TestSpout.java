package zx.soft.storm_redis_demo.spout;

import java.util.Map;

import redis.clients.jedis.Jedis;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class TestSpout extends BaseRichSpout {
	private static final long serialVersionUID = 1L;
	Jedis jedis;
	String host;
	int port;
	SpoutOutputCollector collector;

	@Override
	@SuppressWarnings("rawtypes")
	public void open(final Map stormConf, final TopologyContext context, final SpoutOutputCollector collector) {
		host = stormConf.get("redis-host").toString();
		port = Integer.valueOf(stormConf.get("redis-port").toString());
		this.collector = collector;
		reconnect();
	}

	private void reconnect() {
		jedis = new Jedis(host, port);
	}

	@Override
	public void nextTuple() {
		String content = jedis.rpop("queue");
		if (content == null || "nil".equals(content)) {
			//	如果redis中指定列表已经是空的，就休眠0.3秒，以免使用忙等待循环阻塞服务器。
			try {
				Thread.sleep(300);
			} catch (InterruptedException e) {
			}
		} else {
			collector.emit(new Values(content));
			System.out.println("从数据源redis中读到一条记录:=" + content);
		}
	}

	@Override
	public void declareOutputFields(final OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("data"));
	}

}
