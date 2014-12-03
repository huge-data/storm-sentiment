package zx.soft.storm.web.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.storm.analysis.demo.LoggerAnalysisDemo;
import zx.soft.storm.core.demo.LoggerCoreDemo;
import zx.soft.storm.redis.demo.LoggerRedisDemo;

public class LoggerWebDemo {

	private static Logger logger = LoggerFactory.getLogger(LoggerWebDemo.class);

	public static void main(String[] args) {
		logger.info("Start logging web ...");
		LoggerRedisDemo.loggerRedis();
		LoggerAnalysisDemo.loggerAnalysis();
		LoggerCoreDemo.loggerCore();
		LoggerWebDemo.loggerWeb();
		logger.info("Finish logging web ...");
	}

	public static void loggerWeb() {
		logger.info("storm web ...");
	}

}
