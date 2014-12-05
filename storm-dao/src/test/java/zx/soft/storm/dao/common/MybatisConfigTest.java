package zx.soft.storm.dao.common;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;

public class MybatisConfigTest {

	@Test
	public void test() {
		SqlSessionFactory sqlSessionFactory = MybatisConfig.getSqlSessionFactory(MybatisConfig.ServerEnum.wordcount);
		SqlSession session = sqlSessionFactory.openSession();
	}

}
