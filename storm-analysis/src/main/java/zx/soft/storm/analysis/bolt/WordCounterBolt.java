package zx.soft.storm.analysis.bolt;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.storm.dao.common.MybatisConfig;
import zx.soft.storm.dao.wordcount.WordCount;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;

public class WordCounterBolt extends BaseRichBolt {

	/**
	 * 单词计数
	 */
	private static final long serialVersionUID = 1L;

	private static Logger logger = LoggerFactory.getLogger(WordCounterBolt.class);

	private static final WordCount WORD_COUNT_DB = new WordCount(MybatisConfig.ServerEnum.wordcount);
	private static final String TABLENAME = "wordcount";

	@Override
	public void cleanup() {
		//
	}

	@Override
	public void declareOutputFields(final OutputFieldsDeclarer declarer) {
	}

	@Override
	public void execute(final Tuple input) {
		String word = input.getString(0);
		if (WORD_COUNT_DB.isWordCountExisted(word)) {
			WORD_COUNT_DB.addWordCountByOne(word);
		} else {
			WORD_COUNT_DB.insertWordCount(TABLENAME, word, 1);
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(final Map stormConf, final TopologyContext context, final OutputCollector collector) {
		logger.info("WordCounterBolt ...");
	}

}
