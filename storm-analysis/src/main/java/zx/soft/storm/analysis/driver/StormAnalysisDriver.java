package zx.soft.storm.analysis.driver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.storm.analysis.topology.WordCountTopology;

public class StormAnalysisDriver {

	private static Logger logger = LoggerFactory.getLogger(StormAnalysisDriver.class);

	/**
	 * 主函数
	 */
	public static void main(String[] args) {

		if (args.length == 0) {
			System.err.println("Usage: Driver <class-name>");
			System.exit(-1);
		}
		String[] leftArgs = new String[args.length - 1];
		System.arraycopy(args, 1, leftArgs, 0, leftArgs.length);

		switch (args[0]) {
		case "wordCountTopology":
			logger.info("流式数据服务，词频统计拓扑结构： ");
			WordCountTopology.main(leftArgs);
			break;
		default:
			return;
		}

	}

}
