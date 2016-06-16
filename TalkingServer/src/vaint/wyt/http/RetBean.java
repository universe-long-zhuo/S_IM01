package vaint.wyt.http;

import net.sf.json.JSONObject;

public class RetBean {
	private boolean success;
	private JSONObject reqJson;
	private JSONObject respJson;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public JSONObject getReqJson() {
		return reqJson;
	}

	public void setReqJson(JSONObject reqJson) {
		this.reqJson = reqJson;
	}

	public JSONObject getRespJson() {
		return respJson;
	}

	public void setRespJson(JSONObject respJson) {
		this.respJson = respJson;
	}

}
