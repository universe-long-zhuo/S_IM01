package vaint.wyt.manager;

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

public class ModifyPsdServlet extends HttpServlet {
	private static final long serialVersionUID = -4973467909703489069L;
	static Logger logger = Logger.getLogger(ModifyPsdServlet.class);

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
				.optString(Constants.ModifyUserInfo.RequestParams.USER_ID);
		String oldPsd = reqJson
				.optString(Constants.ModifyUserInfo.RequestParams.OLD_PSD);
		String newPsd = reqJson
				.optString(Constants.ModifyUserInfo.RequestParams.PASSWORD);

		// 参数缺失
		if (userId.isEmpty() || oldPsd.isEmpty() || newPsd.isEmpty()) {
			logger.error("请求参数缺失");
			respJson.element(Constants.ResponseParams.RES_CODE,
					Constants.ResponseInfo.CODE_PARAM_MISSED);
			respJson.element(Constants.ResponseParams.RES_MSG,
					Constants.ResponseInfo.MSG_PARAM_MISSED);
			out.write(respJson.toString());
			return;
		}

		User user = (User) HibernateUtils.get(User.class, userId);
		if (user == null) {
			logger.error("当前用户不存在");
			respJson.element(Constants.ResponseParams.RES_CODE,
					Constants.ModifyUserInfo.ErrorInfo.CODE_USER_NOT_EXIST);
			respJson.element(Constants.ResponseParams.RES_MSG,
					Constants.ModifyUserInfo.ErrorInfo.MSG_USER_NOT_EXIST);
			out.write(respJson.toString());
			return;
		}

		if (!user.getPassword().equals(oldPsd)) {
			logger.error("旧密码不正确");
			respJson.element(Constants.ResponseParams.RES_CODE,
					Constants.ModifyUserInfo.ErrorInfo.CODE_OLD_PSD_WRONG);
			respJson.element(Constants.ResponseParams.RES_MSG,
					Constants.ModifyUserInfo.ErrorInfo.MSG_OLD_PSD_WRONG);
			out.write(respJson.toString());
			return;
		}

		user.setPassword(newPsd);
		boolean ret = HibernateUtils.update(user);
		if (ret) {
			logger.debug("修改登录密码成功");
			respJson.element(Constants.ResponseParams.RES_CODE,
					Constants.ResponseInfo.CODE_SUCCESS);
			respJson.element(Constants.ResponseParams.RES_MSG,
					Constants.ResponseInfo.MSG_SUCCESS);
		} else {
			logger.error("修改登录密码失败");
			respJson.element(Constants.ResponseParams.RES_CODE,
					Constants.ModifyUserInfo.ErrorInfo.CODE_MODIFY_FAIL);
			respJson.element(Constants.ResponseParams.RES_MSG,
					Constants.ModifyUserInfo.ErrorInfo.MSG_MODIFY_FAIL);
		}
		out.write(respJson.toString());
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}

}
