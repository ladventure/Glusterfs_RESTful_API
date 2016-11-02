
package com.platform.glusterfs;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.platform.entities.PostData;
import com.platform.entities.VolumeEntity;
import com.platform.utils.Constant;
import com.platform.utils.GanymedSSH;
import com.platform.utils.MyThread;
import com.platform.utils.Support;

public class SetCluster {
	public static Logger log = Logger.getLogger(SetCluster.class);

	/**
	 * 向集群中添加节点 返回1表示添加成功；-1表示ip不合法，-2 表示出错，0表示添加失败
	 * 
	 * @param peerip
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public int probePeer(PostData resData, String peerip) {
		log.info("probe peer!");
		if (!Support.checkIP(peerip)) {
			String mess = "4201 " + peerip + "is illegal!";
			log.error(mess);
			resData.pushExceptionsStack(mess);
			return -1;
		}
		String cmd = "gluster peer probe " + peerip;
		List<String> reStrings = Constant.execCmdObject.execCmdWaitAcquiescent(cmd, resData);
		if (reStrings == null || reStrings.size() == 0) {
			String mess = "4202 probe error! ";
			log.error(mess);
			resData.pushExceptionsStack(mess);
			return -2;
		}
		if (reStrings.contains(Constant.success)) {
			return 1;
		}
		String mess = "4203 probe failed!";
		log.error(mess);
		resData.pushExceptionsStack(mess);
		resData.pushExceptionsStackList(reStrings);
		return 0;
	}

	/**
	 * 删除集群中节点 返回1表示删除成功；-1表示ip不合法，-2 表示出错，0表示添加失败
	 * 
	 * @param peerip
	 * @return
	 * @see [类、类#方法、类#成员]
	 */
	public int detachPeer(PostData resData, String peerip) {
		log.info("probe peer!");
		if (!Support.checkIP(peerip)) {
			String mess = "4204 " + peerip + "is illegal!";
			log.error(mess);
			resData.pushExceptionsStack(mess);
			return -1;
		}
		String cmd = "gluster peer detach " + peerip;
		List<String> reStrings = Constant.execCmdObject.execCmdWaitAcquiescent(cmd, resData);
		if (reStrings == null || reStrings.size() == 0) {
			String mess = "4205 detach error! ";
			log.error(mess);
			resData.pushExceptionsStack(mess);
			return -2;
		}
		if (reStrings.contains(Constant.success)) {
			return 1;
		}
		String mess = "4206 detach failed!";
		log.error(mess);
		resData.pushExceptionsStack(mess+"\n");
		resData.pushExceptionsStackList(reStrings);
		return 0;
	}

	public List<String> getMountRecord() {
		List<String> mountRecords = new ArrayList<>();
		for(VolumeEntity onEntity:(List<VolumeEntity>)Constant.allVolumeInfo.getData()){
			String mountRecord=onEntity.getPath();
			String name=onEntity.getName();
			if(mountRecord!=null){
				mountRecords.add(name+":"+mountRecord);
			}
		}
		return mountRecords;
	}

	public void saveMoutRecord(List<String> mountRecords, String path) {
		String mountRecordsStr = "";
		for (String one : mountRecords) {
			mountRecordsStr = mountRecordsStr + one + "\n";
		}
		mountRecordsStr = mountRecordsStr.substring(0, mountRecordsStr.length() - 1);
		String command = "echo \"" + mountRecordsStr + "\" > " + path;
		Constant.execCmdObject.execCmdNoWaitAcquiescent(command);
	}

	public void removeMoutRecord(List<String> mountRecords, String oneRecord) {
		for (int i = 0; i < mountRecords.size(); i++) {
			if (mountRecords.get(i).equals(oneRecord)) {
				mountRecords.remove(i);
			}
		}
		Constant.execCmdObject.execCmdNoWaitAcquiescent("umount -l " + oneRecord.split(":")[1]);
		saveMoutRecord(mountRecords, Constant.MountRecordPath);
	}

	public void addMoutRecord(List<String> mountRecords, String oneRecord) {
		mountRecords.add(oneRecord);
		String volumeName = oneRecord.split(":")[0];
		String mountPoint = oneRecord.split(":")[1];
		String cmd = "mount -t glusterfs " + Constant.hostIp + ":" + volumeName + " " + mountPoint;
		Constant.execCmdObject.execCmdNoWaitAcquiescent(cmd);
		saveMoutRecord(mountRecords, Constant.MountRecordPath);
	}

	//@Test
	public void saveMoutRecordTest() {
		Constant.execCmdObject = new GanymedSSH("192.168.0.110", "root", "root", 22);
		List<String> mountRecords = new ArrayList<>();
		mountRecords.add("clw123:/home/clw123");
		mountRecords.add("peng:/home/peng");
		saveMoutRecord(mountRecords, "/gfsAutoMount/mountPoint.record");
	}

	@Test
	public void commandReturnTest() {
		Constant.execCmdObject = new GanymedSSH("192.168.0.110", "root", "root", 22);
		String command = "echo -e \"y\"| gluster volume stop peng";
		List<String> reStrings = Constant.execCmdObject.execCmdWaitAcquiescent(command);
		for (String one : reStrings) {
			System.out.println(one);
		}
	}
}
