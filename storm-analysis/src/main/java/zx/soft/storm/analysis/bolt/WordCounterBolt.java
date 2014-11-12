package zx.soft.storm.analysis.bolt;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.storm.dao.common.MybatisConfig;
import zx.soft.storm.dao.wordcount.WordCount;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class WordCounterBolt extends BaseRichBolt {

	/**
	 * 单词计数
	 */
	private static final long serialVersionUID = 1L;

	private static Logger logger = LoggerFactory.getLogger(WordCounterBolt.class);

	//	private Integer id;
	//	private String name;
	private Map<String, Integer> counters;
	private OutputCollector collector;

	private static final WordCount WORD_COUNT_DB = new WordCount(MybatisConfig.ServerEnum.wordcount);
	private static final String TABLENAME = "wordcount";

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(final Map stormConf, final TopologyContext context, final OutputCollector collector) {
		this.counters = new HashMap<>();
		//		this.name = context.getThisComponentId();
		//		this.id = context.getThisTaskId();
		this.collector = collector;
	}

	@Override
	public void execute(final Tuple input) {
		String word = input.getString(0);
		Integer count = counters.get(word);
		if (count == null) { // 插入新词频
			counters.put(word, 1);
		} else { // 更新词频
			counters.put(word, count.intValue() + 1);
		}
		collector.emit(new Values(word, count));
	}

	@Override
	public void declareOutputFields(final OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("word", "count"));
	}

	@Override
	public void cleanup() {
		logger.info("Start Dumping word-counts to DB ...");
		int count;
		for (Entry<String, Integer> temp : counters.entrySet()) {
			if (WORD_COUNT_DB.isWordCountExisted(temp.getKey())) {
				count = WORD_COUNT_DB.selectWordCountByWord(temp.getKey());
				WORD_COUNT_DB.updateWordCount(temp.getKey(), count + temp.getValue());
			} else {
				WORD_COUNT_DB.insertWordCount(TABLENAME, temp.getKey(), temp.getValue());
			}
		}
		logger.info("Finish Dumping word-counts to DB ...");
	}

}
