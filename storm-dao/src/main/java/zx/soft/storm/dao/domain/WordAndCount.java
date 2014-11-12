package zx.soft.storm.dao.domain;

public class WordAndCount {

	private String word;
	private int count;

	public WordAndCount() {
		//
	}

	public WordAndCount(String word, Integer count) {
		this.word = word;
		this.count = count;
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
