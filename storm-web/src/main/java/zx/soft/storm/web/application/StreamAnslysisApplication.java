package zx.soft.storm.web.application;

import java.util.List;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.storm.core.redis.RedisPersist;
import zx.soft.storm.web.resource.StreamAnalysisResource;

/**
 * 流式数据分析应用类
 * 
 * @author wanggang
 *
 */
public class StreamAnslysisApplication extends Application {

	private static Logger logger = LoggerFactory.getLogger(StreamAnslysisApplication.class);

	private static RedisPersist redisPersist;

	public static final String STREAM_WORD_COUNT_KEY = "stream:word:count";

	public StreamAnslysisApplication() {
		redisPersist = new RedisPersist(STREAM_WORD_COUNT_KEY);
	}

	@Override
	public Restlet createInboundRoot() {
		Router router = new Router(getContext());
		router.attach("/wordcount", StreamAnalysisResource.class);
		return router;
	}

	/**
	 * 持久化到Redis集群中
	 */
	public void insertStreamData(List<String> data) {
		logger.info("insert datas' size=" + data.size());
		redisPersist.addValues(data.toArray(new String[data.size()]));
	}

	public void close() {
		redisPersist.close();
	}

}
