package zx.soft.storm.analysis.topology;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.storm.analysis.bolt.WordAnalyzerBolt;
import zx.soft.storm.analysis.bolt.WordCounterBolt;
import zx.soft.storm.analysis.spout.WordCountSpout;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

public class WordCountTopology {

	private static Logger logger = LoggerFactory.getLogger(WordCountTopology.class);

	/**
	 * 主函数
	 */
	public static void main(final String[] args) {

		logger.info("Start TopologyBuilder Setting ...");
		TopologyBuilder builder = new TopologyBuilder();
		builder.setSpout("Get-Text-From-Redis", new WordCountSpout(), 5);
		builder.setBolt("Text-Analysis", new WordAnalyzerBolt(), 8).shuffleGrouping("Get-Text-From-Redis");
		builder.setBolt("Word-Count", new WordCounterBolt(), 8).fieldsGrouping("Text-Analysis", new Fields("word"));

		logger.info("Start Topology Config ...");

		Config conf = new Config();
		conf.setDebug(true);

		logger.info("Start Topology Submit ...");
		if (args != null && args.length > 0) {
			try {
				StormSubmitter.submitTopology(args[0], conf, builder.createTopology());
			} catch (AlreadyAliveException e) {
				e.printStackTrace();
			} catch (InvalidTopologyException e) {
				e.printStackTrace();
			}
		} else {
			LocalCluster cluster = new LocalCluster();
			cluster.submitTopology("local-AnalyzerDemo", conf, builder.createTopology());
		}
		logger.info("Finish Topology ...");

	}

}