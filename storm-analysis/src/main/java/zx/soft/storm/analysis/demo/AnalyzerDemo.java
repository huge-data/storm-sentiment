package zx.soft.storm.analysis.demo;

import java.util.HashMap;
import java.util.Map.Entry;

import zx.soft.storm.analysis.utils.AnalyzerTool;

public class AnalyzerDemo {

	private static AnalyzerTool analyzerTool;

	public static void main(final String[] args) {
		analyzerTool = new AnalyzerTool();
		String str = "I like to build things. Coding personal projects since the 7th grade has given me a great appreciation "
				+ "for perpetual self-learning through doing.I am a huge a dvocate of open source, AnalyzerDemo driven development"
				+ " (untested code is broken code) and code review (unreviewed code is bad code). ";
		str = "综合消息,近日,湖南湘潭县妇幼保健院一产妇死亡, " + "经媒体报道引发关注。据“央视新闻”消息,湘潭县卫生局称,胎儿出 生后产妇出现呕吐呛咳,院方立即抢救,但产妇因羊水栓塞引 "
				+ "发多器官功能衰竭,抢救无效死亡。目前湘潭县卫生局 、司法局已介入调查。据此前媒体报道,10 日上午11点,医生给张女士"
				+ "做了产前检查,胎位正常,由于胎儿较重,医生 建议家属做剖腹产。随后,张女士被推进五楼的手术室。12点05分,手术室护士告"
				+ "诉家属,产妇顺利产下宝宝。半个 小时后,护士通知守在手术室外的刘先生,称产妇出现大出血情况,刘先生说,1个小时后,护士又"
				+ "通知他,称出血情况 没止住,要其赶紧去买止血药。下午5点左 右,一名护士通知刘先生,称产妇大出血,现在必须切除子宫,需要"
				+ "签字。晚上9点,刘先生家人听到有人在议论,说产妇已经死了。情急之下的刘先生上前使劲拍打手术室的大 门,这时,出来一 个" + "自称是代理院长的人,说产妇仍在抢救,有脉搏。";
		System.out.println(str.toLowerCase());
		testAnalyzerEngLish(str);
		str = str.replaceAll("\\p{Punct}", " ");
		testAnalyzerEngLish(str);
	}

	public static void testAnalyzerEngLish(final String str) {
		HashMap<String, Integer> result = analyzerTool.getWordAndCounts(str);
		System.out.println(result.toString());
		for (Entry<String, Integer> temp : result.entrySet()) {
			System.out.println("key: " + temp.getKey());
			System.out.println("value: " + temp.getValue());
		}
		System.out.println("中文lowercase".toLowerCase());
	}

}
