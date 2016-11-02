
/**
 * @author 李乾坤
 * 进行volume的一系列操作，如创建、开启停止volume，为volume添加或删除brick
 */
package com.platform.glusterfs;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;

import com.platform.entities.PostData;
import com.platform.entities.VolumeEntity;
import com.platform.utils.Constant;
import com.platform.utils.MyThread;

public class SetVolume {
	public Logger log = Logger.getLogger(SetVolume.class);

	/**
	 * 创建volume 返回值：创建并挂载成功 1 1:可以创建 ;-1：brick的ip不在集群中或者未连接; -2 -3
	 * -4:类型与brick数目不匹配 ; -5 :volumeName 已经存在；-6：挂载点存在且不为空，不能作为挂载点； -7：未知错误
	 * 
	 * @param volumeName
	 * @param count
	 * @param type
	 * @param bricks
	 * @param mountPoint
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public int createVolume(String volumeName, int count, String type, List bricks, String mountPoint,
			PostData resData) {
		log.info("Creat new volume");
		// 判断创建volume的条件是否满足
		int able = IsBrickCountRight(volumeName, count, type, bricks, Constant.allVolumeInfo);
		if (able == -5) {
			String mess = "4301 " + volumeName + " is exists! ";
			log.error(mess);
			resData.pushExceptionsStack(mess);
			return able;
		}
		if (able == -1) {
			String mess = "4301 brick的ip不在集群中或者未连接 ";
			log.error(mess);
			resData.pushExceptionsStack(mess);
			return able;
		}
		if (able == -2 || able == -3 || able == -4) {
			String mess = "4302 brick的数量与类型不符";
			log.error(mess);
			resData.pushExceptionsStack(mess);
			return able;
		}

		String command = null;
		// 将brics从List变量中提取出来并连接成可以在命令行中使用的格式
		String commandarg = getBrickStr(bricks);

		/*
		 * verify the type
		 */
		if (type.equals(Constant.distributed)) {
			command = "gluster volume create " + volumeName + " " + commandarg + "force";
		} else if (type.equals(Constant.replica) || type.equals(Constant.stripe)) {
			command = "gluster volume create " + volumeName + " " + type + " " + count + " " + commandarg + "force";
		}

		command = command + " && gluster volume start " + volumeName;
		// 执行命令
		List<String> reStrings = Constant.execCmdObject.execCmdWaitAcquiescent(command, resData);

		// 创建成功时返回信息格式：volume create: volumename success:
		if (reStrings == null || reStrings.size() == 0) {
			String mess = "4303 命令运行无返回";
			log.error(mess);
			resData.pushExceptionsStack(mess);
			return -7;
		}
		if (!reStrings.get(0).contains("volume create: " + volumeName + ": " + "success")) {
			String mess = "4305  volume create failed with error " + reStrings.get(0);
			log.error(mess);
			resData.pushExceptionsStack(mess);
			// System.out.println(reStrings.get(0));
			return -7;
		}
		log.info("create volume " + volumeName + " successed!");
		if (reStrings.size() == 1) {
			String mess = "4304  start volume命令无返回";
			log.error(mess);
			resData.pushExceptionsStack(mess);
			return -8;
		}
		if (!reStrings.get(1).contains("volume start: " + volumeName + ": " + "success")) {
			String mess = "4305  start volume失败 !/n " + reStrings.get(1);
			log.error(mess);
			resData.pushExceptionsStack(mess);
			return -8;
		}
		// 创建成功则启动并进行挂载

		log.info("start volume " + volumeName + " successed!");

		log.info("create " + mountPoint);
		new CopyData().createFolders(mountPoint);

		
		// 进行挂载
		new SetCluster().addMoutRecord(Constant.mountRecords, volumeName + ":" + mountPoint);
		/**
		 * 开始constant.allClusterInfo更新
		 */
		new VolumeInfo().controllerGetAllvolumeInfo(1);
		
