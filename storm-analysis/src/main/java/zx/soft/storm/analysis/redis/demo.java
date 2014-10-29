package zx.soft.storm.analysis.redis;

public class demo {

	public static void main(final String[] args) {
		// TODO Auto-generated method stub
		WordCount wc = new WordCount("插入中文数据测试", 4);
		WordCountDao wcd = new WordCountDao();
		if (wcd.insert(wc) > 0) {
			System.out.println("正确插入数据！");
		}
	}

}
