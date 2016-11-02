
/**
 * 文件名   :   MyException.java
 * 版权       :   <版权/公司名>
 * 描述       :   <描述>
 * @author  liliy
 * 版本       :   <版本>
 * 修改时间：      2016年10月19日
 * 修改内容：      <修改内容>
 */
package com.platform.entities;

/**
 * <一句话功能简述>
 * <功能详细描述>
 * @author    liliy
 * @version   [版本号，2016年10月19日]
 * @see       [相关类/方法]
 * @since     [产品/模块版本]
 */

public class MyException {
	private String mess;
	/**
	 * <一句话功能简述>
	 * <功能详细描述>
	 * @see [类、类#方法、类#成员]
	 */
	public MyException(String mess) {
		// TODO Auto-generated constructor stub
		setMess(mess);
	}
	
	public void setMess(String mess){
		this.mess=mess;
	}
	public String getMess(){
		return this.mess;
	}
}