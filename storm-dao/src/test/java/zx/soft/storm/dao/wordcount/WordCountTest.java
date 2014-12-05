package zx.soft.storm.dao.wordcount;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import zx.soft.storm.dao.common.MybatisConfig;
import zx.soft.utils.json.JsonUtils;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class WordCountTest {

	private static WordCount wordCount;

	@BeforeClass
	public static void perpare() {
		wordCount = new WordCount(MybatisConfig.ServerEnum.wordcount);
	}

	@Test(expected = Exception.class)
	public void testWC0_InsertWordCount_插入词频统计结果_抛出异常() {
		wordCount.insertWordCount("wordcount1", "词语1", 10);
	}

	@Test
	public void testWC1_InsertWordCount_插入词频统计结果() {
		long actualReturn = 0;
		try {
			wordCount.insertWordCount("wordcount", "词语1", 10);
			wordCount.insertWordCount("wordcount", "词语2", 15);
			wordCount.insertWordCount("wordcount", "词语3", 9);
		} catch (Exception e) {
			actualReturn = -1;
		}
		assertEquals(0L, actualReturn);
	}

	@Test
	public void testWC2_IsWordCountExisted_查看结果是否存在() {
		assertTrue(wordCount.isWordCountExisted("词语1"));
		assertTrue(wordCount.isWordCountExisted("词语2"));
		assertTrue(wordCount.isWordCountExisted("词语3"));
		assertFalse(wordCount.isWordCountExisted("词语33"));
	}

	@Test
	public void testWC3_UpdateWordCount_更新词频统计结果() {
		long actualReturn = 0;
		try {
			wordCount.updateWordCount("词语1", 13);
		} catch (Exception e) {
			actualReturn = -1;
		}
		assertEquals(0L, actualReturn);
	}

	@Test
	public void testWC4_SelectWordCountByWord_查询词频统计结果0查询某个词频结果() {
		assertEquals(13, wordCount.selectWordCountByWord("词语1"));
		assertEquals(15, wordCount.selectWordCountByWord("词语2"));
		assertEquals(9, wordCount.selectWordCountByWord("词语3"));
	}

	/**
	 *
	 */
	@Test
	public void testWC5_SelectWordCountByWord_查询词频统计结果0查询频次开前的N个结果() {
		assertEquals("[{\"word\":\"词语2\",\"count\":15},{\"word\":\"词语1\",\"count\":13}]",
				JsonUtils.toJsonWithoutPretty(wordCount.selectWordCountTopN(2)));
	}

	@Test
	public void testWC6_DeleteWordCount_删除某个词频统计结果() {
		wordCount.deleteWordCount("词语1");
		wordCount.deleteWordCount("词语2");
		wordCount.deleteWordCount("词语3");
	}

	@AfterClass
	public static void cleanup() {
		wordCount = null;
	}

}
