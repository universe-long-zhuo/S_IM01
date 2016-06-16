package vaint.wyt.db;
/**
 * 用户信息
 * @author Vaint
 *@E-mail vaintwyt@163.com
 *
 */
public class User {
	private String userId;
	private String name;
	private String gender;
	private String password;
	private String photo;//Base64Coder编码后的字符串
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
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
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getPhoto() {
		return photo;
	}
	public void setPhoto(String photo) {
		this.photo = photo;
	}
}
