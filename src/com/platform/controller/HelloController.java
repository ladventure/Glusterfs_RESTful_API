package com.platform.controller;

import java.util.ArrayList;
import java.util.List;
import net.sf.json.JSONObject;
import com.jfinal.core.Controller;
import com.platform.entities.FolderNode;
import com.platform.entities.VolumeEntity;
import com.platform.form.baseForm;
import com.platform.utils.Constant;

public class HelloController extends Controller {
	
	public void index() {
		setAttr("volumeList", (List<VolumeEntity>)Constant.allVolumeInfo.getData());
		render("/index/index.jsp");
//		renderText("hello jfinal index !");
	}
	
	public void tojson() {
		renderText("hello jfinal tojson !");
	}
	
	public void gain() {
		baseForm form = new baseForm();
		form.setId("12");
		form.setName("n3");
		
		String base = getPara("jsondata");
		System.err.println(base);
		if (null != base) {
			FolderNode f = new FolderNode();
			f.setIsFolder(2);
			f.setName("1");
			f.setPath("/da");
			FolderNode f1 = new FolderNode();
			f1.setIsFolder(2);
			f1.setName("1");
			f1.setPath("/da");
			List<FolderNode> lis = new ArrayList<FolderNode>();
			lis.add(f1);
			f.setChildNodes(lis);
			JSONObject jsondata = JSONObject.fromObject(f);
			renderJson(jsondata.toString());
		}
		else {
			renderText("hello jfinal gaindata ! ");
		}
	}
	

}
