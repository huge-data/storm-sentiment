package zx.soft.storm.web.utils;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Test;

public class ConfigUtilTest {

	@Test
	public void testGetProps() {
		Properties props = ConfigUtil.getProps("web-server.properties");
		assertEquals("8900", props.getProperty("api.port"));
	}

	@Test(expected = Exception.class)
	public void testExceptions() throws Exception {
		Properties props = ConfigUtil.getProps("web-server1.properties");
		assertEquals("8900", props.getProperty("api.port"));
	}

}
