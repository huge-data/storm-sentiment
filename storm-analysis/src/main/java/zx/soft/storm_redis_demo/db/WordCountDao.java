package zx.soft.storm_redis_demo.db;

import java.sql.SQLException;

import com.mysql.jdbc.Connection;
import com.mysql.jdbc.PreparedStatement;

public class WordCountDao {
	private int ret = 0;

	public int insert(final WordCount s) //插入语句
	{
		try {
			Connection conn = (Connection) DbConn.getConn();

			String sql = "insert into wordcount(word,count)values(?,?)";
			PreparedStatement st = (PreparedStatement) conn.prepareStatement(sql);
			st.setString(1, s.getWord());
			st.setLong(2, s.getCount());
			ret = st.executeUpdate();

			DbConn.free(st, conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public int update(final WordCount s) //修改语句
	{
		try {
			Connection conn = (Connection) DbConn.getConn();

			String sql = "update wordcount set count=? where word=?";
			PreparedStatement st = (PreparedStatement) conn.prepareStatement(sql);
			st.setLong(1, s.getCount());
			st.setString(2, s.getWord());
			ret = st.executeUpdate();

			DbConn.free(st, conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public int delete(final WordCount s) { //删除语句
		try {
			Connection conn = (Connection) DbConn.getConn();

			String sql = "delete from wordcount where word=?";
			PreparedStatement st = (PreparedStatement) conn.prepareStatement(sql);
			st.setString(1, s.getWord());
			ret = st.executeUpdate();

			DbConn.free(st, conn);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}
}
