package vaint.wyt.chat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import vaint.wyt.bean.ChatData;

/**
 * 管理离线消息
 * 
 * @author Vaint
 * @E-mail vaintwyt@163.com
 * 
 */
public class MsgManager {
	static Logger logger = Logger.getLogger(MsgManager.class);
	
	// <用户ID，消息序列(旧-新)>
	private static Map<String, List<ChatData.MSG>> msgManager = new HashMap<String, List<ChatData.MSG>>();
//	static{
//		List<ChatData.MSG> list = new ArrayList<ChatData.MSG>();
//		ChatData.MSG msg = new ChatData.MSG();
//		msg.setType(ChatData.Type.CHATTING);
//		msg.setFromId("15989078900");
//		msg.setMsg("测试数据");
//		msg.setTime("05-29 22:25");
//		list.add(msg);
//		list.add(msg);
//		
//		msgManager.put("15902086000", list);
//	}
	/** 添加离线消息缓存 */
	public static void addMsgCache(String id, ChatData.MSG msg) {
		List<ChatData.MSG> list;
		if (msgManager.containsKey(id))// 原先存在离线消息
		{
			list = msgManager.get(id);
			list.add(msg);
		} else {
			list = new ArrayList<ChatData.MSG>();
			list.add(msg);
		}
		logger.debug("ID:"+id+"有一条离线消息:"+msg.getMsg());
		msgManager.put(id, list);
	}

	/** 获得离线消息序列 */
	public static List<ChatData.MSG> getOfflineMsg(String id) {
		return msgManager.get(id);
	}

	/** 移除消息序列 */
	public static void removeMsgCache(String id) {
		msgManager.remove(id);
	}
}
