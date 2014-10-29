package zx.soft.storm.analysis.bolt;

import java.util.HashMap;
import java.util.Map;

import zx.soft.storm.analysis.utils.AnalyzerTool;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class TestWordProcessed extends BaseRichBolt {

	/**
	 *标准化分詞处理
	 */
	private static final long serialVersionUID = 1L;
	private OutputCollector collector;
	private AnalyzerTool analyzerTool;

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(final Map stormConf, final TopologyContext context, final OutputCollector collector) {
		this.collector = collector;
		this.analyzerTool = new AnalyzerTool();
	}

	@Override
	public void execute(final Tuple input) {
		//spout reads from posted data and emits one tuple per line
		//the most important method in bolt is execute(), it is called once per tuple received, it will emit several tuples for each tuple received
		String sentence = input.getString(0);
		//	调用IKAnalyzer进行分词
		HashMap<String, Integer> word_counts = analyzerTool.getWordAndCounts(sentence);
		String key = "";
		int val = 0;
		for (Map.Entry<String, Integer> entry : word_counts.entrySet()) {
			key = entry.getKey();
			val = entry.getValue();
			if (!key.trim().isEmpty()) {
				key = key.toLowerCase();
				collector.emit(new Values(key, val));
				System.out.println(key + ": " + val);
			}
		}
	}

	@Override
	public void declareOutputFields(final OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("word", "count"));
	}

}
