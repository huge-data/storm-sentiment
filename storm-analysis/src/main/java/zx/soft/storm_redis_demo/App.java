package zx.soft.storm_redis_demo;

/**
 * Hello world!
 *
 */
public class App {
	public static void main(String[] args) {
		String str = "!!！？？!!!!%*）%￥！KTV去符号标号！！当然。!!..**半角";
		// System.out.println(str);
		//		System.out.println(str.replaceAll("\\pP", ""));
		System.out.println(str.replaceAll("\\p{P}", ""));
		System.out.println("Hello World!");

		String s = "哈哈!@#W";
		s = s.replaceAll("\\pP", "");
		System.out.print(s);
	}
}
