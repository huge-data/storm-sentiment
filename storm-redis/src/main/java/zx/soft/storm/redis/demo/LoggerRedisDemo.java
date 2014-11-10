package zx.soft.storm.redis.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerRedisDemo {

	private static Logger logger = LoggerFactory.getLogger(LoggerRedisDemo.class);

	public static void main(String[] args) {
		logger.info("Start logging redis ...");
		LoggerRedisDemo.loggerRedis();
		logger.info("Finish logging redis ...");
	}

	public static void loggerRedis() {
		logger.info("storm redis ...");
	}

}
