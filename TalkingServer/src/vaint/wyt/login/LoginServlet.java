package vaint.wyt.login;

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

public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 7037276994882555441L;
	static Logger logger = Logger.getLogger(LoginServlet.class);

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

		String userId = reqJson
				.optString(Constants.Login.RequestParams.USER_ID);
		String password = reqJson
				.optString(Constants.Login.RequestParams.PASSWORD);

		JSONObject respJson = new JSONObject();
		// 参数缺失
		if (userId.isEmpty() || password.isEmpty()) {
			logger.error("请求参数缺失");
			respJson.element(Constants.ResponseParams.RES_CODE,
					Constants.ResponseInfo.CODE_PARAM_MISSED);
			respJson.element(Constants.ResponseParams.RES_MSG,
					Constants.ResponseInfo.MSG_PARAM_MISSED);
			out.write(respJson.toString());
			return;
		}

		// 校验用户ID和密码
		User user = (User) HibernateUtils.get(User.class, userId);
		if (user != null) {
			String psd = user.getPassword();
			if (psd.equals(password)) {
				logger.debug("userId=" + userId + " 登录成功");
				respJson.element(Constants.ResponseParams.RES_CODE,
						Constants.ResponseInfo.CODE_SUCCESS);
				respJson.element(Constants.ResponseParams.RES_MSG,
						Constants.ResponseInfo.MSG_SUCCESS);
				// 返回用户信息，用于缓存
				respJson.element(Constants.Login.ResponseParams.USER_ID,
						user.getUserId());
				respJson.element(Constants.Login.ResponseParams.NAME,
						user.getName());
				respJson.element(Constants.Login.ResponseParams.GENDER,
						user.getGender());
				respJson.element(Constants.Login.ResponseParams.PHOTO,
						user.getPhoto());

			} else {
				logger.debug("userId=" + userId + " 登录密码不正确");
				respJson.element(Constants.ResponseParams.RES_CODE,
						Constants.Login.ErrorInfo.CODE_PSD_ERROR);
				respJson.element(Constants.ResponseParams.RES_MSG,
						Constants.Login.ErrorInfo.MSG_PSD_ERROR);
			}
		} else {
			logger.debug("userId=" + userId + " 未注册");
			respJson.element(Constants.ResponseParams.RES_CODE,
					Constants.Login.ErrorInfo.CODE_ID_UNRESGISTER);
			respJson.element(Constants.ResponseParams.RES_MSG,
					Constants.Login.ErrorInfo.MSG_ID_UNRESGISTER);
		}
		logger.debug("返回参数:" + respJson.toString());
		out.write(respJson.toString());
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}

}
