package com.platform.controller;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.jfinal.core.Controller;
import com.mchange.v2.async.StrandedTaskReporting;
import com.mysql.fabric.xmlrpc.base.Array;
import com.platform.entities.FolderNode;
import com.platform.entities.PostData;
import com.platform.entities.VolumeEntity;
import com.platform.glusterfs.ClusterInfo;
import com.platform.glusterfs.SetCluster;
import com.platform.glusterfs.SetVolume;
import com.platform.glusterfs.VolumeInfo;
import com.platform.utils.Constant;
import com.platform.utils.MyThread;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;


public class GfsController extends Controller {
	public Logger log = Logger.getLogger(GfsController.class);

	/**
	 * 首页-"/gfs"下没有匹配到的路径时的请求方法
	 */
	public void index() {
		renderText("hello gfs !");
	}

	/**
	 * 
	 * 获取集群信息api <功能详细描述>
	 * 返回data为一个Map，包含集群节点和节点状态
	 * @see [类、类#方法、类#成员]
	 */
	public void getClusterInfo() {

		new ClusterInfo().controllerGetClusterInfo(Constant.timeout);
		JSONObject jsondata = JSONObject.fromObject(Constant.clusterInfo);
		renderJson(jsondata);
	}

	/**
	 * 获取所有volume api <一句话功能简述> <功能详细描述>
	 * 返回所有volume信息和volume数据
	 * @see [类、类#方法、类#成员]
	 */
	public void getAllVolume() {
		JSONObject jsondata = null;
		try {
			new VolumeInfo().controllerGetAllvolumeInfo(Constant.timeout);
			List<VolumeEntity> volumes = (List<VolumeEntity>) (Constant.allVolumeInfo.getData());
			List<MyThread> myThreads = new ArrayList<MyThread>();
			for (VolumeEntity one : volumes) {
				MyThread myThread3 = new MyThread(new VolumeInfo(), "setAllVolumeData", one.getName(),
						Constant.allVolumeInfo);
				myThread3.execFunction(Constant.allVolumeInfo);
//				myThreads.add(myThread3);
			}
//			for (MyThread one : myThreads) {
//				one.join();
//			}
			jsondata = JSONObject.fromObject(Constant.allVolumeInfo);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			jsondata = JSONObject.fromObject(e);
		}
		renderJson(jsondata);
	}

