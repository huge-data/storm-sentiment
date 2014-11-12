package zx.soft.storm.dao.wordcount;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zx.soft.storm.dao.common.MybatisConfig;
import zx.soft.storm.dao.domain.InsertWordCount;
import zx.soft.storm.dao.domain.WordAndCount;

/**
 * 词频统计封装
 * 
 * @author wanggang
 *
 */
public class WordCount {

	private static Logger logger = LoggerFactory.getLogger(WordCount.class);

	private static SqlSessionFactory sqlSessionFactory;

	public WordCount(MybatisConfig.ServerEnum server) {
		try {
			sqlSessionFactory = MybatisConfig.getSqlSessionFactory(server);
		} catch (RuntimeException e) {
			logger.error("WordCount RuntimeException: " + e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * 插入词频统计结果
	 */
	public void insertWordCount(String tablename, String word, int count) {
		try (SqlSession sqlSession = sqlSessionFactory.openSession();) {
			WordCountDao wordCountDao = sqlSession.getMapper(WordCountDao.class);
			wordCountDao.insertWordCount(new InsertWordCount(tablename, word, count));
		}
	}

	/**
	 * 更新词频统计结果
	 */
	public void updateWordCount(String word, int count) {
		try (SqlSession sqlSession = sqlSessionFactory.openSession();) {
			WordCountDao wordCountDao = sqlSession.getMapper(WordCountDao.class);
			wordCountDao.updateWordCount(word, count);
		}
	}

	/**
	 * 查询词频统计结果，查询某个词频结果
	 */
	public int selectWordCountByWord(String word) {
		try (SqlSession sqlSession = sqlSessionFactory.openSession();) {
			WordCountDao wordCountDao = sqlSession.getMapper(WordCountDao.class);
			return wordCountDao.selectWordCountByWord(word);
		}
	}

	/**
	 * 查询词频统计结果，查询频次开前的N个结果
	 */
	public List<WordAndCount> selectWordCountTopN(int N) {
		try (SqlSession sqlSession = sqlSessionFactory.openSession();) {
			WordCountDao wordCountDao = sqlSession.getMapper(WordCountDao.class);
			return wordCountDao.selectWordCountTopN(N);
		}
	}

	/**
	 * 删除某个词频统计结果
	 */
	public void deleteWordCount(String word) {
		try (SqlSession sqlSession = sqlSessionFactory.openSession();) {
			WordCountDao wordCountDao = sqlSession.getMapper(WordCountDao.class);
			wordCountDao.deleteWordCount(word);
		}
	}

}
