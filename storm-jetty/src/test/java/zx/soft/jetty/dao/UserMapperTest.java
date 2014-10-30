package zx.soft.jetty.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

import zx.soft.jetty.dao.UserMapper;
import zx.soft.jetty.model.User;
import zx.soft.jetty.model.UserQueryCondition;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/webapp/WEB-INF/applicationContext.xml")
@TransactionConfiguration(transactionManager = "transactionManager")
@Transactional
public class UserMapperTest {

	final long uid = 1;
	final String member_100 = "User [uid=1, mid=100, name=张三, nick=张三昵称, gender=0]";
	final String member_101 = "User [uid=1, mid=101, name=李四, nick=李四昵称, gender=1]";
	final String member_102 = "User [uid=1, mid=101, name=李四, nick=李四昵称, gender=1]";
	final String member_103 = "User [uid=1, mid=100, name=张三, nick=张三昵称, gender=0]";

	@Inject
	private UserMapper userMapper;

	@Test
	public void testAdd() {
		User user = new User().setUid(200).defaultValue();
		userMapper.add(user);
		assertEquals(user.toString(), userMapper.get(200, user.getMid()).toString());

		user = new User().setUid(200).setName("赵六").setNick("赵六昵称").setGender(2);
		userMapper.add(user);
		assertEquals(user.toString(), userMapper.get(200, user.getMid()).toString());
	}

	@Test
	public void testGetUser() {
		User user = userMapper.get(uid, 101);
		assertEquals(member_102, user.toString());
		assertNull(userMapper.get(uid, 12345678)); // 没有该用户
	}

	@Test
	public void testGetUsers() {
		UserQueryCondition condition = new UserQueryCondition().setUid(uid);
		List<User> users = userMapper.list(condition);

		assertEquals(2, users.size());
		assertEquals(member_102, users.get(0).toString());
	}

	@Test
	public void testGetUsers_gender() {
		UserQueryCondition condition = new UserQueryCondition().setUid(uid).setGender(1);
		List<User> users = userMapper.list(condition);
		assertEquals(1, users.size());
		assertEquals(member_101, users.get(0).toString());
	}

	@Test
	public void testQueryCountByUid() {
		int count = userMapper.queryCountByUid(1);
		assertEquals(count, 3);
	}

	@Test
	public void testUpdate() {
		User user = userMapper.get(uid, 100);
		user.setName("张三更新").setGender(1);
		userMapper.update(user);
		assertEquals(
				"User [uid=1, mid=100, is_member=0, identify=18888888888, name=张三更新, nick=张三昵称, gender=1, status=1]",
				userMapper.get(uid, 100).toString());

		user.setName(""); // 抹掉名字
		userMapper.update(user);
		assertEquals("User [uid=1, mid=100, is_member=0, identify=18888888888, name=, nick=张三昵称, gender=1, status=1]",
				userMapper.get(uid, 100).toString());
	}

	@Test
	public void toJson() {
		User user = new User().setUid(1).setMid(1).setName("zhangsan").setGender(1);
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			System.out.println(objectMapper.writeValueAsString(user));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
