package zx.soft.storm.analysis.redis;

/*每次连接数据库都要注册驱动，并且注册驱动只要一次即可
 * 用一次注册一次相当麻烦，写入一个类自动注册驱动
 * 只要调用此类, static块就会自动执行并且只会执行一次
*/

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DbConn {

	static String dbname = "test";
	static String user = "www-data"; //输入用户与密码
	static String pass = "www-data";
	//	static String host = "127.0.0.1";
	static String host = "192.168.3.23"; //框架怎么执行程序，192.168.3.21和3.22均需要访问3.23？
	static String encode = "?useUnicode=true&characterEncoding=utf8"; //统一编码格式为utf8，保证数据库和程序中编码一致
	static String url = "jdbc:mysql://" + host + ":3306/" + dbname + encode; //注册驱动

	static { //一旦程序调用了此类，此段代码便会自动执行
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static Connection getConn() throws SQLException //返回一个Connection对象，方便数据的插入
	{
		return DriverManager.getConnection(url, user, pass);
	}

	public static void free(final Statement st, final Connection conn) //等sql语句执行完毕，关闭连接
	{
		if (st != null) {
			try {
				st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			} finally { //如果前面抛异常，conn照样可以关闭
				if (conn != null) {
					try {
						conn.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
		/*st.close();
		conn.close();*/
	}

	public static void free(final ResultSet rs, final Statement st, final Connection conn) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		free(st, conn);
	}
}
