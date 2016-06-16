package vaint.wyt.chat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

import org.apache.log4j.Logger;

import vaint.wyt.bean.ChatData;
import vaint.wyt.bean.ChatData.MSG;
import vaint.wyt.db.FriendsUtils;

/**
 * 维持客户端连接的线程<BR/>
 * 每个客户端都有独立的线程
 * 
 * @author Vaint
 * @E-mail vaintwyt@163.com
 * 
 */
public class ChatConnThread extends Thread {
	static Logger logger = Logger.getLogger(ChatConnThread.class);
	
	private Socket socket;
	private ObjectOutputStream out;
	private ObjectInputStream in;
	private String userId;
	/**线程运行终止标识*/
	private volatile boolean flag = true;

	public ChatConnThread(String id, Socket s, ObjectInputStream ois) throws IOException {
		userId = id;
		socket = s;
		in = ois;
		out = new ObjectOutputStream(socket.getOutputStream());
	}

	/**发送消息给本客户端*/
	public void sendMsg(ChatData.MSG msg) {
				try {
					out.writeObject(msg);// 发送消息
					out.flush();
				} catch (IOException e) {
					e.printStackTrace();
				}
	}

	/**发送离线消息给本客户端*/
	public void sendOfflineMsg(String userId) {
		// 发送离线消息给客户端
		List<MSG> list = MsgManager.getOfflineMsg(userId);
		if (list == null)
			return;

		for (int i = 0; i < list.size(); i++) {
			sendMsg(list.get(i));
		}
		// 清除对应离线消息
		MsgManager.removeMsgCache(userId);
	}

	@Override
	public void run() {
		while (flag) {
			try {
				Object obj = in.readObject();
				if (obj instanceof ChatData.MSG) {
					ChatData.MSG msg = (MSG) obj;
					// 判断消息类型
					switch (msg.getType()) {
					case CHATTING:// 普通消息
						logger.debug(msg.getFromId() + " 发送消息给 "
								+ msg.getToId());
						// 发送消息给对应客户端
						ChatConnManager.sendMsg(msg);
						break;
						
					case OFFLINE_MSG:// 获取离线消息
						logger.debug(msg.getFromId() + "获取离线消息");
						sendOfflineMsg(msg.getFromId());
						break;
					case ADD_FRIEND:// 添加好友请求
						logger.debug(msg.getFromId() + " 发起添加 " + msg.getToId()
								+ " 的好友请求");
						// 发送消息给对应客户端
						ChatConnManager.sendMsg(msg);
						break;
						
					case ADD_AGREE:// 同意添加好友
						logger.debug(msg.getFromId() + " 同意添加 " + msg.getToId()
								+ " 为好友");
						FriendsUtils.AddFriend(msg.getFromId(), msg.getToId());
						ChatConnManager.sendMsg(msg);
						break;
						
					case LOGOUT:// 注销登录
						logger.debug(msg.getFromId() + " 退出登录");
						ChatConnManager.removeConnThread(msg.getFromId());
						break;
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
				logger.debug(userId+" 通信线程出现异常");
				//出现异常，退出循环。即断开连接线程
				flag = false;
				break;
			} 
		}

		closeSocket();
	}
	
	/**关闭客户端连接线程*/
	public void closeConn()
	{
		flag = false;
	}
	
	/**关闭Socket连接*/
	private void closeSocket()
	{
		try {
			if(socket != null)
			{
				out.close();
				in.close();
				socket.close();
				
				socket = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**判断客户端连接*/
	public boolean isOnLine()
	{
		boolean ret = true;
		try{
			/*
			 * 发送测试数据
			 * 往输出流发送一个字节的数据，只要对方Socket的SO_OOBINLINE属性没有打开，
			 * 就会自动舍弃这个字节，而SO_OOBINLINE属性默认情况下就是关闭的
			 */
		      socket.sendUrgentData(0xFF);
		}catch(Exception e){
			logger.debug(userId + " 掉线了");
			ret = false;
		}
		
		return ret;
	}
}
