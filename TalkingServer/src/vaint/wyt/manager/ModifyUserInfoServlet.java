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

public class ModifyUserInfoServlet extends HttpServlet {
	private static final long serialVersionUID = -724285182909940927L;
	static Logger logger = Logger.getLogger(ModifyUserInfoServlet.class);

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
		
		//获得photo参数
		String photo = request.getParameter(Constants.ModifyUserInfo.RequestParams.PHOTO);
		logger.debug("photo="+photo);

		JSONObject respJson = new JSONObject();
		String userId = reqJson
				.optString(Constants.ModifyUserInfo.RequestParams.USER_ID);
		String name = reqJson
				.optString(Constants.ModifyUserInfo.RequestParams.NAME);
		String gender = reqJson
				.optString(Constants.ModifyUserInfo.RequestParams.GENDER);
		
		// 参数缺失
		if (userId.isEmpty() || name.isEmpty() || gender.isEmpty()) {
			logger.error("请求参数缺失");
			respJson.element(Constants.ResponseParams.RES_CODE,
					Constants.ResponseInfo.CODE_PARAM_MISSED);
			respJson.element(Constants.ResponseParams.RES_MSG,
					Constants.ResponseInfo.MSG_PARAM_MISSED);
			out.write(respJson.toString());
			return;
		}
		
		User user = (User) HibernateUtils.get(User.class, userId);
		if(user == null)
		{
			logger.error("当前用户不存在");
			respJson.element(Constants.ResponseParams.RES_CODE,
					Constants.ModifyUserInfo.ErrorInfo.CODE_USER_NOT_EXIST);
			respJson.element(Constants.ResponseParams.RES_MSG,
					Constants.ModifyUserInfo.ErrorInfo.MSG_USER_NOT_EXIST);
			out.write(respJson.toString());
			return;
		}
		
		user.setName(name);
		user.setGender(gender);
		if(photo != null && !photo.isEmpty())
		{
			user.setPhoto(photo);
		}
		boolean ret = HibernateUtils.update(user);
		if(ret)
		{
			logger.debug("修改用户信息成功");
			respJson.element(Constants.ResponseParams.RES_CODE,
					Constants.ResponseInfo.CODE_SUCCESS);
			respJson.element(Constants.ResponseParams.RES_MSG,
					Constants.ResponseInfo.MSG_SUCCESS);
		}else
		{
			logger.error("修改用户信息失败");
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
