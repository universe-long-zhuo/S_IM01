package vaint.wyt.register;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import vaint.wyt.constant.Constants;
import vaint.wyt.http.HttpUtils;
import vaint.wyt.http.RetBean;

public class GetVerifyCodeServlet extends HttpServlet {
	private static final long serialVersionUID = 1009260845016545924L;
	static Logger logger = Logger.getLogger(GetVerifyCodeServlet.class);

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();

		// 解析请求参数
		RetBean ret = HttpUtils.ResolveParams(request);
		if (!ret.isSuccess()) {
			out.write(ret.getRespJson().toString());
			return;
		}

		JSONObject reqJson = ret.getReqJson();

		JSONObject respJson = new JSONObject();
		String phone = reqJson
				.optString(Constants.CheckVerifyCode.RequestParams.PHONE);
		if (phone.isEmpty()) {
			logger.error("请求参数缺失");
			respJson.element(Constants.ResponseParams.RES_CODE,
					Constants.ResponseInfo.CODE_PARAM_MISSED);
			respJson.element(Constants.ResponseParams.RES_MSG,
					Constants.ResponseInfo.MSG_PARAM_MISSED);
			out.write(respJson.toString());
			return;
		}
		logger.debug("验证码请求手机号:" + phone);

		Random ran = new Random();
		String code = Math.abs(ran.nextLong()) % (999999 - 100000 + 1) + 100000
				+ "";
		logger.debug("生成的验证码:" + code);
		boolean bRet = VerifyCodeUtil.DispatchVerifyCode(phone, code);

		if (bRet) {
			logger.info("下发验证码成功  phone=" + phone + ", code=" + code);
			// 用application存储验证码信息
			JSONObject codeJson = new JSONObject();
			codeJson.element(Constants.CheckVerifyCode.CODE_STR, code);
			codeJson.element(Constants.CheckVerifyCode.TIME_LONG,
					System.currentTimeMillis());
			ServletContext sc = request.getSession().getServletContext();
			sc.setAttribute(
					Constants.CheckVerifyCode.VERIFY_CODE_SESSION_PREFIX
							+ phone, codeJson);

			respJson.element(Constants.ResponseParams.RES_CODE,
					Constants.ResponseInfo.CODE_SUCCESS);
			respJson.element(Constants.ResponseParams.RES_MSG,
					Constants.ResponseInfo.MSG_SUCCESS);
		} else {
			logger.info("下发验证码失败  phone=" + phone + ", code=" + code);

			respJson.element(Constants.ResponseParams.RES_CODE,
					Constants.Register.ErrorInfo.CODE_DISPATCH_FAILED);
			respJson.element(Constants.ResponseParams.RES_MSG,
					Constants.Register.ErrorInfo.MSG_DISPATCH_FAILED);
		}

		out.write(respJson.toString());
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		doGet(request, response);
	}

}
