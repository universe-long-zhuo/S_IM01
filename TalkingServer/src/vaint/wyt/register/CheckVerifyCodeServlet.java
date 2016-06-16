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
import vaint.wyt.http.HttpUtils;
import vaint.wyt.http.RetBean;

public class CheckVerifyCodeServlet extends HttpServlet {
	private static final long serialVersionUID = 7034414310448253290L;
	static Logger logger = Logger.getLogger(CheckVerifyCodeServlet.class);

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

		String phone = reqJson
				.optString(Constants.CheckVerifyCode.RequestParams.PHONE);
		String verifyCode = reqJson
				.optString(Constants.CheckVerifyCode.RequestParams.VERIFY_CODE);
		String timestamp = reqJson
				.optString(Constants.CheckVerifyCode.RequestParams.TIME_STAMP);

		JSONObject respJson = new JSONObject();
		// 参数缺失
		if (phone.isEmpty() || verifyCode.isEmpty() || timestamp.isEmpty()) {
			logger.error("请求参数缺失");
			respJson.element(Constants.ResponseParams.RES_CODE,
					Constants.ResponseInfo.CODE_PARAM_MISSED);
			respJson.element(Constants.ResponseParams.RES_MSG,
					Constants.ResponseInfo.MSG_PARAM_MISSED);
			out.write(respJson.toString());
			return;
		}

		// 取出application数据
		JSONObject codeJson = (JSONObject) request
				.getSession()
				.getServletContext()
				.getAttribute(
						Constants.CheckVerifyCode.VERIFY_CODE_SESSION_PREFIX
								+ phone);
		logger.debug("session数据:" + codeJson.toString());
		String code = codeJson.optString(Constants.CheckVerifyCode.CODE_STR);
		long time = codeJson.optLong(Constants.CheckVerifyCode.TIME_LONG);

		// 校验验证码和时间戳
		if (code != null && code.equals(verifyCode)) {
			long spanTime;
			try {
				spanTime = Long.parseLong(timestamp) - time;
			} catch (Exception e) {
				logger.error("userid=" + phone + " 请求参数timestamp格式不正确");
				respJson.element(Constants.ResponseParams.RES_CODE,
						Constants.Register.ErrorInfo.CODE_TIMESTAMP_ERROR);
				respJson.element(Constants.ResponseParams.RES_MSG,
						Constants.Register.ErrorInfo.MSG_TIMESTAMP_ERROR);
				out.write(respJson.toString());
				return;
			}

			if (spanTime > 0 && spanTime < 2 * 60 * 1000)// 两分钟内
			{
				logger.info("userid=" + phone + " 验证成功");
				respJson.element(Constants.ResponseParams.RES_CODE,
						Constants.ResponseInfo.CODE_SUCCESS);
				respJson.element(Constants.ResponseParams.RES_MSG,
						Constants.ResponseInfo.MSG_SUCCESS);
			} else// 验证码超时
			{
				logger.info("userid=" + phone + " 验证码过期");
				respJson.element(Constants.ResponseParams.RES_CODE,
						Constants.Register.ErrorInfo.CODE_VERIFY_CODE_EXPIRED);
				respJson.element(Constants.ResponseParams.RES_MSG,
						Constants.Register.ErrorInfo.MSG_VERIFY_CODE_EXPIRED);
			}
		} else// 验证码错误
		{
			logger.info("phone=" + phone + "的验证码错误");
			respJson.element(Constants.ResponseParams.RES_CODE,
					Constants.Register.ErrorInfo.CODE_VERIFY_CODE_ERROR);
			respJson.element(Constants.ResponseParams.RES_MSG,
					Constants.Register.ErrorInfo.MSG_VERIFY_CODE_ERROR);
		}
		out.write(respJson.toString());
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}

}
