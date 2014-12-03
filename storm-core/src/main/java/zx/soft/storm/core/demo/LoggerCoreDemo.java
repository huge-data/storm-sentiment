package zx.soft.storm.core.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.storm.analysis.demo.LoggerAnalysisDemo;
import zx.soft.storm.redis.demo.LoggerRedisDemo;

public class LoggerCoreDemo {

	private static Logger logger = LoggerFactory.getLogger(LoggerCoreDemo.class);

	public static void main(String[] args) {
		logger.info("Start logging core ...");
		LoggerAnalysisDemo.loggerAnalysis();
		LoggerRedisDemo.loggerRedis();
		LoggerCoreDemo.loggerCore();
		logger.info("Finish logging core ...");
	}

	public static void loggerCore() {
		logger.info("Storm core ...");
	}

}
