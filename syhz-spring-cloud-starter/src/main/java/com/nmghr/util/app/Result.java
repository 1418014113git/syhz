/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.util.app;

import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONObject;
import com.nmghr.entity.ErrorEntity;

/**
 * 返回结果工具类.
 *
 * @author kaven
 * @date 2019年11月25日 上午11:44:07
 * @version 1.0
 */
public class Result extends com.nmghr.basic.common.Result {

	private String jsonrpc;
	private String id;
	private Object result;
	private ErrorEntity error;// 错误信息

	public String getJsonrpc() {
		return jsonrpc;
	}

	public void setJsonrpc(String jsonrpc) {
		this.jsonrpc = jsonrpc;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

	public ErrorEntity getError() {
		return error;
	}

	public void setError(ErrorEntity error) {
		this.error = error;
	}

	protected Result(Object code, String message, Object data) {
	}

	public Result(String jsonrpc, String id, Object result) {
		this.jsonrpc = jsonrpc;
		this.id = id;
		this.result = result;
	}

	/**
	 * 创建返回结果.
	 * 
	 * @param code
	 *            返回码
	 * @param message
	 *            返回信息
	 * @param data
	 *            返回数据
	 * @param desc
	 *            返回描述
	 * @return Result
	 */
	public static String create(String jsonrpc, String id, Object result) {
		return JSONObject.toJSONString(new Result(jsonrpc, id, result));
	}

	/**
	 * 创建返回成功结果.
	 * 
	 * @param data
	 *            数据
	 * @return Result
	 */
	public static String ok(String jsonrpc, String id, Object result) {
		// Result resultOk = new Result(null, null);
		// resultOk.setJsonrpc(jsonrpc);
		// resultOk.setId(id);
		// resultOk.setResult(result);
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("jsonrpc", jsonrpc);
		retMap.put("id", id);
		retMap.put("result", result);
		String JsonStr = JSONObject.toJSONString(retMap);
		System.out.println("----JsonStr:" + JsonStr);
		return JsonStr;
	}

	/**
	 * 返回错误结果.
	 * 
	 * @param code
	 *            返回码
	 * @param errMessage
	 *            错误信息
	 * @return Result
	 */
	public static String fail(String jsonrpc, String id, String code, String message) {
		// Result resultFail = new Result(null, null);
		// resultFail.setJsonrpc(jsonrpc);
		// resultFail.setId(id);
		ErrorEntity error = new ErrorEntity(code, message);
		// resultFail.setError(error);
		Map<String, Object> retMap = new HashMap<String, Object>();
		retMap.put("jsonrpc", jsonrpc);
		retMap.put("id", id);
		retMap.put("error", error);
		return JSONObject.toJSONString(retMap);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Result [jsonrpc=");
		builder.append(jsonrpc);
		builder.append(", id=");
		builder.append(id);
		builder.append(", result=");
		builder.append(result);
		builder.append(']');
		return builder.toString();
	}

}
