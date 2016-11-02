
/**
 * 文件名   :   PostObj.java
 * 版权       :   <版权/公司名>
 * 描述       :   <描述>
 * @author  liliy
 * 版本       :   <版本>
 * 修改时间：      2016年10月18日
 * 修改内容：      <修改内容>
 */
package com.platform.entities;

import java.util.List;
import java.util.Stack;

/**
 * <一句话功能简述>
 * <功能详细描述>
 * @author    liliy
 * @version   [版本号，2016年10月18日]
 * @see       [相关类/方法]
 * @since     [产品/模块版本]
 */

public class PostData {
	private Object data;
	private Stack<MyException> exceptionsStack;
	/**
	 * <一句话功能简述>
	 * <功能详细描述>
	 * @see [类、类#方法、类#成员]
	 */
	public PostData() {
		// TODO Auto-generated constructor stub
		exceptionsStack=new Stack<>();
	}
	public PostData(Object data) {
		// TODO Auto-generated constructor stub
		this.data=data;
		exceptionsStack=new Stack<>();
	}
	/**
	 * @return the data
	 */
	public Object getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(Object data) {
		this.data = data;
	}
	/**
	 * 
	 * <一句话功能简述>
	 * <功能详细描述>
	 * @param e
	 * @see [类、类#方法、类#成员]
	 */
	public void pushExceptionsStack(String mess){
		MyException myException=new MyException(mess);
		exceptionsStack.push(myException);
	}
	public void pushExceptionsStackList(List<String> messs){
		if(messs==null)return;
		for(String mess:messs){
		MyException myException=new MyException(mess);
		exceptionsStack.push(myException);
		}
	}
	/**
	 * 
	 * <一句话功能简述>
	 * <功能详细描述>
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public MyException popExceptionsStack(){
		if (isEmptyExceptionsStack()){
			return null;
		}
		return exceptionsStack.pop();
	}
	/**
	 * 
	 * <一句话功能简述>
	 * <功能详细描述>
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public boolean isEmptyExceptionsStack(){
		return exceptionsStack.empty();
	}
	/**
	 * 
	 * <一句话功能简述>
	 * <功能详细描述>
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public Stack<MyException> getExceptionsStack() {
		return exceptionsStack;
	}
}


