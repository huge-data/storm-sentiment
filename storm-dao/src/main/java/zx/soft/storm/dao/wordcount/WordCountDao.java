package zx.soft.storm.dao.wordcount;

import zx.soft.storm.dao.domain.InsertWordCount;

/**
 * 词频统计接口
 * 
 * @author wanggang
 *
 */
public interface WordCountDao {

	/**
	 * 插入词频统计结果
	 */
	public void insertWordCount(InsertWordCount insertWordCount);

	/**
	 * 
	 */

}
