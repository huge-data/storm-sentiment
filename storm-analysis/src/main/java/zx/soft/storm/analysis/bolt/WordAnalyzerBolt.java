package zx.soft.storm.analysis.bolt;

import java.util.Map;

import zx.soft.storm.analysis.utils.AnalyzerTool;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class WordAnalyzerBolt extends BaseRichBolt {

	private static final long serialVersionUID = 1L;

	private OutputCollector collector;

	// 分词器
	//	private static final AnalyzerTool ANALYZER_TOOL = new AnalyzerTool();
	private AnalyzerTool analyzerTool;

	@Override
	public void cleanup() {
		analyzerTool.close();
	}

	@Override
	public void declareOutputFields(final OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("word"));
	}

	@Override
	public void execute(final Tuple input) {
		String text = input.getString(0);
		String[] words = analyzerTool.analyzerTextToArr(text);
		for (String word : words) {
			word = word.trim();
			if (word.length() < 2 || word.length() > 6) {
				continue;
			}
			if (!word.isEmpty()) {
				word = word.toLowerCase(); // 只针对英文字符，中文字符不起作用
				collector.emit(new Values(word));
			}
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(final Map stormConf, final TopologyContext context, final OutputCollector collector) {
		this.collector = collector;
		// 如果pepare执行频率比较高的化，不能在这里初始化AnalyzerTool
		analyzerTool = new AnalyzerTool();
	}

}
