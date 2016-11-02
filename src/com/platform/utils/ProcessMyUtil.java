package com.platform.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

public class ProcessMyUtil {
	

	public  List<String> execCmdWaitAcquiescent(String cmd) {
		return runcmd(cmd);
	}
	
	private List<String> runcmd(String cmd) {
		List<String> result = new ArrayList<String>();
		InputStream in = null;
		String[] cmds = {"/bin/sh", "-c", cmd};
		try {
			Process pro = Runtime.getRuntime().exec(cmds);
			pro.waitFor();
			in = pro.getInputStream();
			BufferedReader read = new BufferedReader(new InputStreamReader(in));
			String line = null;
			while ((line=read.readLine()) != null) {
				result.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
//		String[] strs = new String[result.size()];
//		return result.toArray(strs);
		return result;
	}

}
