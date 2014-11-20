package zx.soft.storm.dao.wordcount;

import java.util.List;

import org.apache.ibatis.annotations.Arg;
import org.apache.ibatis.annotations.ConstructorArgs;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.ResultType;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import zx.soft.storm.dao.domain.InsertWordCount;
import zx.soft.storm.dao.domain.WordAndCount;

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
	 * 更新词频统计结果
	 */
	// @ConstructorArgs中指定的参数为：返回结果对象的构造函数参数
	@Update("UPDATE `wordcount` SET `count` = #{count} WHERE `word` = #{word}")
	public void updateWordCount(@Param("word") String w, @Param("count") int c);

	/**
	 * 更新词频统计数据, 加1
	 */
	// @ConstructorArgs中指定的参数为：返回结果对象的构造函数参数
	@Update("UPDATE `wordcount` SET `count` = count + 1  WHERE `word` = #{word}")
	public void addWordCountByOne(@Param("word") String w);


	/**
	 * 判断某个词频结果存在于否
	 */
	@Select("SELECT `word` FROM `wordcount` WHERE `word` = #{word}")
	public String isWordCountExisted(@Param("word") String w);

	/**
	 * 查询词频统计结果，查询某个词频结果
	 */
	@Select("SELECT `count` FROM `wordcount` WHERE `word` = #{word}")
	public int selectWordCountByWord(@Param("word") String w);

	/**
	 * 查询词频统计结果，查询频次开前的N个结果
	 */
	@Select("SELECT `word`,`count` FROM `wordcount` ORDER BY `count` DESC LIMIT #{N}")
	@ConstructorArgs(value = { @Arg(column = "word", javaType = String.class),
			@Arg(column = "count", javaType = Integer.class) })
	@ResultType(value = WordAndCount.class)
	public List<WordAndCount> selectWordCountTopN(int N);

	/**
	 * 删除某个词频统计结果
	 */
	@Delete("DELETE FROM `wordcount` WHERE `word` = #{word}")
	public void deleteWordCount(@Param("word") String w);

}
