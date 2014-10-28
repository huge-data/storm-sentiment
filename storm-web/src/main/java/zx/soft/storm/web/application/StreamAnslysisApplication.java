package zx.soft.storm.web.application;

import java.util.List;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

import zx.soft.storm.web.resource.StreamAnalysisResource;

/**
 * 流式数据分析应用类
 * 
 * @author wanggang
 *
 */
public class StreamAnslysisApplication extends Application {

	//	private static Cache cache;

	//	public static final String SITE_GROUPS = "sent:site:groups";

	public StreamAnslysisApplication() {
		//		cache = CacheFactory.getInstance();
	}

	@Override
	public Restlet createInboundRoot() {
		Router router = new Router(getContext());
		router.attach("/wordcount", StreamAnalysisResource.class);
		return router;
	}

	/**
	 * 插入站点组合数据
	 */
	public void insertStreamData(List<String> data) {
		for (String d : data) {
			System.out.println(d);
			// 设置hash表
			//			cache.hset(SITE_GROUPS, CheckSumUtils.getMD5(sites), sites);
		}
	}

	public void close() {
		//		cache.close();
	}

}
