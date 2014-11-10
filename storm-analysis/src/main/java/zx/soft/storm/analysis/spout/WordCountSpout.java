package zx.soft.storm.analysis.spout;

import java.util.Map;

import zx.soft.storm.redis.cache.dao.Cache;
import zx.soft.storm.redis.cache.factory.CacheFactory;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;

public class WordCountSpout extends BaseRichSpout {

	private static final long serialVersionUID = 1L;

	private Cache cache;

	public static final String STREAM_WORD_COUNT_KEY = "stream:word:count";

	SpoutOutputCollector collector;

	@SuppressWarnings("rawtypes")
	@Override
	public void open(final Map stormConf, final TopologyContext context, final SpoutOutputCollector collector) {
		this.collector = collector;
		cache = CacheFactory.getInstance();
	}

	@Override
	public void nextTuple() {
		String value = cache.spop(STREAM_WORD_COUNT_KEY);
		while (value != null) {
			collector.emit(new Values(value));
			value = cache.spop(STREAM_WORD_COUNT_KEY);
		}
	}

	@Override
	public void declareOutputFields(final OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("text"));
	}

	@Override
	public void ack(Object msgId) {
		//
	}

	@Override
	public void fail(Object msgId) {
		//
	}

}
