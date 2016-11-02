

package com.platform.glusterfs;



import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.platform.entities.PostData;
import com.platform.utils.Constant;
import com.platform.utils.MyProcess;
import com.platform.utils.MyThread;
import com.platform.utils.ProcessMyUtil;
import com.platform.utils.getTreeDataByPath;


import ch.ethz.ssh2.Connection;

/**
 * 获取集群信息
 * @author    liliy
 * @version   [版本号，2016年9月12日]
 * @see       [相关类/方法]
 * @since     [产品/模块版本]
 */
public class ClusterInfo {
	ProcessMyUtil proMy = new ProcessMyUtil();
	private final Logger log = Logger.getLogger("");
	
	public void controllerGetClusterInfo(int timeout) {
		try {

			MyThread myThread = new MyThread(new ClusterInfo(), "setClusterInfo", Constant.clusterInfo,timeout);
			myThread.start();
			myThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * 获取集群信息存入缓存
	 * 获取集群节点信息
	 * 如果获取不正常，则返回null，如果获取正常，返回map表示节点ip和ip的状态
	 * 如果ip在集群中且联通状态为PeerinCluster(Connected)
	 * 如果ip在集群中且但不连通为PeerinCluster(Disconnected)
	 * @see [类、类#方法、类#成员]
	 */
	public void setClusterInfo(PostData clusterInfo){
		log.info("get cluster info");
//		new_clusterInfo=new PostData(new HashMap<String,String>());
		@SuppressWarnings("unchecked")
		Map<String, String> peerIps = (Map<String,String>)Constant.clusterInfo.getData();
		peerIps.put(Constant.hostIp, Constant.peerincluster_connected);
		String cmd="gluster peer status";
		List<String> reStrings = Constant.execCmdObject.execCmdWaitAcquiescent(cmd, clusterInfo);
		if (reStrings == null) {
			
			String mess="4001 command get result is null";
			log.error(mess);
			clusterInfo.pushExceptionsStack(mess);
			return ;
		}
		if (reStrings.size() == 0) {
			String mess="4002 command get result is null";
			log.error(mess);
			clusterInfo.pushExceptionsStack(mess);
			return ;
		}
		
		if (reStrings.get(0).contains("No peers present")) {
			clusterInfo.setData(peerIps);
			return ;
		}
		
		if (!(reStrings.get(0).split(":")[0].contains("Number of Peers"))) {
			String mess="4003 command get result is null";
			log.error(mess);
			clusterInfo.pushExceptionsStack(mess);
			return ;
		}
		int flag = 0;
		String ipString = "";
		String state = "";
		for (Iterator it2 = reStrings.iterator(); it2.hasNext();) {
			String line = (String) it2.next();
			line=line.replaceAll(" +", " ");
			String keyValue[] = line.split(":");
			if (keyValue[0].equals("Hostname")) {

				if (keyValue.length < 2) {
					String mess="4004 command get result is null";
					log.error(mess);
					clusterInfo.pushExceptionsStack(mess);
					continue;
				}

				ipString = keyValue[1].replaceAll(" ", "");
				flag = 1;
			} else if (flag == 1 && keyValue[0].equals("State")) {

				if (keyValue.length < 2) {
					String mess="4005 command get result is null";
					log.error(mess);
					clusterInfo.pushExceptionsStack(mess);
					continue;
				}

				state = keyValue[1].replaceAll(" ", "");
				flag = 0;
				peerIps.put(ipString, state);
			}
	}
		Constant.clusterInfo.setData(peerIps);
	}
	
	/**
	 * 获取集群节点信息
	 * 如果获取不正常，则返回null，如果获取正常，返回map表示节点ip和ip的状态
	 * 如果ip在集群中且联通状态为PeerinCluster(Connected)
	 * 如果ip在集群中且但不连通为PeerinCluster(Disconnected)
	 * @return
	 * @throws IOException 
	 * @see [类、类#方法、类#成员]
	 */
	public Map<String, String> getClusterInfo() {
//		log.info("get cluster info");
		Map<String, String> peerIps = new HashMap<String, String>();
		peerIps.put(Constant.hostIp, Constant.peerincluster_connected);
		List<String> reStrings = proMy.execCmdWaitAcquiescent(Constant.glusterPeerStatus);
		if (reStrings == null) {
			log.error("1101 command get result is null");
			return null;
		}
		if (reStrings.size() == 0) {
			log.error("1102 command get result is nothing");
			return null;
		}
		
		if (reStrings.get(0).contains("No peers present")) {
			return peerIps;
		}
		
		if (!(reStrings.get(0).split(":")[0].contains("Number of Peers"))) {
			log.error("1103 get result string wrong");
			return null;
		}
		

		// System.out.print(reStrings.get(0));

		int flag = 0;
		String ipString = "";
		String state = "";
		for (Iterator it2 = reStrings.iterator(); it2.hasNext();) {
			String line = (String) it2.next();
			line=line.replaceAll(" +", " ");
			String keyValue[] = line.split(":");
			if (keyValue[0].equals("Hostname")) {

				if (keyValue.length < 2) {
					log.error("1105 command get result is wrong");
					continue;
				}

				ipString = keyValue[1].replaceAll(" ", "");
				flag = 1;
			} else if (flag == 1 && keyValue[0].equals("State")) {

				if (keyValue.length < 2) {
					log.error("1106 command get result is wrong");
					continue;
				}

				state = keyValue[1].replaceAll(" ", "");
				flag = 0;
				peerIps.put(ipString, state);
			}

		}
		
//		for (Map.Entry<String, String> entry:peerIps.entrySet()){
//			String key=entry.getKey();
//			if(key.equals(Constant.hostIp)){
//				continue;
//			}
//			String value=entry.getValue();
//			if(Constant.ganymedSSH.otherConns==null){
//				Constant.ganymedSSH.otherConns=new HashMap<String,Connection>();
//			}
//			if(!Constant.ganymedSSH.otherConns.containsKey(key)){
//				Connection connection=null;
//				try {
//					connection = Constant.ganymedSSH.getOpenedConnection(key, Constant.rootUser, Constant.rootPasswd, Constant.port);
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				Constant.ganymedSSH.otherConns.put(key,connection);
//			}
//		}
		
		return peerIps;
	}
 
	/**
	 * 根据给定的ip获的ip的状态，即是否在集群中并联通
	 * 如果ip不在集群中，返回null
	 * 如果ip在集群中且联通状态为PeerinCluster(Connected)
	 * 如果ip在集群中且但不连通为PeerinCluster(Disconnected)
	 * @param peerip
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public String getPeerStatus(String peerip){
		Map<String, String> peerIps=getClusterInfo();
		if(peerIps==null || peerIps.size()==0){
			return null;
		}
		
		if(peerip.equals(Constant.hostIp)){
			return Constant.peerincluster_connected;
		}
		if(!peerIps.containsKey(peerip)){
			return Constant.peerNotinCluster;
		}
		return peerIps.get(peerip);
	}
	

	public static void main(String[] args) {
//		PropertyConfigurator.configure("log4j.properties");
		System.out.println(new ClusterInfo().getClusterInfo());
		System.out.println(new ClusterInfo().getPeerStatus("192.168.0.116"));
	}
}


