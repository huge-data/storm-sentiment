package zx.soft.storm.analysis.topology;

import zx.soft.storm.analysis.bolt.TestWordProcessed;
import zx.soft.storm.analysis.bolt.TestWordWriteIntoDb;
import zx.soft.storm.analysis.spout.TestSpout;
import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.tuple.Fields;

public class TopologyStarter {
	//	public final static String REDIS_HOST = "localhost";
	public final static String REDIS_HOST = "192.168.3.23";
	public final static int REDIS_PORT = 6379;

	public static void main(final String[] args) {
		System.out.println("-----------------------------------");
		System.out.println("args:" + args.length);
		System.out.println("-----------------------------------");

		TopologyBuilder builder = new TopologyBuilder();

		builder.setSpout("read-data", new TestSpout(), 3);

		//		builder.setBolt("print-data", new TestClassifyBolt(), 3).shuffleGrouping("read-data");
		//		builder.setBolt("word-normalizer", new TestWordNormalizer(), 3).shuffleGrouping("read-data");
		//		builder.setBolt("word-counter", new TestWordCounter(), 2).fieldsGrouping("word-normalizer", new Fields("word"));

		builder.setBolt("word-processer", new TestWordProcessed(), 3).shuffleGrouping("read-data");
		builder.setBolt("word-writer", new TestWordWriteIntoDb(), 2).fieldsGrouping("word-processer",
				new Fields("word"));

		Config conf = new Config();
		conf.setDebug(true);

		conf.put("redis-host", REDIS_HOST);
		conf.put("redis-port", REDIS_PORT);

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
			cluster.submitTopology("local-test", conf, builder.createTopology());
		}
	}
}