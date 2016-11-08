
package com.platform.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

import com.platform.data.TaskOperateData;
import com.platform.entities.PostData;

public class Constant {
	private static final Logger log = Logger.getLogger("");
	public static  int tomcatPort=0;
	/**
	 * 远程模式配置
	 */
	public static String rootUser = "root";
	public static String rootPasswd = "root";
	// public static String hostIp = "192.168.191.23";
	public static String hostIp = "192.168.0.110";
	public static int port = 22;
	
	/**
	 * 常用命令
	 */
	public static final String glusterPeerStatus = "gluster peer status";
	public static final String glusterVolumeInfo = "gluster volume info ";
	public static final String df = "df -k ";
	
	/**
	 * 常量字符串
	 */
	public static final String peerincluster_connected = "PeerinCluster(Connected)";
	public static final String peerincluster_disconnected = "PeerinCluster(Disconnected)";
	public static final String peerNotinCluster = "PeerNotinCluster";
	public static final String distributed = "distributed";
	public static final String replica = "replica";
	public static final String stripe = "stripe";
	public static final String noVolume = "No volumes present";
	public static final String VolumeName = "Volume Name";
	public static final String success = "success";
	public static final String failed = "failed";
	public static final String EndWith = "Do you want to continue? (y/n) ";
	public static final String noSuchFile = "No such file or directory";
	public static ExecuteCommand execCmdObject = null;
	
	/**
	 * 是否是本地模式，true表示本地模式，false表示远程模式
	 */
	public static boolean localMode = false;
	
	/**
	 * 集群和数据卷的本地缓存
	 */
	public static PostData allVolumeInfo = null;
	public static PostData clusterInfo = null;
	
	/**
	 * 文件迁移时候设置更新进度时间间隔
	 */
	public static int timeSetProgress=2*1000;
	
	/**
	 * 数据拷贝缓存
	 */
	public static int bufferSize = 8 * 1024;
	
	/**
	 * 数据拷贝任务列表
	 */
	public static List<TaskOperateData> copyDataTask=new ArrayList<TaskOperateData>();
	
	/**
	 *数据拷贝任务列表持久化文件 
	 */
	public static String copyDataTaskFilePath="/restfulGfs/operateDataTaskFile.record";
	
	/**
	 * api访问超时限制
	 */
	public static int timeout = 2000;
	
	/**
	 * 维护挂载点信息文件和全局变量
	 */
	public static String AutoMountfilePath = "/restfulGfs/AutoRun.sh";
	public static String MountRecordPath = "/restfulGfs/mountPoint.record";
	public static List<String> mountRecords = new ArrayList<>();
	
	/**
	 * volume 获取的线程休眠时间
	 */
	public final static int moveFileMaxNum = 1;

	/**
	 * volume 获取的线程休眠时间
	 */
	public final static int get_volume_sleep_time = 10000;

	/**
	 * volume 获取的线程休眠时间
	 */
	public final static int update_dataInfo_sleep_time = 1500;

}
