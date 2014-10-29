package zx.soft.storm_redis_demo.bolt;

import java.util.Map;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

public class TestWordNormalizer extends BaseRichBolt {

	/**
	 *标准化处理
	 */
	private static final long serialVersionUID = 1L;
	private OutputCollector collector;

	@SuppressWarnings("rawtypes")
	@Override
	public void prepare(final Map stormConf, final TopologyContext context, final OutputCollector collector) {
		this.collector = collector;
	}

	@Override
	public void execute(final Tuple input) {
		//spout reads from posted data and emits one tuple per line
		//the most important method in bolt is execute(), it is called once per tuple received, it will emit several tuples for each tuple received
		String sentence = input.getString(0);
		// 去标点符号：
		//		"!!！？？!!!!%*）%￥！KTV去符号标号！！当然。!!..**半角"
		//		"￥KTV去符号标号当然半角"
		sentence = sentence.replaceAll("\\pP", " ");
		String[] words = sentence.split(" ");
		for (String word : words) {
			word = word.trim();
			if (!word.isEmpty()) {
				word = word.toLowerCase();
				collector.emit(new Values(word));
			}
		}
	}

	@Override
	public void declareOutputFields(final OutputFieldsDeclarer declarer) {
		// TODO Auto-generated method stub
		declarer.declare(new Fields("word"));
	}

}
