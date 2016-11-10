
/**
 * 文件名   :   SomeFunctionsTest.java
 * 版权       :   <版权/公司名>
 * 描述       :   <描述>
 * @author  liliy
 * 版本       :   <版本>
 * 修改时间：      2016年10月24日
 * 修改内容：      <修改内容>
 */
package test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.xml.bind.helpers.ValidationEventImpl;

import org.junit.Test;

import com.platform.data.SshOperateData;

/**
 * <一句话功能简述> <功能详细描述>
 * 
 * @author liliy
 * @version [版本号，2016年10月24日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */

public class SomeFunctionsTest {
	// @Test
	public void switch_string_test() {
		String aString = "aaa0";
		switch (aString) {
		case "aaa":
			System.out.println("aaa");
			break;
		case "[A-z]+":
			System.out.println("bbb");
		default:
			break;
		}
	}

	// @Test
	public void regex_test() {
		String one = "Brick1";
		System.out.println(one.matches("Brick[0-9]+"));
	}

//	@Test
	public void strReplaceTest() {
		String temp = "/@home/@peng/testDta";
		temp = temp.replaceAll("@", ".");
		System.out.println(temp);
	}

	public String replaceSpace() {
		StringBuffer str = new StringBuffer("We Are Happy");
		String new_str = str.toString().replaceAll(" ", "%20");
		return new_str;
	}

//	@Test
	public void urlEndecodeTest() {
		String src = "/home/FTP_GFS_point/";
		try {
			String dst_utf8 = URLEncoder.encode(src, "utf-8");
			System.out.println(dst_utf8);
			String sst_utf8=URLDecoder.decode(dst_utf8, "utf-8");
			System.out.println(sst_utf8);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	@Test
	public void threadStopTest(){
		Thread newThread=new Thread(new Runnable() {
			public void run() {
				while(true){
					System.out.println(1);
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		newThread.start();
		try {
			Thread.sleep(5*1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		newThread.stop();
	}
}
