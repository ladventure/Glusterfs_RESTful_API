
/**
 * 文件名   :   JfinalConfig.java
 * 版权       :   <版权/公司名>
 * 描述       :   <描述>
 * @author  liliy
 * 版本       :   <版本>
 * 修改时间：      2016年10月18日
 * 修改内容：      <修改内容>
 */
package com.common.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

import javax.annotation.Resource;

import com.jfinal.config.Constants;
import com.jfinal.config.Handlers;
import com.jfinal.config.Interceptors;
import com.jfinal.config.JFinalConfig;
import com.jfinal.config.Plugins;
import com.jfinal.config.Routes;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PropKit;
import com.platform.controller.GfsController;
import com.platform.controller.HelloController;
import com.platform.entities.PostData;
import com.platform.entities.VolumeEntity;
import com.platform.glusterfs.ClusterInfo;
import com.platform.glusterfs.SetCluster;
import com.platform.glusterfs.VolumeInfo;
import com.platform.utils.Constant;
import com.platform.utils.FileOperateHelper;
import com.platform.utils.GanymedSSH;
import com.platform.utils.MyProcess;
import com.platform.utils.MyThread;

/**
 * <一句话功能简述>
 * <功能详细描述>
 * @author    liliy
 * @version   [版本号，2016年10月18日]
 * @see       [相关类/方法]
 * @since     [产品/模块版本]
 */

public class JfinalConfig extends JFinalConfig{
	@Override
	public void configConstant(Constants me) {
		
		PropKit.use("a_little_config.txt");
		me.setDevMode(PropKit.getBoolean("devMode", false));
		Constant.hostIp=PropKit.get("hostIp");
		Constant.rootPasswd=PropKit.get("rootPasswd");
		Constant.port=PropKit.getInt("port");
		Constant.allVolumeInfo=new PostData(new ArrayList<VolumeEntity>());
		Constant.clusterInfo=new PostData(new HashMap<String,String>());
		if(PropKit.getBoolean("localMode")){
		Constant.execCmdObject=new MyProcess();
		}else {
		Constant.execCmdObject = new GanymedSSH(Constant.hostIp, Constant.rootUser, Constant.rootPasswd, Constant.port);	
		}
		
	}

	@Override
	public void configHandler(Handlers me) {
	}

	@Override
	public void configInterceptor(Interceptors me) {
	}

	@Override
	public void configPlugin(Plugins me) {
	}

	@Override
	public void configRoute(Routes me) {
		me.add("/hello", HelloController.class);
		me.add("/gfs", GfsController.class);
	}
	
	@Override
	public void afterJFinalStart(){
		MyThread myThread=new MyThread(new ClusterInfo(), "setClusterInfo", Constant.clusterInfo) ;
		myThread.execFunction(Constant.clusterInfo);
		VolumeInfo volumeInfo=new VolumeInfo();
		myThread=new MyThread(volumeInfo, "setAllVolumeInfo", Constant.allVolumeInfo) ;
		myThread.execFunction(Constant.allVolumeInfo);
		myThread=new MyThread(volumeInfo, "setAllVolumeDf", Constant.allVolumeInfo) ;
		myThread.execFunction(Constant.allVolumeInfo);
		myThread=new MyThread(volumeInfo, "setAllVolumeData", Constant.allVolumeInfo) ;
		myThread.execFunction(Constant.allVolumeInfo);
		Constant.mountRecords=new SetCluster().getMountRecord(); 
	}
	
	/* (non-Javadoc)
	 * @see com.jfinal.config.JFinalConfig#beforeJFinalStop()
	 */
	@Override
	public void beforeJFinalStop() {
		// TODO Auto-generated method stub
		super.beforeJFinalStop();
		new SetCluster().saveMoutRecord(Constant.mountRecords, Constant.MountRecordPath);
	}
	public static void main(String[] args) {
		JFinal.start("WebRoot", 80, "/", 5);
//		new ThreadVolume().start();
		
	}
}
