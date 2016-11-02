
package com.platform.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

import com.platform.entities.PostData;

public class Constant {
	private static final Logger log = Logger.getLogger("");
	public static String rootUser = "root";
	public static String rootPasswd = "root";
	// public static String hostIp = "192.168.191.23";
	public static String hostIp = "192.168.0.110";
	public static int port = 22;
	public static String glusterPeerStatus = "gluster peer status";
	public static String glusterVolumeInfo = "gluster volume info ";
	public static String df = "df -k ";
	public static String peerincluster_connected = "PeerinCluster(Connected)";
	public static String peerincluster_disconnected = "PeerinCluster(Disconnected)";
	public static String peerNotinCluster = "PeerNotinCluster";
	public static String distributed = "distributed";
	public static String replica = "replica";
	public static String stripe = "stripe";
	public static String noVolume = "No volumes present";
	public static String VolumeName = "Volume Name";
	public static String success = "success";
	public static String failed = "failed";
	public static String EndWith = "Do you want to continue? (y/n) ";
	public static String noSuchFile = "No such file or directory";
	public static ExecuteCommand execCmdObject = null;
	// public static GanymedSSH ganymedSSH = null;
	public static PostData allVolumeInfo = null;
	public static PostData clusterInfo = null;

	public static int timeout = 2000;
	public static String AutoMountfilePath = "/gfsAutoMount/AutoRun.sh";
	public static String MountRecordPath = "/gfsAutoMount/mountPoint.record";
	public static List<String> mountRecords=new ArrayList<>();
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
