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
import vaint.wyt.db.FriendsUtils;
import vaint.wyt.http.HttpUtils;
import vaint.wyt.http.RetBean;

public class GetFriendListServlet extends HttpServlet {
	private static final long serialVersionUID = -3504833814436282009L;
	static Logger logger = Logger.getLogger(GetFriendListServlet.class);

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
				.optString(Constants.GetFriendList.RequestParams.USER_ID);
		// 参数缺失
		if (userId.isEmpty()) {
			logger.error("请求参数缺失");
			respJson.element(Constants.ResponseParams.RES_CODE,
					Constants.ResponseInfo.CODE_PARAM_MISSED);
			respJson.element(Constants.ResponseParams.RES_MSG,
					Constants.ResponseInfo.MSG_PARAM_MISSED);
			out.write(respJson.toString());
			return;
		}

		// 获得好友信息
		respJson = FriendsUtils.GetFriendInfo(userId);

		respJson.element(Constants.ResponseParams.RES_CODE,
				Constants.ResponseInfo.CODE_SUCCESS);
		respJson.element(Constants.ResponseParams.RES_MSG,
				Constants.ResponseInfo.MSG_SUCCESS);

		logger.debug("返回参数:" + respJson.toString());
		out.write(respJson.toString());
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}

}