package zx.soft.storm.web.server;

import java.util.Properties;

import org.restlet.Component;
import org.restlet.data.Protocol;

import zx.soft.negative.sentiment.jackson.ReplaceConvert;
import zx.soft.storm.web.application.StreamAnslysisApplication;
import zx.soft.utils.config.ConfigUtil;

/**
 * 流式数据服务，统计词频
 * 
 * @author wanggang
 * 示例：
 *     1、http://localhost:8100/stream/wordcount
 *     POST: [ "测试数据1", "测试数据2" ]
 *     2、http://localhost:8100/stream/wordcount/{topn}
 *     GET
 *
 */
public class StreamAnalysisServer {

	private final Component component;
	private final StreamAnslysisApplication siteApplication;

	private final int PORT;

	public StreamAnalysisServer() {
		Properties props = ConfigUtil.getProps("web-server.properties");
		PORT = Integer.parseInt(props.getProperty("api.port"));
		component = new Component();
		siteApplication = new StreamAnslysisApplication();
	}

	/**
	 * 主函数
	 */
	public static void main(String[] args) {

		StreamAnalysisServer server = new StreamAnalysisServer();
		server.start();

	}

	public void start() {
		component.getServers().add(Protocol.HTTP, PORT);
		try {
			component.getDefaultHost().attach("/stream", siteApplication);
			ReplaceConvert.configureJacksonConverter();
			component.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public void stop() {
		try {
			component.stop();
			siteApplication.close();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
