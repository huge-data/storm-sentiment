package zx.soft.storm_redis_demo.bolt;

import java.util.Map;

import zx.soft.negative.sentiment.core.NegativeClassify;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;

public class TestClassifyBolt extends BaseBasicBolt {
	private static final long serialVersionUID = 1L;

	@Override
	@SuppressWarnings("rawtypes")
	public void prepare(Map stormConf, TopologyContext context) {
		super.prepare(stormConf, context);
	}

	/**
	 * 对post过来的文本进行负面词打分
	 * @param text
	 */
	public void negativeClassify(String text) {
		NegativeClassify negativeClassify = new NegativeClassify();
		System.out.println("score is: " + negativeClassify.getTextScore(text));
		negativeClassify.cleanup();
	}

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		System.out.println("-- Next item of list queue in redis --");
		System.out.println("text is: " + input.getString(0));
		negativeClassify(input.getString(0));
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	}
}
