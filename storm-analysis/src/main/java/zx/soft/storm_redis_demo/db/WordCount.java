package zx.soft.storm_redis_demo.db;

public class WordCount {
	//将要插入的数据封装成一个对象，并且生成每个属性的get和set方法
	private String word;
	private long count;

	public WordCount() {
	}

	public WordCount(final String word, final long count) {
		this.word = word;
		this.count = count;
	}

	public String getWord() {
		return word;
	}

	public void setWord(final String word) {
		this.word = word;
	}

	public long getCount() {
		return count;
	}

	public void setCount(final long count) {
		this.count = count;
	}
}
