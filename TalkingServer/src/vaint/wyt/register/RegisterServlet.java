package vaint.wyt.register;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import vaint.wyt.constant.Constants;
import vaint.wyt.db.HibernateUtils;
import vaint.wyt.db.User;
import vaint.wyt.http.HttpUtils;
import vaint.wyt.http.RetBean;

public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = -5635576450887704312L;
	static Logger logger = Logger.getLogger(RegisterServlet.class);

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();

		// 解析请求参数
		RetBean retBean = HttpUtils.ResolveParams(request);
		if (!retBean.isSuccess()) {
			out.write(retBean.getRespJson().toString());
			return;
		}

		JSONObject reqJson = retBean.getReqJson();

		JSONObject respJson = new JSONObject();
		
		String userId = reqJson
				.optString(Constants.Register.RequestParams.USER_ID);
		String name = reqJson.optString(Constants.Register.RequestParams.NAME);
		String gender = reqJson.optString(Constants.Register.RequestParams.GENDER);
		String password = reqJson
				.optString(Constants.Register.RequestParams.PASSWORD);

		// 参数缺失
		if (userId.isEmpty() || name.isEmpty() || gender.isEmpty() || password.isEmpty()) {
			logger.error("请求参数缺失");
			respJson.element(Constants.ResponseParams.RES_CODE,
					Constants.ResponseInfo.CODE_PARAM_MISSED);
			respJson.element(Constants.ResponseParams.RES_MSG,
					Constants.ResponseInfo.MSG_PARAM_MISSED);
			out.write(respJson.toString());
			return;
		}

		// 查询该号码是否已经注册
		User user = (User) HibernateUtils.get(User.class, userId);
		if (user != null) {
			logger.info("手机号:" + userId + " 已经注册过");
			respJson.element(Constants.ResponseParams.RES_CODE,
					Constants.Register.ErrorInfo.CODE_ID_EXISTED);
			respJson.element(Constants.ResponseParams.RES_MSG,
					Constants.Register.ErrorInfo.MSG_ID_EXISTED);
			out.write(respJson.toString());
			return;
		}
		// 保存用户信息
		User userInfo = new User();
		userInfo.setUserId(userId);
		userInfo.setName(name);
		userInfo.setGender(gender);
		userInfo.setPassword(password);
		boolean bRet = HibernateUtils.add(userInfo);

		if (bRet) {
			logger.info("userid:" + userId + " 注册成功");
			respJson.element(Constants.ResponseParams.RES_CODE,
					Constants.ResponseInfo.CODE_SUCCESS);
			respJson.element(Constants.ResponseParams.RES_MSG,
					Constants.ResponseInfo.MSG_SUCCESS);
		} else {
			logger.info("userid:" + userId + " 注册失败");
			respJson.element(Constants.ResponseParams.RES_CODE,
					Constants.Register.ErrorInfo.CODE_REGISTER_FAILED);
			respJson.element(Constants.ResponseParams.RES_MSG,
					Constants.Register.ErrorInfo.MSG_REGISTER_FAILED);
		}
		out.write(respJson.toString());
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}

}
