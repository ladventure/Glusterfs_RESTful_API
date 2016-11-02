
package com.platform.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.platform.entities.PostData;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

import ch.ethz.ssh2.*;

public class GanymedSSH extends ExecuteCommand {
	Connection conn;
	public Map<String, Connection> otherConns;
	public boolean status = true;// 锟角凤拷锟斤拷锟街达拷锟斤拷锟斤拷锟阶刺�

	public GanymedSSH() {
		// TODO Auto-generated constructor stub

	}

	public GanymedSSH(String host, String username, String password, int port) {
		// TODO Auto-generated constructor stub
		try {
			conn = getOpenedConnection(host, username, password, port);
			
			otherConns = new HashMap<String, Connection>();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static Connection getOpenedConnection(String host, String username, String password, int port)
			throws IOException {

		Connection conns = new Connection(host, port);
		conns.connect(); // make sure the connection is opened
		boolean isAuthenticated = conns.authenticateWithPassword(username, password);
		if (isAuthenticated == false)
			throw new IOException("Authentication failed.");
		return conns;
	}	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.platform.utils.ExecuteCommand#execCmdNoWaitAcquiescent(java.lang.
	 * String, com.platform.entities.PostData)
	 */
	@Override
	public void execCmdNoWaitAcquiescent(String cmd, PostData postData) {
		// TODO Auto-generated method stub
		Session sess = null;
		try {
			sess = conn.openSession();
			// 执锟斤拷cmd
			sess.execCommand(cmd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			String mess = "3101" + e.toString();
			log.error(mess);
			postData.pushExceptionsStack(mess);
		} finally {
			sess.close();

		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.platform.utils.ExecuteCommand#execCmdWaitAcquiescent(java.lang.
	 * String, com.platform.entities.PostData)
	 */
	@Override
	public List<String> execCmdWaitAcquiescent(String cmd, PostData postData) {
		// TODO Auto-generated method stub
		List<String> reStrings = new ArrayList<String>();

		Session sess = null;
		try {
			// conn = getOpenedConnection(host, username, password, port);
			sess = conn.openSession();
			// 执锟斤拷cmd
			sess.execCommand(cmd);

			InputStream stdout = new StreamGobbler(sess.getStdout());
			InputStream stderr = new StreamGobbler(sess.getStderr());
			BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(stdout));
			BufferedReader stderrReader = new BufferedReader(new InputStreamReader(stderr));
			while (true) {
				String line = stdoutReader.readLine();

				if (line != null) {
					// System.out.println(line);
					reStrings.add(line);
				} else {
					break;
				}
			}

			if (reStrings.size() == 0) {
				while (true) {
					String line = stderrReader.readLine();

					if (line != null) {
						// System.out.println(line);
						reStrings.add(line);
					} else {
						break;
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			String mess = "3102" + e.toString();
			log.error(mess);
			postData.pushExceptionsStack(mess);
		} finally {
			if (null != sess) {
				sess.close();
			}

		}
		return reStrings;
	}
/**
	public void execCmdNoWait(String host, String username, String password, int port, String cmd) {

		Session sess = null;
		Connection new_conn;
		try {
			if (Constant.hostIp.equals(host)) {
				new_conn = conn;
			} else if (otherConns.containsKey(host) && otherConns.get(host) != null) {
				new_conn = otherConns.get(host);
			} else {
				new_conn = getOpenedConnection(host, username, password, port);
				otherConns.put(host, new_conn);
			}
			sess = new_conn.openSession();
			// 执锟斤拷cmd
			sess.execCommand(cmd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			sess.close();

		}
	}
*/

	public List<String> execCmdWait(String host, String username, String password, int port, String cmd) {
		List<String> reStrings = new ArrayList<String>();
		Session sess = null;
		Connection new_conn;
		try {
			if (Constant.hostIp.equals(host)) {
				new_conn = conn;
			} else if (otherConns.containsKey(host) && otherConns.get(host) != null) {
				new_conn = otherConns.get(host);
			} else {
				new_conn = getOpenedConnection(host, username, password, port);
				otherConns.put(host, new_conn);
			}
			sess = new_conn.openSession();
			// 执锟斤拷cmd
			sess.execCommand(cmd);
			InputStream stdout = new StreamGobbler(sess.getStdout());
			InputStream stderr = new StreamGobbler(sess.getStderr());
			BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(stdout));
			BufferedReader stderrReader = new BufferedReader(new InputStreamReader(stderr));
			while (true) {
				String line = stdoutReader.readLine();
				if (line != null) {
					// System.out.println(line);
					reStrings.add(line);
				} else {
					break;
				}
			}
			if (reStrings.size() == 0) {
				while (true) {
					String line = stderrReader.readLine();

					if (line != null) {
						// System.out.println(line);
						reStrings.add(line);
					} else {
						break;
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			sess.close();

		}
		return reStrings;
	}

	public Map<String, String> execMD5cmd(String cmd) {
		Map<String, String> md5 = new HashMap<String, String>();

		Session sess = null;
		try {

			sess = conn.openSession();
			// 执锟斤拷cmd
			sess.execCommand(cmd);

			InputStream stdout = new StreamGobbler(sess.getStdout());
			InputStream stderr = new StreamGobbler(sess.getStderr());
			BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(stdout));
			BufferedReader stderrReader = new BufferedReader(new InputStreamReader(stderr));
			while (true) {
				String line = stdoutReader.readLine();

				if (line != null) {
					String[] lines = line.split("  ");
					String key = lines[1].trim();
					String value = lines[0].trim();
					md5.put(key, value);
				} else {
					break;
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			sess.close();

		}
		return md5;
	}

	public String execGetSize(String cmd) {
		status = false;
		String str_size = "0";
		Session sess = null;
		try {

			// 执锟斤拷cmd
			sess = conn.openSession();
			sess.execCommand(cmd);
			InputStream stdout = new StreamGobbler(sess.getStdout());

			@SuppressWarnings("resource")
			BufferedReader br = new BufferedReader(new InputStreamReader(stdout));
			while (true) {
				String line = br.readLine();
				if (line != null) {
					// String[] lines=line.split(" ");
					// str_size=lines[0];

					str_size = line;
				} else {
					break;
				}

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			sess.close();
		}
		status = true;
		return str_size;
	}

	public static void main(String[] args) {
		PropertyConfigurator.configure("log4j.properties");
	}

	/* (non-Javadoc)
	 * @see com.platform.utils.ExecuteCommand#execCmdNoWaitAcquiescent(java.lang.String)
	 */
	@Override
	public void execCmdNoWaitAcquiescent(String cmd) {
		// TODO Auto-generated method stub
		Session sess = null;
		try {
			sess = conn.openSession();
			// 执锟斤拷cmd
			sess.execCommand(cmd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			String mess = "3101" + e.toString();
			log.error(mess);
		} finally {
			sess.close();

		}
	}

	/* (non-Javadoc)
	 * @see com.platform.utils.ExecuteCommand#execCmdWaitAcquiescent(java.lang.String)
	 */
	@Override
	public List<String> execCmdWaitAcquiescent(String cmd) {
		// TODO Auto-generated method stub
		List<String> reStrings = new ArrayList<String>();

		Session sess = null;
		try {
			// conn = getOpenedConnection(host, username, password, port);
			sess = conn.openSession();
			// 执锟斤拷cmd
			sess.execCommand(cmd);

			InputStream stdout = new StreamGobbler(sess.getStdout());
			InputStream stderr = new StreamGobbler(sess.getStderr());
			BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(stdout));
			BufferedReader stderrReader = new BufferedReader(new InputStreamReader(stderr));
			while (true) {
				String line = stdoutReader.readLine();

				if (line != null && !line.endsWith(Constant.EndWith)) {
					// System.out.println(line);
					reStrings.add(line);
				} else {
					break;
				}
			}

			if (reStrings.size() == 0) {
				while (true) {
					String line = stderrReader.readLine();

					if (line != null) {
						// System.out.println(line);
						reStrings.add(line);
					} else {
						break;
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			String mess = "3102" + e.toString();
			log.error(mess);
		} finally {
			if (null != sess) {
				sess.close();
			}

		}
		return reStrings;
		
	}

}
