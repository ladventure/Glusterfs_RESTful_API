
/**
 * 文件名   :   FutureTest.java
 * 版权       :   <版权/公司名>
 * 描述       :   <描述>
 * @author  liliy
 * 版本       :   <版本>
 * 修改时间：      2016年10月20日
 * 修改内容：      <修改内容>
 */
package test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Test;

/**
 * <一句话功能简述>
 * <功能详细描述>
 * @author    liliy
 * @version   [版本号，2016年10月20日]
 * @see       [相关类/方法]
 * @since     [产品/模块版本]
 */

public class FutureTest {
	int variable;
	@Test
	public void testFuture(){
		final ExecutorService exec = Executors.newFixedThreadPool(1);
		Callable<String> call = new Callable<String>() {
		    public String call() throws Exception {
		        // 放入耗时操作代码块        
		        int cash = 300;
		        String name = "张三";
		        System.out.println(name + "现在有" + cash + "元存款");
		        variable=1;
		        //耗时代码块结束
		        Thread.sleep(1000 * 5);
		        variable=2;
		        return "线程执行完成";
		    }
		};
		try {
		    Future<String> future = exec.submit(call);
		    String obj = future.get(1000 * 2, TimeUnit.MILLISECONDS); // 任务处理超时时间设为1 秒
		    System.out.println("任务成功返回:" + obj);
		} catch (TimeoutException ex) {
		    System.out.println("处理超时啦....");
		    variable=4;
		    
		} catch (Exception e) {
		    System.out.println("处理失败.");
		    e.printStackTrace();
		}
		System.out.println(variable);
		exec.shutdown();  // 关闭线程池
	}
	
}
