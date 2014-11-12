package zx.soft.storm.dao.demo;

import zx.soft.storm.dao.common.JsonUtils;
import zx.soft.storm.dao.common.MybatisConfig;
import zx.soft.storm.dao.wordcount.WordCount;

public class WordCountDemo {

	/**
	 * 主函数
	 */
	public static void main(String[] args) {

		WordCount wordCount = new WordCount(MybatisConfig.ServerEnum.wordcount);
		/**
		 * 插入词频统计结果
		 */
		wordCount.insertWordCount("wordcount", "词语1", 10);
		wordCount.insertWordCount("wordcount", "词语2", 15);
		wordCount.insertWordCount("wordcount", "词语3", 9);
		System.out.println("insert ok!");
		/**
		 * 查看结果是否存在
		 */
		System.out.println(wordCount.isWordCountExisted("词语11"));
		/**
		 * 更新词频统计结果
		 */
		wordCount.updateWordCount("词语1", 13);
		System.out.println("update ok!");
		/**
		 * 查询词频统计结果，查询某个词频结果
		 */
		System.out.println(wordCount.selectWordCountByWord("词语1"));
		System.out.println("select one ok!");
		/**
		 * 查询词频统计结果，查询频次开前的N个结果
		 */
		System.out.println(JsonUtils.toJson(wordCount.selectWordCountTopN(2)));
		System.out.println("select top ok!");
		/**
		 * 删除某个词频统计结果
		 */
		wordCount.deleteWordCount("词语1");
		wordCount.deleteWordCount("词语2");
		wordCount.deleteWordCount("词语3");
		System.out.println("delete ok!");
	}

}
