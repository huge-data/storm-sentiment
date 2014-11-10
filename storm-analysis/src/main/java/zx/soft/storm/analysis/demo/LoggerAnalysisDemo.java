package zx.soft.storm.analysis.demo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggerAnalysisDemo {

	private static Logger logger = LoggerFactory.getLogger(LoggerAnalysisDemo.class);

	public static void main(String[] args) {
		logger.info("Start logging analysis ...");
		LoggerAnalysisDemo.loggerAnalysis();
		logger.info("Finish logging analysis ...");
	}

	public static void loggerAnalysis() {
		logger.info("storm analysis ...");
	}

}
