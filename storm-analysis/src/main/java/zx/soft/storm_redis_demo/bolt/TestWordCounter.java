package zx.soft.storm_redis_demo.bolt;

import java.util.HashMap;
import java.util.Map;

import zx.soft.storm_redis_demo.db.WordCount;
import zx.soft.storm_redis_demo.db.WordCountDao;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class TestWordCounter extends BaseRichBolt {

	/**
	 * 单词计数
	 */
	private static final long serialVersionUID = 1L;
	Integer id;
	String name;
	Map<String, Integer> counters;
	private OutputCollector collector;
	private WordCountDao wcd; //写wordcount对象入表
	private WordCount wc;

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(final Map stormConf, final TopologyContext context, final OutputCollector collector) {
		this.counters = new HashMap<String, Integer>();
		this.name = context.getThisComponentId();
		this.id = context.getThisTaskId();
		this.collector = collector;
		this.wcd = new WordCountDao();
	}

	public void writeWordIntoDb(final String word, final long count) {
		wc = new WordCount(word, count);
		if (count == 1) {
			wcd.insert(wc);
		} else {
			wcd.update(wc);
		}
	}

	@Override
	public void execute(final Tuple input) {
		String word = input.getString(0);
		Integer count = counters.get(word);
		if (count == null) {
			count = 0;
		}
		count++;
		counters.put(word, count);
		collector.emit(new Values(word, count));

		writeWordIntoDb(word, count);
	}

	@Override
	public void declareOutputFields(final OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("word", "count"));
	}

	@Override
	public void cleanup() {
		//	when the topology is terminated, it is called
		// when this is called, count for each word is shown
		//	close active connections  and other resources
		System.out.println("-- Word Counter [" + name + id + "] --");
		for (Map.Entry<String, Integer> entry : counters.entrySet()) {
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}
	}
}
