package zx.soft.storm.web.application;

import java.util.List;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.storm.core.redis.RedisPersist;
import zx.soft.storm.dao.common.MybatisConfig;
import zx.soft.storm.dao.domain.WordAndCount;
import zx.soft.storm.dao.wordcount.WordCount;
import zx.soft.storm.web.resource.StreamAnalysisResource;
import zx.soft.storm.web.resource.WordCountResource;

/**
 * 流式数据分析应用类
 *
 * @author wanggang
 *
 */
public class StreamAnslysisApplication extends Application {

	private static Logger logger = LoggerFactory.getLogger(StreamAnslysisApplication.class);

	// 持久化到Redis类
	private static RedisPersist redisPersist;

	// 数据库操作类
	private static WordCount wordCount;

	public static final String STREAM_WORD_COUNT_KEY = "stream:word:count";

	public StreamAnslysisApplication() {
		redisPersist = new RedisPersist(STREAM_WORD_COUNT_KEY);
		wordCount = new WordCount(MybatisConfig.ServerEnum.wordcount);
	}

	public void close() {
		redisPersist.close();
	}

	@Override
	public Restlet createInboundRoot() {
		Router router = new Router(getContext());
		// POST
		router.attach("/wordcount", StreamAnalysisResource.class);
		// GET
		router.attach("/wordcount/{topn}", WordCountResource.class);
		return router;
	}

	/**
	 * 持久化到Redis集群中
	 */
	public void insertStreamData(List<String> data) {
		logger.info("insert datas' size=" + data.size());
		redisPersist.addValues(data.toArray(new String[data.size()]));
	}

	/**
	 * 查询词频统计结果，查询某个词频结果
	 */
	public int selectWordCountByWord(String word) {
		return wordCount.selectWordCountByWord(word);
	}

	/**
	 * 查询词频统计结果，查询频次开前的N个结果
	 */
	public List<WordAndCount> selectWordCountTopN(int N) {
		return wordCount.selectWordCountTopN(N);
	}

}
