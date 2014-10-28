package zx.soft.storm.web.driver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.storm.web.server.StreamAnalysisServer;

public class StormWebDriver {

	private static Logger logger = LoggerFactory.getLogger(StormWebDriver.class);

	/**
	 * 主函数
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length == 0) {
			System.err.println("Usage: Driver <class-name>");
			System.exit(-1);
		}
		String[] leftArgs = new String[args.length - 1];
		System.arraycopy(args, 1, leftArgs, 0, leftArgs.length);

		switch (args[0]) {
		case "streamAnalysisServer":
			logger.info("流式数据服务，统计词频： ");
			StreamAnalysisServer.main(leftArgs);
			break;
		default:
			return;
		}

	}

}
