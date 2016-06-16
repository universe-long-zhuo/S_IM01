package vaint.wyt.db;

import java.util.List;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import vaint.wyt.constant.Constants;

/**
 * 好友列表工具类
 * @author Vaint
 *@E-mail vaintwyt@163.com
 *
 */
public class FriendsUtils {

	/**
	 * 获得JSON格式的好友信息：ID，昵称，性别，头像<BR/>
	 * 用于返回给客户端<BR/>
	 * 格式<BR/>
	 * {friend_num:X,friends:{0:[friend_id,friend_name,friend_gender,friend_photo],..}
	 */
	public static JSONObject GetFriendInfo(String userId) {
		JSONObject respJson = new JSONObject();
		Friends friends = (Friends) HibernateUtils.get(Friends.class, userId);
		if(friends == null)//没有该ID的好友列表
		{
			return respJson;
		}
		
		JSONObject friendsJson = new JSONObject();
		
		List<String> list = friends.getList();
		int index = 0;
		User friend;
		for (int i = 0; i < list.size(); i++) {
			JSONArray friendInfo = new JSONArray();
			friend = (User) HibernateUtils
					.get(User.class, list.get(i));
			if (friend == null) {
				continue;
			}
			
			friendInfo.add(0, friend.getUserId());
			friendInfo.add(1, friend.getName());
			friendInfo.add(2, friend.getGender());
			String photo = friend.getPhoto();
			if(photo == null || photo.isEmpty())
			{
				photo = "";//设置为空串，因为数据库中可能获取的值为null
			}
			friendInfo.add(3, photo);

			friendsJson.element(index + "", friendInfo);
			
			index++;
		}
		respJson.element(Constants.GetFriendList.ResponseParams.NUM, index);
		respJson.element(Constants.GetFriendList.ResponseParams.FRIENDS,
				friendsJson.toString());

		return respJson;
	}

	/** 添加好友ID到对应的好友列表 ，双向*/
	public static boolean AddFriend(String userId, String friendId) {
		boolean ret = true;
		List<String> friendList;
		//将friendId添加到userId的好友列表
		Friends user = (Friends) HibernateUtils.get(Friends.class, userId);
		if(user == null)//原先不存在好友列表，即没有好友
		{
			user = new Friends();
			user.setUserId(userId);
			user.setFriendList(friendId);
			ret = HibernateUtils.update(user);
		}
		else
		{
			//判断好友ID是否已经存在
			friendList = user.getList();
			if(!friendList.contains(friendId))//不存在，则添加。否则不操作
			{
				friendList.add(friendId);
				user.setList(friendList);
				ret = HibernateUtils.update(user);
			}
		}
		
		//将userId添加到friendId的好友列表
		Friends friend = (Friends) HibernateUtils.get(Friends.class, friendId);
		if (friend == null)// 原先不存在好友列表，即没有好友
		{
			friend = new Friends();
			friend.setUserId(userId);
			friend.setFriendList(friendId);
			ret = HibernateUtils.update(friend);
		} else {
			//判断好友ID是否已经存在
			friendList = friend.getList();
			if(!friendList.contains(userId))//不存在，则添加。否则不操作
			{
				friendList.add(userId);
				friend.setList(friendList);
				ret = HibernateUtils.update(friend);
			}
		}
		return ret;
	}

	/**移除好友 双向*/
	public static boolean RemoveFriend(String userId1, String userId2) {
		boolean ret = true;
		List<String> friendList;
		//将friendId添加到userId的好友列表
		Friends user1 = (Friends) HibernateUtils.get(Friends.class, userId1);
		if(user1 != null)
		{
			//判断好友ID是否已经存在
			friendList = user1.getList();
			friendList.remove(userId2);//删除好友ID，如果不存在，则无影响
			user1.setList(friendList);
			ret = HibernateUtils.update(user1);
		}
		
		//将userId添加到friendId的好友列表
		Friends user2 = (Friends) HibernateUtils.get(Friends.class, userId2);
		if (user2 != null)
		{
			friendList = user2.getList();
			friendList.remove(userId1);//删除好友ID，如果不存在，则无影响
			user2.setList(friendList);
			ret = HibernateUtils.update(user2);
		}
		return ret;
	}

}
