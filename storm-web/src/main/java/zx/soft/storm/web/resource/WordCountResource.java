package zx.soft.storm.web.resource;

import java.util.List;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.storm.dao.domain.WordAndCount;
import zx.soft.storm.web.application.StreamAnslysisApplication;
import zx.soft.storm.web.domain.ErrorResponse;
import zx.soft.utils.chars.JavaPattern;
import zx.soft.utils.codec.URLCodecUtils;

/**
 * 流式数据
 * 
 * @author wanggang
 *
 */
public class WordCountResource extends ServerResource {

	private static Logger logger = LoggerFactory.getLogger(WordCountResource.class);

	private StreamAnslysisApplication application;

	private String topn = "";

	@Override
	public void doInit() {
		application = (StreamAnslysisApplication) getApplication();
		topn = (String) this.getRequest().getAttributes().get("topn");
		logger.info("Request Url: " + URLCodecUtils.decoder(getReference().toString(), "utf-8") + ".");
	}

	@Get("json")
	public Object retrivalTopNWordCount() {
		if (topn == null || topn.length() == 0 || !JavaPattern.isAllNum(topn)) {
			logger.error("Params `type` or `datestr` is null.");
			return new ErrorResponse.Builder(-1, "params error!").build();
		}
		List<WordAndCount> wordCounts = application.selectWordCountTopN(Integer.parseInt(topn));
		return wordCounts;
	}

}
