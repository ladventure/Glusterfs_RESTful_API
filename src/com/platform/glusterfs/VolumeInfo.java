
package com.platform.glusterfs;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.map.Flat3Map;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.junit.Test;
import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

import com.jfinal.plugin.activerecord.OneConnectionPerThread;
import com.platform.entities.Brick;
import com.platform.entities.FolderNode;
import com.platform.entities.PostData;
import com.platform.entities.VolumeEntity;
import com.platform.utils.CacheTreeData;
import com.platform.utils.Constant;
import com.platform.utils.GanymedSSH;
import com.platform.utils.MyThread;
import com.platform.utils.ProcessMyUtil;
import com.platform.utils.StringHelper;

import net.sf.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 获取volume信息 <功能详细描述>
 * 
 * @author liliy
 * @version [版本号，2016年9月13日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
public class VolumeInfo {
	public static Logger log = Logger.getLogger(VolumeInfo.class);
	
	public void controllerGetAllvolumeInfo(int timeout) {
		try {
			MyThread myThread1 = new MyThread(new VolumeInfo(), "setAllVolumeInfo", Constant.allVolumeInfo,timeout);
			MyThread myThread2 = new MyThread(new VolumeInfo(), "setAllVolumeDf", Constant.allVolumeInfo,timeout);
			myThread1.start();
			myThread1.join();
			myThread2.start();
			myThread2.join();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 获取所有volume的信息 <一句话功能简述> <功能详细描述>
	 * 
	 * @param allVolumeInfo
	 * @see [类、类#方法、类#成员]
	 */
	public void setAllVolumeInfo(PostData allVolumeInfo) {
		log.info("get all volume info");

		List<VolumeEntity> volumeList = new ArrayList<VolumeEntity>();

		try {
			String cmd = "gluster volume info";
			List<String> reStrings = Constant.execCmdObject.execCmdWaitAcquiescent(cmd, allVolumeInfo);
			if (reStrings == null) {
				String mess = "4101 get result is null";
				log.error(mess);
				allVolumeInfo.pushExceptionsStack(mess);
				return;
			}
			if (reStrings.size() == 0) {
				String mess = "4102 get result is nothing";
				log.error(mess);
				allVolumeInfo.pushExceptionsStack(mess);
				return;
			}
			if (reStrings.get(0).contains(Constant.noVolume)) {
				allVolumeInfo.setData(volumeList);
				return;
			}
			VolumeEntity oneVolume = null;
			for (String one : reStrings) {
				String map_key = StringHelper.getMapKey(one);
				switch (map_key) {
				case "Volume Name":
					if (oneVolume != null) {
						volumeList.add(oneVolume);
					}
					oneVolume = new VolumeEntity();
					oneVolume.setName(StringHelper.getMapValue(one));
					break;
				case "Type":
					oneVolume.setType(StringHelper.getMapValue(one));
					break;
				case "Status":
					oneVolume.setStatus(StringHelper.getMapValue(one));
					break;
				default:
					break;
				}
				if (map_key.matches("Brick[0-9]+")) {
					oneVolume.getBrick().add(getOneBrick(one));
				}
			}
			volumeList.add(oneVolume);

		} catch (Exception e) {
			String mess = "4103 " + e.toString();
			log.error(mess);
			allVolumeInfo.pushExceptionsStack(mess);
		}
		Constant.allVolumeInfo.setData(volumeList);
	}

	/**
	 * 用df命令获取所有volume的挂载点和容量情况 <一句话功能简述> <功能详细描述>
	 * 
	 * @param allVolumeInfo
	 * @see [类、类#方法、类#成员]
	 */
	public void setAllVolumeDf(PostData allVolumeInfo) {
		log.info("get all volume size and mount point");

		List<VolumeEntity> volumeList = null;

		try {
			volumeList = (List<VolumeEntity>) (Constant.allVolumeInfo.getData());
			String cmd = "df|awk '{print $1,$2,$3,$6}'";
			List<String> reStrings = Constant.execCmdObject.execCmdWaitAcquiescent(cmd, allVolumeInfo);
			if (reStrings == null) {
				String mess = "4104 get result is null";
				log.error(mess);
				allVolumeInfo.pushExceptionsStack(mess);
				return;
			}
			if (reStrings.size() == 0) {
				String mess = "4105 get result is nothing";
				log.error(mess);
				allVolumeInfo.pushExceptionsStack(mess);
				return;
			}
//			reStrings=reStrings.subList(1,reStrings.size());
			for (VolumeEntity oneVolume : volumeList) {
				for (String one : reStrings) {
					String[] one_split = one.replaceAll(" +", " ").split(" ");
					if (one_split.length != 4) {
						String mess = "4106 the command of df return wrong result";
						log.error(mess);
						allVolumeInfo.pushExceptionsStack(mess);
						continue;
					}
					String volumeName = StringHelper.getMapValue(one_split[0]);
					if (volumeName.equals(oneVolume.getName())) {
						Double allsize = 0.0;
						Double usedsize = 0.0;
						try {
							allsize = Double.parseDouble(one_split[1]);
							usedsize = Double.parseDouble(one_split[2]);
						} catch (NumberFormatException e) {
							String mess = "4107 " + e.toString();
							log.error(mess);
							allVolumeInfo.pushExceptionsStack(mess);
						}
						oneVolume.setAllSize(allsize);
						oneVolume.setUsedSize(usedsize);
						oneVolume.setPath(one_split[3]);
					}
				}
			}
		} catch (Exception e) {
			String mess = "4108 " + e.toString();
			log.error(mess);
			allVolumeInfo.pushExceptionsStack(mess);
		}
	}

	public void setOneVolumeData(PostData allVolumeInfo,String volumeName){
		log.info("set volume "+volumeName+" data ");
		FolderNode folderTree =getOneVolumeData(allVolumeInfo, volumeName);
		if(folderTree==null){
			return;
		}
		List<VolumeEntity> volumes = (List<VolumeEntity>) (Constant.allVolumeInfo.getData());
		for(VolumeEntity one:volumes){
			if(one.getName().equals(volumeName)){
				one.setFolder(folderTree);
			}
		}
		return ;
	}
	
	
	/**
	 * 设置所有volume数据
	 * <一句话功能简述>
	 * <功能详细描述>
	 * @param allVolumeInfo
	 * @see [类、类#方法、类#成员]
	 */
	public void setAllVolumeData(PostData allVolumeInfo) {
		log.info("get all volume data ");
		List<VolumeEntity> volumes = (List<VolumeEntity>) (Constant.allVolumeInfo.getData());
		
		try {	
		for (VolumeEntity oneVolume : volumes) {
			FolderNode folderTree=getOneVolumeData(allVolumeInfo,oneVolume.getName());
			oneVolume.setFolder(folderTree);

		}
		} catch (Exception e) {
			// TODO: handle exception
			String mess = "4109 " + e.toString();
			log.error(mess); 
			allVolumeInfo.pushExceptionsStack(mess);
		}
	}

	/**
	 * 显示所有volume名称 <功能详细描述>
	 * 
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public List<String> getAllVolumeName(PostData resData) {
		log.info("get all volume name");
		List<String> volNames = new ArrayList<String>();
		List<VolumeEntity> volumeEntities=(List<VolumeEntity>)Constant.allVolumeInfo.getData();
		for(VolumeEntity oneVolume:volumeEntities){
			volNames.add(oneVolume.getName());
		}
		return volNames;
	}

	/**
	 * 给定参数volume的名称获得volume的类型
	 * 
	 * @param volumeName
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public String getVolumeType(PostData resData,String volumeName) {
		log.info("get volume"+volumeName+" type");
		String volType = null;
		List<VolumeEntity> volumeEntities=(List<VolumeEntity>)Constant.allVolumeInfo.getData();
		for(VolumeEntity oneVolume:volumeEntities){
			if(oneVolume.getName().equals(volumeName));
			{
				volType=oneVolume.getType();
				break;
			}
		}
		if(volType==null){
			String mess="4110 "+volumeName+" is not exists!";
			log.error(mess);
			resData.pushExceptionsStack(mess);
		}
		return volType;
	}

	/**
	 * 获取volumeName的状态 ,如果volumeName不存在则返回null
	 * 正常返回状态Started,Stopped,Created
	 * 
	 * @param volumeName
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public String getVolumeStatus(PostData resData,String volumeName) {
		log.info("get volume "+volumeName+" status");
		String volStatus = null;
		List<VolumeEntity> volumeEntities=(List<VolumeEntity>)Constant.allVolumeInfo.getData();
		for(VolumeEntity oneVolume:volumeEntities){
			if(oneVolume.getName().equals(volumeName))
			{
				volStatus=oneVolume.getStatus();
				break;
			}
		}
		if(volStatus==null){
			String mess="4111 "+volumeName+" is not exists!";
			log.error(mess);
			resData.pushExceptionsStack(mess);
		}
		return volStatus;
	}

	/**
	 * 获取volumeName的所有大小 ,如果volumeName不存在则返回null
	 * 
	 * @param volumeName
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public Double getVolumeAllSize(PostData resData,String volumeName) {
		
		log.info("get volume "+volumeName+" allSize");
		Double allSize =null;
		List<VolumeEntity> volumeEntities=(List<VolumeEntity>)Constant.allVolumeInfo.getData();
		for(VolumeEntity oneVolume:volumeEntities){
			if(oneVolume.getName().equals(volumeName));
			{
				allSize=oneVolume.getAllSize();
				break;
			}
		}
		if(allSize==null){
			String mess="4112 "+volumeName+" is not exists!";
			log.error(mess);
			resData.pushExceptionsStack(mess);
		}
		return allSize;
	}

	/**
	 * 获取volumeName已用空间 ,如果volumeName不存在则返回null
	 * 
	 * @param volumeName
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public Double getVolumeUseSize(PostData resData,String volumeName)  {
		log.info("get volume "+volumeName+" usedSize");
		Double usedSize =null;
		List<VolumeEntity> volumeEntities=(List<VolumeEntity>)Constant.allVolumeInfo.getData();
		for(VolumeEntity oneVolume:volumeEntities){
			if(oneVolume.getName().equals(volumeName));
			{
				usedSize=oneVolume.getAllSize();
				break;
			}
		}
		if(usedSize==null){
			String mess="4113 "+volumeName+" is not exists!";
			log.error(mess);
			resData.pushExceptionsStack(mess);
		}
		return usedSize;
	}

	/**
	 * 获取volumeName的bricks 返回一个bircks的Brick的列表，异常返回null
	 * 
	 * @param volumeName
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public List<Brick> getVolumeBricks(PostData resData, String volumeName) {
		log.info("get volume " + volumeName + " bricks");
		List<Brick> bricks = null;
		List<VolumeEntity> volumes = (List<VolumeEntity>) (Constant.allVolumeInfo.getData());
		for (VolumeEntity oneVolume : volumes) {
			if (oneVolume.getName().equals(volumeName)) {
				bricks = oneVolume.getBrick();
				break;
			}
		}
		if(bricks==null){
			String mess="4114 "+volumeName+" is not exists!";
			log.error(mess);
			resData.pushExceptionsStack(mess);
		}
		return bricks;
	}

	/**
	 * 获取volumeName挂载点 <功能详细描述>
	 * 
	 * @param volumeName
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public String getVolumeMountPoint(PostData resData,String volumeName) {
		log.info("get volume " + volumeName + " MountPoint");
		String mountPoint=null;
		List<VolumeEntity> volumes = (List<VolumeEntity>) (Constant.allVolumeInfo.getData());
		for (VolumeEntity oneVolume : volumes) {
			if (oneVolume.getName().equals(volumeName)) {
				mountPoint = oneVolume.getPath();
				break;
			}
		}
		if(mountPoint==null){
			String mess="4115 "+volumeName+" is not exists!";
			log.error(mess);
			resData.pushExceptionsStack(mess);
		}
		return mountPoint;
	}

	/**
	 * 判断volumeName是否存在，存在返回true，不存在返回false
	 * 
	 * @param volumeName
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public boolean volumeIsExists(PostData resData,String volumeName) {
		List<String> volumes = getAllVolumeName(resData);
		if (volumes == null) {
			return false;
		}
		if (volumes.contains(volumeName)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 获取所有volume的数据的树形结构 <一句话功能简述> <功能详细描述>
	 * 
	 * @param allVolumeInfo
	 * @see [类、类#方法、类#成员]
	 */
	public FolderNode getOneVolumeData(PostData resData,String volumeName){
		log.info("get volume "+volumeName+" data ");
		FolderNode folderTree = null;
		if(volumeIsExists(resData, volumeName)==false){
			String mess="4116 "+volumeName+" is not exists!";
			log.error(mess);
			resData.pushExceptionsStack(mess);
			return folderTree;
		}
		GetTreeData getTreeData = new GetTreeData();
		List<Brick> bricks = getVolumeBricks(resData, volumeName);
		boolean flag=false;
		for (Brick oneBrcik : bricks) {
			if (oneBrcik.getIp().equals(Constant.hostIp)) {
				folderTree = getTreeData.getDatasWithShell(oneBrcik.getPath(), resData,
						Constant.execCmdObject);
				flag=true;
				break;
			}
		}
		if(flag==false){	
		 folderTree = getTreeData.getDatasWithShell(bricks.get(0).getPath(), resData,
				new GanymedSSH(Constant.hostIp, Constant.rootUser, Constant.rootPasswd, Constant.port));
		}
		return folderTree;
	}
	/**
	 * 从string中解析出brick信息，返回brick
	 * <一句话功能简述>
	 * <功能详细描述>
	 * @param brickString
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public Brick getOneBrick(String brickString) {
		String[] brickString_split = brickString.split(":");
		Brick oneBrick = new Brick();
		if (brickString_split.length != 3) {
			return null;
		}
		oneBrick.setIp(brickString_split[1].trim());
		oneBrick.setPath(brickString_split[2].trim());
		Map<String, String> peerIps = (Map<String, String>) (Constant.clusterInfo.getData());
		if (peerIps.containsKey(oneBrick.getIp())
				&& peerIps.get(oneBrick.getIp()).equals(Constant.peerincluster_connected)) {
			oneBrick.setStatus(true);
		} else {
			oneBrick.setStatus(false);
		}
		return oneBrick;
	}
//	/**
//	 * 获取volumeName的所有brick中数据占用空间的大小 返回一个map表示bricks和数据大小 <ip:path,data_size>
//	 * 
//	 * @param volumeName
//	 * @return
//	 * @see [类、类#方法、类#成员]
//	 */
//	
//	public Map<String, Double> getVolumebricksDataSize(String volumeName) {
//		List<String> bricks = getVolumeBricks(volumeName);
//		Map<String, Double> brick_size = new HashMap<>();
//		if (bricks == null) {
//			return null;
//		}
//		for (String brick : bricks) {
//			String ipAndpath[] = brick.split(":");
//			String ip = ipAndpath[0];
//			String path = ipAndpath[1];
//			String cmd = "du -d 0 " + path + "|awk '{print $1}'";
//			List<String> reStrings = Constant.ganymedSSH.execCmdWait(ip, Constant.rootUser, Constant.rootPasswd,
//					Constant.port, cmd);
//			// System.out.println(reStrings);
//			if (reStrings == null) {
//				log.error("1901 get result is null");
//				return null;
//			}
//			if (reStrings.size() == 0) {
//				log.error("1902 " + brick + " is not exits!");
//				return null;
//			}
//			Pattern pattern = Pattern.compile("[0-9]*");
//			Matcher isNum = pattern.matcher(reStrings.get(0));
//			if (!isNum.matches()) {
//				log.error("1903 " + reStrings.get(0) + " is unexpect");
//				return null;
//			}
//			brick_size.put(brick, Double.parseDouble(reStrings.get(0)));
//		}
//		return brick_size;
//	}
//
//	/**
//	 * 获取volumeName的所有brick中可用空间大小 返回一个map表示bricks和可用空间大小
//	 * <ip:path,available_size>
//	 * 
//	 * @param volumeName
//	 * @return
//	 * @see [类、类#方法、类#成员]
//	 */
//	public Map<String, Double> getVolumebricksAvailableSize(String volumeName) {
//		List<String> bricks = getVolumeBricks(volumeName);
//		Map<String, Double> brick_size = new HashMap<>();
//		if (bricks == null) {
//			return null;
//		}
//		for (String brick : bricks) {
//			String ipAndpath[] = brick.split(":");
//			String ip = ipAndpath[0];
//			String path = ipAndpath[1];
//			String cmd = "df " + path + "|awk '{print $4}'";
//			List<String> reStrings = Constant.ganymedSSH.execCmdWait(ip, Constant.rootUser, Constant.rootPasswd,
//					Constant.port, cmd);
//			// System.out.println(reStrings);
//			if (reStrings == null) {
//				log.error("1901 get result is null");
//				return null;
//			}
//			if (reStrings.size() == 0) {
//				log.error("1902 get result is nothing");
//				return null;
//			}
//			Pattern pattern = Pattern.compile("[0-9]*");
//			Matcher isNum = pattern.matcher(reStrings.get(1));
//			if (!isNum.matches()) {
//				log.error("1903 " + reStrings.get(1) + " is unexpect");
//				return null;
//			}
//			brick_size.put(brick, Double.parseDouble(reStrings.get(1)));
//		}
//		return brick_size;
//	}
//
//	
}