	/**
	 * 获取volume信息api <一句话功能简述> <功能详细描述>
	 * 
	 * @see [类、类#方法、类#成员]
	 */
	public void getAllVolumeInfo() {
		try {

			MyThread myThread1 = new MyThread(new VolumeInfo(), "setAllVolumeInfo", Constant.allVolumeInfo);
			MyThread myThread2 = new MyThread(new VolumeInfo(), "setAllVolumeDf", Constant.allVolumeInfo);
			myThread1.start();
			myThread1.join();
			myThread2.start();
			myThread2.join();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JSONObject jsondata = JSONObject.fromObject(Constant.allVolumeInfo);
		renderJson(jsondata);
	}

	/**
	 * 获取一个volume的api <一句话功能简述> <功能详细描述>
	 * 
	 * @see [类、类#方法、类#成员]
	 */
	public void getOneVolume() {
		VolumeEntity onEntity = null;
		PostData resData=new PostData();
		try {
			
			String volumeName = getPara(0, "");
			MyThread myThread1 = new MyThread(new VolumeInfo(), "setAllVolumeInfo", resData);
			MyThread myThread2 = new MyThread(new VolumeInfo(), "setAllVolumeDf", resData);
			MyThread myThread3 = new MyThread(new VolumeInfo(), "setOneVolumeData", volumeName, resData);
			myThread1.start();
			myThread1.join();
			myThread2.start();
			myThread3.start();
			myThread2.join();
			myThread3.join();
			List<VolumeEntity> volumes = (List<VolumeEntity>) (Constant.allVolumeInfo.getData());
			for (VolumeEntity one : volumes) {
				if (one.getName().equals(volumeName)) {
					onEntity = one;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		resData.setData(onEntity);
		JSONObject jsondata = JSONObject.fromObject(resData);
		renderJson(jsondata);

	}

	/**
	 * 获取一个volume信息的api <一句话功能简述> <功能详细描述>
	 * 
	 * @see [类、类#方法、类#成员]
	 */
	public void getOneVolumeInfo() {
		VolumeEntity onEntity = null;
		try {
			String volumeName = getPara();
			MyThread myThread1 = new MyThread(new VolumeInfo(), "setAllVolumeInfo", Constant.allVolumeInfo);
			MyThread myThread2 = new MyThread(new VolumeInfo(), "setAllVolumeDf", Constant.allVolumeInfo);

			myThread1.start();
			myThread1.join();
			myThread2.start();

			myThread2.join();

			List<VolumeEntity> volumes = (List<VolumeEntity>) (Constant.allVolumeInfo.getData());
			for (VolumeEntity one : volumes) {
				if (one.getName().equals(volumeName)) {
					onEntity = one;
				}
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JSONObject jsondata = JSONObject.fromObject(onEntity);
		renderJson(jsondata);

	}

	/**
	 * 
	 * 向添加节点 <功能详细描述>
	 * 
	 * @see [类、类#方法、类#成员]
	 */
	public void probePeer() {
		String peerip = getPara(0, "").replaceAll("@", ".");
		SetCluster setCluster = new SetCluster();
		PostData resData = new PostData();
		int resCode = setCluster.probePeer(resData, peerip);
		resData.setData(resCode);
		JSONObject jsondata = JSONObject.fromObject(resData);
		renderJson(jsondata);

	}

	/**
	 * 
	 * 解除集群某个节点 <功能详细描述>
	 * 
	 * @see [类、类#方法、类#成员]
	 */
	public void detachPeer() {
		String peerip = getPara(0, "").replaceAll("@", ".");
		SetCluster setCluster = new SetCluster();
		PostData resData = new PostData();
		int resCode = setCluster.detachPeer(resData, peerip);
		resData.setData(resCode);
		JSONObject jsondata = JSONObject.fromObject(resData);
		renderJson(jsondata);

	}

	/**
	 * 创建volume,从url传入参数，volumeName, count, type, bricks,
	 * mountPoint，其中bricks以~分隔,地址点用@代替；0:参数不合格； 1:可以创建 ;-1：brick的ip不在集群中或者未连接;
	 * -2 -3 -4:类型与brick数目不匹配 ; -5 :volumeName 已经存在；-6：挂载点存在且不为空，不能作为挂载点；
	 * -7：未知错误 <一句话功能简述> <功能详细描述>
	 * 
	 * @see [类、类#方法、类#成员]
	 */
	public void createVolume() {
		SetVolume setVolume = new SetVolume();
		PostData resData = new PostData();
		try {
			String[] parms = URLDecoder.decode(getPara(), "utf-8").replaceAll("@", ".").split("-");
			if (parms.length != 5) {
				resData.setData(0);
				String mess = "0001 参数不合格";
				log.error(mess);
				resData.pushExceptionsStack(mess);
			}
			String volumeName = parms[0];
			int count = 0;
			try {
				count = Integer.parseInt(parms[1]);
			} catch (Exception e) {
				// TODO: handle exception
			}

			String type = parms[2];
			String strBricks = parms[3];
			String mountPoint = parms[4];
			String split_strBricks[] = strBricks.split("~");
			List<String> bricks = Arrays.asList(split_strBricks);

			int resCode = setVolume.createVolume(volumeName, count, type, bricks, mountPoint, resData);
			resData.setData(resCode);
		} catch (Exception e) {
			// TODO: handle exception
		}
		JSONObject jsondata = JSONObject.fromObject(resData);
		renderJson(jsondata);
	}

	/**
	 * 删除volume，传入volume的名称 <一句话功能简述> <功能详细描述>
	 * 
	 * @see [类、类#方法、类#成员]
	 */
	public void deleteVolume() {
		SetVolume setVolume = new SetVolume();
		PostData resData = new PostData();
		String volumeName = getPara(0, "");
		int resCode = setVolume.deleteVolume(volumeName, resData);
		resData.setData(resCode);
		JSONObject jsondata = JSONObject.fromObject(resData);
		renderJson(jsondata);
	}

	/**
	 * 停止volume，传入volumeName <一句话功能简述> <功能详细描述>
	 * 
	 * @see [类、类#方法、类#成员]
	 */
	public void stopVolume() {
		SetVolume setVolume = new SetVolume();
		PostData resData = new PostData();
		String volumeName = getPara(0, "");
		int resCode = setVolume.stopVolume(volumeName, resData);
		resData.setData(resCode);
		JSONObject jsondata = JSONObject.fromObject(resData);
//		System.out.println(jsondata.toString());
		renderJson(jsondata);
	}

	/**
	 * 开始volume，传入volumeName <一句话功能简述> <功能详细描述>
	 * 
	 * @see [类、类#方法、类#成员]
	 */
	public void startVolume() {
		SetVolume setVolume = new SetVolume();
		PostData resData = new PostData();
		String volumeName = getPara(0, "");
		int resCode = setVolume.startVolume(volumeName, resData);
		resData.setData(resCode);
		JSONObject jsondata = JSONObject.fromObject(resData);
		renderJson(jsondata);
	}
}
