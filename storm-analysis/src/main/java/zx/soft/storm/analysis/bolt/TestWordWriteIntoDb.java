package zx.soft.storm.analysis.bolt;

import java.util.HashMap;
import java.util.Map;

import zx.soft.storm.analysis.redis.WordCount;
import zx.soft.storm.analysis.redis.WordCountDao;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class TestWordWriteIntoDb extends BaseRichBolt {

	/**
	 * 单词计数
	 */
	private static final long serialVersionUID = 1L;
	private Integer id;
	private String name;
	private Map<String, Long> counters;
	private OutputCollector collector;
	private WordCountDao wcd; //写wordcount入表dao对象
	private WordCount wc;

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(final Map stormConf, final TopologyContext context, final OutputCollector collector) {
		this.counters = new HashMap<String, Long>();
		this.name = context.getThisComponentId();
		this.id = context.getThisTaskId();
		this.collector = collector;
		this.wcd = new WordCountDao();
	}

	@Override
	public void execute(final Tuple input) {
		String word = input.getString(0);
		long _count = input.getInteger(1);
		Long count = counters.get(word);
		if (count == null) { //数据表无记录则插入一条数据
			count = _count;
			wc = new WordCount(word, count);
			wcd.insert(wc);
		} else {//数据表有记录则修改该条数据
			count += _count;
			wc = new WordCount(word, count);
			wcd.update(wc);
		}
		counters.put(word, count);
		collector.emit(new Values(word, count));
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
		for (Map.Entry<String, Long> entry : counters.entrySet()) {
			System.out.println(entry.getKey() + ": " + entry.getValue());
		}
	}
}
