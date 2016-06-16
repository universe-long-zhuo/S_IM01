package vaint.wyt.bean;

import java.io.Serializable;

/***
 * 聊天通信的数据类<BR/>
 * 需要与客户端对应的对象具有相同的包名和类名
 * 
 * @author Vaint
 * @E-mail vaintwyt@163.com
 * 
 */
public class ChatData implements Serializable {
	private static final long serialVersionUID = -5598765559893512679L;

	/** 消息类型 */
	public enum Type {
		/** 聊天消息 */
		CHATTING,
		/** 离线消息 */
		OFFLINE_MSG,
		/** 添加好友 */
		ADD_FRIEND,
		/** 同意添加好友 */
		ADD_AGREE,
		/** 注销登录 */
		LOGOUT
	}

	/**
	 * 用户ID，作为对应线程的标识
	 */
	public static class ID implements Serializable {
		private static final long serialVersionUID = 2788053974101111966L;
		private String userId;

		public String getUserId() {
			return userId;
		}

		public void setUserId(String userId) {
			this.userId = userId;
		}
	}

	/**
	 * 消息数据
	 */
	public static class MSG implements Serializable {
		private static final long serialVersionUID = 7266838079022652922L;
		private Type type;
		private String fromId;
		private String toId;
		private String msg;
		private String time;

		// 添加好友时，需要的fromId的用户信息
		private String photo;
		private String name;
		private String gender;

		public Type getType() {
			return type;
		}

		public void setType(Type type) {
			this.type = type;
		}

		public String getFromId() {
			return fromId;
		}

		public void setFromId(String fromId) {
			this.fromId = fromId;
		}

		public String getToId() {
			return toId;
		}

		public void setToId(String toId) {
			this.toId = toId;
		}

		public String getMsg() {
			return msg;
		}

		public void setMsg(String msg) {
			this.msg = msg;
		}

		public String getTime() {
			return time;
		}

		public void setTime(String time) {
			this.time = time;
		}

		public String getPhoto() {
			return photo;
		}

		public void setPhoto(String photo) {
			this.photo = photo;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getGender() {
			return gender;
		}

		public void setGender(String gender) {
			this.gender = gender;
		}
	}
}
