
/**
 * 文件名   :   Support.java
 * 版权       :   <版权/公司名>
 * 描述       :   <描述>
 * @author  liliy
 * 版本       :   <版本>
 * 修改时间：      2016年9月12日
 * 修改内容：      <修改内容>
 */
package com.platform.utils;

import java.util.regex.Pattern;

/**
 * <一句话功能简述>
 * <功能详细描述>
 * @author    liliy
 * @version   [版本号，2016年9月12日]
 * @see       [相关类/方法]
 * @since     [产品/模块版本]
 */

public class Support {
	 public static boolean checkIP(String str) {
	        Pattern pattern = Pattern
	                .compile("^((\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]"
	                        + "|[*])\\.){3}(\\d|[1-9]\\d|1\\d\\d|2[0-4]\\d|25[0-5]|[*])$");
	        return pattern.matcher(str).matches();
	    }
}