		return 1;

	}

	/**
	 * 删除volume 1 表示成功 ；-1表示volume name不存在；-2表示volume 不在停止状态不能删除；
	 * -3表示删除失败，-4表示/gfsAutoMount/mountPoint.record文件不存在;-5表示其他错误
	 * 
	 * @param volumeName
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public int deleteVolume(String volumeName, PostData resData) {
		int status = 0;
		VolumeInfo volumeInfo = new VolumeInfo();
		List<VolumeEntity> volumes = (List<VolumeEntity>) Constant.allVolumeInfo.getData();
		List<String> reStrings = null;
		String path = null;
		String Volumestatus=null;
		for (VolumeEntity onEntity : volumes) {
			if (onEntity.getName().equals(volumeName)) {
				path = onEntity.getPath();
				Volumestatus=onEntity.getStatus();
				String command ="echo -e \"y\"| gluster volume delete " + volumeName;
				if(!Volumestatus.equals("Stopped")){
					String mess = "4306 " + volumeName + " 处于开始状态，不能删除";
					log.error(mess);
					resData.pushExceptionsStack(mess);
					return -2;
				}
				reStrings = Constant.execCmdObject.execCmdWaitAcquiescent(command);
				break;
			}
		}

		if (reStrings == null) {
			String mess = "4306 " + volumeName + " 不存在";
			log.error(mess);
			resData.pushExceptionsStack(mess);
			return -1;
		}
		if (reStrings.size() == 0) {
			String mess = "4307 命令没有返回";
			log.error(mess);
			resData.pushExceptionsStack(mess);
			return -2;
		}

		if (!reStrings.get(0).contains("volume delete: " + volumeName + ": " + "success")) {
			String mess = "4309  delete volume失败 !/n " + reStrings.get(0);
			log.error(mess);
			resData.pushExceptionsStack(mess);
			return -3;
		}
		log.info("delete volume " + volumeName + " successed!");
		new SetCluster().removeMoutRecord(Constant.mountRecords, volumeName + ":" + path);
		/**
		 * 开始constant.allClusterInfo更新
		 */
		new VolumeInfo().controllerGetAllvolumeInfo(1);
		return 1;
	}

	/**
	 * 停止指定volume 参数中需给出volume的名字 返回值： 1 表示成功 ;0表示已经处于停止状态；-1
	 * volumeName不存在；-2表示停止失败，其他错误 
	 */
	public int stopVolume(String volumeName, PostData resData) {
		log.info("stop volume");
		VolumeInfo volumeInfo = new VolumeInfo();
		// 首先需要判断volume是否存在，调用其他函数返回所有volume的名字
		boolean volumeExist = volumeInfo.volumeIsExists(Constant.allVolumeInfo, volumeName);

		if (!volumeExist) {
			// volume不存在
			String mess = "4311 " + volumeName + " 不存在";
			log.error(mess);
			resData.pushExceptionsStack(mess);
			return -1;
		}
		// volume存在，则需判断volume的状态是否已经为“stop”
		if (volumeInfo.getVolumeStatus(Constant.allVolumeInfo, volumeName).equals("Stopped")) {
			String mess = "4312 " + volumeName + " 已经停止";
			log.error(mess);
			resData.pushExceptionsStack(mess);
			return 0;
		}
		String command = "echo -e \"y\"| gluster volume stop " + volumeName;
		List<String> reStrings = Constant.execCmdObject.execCmdWaitAcquiescent(command);

		// 标记操作结果：operation = 1 操作成功
		// operation = 0 操作失败
		int operation = 0;
		for (String temp2 : reStrings) {
			if (temp2.contains("volume stop: " + volumeName + ": " + "success")) {
				operation = 1;
				break;
			}

		}

		if (operation == 1) {
			log.info("停止成功");
			/**
			 * 开始constant.allClusterInfo更新
			 */
			new VolumeInfo().controllerGetAllvolumeInfo(1);
			return 1;
		} else {
			String mess = "4312 " + volumeName + " 停止失败\n" + reStrings.get(0);
			log.error(mess);
			resData.pushExceptionsStack(mess);
			return -2;
		}
		
	}

	/**
	 * 开始指定volume 参数中需给出volume的名字 返回值： 1 表示成功 ;0表示已经处于开始状态；-1
	 * volumeName不存在；-2表示开始失败，其他错误； 
	 */
	public int startVolume(String volumeName, PostData resData) {
		log.info("start volume");
		VolumeInfo volumeInfo = new VolumeInfo();
		// 首先需要判断volume是否存在，调用其他函数返回所有volume的名字
		boolean volumeExist = volumeInfo.volumeIsExists(Constant.allVolumeInfo, volumeName);

		if (!volumeExist) {
			// volume不存在
			String mess = "4311 " + volumeName + " 不存在";
			log.error(mess);
			resData.pushExceptionsStack(mess);
			return -1;
		}
		// volume存在，则需判断volume的状态是否已经为“stop”
		if (volumeInfo.getVolumeStatus(Constant.allVolumeInfo, volumeName).equals("Started")) {
			String mess = "4312 " + volumeName + " 已经开始";
			log.error(mess);
			resData.pushExceptionsStack(mess);
			return 0;
		}
		String command = "echo -e \"y\"| gluster volume start " + volumeName;
		List<String> reStrings = Constant.execCmdObject.execCmdWaitAcquiescent(command);

		// 标记操作结果：operation = 1 操作成功
		// operation = 0 操作失败
		int operation = 0;
		for (String temp2 : reStrings) {
			if (temp2.contains("volume start: " + volumeName + ": " + "success")) {
				operation = 1;
				break;
			}

		}

		if (operation == 1) {
			log.info("开始成功");
			/**
			 * 开始constant.allClusterInfo更新
			 */
			new VolumeInfo().controllerGetAllvolumeInfo(1);
			return 1;
		} else {
			String mess = "4312 " + volumeName + " 开始失败\n" + reStrings.get(0);
			log.error(mess);
			resData.pushExceptionsStack(mess);
			return -2;
		}

	}

	/**
	 * 为指定的volume添加brick,参数中需要指定类型、数量等 返回值：1成功 ;-1volumeName不存在；-2，-3，-4数量与类型不匹配；-5 其他错误
	 * 过程中需要先检查volume是否存在，还需检查给出的brick数量与类型、count是否相符
	 * 
	 * @param volumeName
	 * @param brickName
	 * @param count
	 * @param type
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public int addBrickVolume(String volumeName, List<String> brickName, int count, String type,PostData resData) {
		log.info("add brick to the specified volume");
		// 检查是否满足添加bricks的条件
		int able = IsBrickCountRight(volumeName, count, type, brickName, Constant.allVolumeInfo);
		if (able == -1) {
			String mess = "4313 " + volumeName + " 不存在" ;
			log.error(mess);
			resData.pushExceptionsStack(mess);
			return able;
		} 
		if (able != 1) {
			String mess = "4314 brick数量与类型要求不匹配" ;
			log.error(mess);
			resData.pushExceptionsStack(mess);
			return able;
		} 

		String command = "";
		

		String brick = getBrickStr(brickName);

		if (type.equals(Constant.distributed))
			command = "gluster volume add-brick " + volumeName + " " + brick + "force";
		else if (type.equals(Constant.replica))
			command = "gluster volume add-brick " + volumeName + " " + "replica " + count + " " + brick + "force";
		else if (type.equals(Constant.stripe))
			command = "gluster volume add-brick " + volumeName + " " + "stripe " + count + " " + brick + "force";

		List<String> reStrings = Constant.execCmdObject.execCmdWaitAcquiescent(command);

		// 添加成功的返回信息是：volume add-brick: success
		if (reStrings != null && reStrings.size() > 0 && reStrings.get(0).contains("volume add-brick: success")) {
			log.info("添加brick成功！");
			return 1;
		} else {
			String mess = "4315 添加失败\n" ;
			log.error(mess);
			resData.pushExceptionsStack(mess);
			return -5;
		}
	}
	/**
	 * 为指定的volume添加brick,参数中需要指定类型、数量等 返回值：1成功 ;-1volumeName不存在；-2，-3，-4数量与类型不匹配；-5 其他错误
	 * 过程中需要先检查volume是否存在，还需检查给出的brick数量与类型、count是否相符
	 * 
	 * @param volumeName
	 * @param brickName
	 * @param count
	 * @param type
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public int deleteBrickVolume(String volumeName, List<String> brickName, int count, String type,PostData resData) {
		log.info("delete brick to the specified volume");
		// 检查是否满足添加bricks的条件
		int able = IsBrickCountRight(volumeName, count, type, brickName, Constant.allVolumeInfo);
		if (able == -1) {
			String mess = "4316 " + volumeName + " 不存在" ;
			log.error(mess);
			resData.pushExceptionsStack(mess);
			return able;
		} 
		if (able != 1) {
			String mess = "4317 brick数量与类型要求不匹配" ;
			log.error(mess);
			resData.pushExceptionsStack(mess);
			return able;
		} 

		String command = "";
		

		String brick = getBrickStr(brickName);

		if (type.equals(Constant.distributed)) {
			command = "echo -e \"y\" | gluster volume remove-brick " + volumeName + " " + brick + " force";
		} else if (type.equals(Constant.replica)) {
			command = "echo -e \"y\" | gluster volume remove-brick " + volumeName + " repli " + count + " " + brick
					+ " force";
		} else if (type.equals(Constant.stripe)) {
			command = "echo -e \"y\" | gluster volume remove-brick " + volumeName + " stripe " + count + " " + brick
					+ " force";
		}

		List<String> reStrings = Constant.execCmdObject.execCmdWaitAcquiescent(command);

		// 添加成功的返回信息是：volume add-brick: success
		if (reStrings != null && reStrings.size() > 0 && reStrings.get(0).contains("volume remove-brick: success")) {
			log.info("删除brick成功！");
			return 1;
		} else {
			String mess = "4318 删除失败\n" ;
			log.error(mess);
			resData.pushExceptionsStack(mess);
			return -5;
		}
	}

	// 需要将存于List变量中的brick的位置组装成可以在glusterfs命令行中直接使用的格式
	public String getBrickStr(List<String> brickName) {
		StringBuffer result = new StringBuffer();
		int len = brickName.size();
		for (int i = 0; i < len; i++) {
			result.append(brickName.get(i));
			result.append(" ");
		}
		return result.toString();
	}

	/*
	 * 只在创建volume时使用此函数 创建volume时对不同数据卷，brick的数量需要满足和count的关系
	 * 首先判断它们是否满足关系，在不满足的关系的情况下是肯定无法完成操作的 1:可以创建 ;-1：brick的ip不在集群中或者未连接; -2 -3
	 * -4 :类型与brick数目不匹配 ; -5 :volumeName 已经存在；-6：挂载点存在且不为空，不能作为挂载点； -7：未知错误
	 */
	public int IsBrickCountRight(String volumeName, int count, String type, List<String> bricks,
			PostData allVolumeInfo) {
		int status = 0;

		int length = bricks.size();
		
		if (new VolumeInfo().volumeIsExists(allVolumeInfo, volumeName)) {
//			log.error("3106 " + volumeName + " is already exists! ");
			return -5;
		}

		if (type.equals(Constant.distributed)) {
			if (count != 0) {

				return -2;
			}
		}
		if (type.equals(Constant.stripe)) {
			if (length % count != 0) {

				return -3;
			}
		}
		if (type.equals(Constant.replica)) {
			if ((length % count) != 0) {

				return -4;
			}
		}

		Map peer_status = (Map<String, String>) Constant.clusterInfo.getData();
		peer_status.put(Constant.hostIp, Constant.peerincluster_connected);
		for (String brick : bricks) {
			brick = brick.split(":")[0];
			if (!(peer_status.containsKey(brick) && peer_status.get(brick).equals(Constant.peerincluster_connected))) {
//				log.error("3105 birck " + brick + " ip is not in cluster");
				return -1;
			}

		}

		
		return 1;
	}

	/**
	 * 添加volume的brick时，首先需要判断volume是否存在，然后需要判断volume类型、count及brick数目
	 * 
	 * @param volumeName
	 * @param count
	 * @param type
	 * @param bricks
	 * @return 1 满足条件，可以添加;-1 :volume name is not exists;-2,-3,-4 类型与brick数量不匹配；
	 */
	public int isAble(String volumeName, int count, String type, List bricks, PostData allVolumeInfo) {
		VolumeInfo volumeInfo = new VolumeInfo();
		if (volumeInfo.volumeIsExists(allVolumeInfo, volumeName)) {
			// String mess="4301 "+ volumeName + " is exists! ";
			// log.error(mess);
			return -1;
		}

		int length = bricks.size();
		if (type.equals("distribute")) {
			if (count == 0)
				return 1;
			else {
				// log.error("4302： the kind of distributed requires the arg of
				// count to be 0");
				return -2;
			}
		}

		if (type.equals("stripe")) {
			if (length % count == 0)
				return 1;
			else {
				// log.error("4303： the number of bricks should be the same as
				// or the times of the stripe count");
				return -3;

			}
		}
		if (type.equals("replicate")) {
			if ((length % count) == 0)
				return 1;
			else {
				// log.error("4304： the number of bricks should be the same as
				// the replicate count or the times of replicate count");
				return -4;
			}
		}

		return 1;
	}

	@Test
	public void test_deleteVolume() {
//		System.out.println(deleteVolume("lili_test1"));
	}

	// @Test
	public void test_createVolume() {
		List<String> bricksToCreate = new ArrayList<String>();
		bricksToCreate.add("192.168.0.110:/lili_test1");
		bricksToCreate.add("192.168.0.116:/lili_test1");
//		System.out.println(createVolume("lili_test1", 0, "distributed", bricksToCreate, "/home/lili_test1_point"));
	}

	public static void main(String[] args) {
		SetVolume setVolume = new SetVolume();
		int operation = 0;
		// PropertyConfigurator.configure("log4j.properties");
		// TODO Auto-generated method stub
		// 测试创建volume的代码

		// List<String> bricksToCreate = new ArrayList<String>();
		// bricksToCreate.add("192.168.0.110:/v2");
		// bricksToCreate.add("192.168.0.116:/v2");
		// operation = setVolume.createVolume("v2", 0, "distributed",
		// bricksToCreate, "/home/v2_point");
		// operation = setVolume.deleteVolume("v3");
		//
		// // 以下是测试添加brick的代码
		//
		// List<String> bricksToAdd = new ArrayList<String>();
		// bricksToAdd.add("192.168.191.23:/v3");
		// operation = setVolume.addBrickVolume("v3", bricksToAdd, 0,
		// "distribute");
		// System.out.println(operation);

		// 以下代码是测试删除brick的代码
		// List<String> bricksToAdd= new ArrayList<String>();
		// bricksToAdd.add("192.168.191.23:/v3");
		// operation =
		// setVolume.deleteBrickVolume("v3",bricksToAdd,0,"distribute");
		// System.out.println(operation);
		// 以下是测试start volume的代码
		// String volumeToStart = "testcreate" ;
		// int startOperation = startVolume(volumeToStart);
		// System.out.println(startOperation);
		// 以下是测试stop volume
		String volumeToStop = "v3";
		// int startOperation = setVolume.stopVolume(volumeToStop);
		// 以下是测试创建volume并完成挂载的代码
		// List<String> bricksToCreate= new ArrayList<String>();
		// bricksToCreate.add("192.168.214.135:/home/create");
		// bricksToCreate.add("192.168.214.138:/home/create");
		//
		// int operation =
		// createVolume("createAndmount",0,"distribute",bricksToCreate,"/mnt/create");
		// System.out.println(operation);
	}
}
