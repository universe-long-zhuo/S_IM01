package vaint.wyt.register;

import java.io.IOException;
import java.net.URI;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

import javax.net.ssl.SSLContext;

import net.sf.json.JSONObject;

import open189.sign.ParamsSign;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;

import vaint.wyt.constant.Constants;
import vaint.wyt.encrypt.EncryptUtils;

/**
 * 下发短信验证码的工具类<BR/>
 * <BR/>
 * 用于访问电信的天翼开放平台
 * 
 * @author Vaint
 * @E-mail vaintwyt@163.com
 * 
 */
public class VerifyCodeUtil {
	static Logger logger = Logger.getLogger(VerifyCodeUtil.class);

	/**
	 * 下发短信验证码
	 * 
	 * @param phone
	 * @param verifyCode
	 * @return 是否成功
	 */
	public static boolean DispatchVerifyCode(String phone, String verifyCode) {
		String accessToken = GetAccessToken();// 获得令牌
		if (accessToken != null) {
			String token = GetToken(accessToken);// 获得信任码
			if (token != null) {
				return SendSms(accessToken, token, phone, verifyCode);
			}
		}
		return false;
	}

	/**
	 * 调用令牌接口，获取access_token<BR/>
	 * <BR/>
	 * 
	 * HTTPS请求格式：POST <BR/>
	 * 响应格式：JOSN
	 * 
	 * @return access_token 如果调用接口失败，则返回null
	 */
	private static String GetAccessToken() {
		String accessToken = null;
		CloseableHttpClient httpClient = CreateSSLInsecureClient();
		HttpPost httpPost = new HttpPost(
				Constants.GetVerifyCode.GET_ACCESS_TOKEN_URL);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(
				Constants.GetVerifyCode.Params.GRANT_TYPE_KEY,
				Constants.GetVerifyCode.Params.GRANT_TYPE_VALUE));
		params.add(new BasicNameValuePair(Constants.GetVerifyCode.Params.APP_ID,
				Constants.GetVerifyCode.APP_ID));
		params.add(new BasicNameValuePair(
				Constants.GetVerifyCode.Params.APP_SECRET,
				Constants.GetVerifyCode.APP_SECRET));
		try {
			HttpEntity reqEntity = new UrlEncodedFormEntity(params);
			logger.debug("获取access_token的请求参数:"
					+ EntityUtils.toString(reqEntity));
			httpPost.setEntity(reqEntity);
			HttpResponse response = httpClient.execute(httpPost);
			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {
				String resStr = EntityUtils.toString(resEntity);
				JSONObject resJson = JSONObject.fromObject(resStr);
				int resCode = resJson
						.getInt(Constants.GetVerifyCode.Params.RES_CODE);
				if (resCode == 0)// 请求成功
				{
					accessToken = resJson
							.getString(Constants.GetVerifyCode.Params.ACCESS_TOKEN);
					logger.debug("获取access_token成功  access_token=" + accessToken);
				} else {
					String resMsg = resJson
							.getString(Constants.GetVerifyCode.Params.RES_MSG);
					logger.error("获取access_token失败  res_message=" + resMsg);
				}
				return accessToken;
			}
		} catch (Exception e) {
			logger.error("调用令牌接口失败:", e);
		} finally {
			httpPost.releaseConnection();
			try {
				httpClient.close();
			} catch (IOException e) {
				logger.error("调用令牌接口时关闭HttpClient异常:", e);
			}
		}
		return accessToken;
	}

	/**
	 * 获取信任码<BR/>
	 * <BR/>
	 * 
	 * HTTP请求格式：GET<BR/>
	 * 响应格式：JOSN
	 * 
	 * @return token 如果调用接口失败，则返回null
	 */
	private static String GetToken(String accessToken) {
		String token = null;
		CloseableHttpClient httpClient = HttpClients.createDefault();
		HttpGet httpGet = new HttpGet();
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		String timeStamp = dateFormat.format(date);

		TreeMap<String, String> paramsMap = new TreeMap<String, String>();
		paramsMap.put(Constants.GetVerifyCode.Params.APP_ID,
				Constants.GetVerifyCode.APP_ID);
		paramsMap.put(Constants.GetVerifyCode.Params.ACCESS_TOKEN, accessToken);
		paramsMap.put(Constants.GetVerifyCode.Params.TIME_STAMP, timeStamp);
		// 处理时间戳的空格，GET请求的URI不允许有空格
		timeStamp = timeStamp.replace(" ", "+");
		String reqUri = Constants.GetVerifyCode.GET_TOKEN_URL
				+ "?"
				+ Constants.GetVerifyCode.Params.APP_ID
				+ "="
				+ Constants.GetVerifyCode.APP_ID
				+ "&"
				+ Constants.GetVerifyCode.Params.ACCESS_TOKEN
				+ "="
				+ accessToken
				+ "&"
				+ Constants.GetVerifyCode.Params.TIME_STAMP
				+ "="
				+ timeStamp
				+ "&"
				+ Constants.GetVerifyCode.Params.SIGN
				+ "="
				+ ParamsSign.value(paramsMap,
						Constants.GetVerifyCode.APP_SECRET);
		logger.debug("获取信任码的请求Uri:" + reqUri);
		try {
			httpGet = new HttpGet(new URI(reqUri));

			HttpResponse response = httpClient.execute(httpGet);
			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {
				String resStr = EntityUtils.toString(resEntity);
				JSONObject resJson = JSONObject.fromObject(resStr);
				int resCode = resJson
						.getInt(Constants.GetVerifyCode.Params.RES_CODE);
				if (resCode == 0)// 请求成功
				{
					token = resJson
							.getString(Constants.GetVerifyCode.Params.TOKEN);
					logger.debug("获取token成功  token=" + token);
				} else {
					String resMsg = resJson
							.getString(Constants.GetVerifyCode.Params.RES_MSG);
					logger.error("获取token失败  res_message=" + resMsg);
				}
			}
		} catch (Exception e) {
			logger.error("调用获取信任码接口失败:", e);
		} finally {
			httpGet.releaseConnection();
			try {
				httpClient.close();
			} catch (IOException e) {
				logger.error("调用获取信任码接口时关闭HttpClient异常:", e);
			}
		}
		return token;
	}

	/**
	 * 下发验证码<BR/>
	 * <BR/>
	 * 
	 * HTTP请求格式：POST<BR/>
	 * 响应格式：JOSN
	 * 
	 * @return 下发验证码是否成功
	 */
	private static boolean SendSms(String accessToken, String token,
			String phone, String verifyCode) {
		boolean bRet = false;
		CloseableHttpClient httpClient = HttpClients.createDefault();

		HttpPost httpPost = new HttpPost(Constants.GetVerifyCode.SEND_SMS_URL);
		httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
		httpPost.setHeader("Host", "app.com");
		// 拼接参数
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(Constants.GetVerifyCode.Params.APP_ID,
				Constants.GetVerifyCode.APP_ID));
		params.add(new BasicNameValuePair(
				Constants.GetVerifyCode.Params.ACCESS_TOKEN, accessToken));
		params.add(new BasicNameValuePair(Constants.GetVerifyCode.Params.TOKEN,
				token));
		params.add(new BasicNameValuePair(Constants.GetVerifyCode.Params.PHONE,
				phone));
		params.add(new BasicNameValuePair(
				Constants.GetVerifyCode.Params.RAND_CODE, verifyCode));

		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd HH:mm:ss");
		String timeStamp = dateFormat.format(date);
		params.add(new BasicNameValuePair(
				Constants.GetVerifyCode.Params.TIME_STAMP, timeStamp));

		// 参数加密
		TreeMap<String, String> paramsMap = new TreeMap<String, String>();
		paramsMap.put(Constants.GetVerifyCode.Params.APP_ID,
				Constants.GetVerifyCode.APP_ID);
		paramsMap.put(Constants.GetVerifyCode.Params.ACCESS_TOKEN, accessToken);
		paramsMap.put(Constants.GetVerifyCode.Params.TOKEN, token);
		paramsMap.put(Constants.GetVerifyCode.Params.PHONE, phone);
		paramsMap.put(Constants.GetVerifyCode.Params.RAND_CODE, verifyCode);
		paramsMap.put(Constants.GetVerifyCode.Params.TIME_STAMP, timeStamp);

		String sign = EncryptUtils.getSmsSign(paramsMap,
				Constants.GetVerifyCode.APP_SECRET);
		params.add(new BasicNameValuePair(Constants.GetVerifyCode.Params.SIGN,
				sign));

		try {
			HttpEntity reqEntity = new UrlEncodedFormEntity(params);
			logger.debug("下发验证码的请求参数:" + EntityUtils.toString(reqEntity));
			httpPost.setEntity(reqEntity);

			HttpResponse response = httpClient.execute(httpPost);

			HttpEntity resEntity = response.getEntity();

			if (resEntity != null) {
				String resStr = EntityUtils.toString(resEntity);
				JSONObject resJson = JSONObject.fromObject(resStr);
				int resCode = resJson
						.getInt(Constants.GetVerifyCode.Params.RES_CODE);
				if (resCode == 0)// 请求成功
				{
					logger.debug("短信验证码下发成功");
					bRet = true;
				} else {
					String resMsg = resJson
							.getString(Constants.GetVerifyCode.Params.RES_MSG);
					logger.error("短信验证码下发失败  res_message=" + resMsg);
					bRet = false;
				}
			}
		} catch (Exception e) {
			logger.error("调用下发验证码接口失败:", e);
			bRet = false;
		} finally {
			httpPost.releaseConnection();
			try {
				httpClient.close();
			} catch (IOException e) {
				logger.error("调用下发验证码接口时关闭HttpClient异常:", e);
			}
		}
		return bRet;
	}

	/** 获得可用于访问Https的HttpClient */
	private static CloseableHttpClient CreateSSLInsecureClient() {
		try {
			SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(
					null, new TrustStrategy() {
						// 信任所有
						public boolean isTrusted(X509Certificate[] chain,
								String authType) throws CertificateException {
							return true;
						}
					}).build();
			SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
					sslContext);
			return HttpClients.custom().setSSLSocketFactory(sslsf).build();
		} catch (Exception e) {
			logger.error("Https配置出现异常：", e);
		}
		return HttpClients.createDefault();
	}

}
