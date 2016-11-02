
/**
 * 文件名   :   StringHelper.java
 * 版权       :   <版权/公司名>
 * 描述       :   <描述>
 * @author  liliy
 * 版本       :   <版本>
 * 修改时间：      2016年10月24日
 * 修改内容：      <修改内容>
 */
package com.platform.utils;

/**
 * <一句话功能简述>
 * <功能详细描述>
 * @author    liliy
 * @version   [版本号，2016年10月24日]
 * @see       [相关类/方法]
 * @since     [产品/模块版本]
 */

public class StringHelper {
	public static String getMapKey(String mapString){
		if(!mapString.contains(":")){
			return "";
		}
		String[] key_value=mapString.split(":");
		if(key_value.length==0){
			return "";
		}
		return key_value[0].trim();
	}
	public static String getMapValue(String mapString){
		if(!mapString.contains(":")){
			return "";
		}
		String[] key_value=mapString.split(":");
		if(key_value.length!=2){
			return "";
		}
		return key_value[1].trim();
	}
}
