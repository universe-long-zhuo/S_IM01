package vaint.wyt.db;

import java.util.ArrayList;
import java.util.List;

/**
 * 好友列表
 * @author Vaint
 *@E-mail vaintwyt@163.com
 *
 */
public class Friends {
	private String userId;
	private String friendList;//字符串存储，格式：id1&id2&...&idN
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getFriendList() {
		return friendList;
	}
	public void setFriendList(String friendList) {
		this.friendList = friendList;
	}
	/**将好友列表字符串转换为List集合*/
	public List<String> getList()
	{
		List<String> list= new ArrayList<String>();
		if(friendList==null || friendList.isEmpty())
		{
			return list;
		}
		String[] friend = friendList.split("&");
		for(int i=0;i<friend.length;i++)
		{
			list.add(friend[i]);
		}
		return list;
	}
	/**将List集合转换为好友列表字符串*/
	public void setList(List<String> list)
	{
		StringBuilder sb = new StringBuilder();
		int size = list.size();
		for(int i=0;i<size;i++)
		{
			sb.append(list.get(i));
			if(i != (size-1))
			{
				sb.append("&");
			}
		}
		friendList = sb.toString();
	}
}
