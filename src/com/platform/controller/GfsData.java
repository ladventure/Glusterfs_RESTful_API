
/**
 * 文件名   :   GfsData.java
 * 版权       :   <版权/公司名>
 * 描述       :   <描述>
 * @author  liliy
 * 版本       :   <版本>
 * 修改时间：      2016年11月6日
 * 修改内容：      <修改内容>
 */
package com.platform.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import com.jfinal.core.Controller;
import com.platform.data.LocalOperateData;
import com.platform.data.OperateData;
import com.platform.data.SshOperateData;
import com.platform.entities.PostData;
import com.platform.utils.Constant;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

/**
 * <一句话功能简述> <功能详细描述>
 * 
 * @author liliy
 * @version [版本号，2016年11月6日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */

public class GfsData extends Controller {
	OperateData operateData;

	/**
	 * <一句话功能简述> <功能详细描述>
	 * 
	 * @see [类、类#方法、类#成员]
	 */
	public GfsData() {
		// TODO Auto-generated constructor stub
		super();
		if (Constant.localMode) {
			operateData = new LocalOperateData();
		} else {
			operateData = new SshOperateData();
		}
	}

	/**
	 * 删除数据 <一句话功能简述> <功能详细描述> 通过url传入参数，需要删除的路径
	 * 
	 * @see [类、类#方法、类#成员]
	 */
	public void removeData() {
		int status = 0;
		PostData resData = new PostData();
		try {
			String fileName = URLDecoder.decode(getPara(), "utf-8").replaceAll("@", ".");

			status = operateData.removeData(fileName, resData);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		resData.setData(status);
		JSONObject jsondata = JSONObject.fromObject(resData);
		renderJson(jsondata);
	}

	/**
	 * 复制数据
	 * <一句话功能简述> <功能详细描述> 通过url传入参数，源文件路径与目标文件路径用~隔开，.用@符号代替;第一个为源路径，第二个为目的路径
	 * 
	 * @see [类、类#方法、类#成员]
	 */
	public void copyData() {
		int status = 0;
		PostData resData = new PostData(status);
		try {
			String fileNames[] = URLDecoder.decode(getPara(), "utf-8").replaceAll("@", ".").split("~");

			String sourceName = fileNames[0];
			String destName = fileNames[1];
			status = operateData.copyData(sourceName, destName, resData);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		resData.setData(status);
		JSONObject jsondata = JSONObject.fromObject(resData);
		renderJson(jsondata);
	}

	/**
	 * 获取数据操作任务列表 <一句话功能简述> <功能详细描述>
	 * 
	 * @see [类、类#方法、类#成员]
	 */
	public void getOperateTasks() {
		PostData resData = new PostData(Constant.copyDataTask);
		JsonConfig config = new JsonConfig();
		config.setExcludes(new String[] { "moveThread" });// 除去emps属性

		JSONObject jsondata = JSONObject.fromObject(resData, config);
//		JSONArray json = new JSONArray();
		renderJson(jsondata);
	}
}
