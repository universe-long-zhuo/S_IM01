package vaint.wyt.chat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

import vaint.wyt.bean.ChatData;
import vaint.wyt.bean.ChatData.ID;
import vaint.wyt.constant.Constants;

/**
 * 聊天服务线程
 * 
 * @author Vaint
 * @E-mail vaintwyt@163.com
 * 
 */
public class ChatServerThread extends Thread {
	static Logger logger = Logger.getLogger(ChatServerThread.class);
	private ServerSocket ss = null;
	/** 线程运行终止标识 */
	private volatile boolean flag = true;

	@Override
	public void run() {
		logger.info("开启聊天服务");
		Socket socket = null;
		ObjectInputStream in = null;

		try {
			ss = new ServerSocket(Constants.SERVER_PORT);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		while (flag) {
			try {
				socket = ss.accept();
				logger.debug("有一个新的连接");
				
				in = new ObjectInputStream(
						socket.getInputStream());
				// 获得客户端上传的用户ID
				Object obj = in.readObject();
				logger.debug("获取到数据");
				if (obj instanceof ChatData.ID) {
					ChatData.ID id = (ID) obj;
					String userId = id.getUserId();
					logger.debug(userId + "连接到聊天服务器");
					// 开启新的线程管理连接
					ChatConnThread connThread = new ChatConnThread(userId, socket, in);
					ChatConnManager.addConnThread(userId, connThread);
					connThread.start();
				}
			} catch (Exception e) {
				e.printStackTrace();
				// 关闭与客户端的连接。服务器的ServerSocket不要轻易关闭
				try {
					if(in != null)
						in.close();
					
					if (socket != null) 
						socket.close();
					
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
		logger.info("聊天服务结束");
		closeSocket();
	}

	/**关闭聊天服务*/
	public void closeServer() {
		flag = false;
	}
	
	private void closeSocket()
	{
		if (ss != null) {
			try {
				ss.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
