package vaint.wyt.chat;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import vaint.wyt.bean.ChatData;

/**
 * 管理客户端连接
 * 
 * @author Vaint
 * @E-mail vaintwyt@163.com
 * 
 */
public class ChatConnManager {
	static Logger logger = Logger.getLogger(ChatConnManager.class);
	
	// <用户ID，连接线程>
	private static Map<String, ChatConnThread> connManager = new HashMap<String, ChatConnThread>();

	/** 添加客户端通信线程 */
	public static void addConnThread(String id, ChatConnThread connThread) {
		logger.info(id+"创建通信连接");
		//如果连接已经存在，则需要断开之前的连接
		ChatConnThread conn = connManager.remove(id);
		if (conn != null)// 如果id不存在则返回null
			conn.closeConn();// 终止线程
		
		connManager.put(id, connThread);
	}

	/** 移除客户端通信线程 */
	public static void removeConnThread(String id) {
		ChatConnThread connThread = connManager.remove(id);
		if (connThread != null)// 如果id不存在则返回null
			connThread.closeConn();// 终止线程
	}

	/** 给指定客户端发送消息 */
	public static void sendMsg(ChatData.MSG msg) {
		String toId = msg.getToId();
		ChatConnThread connThread = connManager.get(toId);
		// 判断接收方是否在线
		if (connThread != null && connThread.isOnLine())// 接收方在线
		{
			// 发送消息
			connThread.sendMsg(msg);
		} else if (connThread != null && !connThread.isOnLine())// 接收方断开连接
		{
			removeConnThread(toId);
			
			// 缓存消息
			MsgManager.addMsgCache(toId, msg);
			
		}else// 接收方不在线
		{
			// 缓存消息
			MsgManager.addMsgCache(toId, msg);
		}
	}
}
