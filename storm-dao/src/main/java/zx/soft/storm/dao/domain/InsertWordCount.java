package zx.soft.storm.dao.domain;

public class InsertWordCount {

	private String tablename;
	private String word;
	private int count;

	public InsertWordCount() {
		//
	}

	public InsertWordCount(String tablename, String word, int count) {
		this.tablename = tablename;
		this.word = word;
		this.count = count;
	}

	public String getTablename() {
		return tablename;
	}

	public void setTablename(String tablename) {
		this.tablename = tablename;
	}

	public String getWord() {
		return word;
	}

	public void setWord(String word) {
		this.word = word;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
