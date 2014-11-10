package zx.soft.storm.dao.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerDaoDemo {

	private static Logger logger = LoggerFactory.getLogger(LoggerDaoDemo.class);

	public static void main(String[] args) {
		logger.info("Start logging dao ...");
		LoggerDaoDemo.loggerDao();
		logger.info("Finish logging dao ...");
	}

	public static void loggerDao() {
		logger.info("storm dao ...");
	}

}
