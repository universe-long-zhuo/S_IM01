package vaint.wyt.http;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

import vaint.wyt.constant.Constants;
import vaint.wyt.encrypt.EncryptUtils;

import net.sf.json.JSONObject;

public class HttpUtils {
	static Logger logger = Logger.getLogger(HttpUtils.class);
	
	/**
	 * 解析请求参数<BR/>判断加密数据是否为空，以及是否正确<BR/>如果正确，则进行RSA解密
	 * @param request
	 * @return 解析成功时返回成功标识和JSON格式的请求参数<BR/>解释失败则返回失败标识和应答参数
	 */
	public static RetBean ResolveParams(HttpServletRequest request) {
		RetBean ret = new RetBean();
		// 返回参数
		JSONObject respJson = new JSONObject();

		String data = request.getParameter(Constants.DATA);
		if (data == null || data.isEmpty()) {
			logger.error("加密数据有误");
			respJson.element(Constants.ResponseParams.RES_CODE,
					Constants.ResponseInfo.CODE_DATA_NULL);
			respJson.element(Constants.ResponseParams.RES_MSG,
					Constants.ResponseInfo.MSG_DATA_NULL);
			
			ret.setSuccess(false);
			ret.setRespJson(respJson);
			return ret;
		}

		// RSA解密
		String deStr = EncryptUtils.GetRsaDecrypt(data);
		if (deStr == null) {
			logger.error("RSA解密错误");
			respJson.element(Constants.ResponseParams.RES_CODE,
					Constants.ResponseInfo.CODE_RSA_DECRYPT_ERROR);
			respJson.element(Constants.ResponseParams.RES_MSG,
					Constants.ResponseInfo.MSG_RSA_DECRYPT_ERROR);
			
			ret.setSuccess(false);
			ret.setRespJson(respJson);
			return ret;
		}

		logger.debug("解密后的请求参数:" + deStr);

		JSONObject reqJson = JSONObject.fromObject(deStr);
		ret.setSuccess(true);
		ret.setReqJson(reqJson);
		return ret;
	}

}
