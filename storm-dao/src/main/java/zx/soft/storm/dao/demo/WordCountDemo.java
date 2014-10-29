package zx.soft.storm.dao.demo;

import zx.soft.storm.dao.common.MybatisConfig;
import zx.soft.storm.dao.wordcount.WordCount;

public class WordCountDemo {

	/**
	 * 主函数
	 */
	public static void main(String[] args) {
		WordCount wordCount = new WordCount(MybatisConfig.ServerEnum.wordcount);
		wordCount.insertWordCount("wordcount", "测试1", 10);
		System.out.println("ok");
	}

}
