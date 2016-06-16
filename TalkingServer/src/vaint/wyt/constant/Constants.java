package vaint.wyt.constant;

/**
 * 常量类<BR/>
 * 请求参数，应答参数，错误码信息等
 * 
 * @author Vaint
 * @E-mail vaintwyt@163.com
 * 
 */
public class Constants {
	/**socket连接的端口*/
	public static final int SERVER_PORT = 10010;

	/** 天翼短信验证码接口的相关常量 */
	public static class GetVerifyCode {
		/** 调用天翼接口所需要的app_id */
		public static final String APP_ID = "你的app_id";//TODO
		/** 调用天翼接口所需要的app_secret */
		public static final String APP_SECRET = "你的app_secret";//TODO
		/**
		 * 短信验证码<BR/>
		 * 获取access_token的URL
		 */
		public static final String GET_ACCESS_TOKEN_URL = "https://oauth.api.189.cn/emp/oauth2/v2/access_token";
		/**
		 * 短信验证码<BR/>
		 * 获取token的URL
		 */
		public static final String GET_TOKEN_URL = "http://api.189.cn/v2/dm/randcode/token";
		/**
		 * 短信验证码<BR/>
		 * 下发验证码的URL
		 */
		public static final String SEND_SMS_URL = "http://api.189.cn/v2/dm/randcode/sendSms";

		public static class Params {
			public static final String APP_ID = "app_id";
			public static final String APP_SECRET = "app_secret";
			public static final String ACCESS_TOKEN = "access_token";
			public static final String TOKEN = "token";
			public static final String TIME_STAMP = "timestamp";
			public static final String GRANT_TYPE_KEY = "grant_type";
			public static final String GRANT_TYPE_VALUE = "client_credentials";
			public static final String PHONE = "phone";
			/** 自定义验证码 6位数字 */
			public static final String RAND_CODE = "randcode";
			public static final String SIGN = "sign";

			public static final String RES_CODE = "res_code";
			public static final String RES_MSG = "res_message";
		}
	}

	/** RSA加密后的参数 */
	public static final String DATA = "data";

	/**校验验证码业务*/
	public static class CheckVerifyCode {
		public static final String VERIFY_CODE_SESSION_PREFIX = "VERIFY_CODE_";
		public static final String CODE_STR = "code";
		public static final String TIME_LONG = "timestamp";

		public static class RequestParams {
			public static final String PHONE = "phone";
			public static final String VERIFY_CODE = "verify_code";
			public static final String TIME_STAMP = "timestamp";
		}
	}

	/**注册业务*/
	public static class Register {
		public static class RequestParams {
			public static final String USER_ID = "user_id";
			public static final String NAME = "name";
			public static final String GENDER = "gender";
			public static final String PASSWORD = "password";
		}

		public static class ErrorInfo {
			public static final String CODE_ID_EXISTED = "201";
			public static final String MSG_ID_EXISTED = "phone has been registered";

			public static final String CODE_DISPATCH_FAILED = "202";
			public static final String MSG_DISPATCH_FAILED = "dispatch verifycode failed";

			public static final String CODE_TIMESTAMP_ERROR = "203";
			public static final String MSG_TIMESTAMP_ERROR = "timestamp is wrong";

			public static final String CODE_VERIFY_CODE_ERROR = "204";
			public static final String MSG_VERIFY_CODE_ERROR = "verify_code is wrong";

			public static final String CODE_VERIFY_CODE_EXPIRED = "205";
			public static final String MSG_VERIFY_CODE_EXPIRED = "verify_code is expired";

			public static final String CODE_REGISTER_FAILED = "206";
			public static final String MSG_REGISTER_FAILED = "register user failed";
		}
	}

	/**登录业务*/
	public static class Login {
		public static class RequestParams {
			public static final String USER_ID = "user_id";
			public static final String PASSWORD = "password";
		}

		/**
		 * 登录业务的应答参数<BR/>
		 * 用于客户端缓存
		 */
		public static class ResponseParams {
			public static final String USER_ID = "user_id";
			public static final String NAME = "name";
			public static final String GENDER = "gender";
			public static final String PHOTO = "photo";
		}

		/** 登录业务的返回码和对应信息 */
		public static class ErrorInfo {
			public static final String CODE_ID_UNRESGISTER = "301";
			public static final String MSG_ID_UNRESGISTER = "userid is unregistered";

			public static final String CODE_PSD_ERROR = "302";
			public static final String MSG_PSD_ERROR = "password id wrong";

		}
	}

	/** 修改用户信息业务 */
	public static class ModifyUserInfo {
		public static class RequestParams {
			public static final String USER_ID = "user_id";
			public static final String NAME = "name";
			public static final String GENDER = "gender";
			public static final String PHOTO = "photo";

			public static final String OLD_PSD = "old_psd";
			public static final String PASSWORD = "password";
		}

		public static class ErrorInfo {
			public static final String CODE_USER_NOT_EXIST = "401";
			public static final String MSG_USER_NOT_EXIST = "user is not exist";

			public static final String CODE_MODIFY_FAIL = "402";
			public static final String MSG_MODIFY_FAIL = "modify userinfo fail";

			public static final String CODE_OLD_PSD_WRONG = "403";
			public static final String MSG_OLD_PSD_WRONG = "old psd is wrong";

		}
	}

	/** 添加好友业务 */
	public static class AddFriend {
		public static class RequestParams {
			public static final String USER_ID = "user_id";

			public static final String VERIFY_MSG = "verify_msg";
		}

		public static class ResponseParams {
			public static final String USER_ID = "user_id";
			public static final String NAME = "name";
			public static final String GENDER = "gender";
			public static final String PHOTO = "photo";
		}

		public static class ErrorInfo {
			public static final String CODE_USER_UNREGISTER = "501";
			public static final String MSG_USER_UNREGISTER = "user is not register";
		}
	}
	
	/**删除好友业务*/
	public static class RemoveFriend{
		public static class RequestParams {
			public static final String USER_ID = "user_id";
			public static final String FRIEND_ID = "friend_id";
		}
		
		public static class ErrorInfo{
			public static final String CODE_REMOVE_FAILED = "601";
			public static final String MSG_REMOVE_FAILED = "remove friend failed";
		}
	}

	/** 获得好友列表业务 */
	public static class GetFriendList {
		public static class RequestParams {
			public static final String USER_ID = "user_id";
		}

		public static class ResponseParams {
			public static final String NUM = "num";// 对应参数是int
			public static final String FRIENDS = "friends";

		}
	}

	/** 公共的返回参数 */
	public static class ResponseParams {
		public static final String RES_CODE = "res_code";
		public static final String RES_MSG = "res_msg";
	}

	/** 公共的返回信息 */
	public static class ResponseInfo {
		public static final String CODE_SUCCESS = "0";
		public static final String MSG_SUCCESS = "success";

		public static final String CODE_DATA_NULL = "101";
		public static final String MSG_DATA_NULL = "encrypt data is null";

		public static final String CODE_PARAM_MISSED = "102";
		public static final String MSG_PARAM_MISSED = "param is missed";

		public static final String CODE_RSA_DECRYPT_ERROR = "103";
		public static final String MSG_RSA_DECRYPT_ERROR = "rsa decrypt error";

		public static final String CODE_SYSTEM_BUSY = "150";
		public static final String MSG_SYSTEM_BUSY = "system busy";
	}

}
