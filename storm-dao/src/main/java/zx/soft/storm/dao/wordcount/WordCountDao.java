package zx.soft.storm.dao.wordcount;

import java.util.HashMap;

import org.apache.ibatis.annotations.Arg;
import org.apache.ibatis.annotations.ConstructorArgs;
import org.apache.ibatis.annotations.Insert;

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
	 * 测试数据
	 */
	@Insert("INSERT INTO `test` (`name`,`text`) VALUES (#{name}, #{text})")
	@ConstructorArgs(value = { @Arg(column = "name", javaType = String.class),
			@Arg(column = "text", javaType = String.class) })
	public void insertTestData(HashMap<String, String> data);

}
