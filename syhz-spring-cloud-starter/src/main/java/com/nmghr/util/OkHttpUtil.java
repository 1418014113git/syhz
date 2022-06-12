/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.util;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * <功能描述/>
 *
 * @author brook
 * @date 2018年10月26日 下午12:04:32
 * @version 1.0
 */
public class OkHttpUtil {
  private static final OkHttpClient OkHttpClient = new OkHttpClient();
  
  private static final Logger log = LoggerFactory.getLogger(OkHttpUtil.class);
  public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

  public static Response execute(Request request) throws IOException {

      return OkHttpClient.newCall(request).execute();
  }

  public static void enqueue(Request request, Callback responseCallback) {

      OkHttpClient.newCall(request).enqueue(responseCallback);
  }

  public static String getStringFromServer(String url) throws IOException {

      Request request = new Request.Builder().url(url).build();
      Response response = execute(request);
      if (response.isSuccessful()) {
          String responseUrl = response.body().string();
          return responseUrl;
      } else {
          throw new IOException("Unexpected code " + response);
      }
  }

  /**
   * 提交json数据
   * 
   * @param url
   * @param json
   * @return
   * @throws IOException
   */
  public static String post(String url, Map<String, Object> map) throws IOException {

      Gson gson = new GsonBuilder().disableHtmlEscaping().create();
      String json = gson.toJson(map);
      /*String json = StringEscapeUtils.unescapeEcmaScript(gson.toJson(map));*/
      log.info("gson format value={}"+ json);
      RequestBody body = RequestBody.create(JSON, json);
      
      Request request = new Request.Builder().url(url).post(body).build();
      Response response = OkHttpClient.newCall(request).execute();
      
      if (response.isSuccessful()) {
          return response.body().string();
      } else {
          log.info(" return response message"+ response);
          throw new IOException("Unexpected code " + response);
      }
  }
  
  public static String post(String url, String json) throws IOException {
    log.info("gson format value={}"+ json);
    RequestBody body = RequestBody.create(JSON, json);
    
    Request request = new Request.Builder().url(url).post(body).build();
    Response response = OkHttpClient.newCall(request).execute();
    
    if (response.isSuccessful()) {
        return response.body().string();
    } else {
        log.info(" return response message"+ response);
        throw new IOException("Unexpected code " + response);
    }
}

  /**
   * 通联支付post
   * 
   * @param url
   * @param body
   * @return
   * @throws IOException
   */
  public static Map<String, String> post(String url, RequestBody body) throws IOException {

      Request request = new Request.Builder().url(url).post(body).build();
      Response response = OkHttpClient.newCall(request).execute();
      if (response.isSuccessful()) {
          String postRequest = response.body().string();
          Map<String, String> requestMap = new HashMap<String, String>();
          String[] strArr = postRequest.split("&");
          if (strArr.length > 1) {
              for (String string : strArr) {
                  String[] target = string.split("=");
                  String name = target[0];
                  String value = target[1];
                  requestMap.put(name, value);
              }
          } else {
              Gson gson = new GsonBuilder().enableComplexMapKeySerialization().create();
              Type type = new TypeToken<Map<String, String>>() {
              }.getType();
              requestMap = gson.fromJson(postRequest, type);
          }
          return requestMap;
      } else {
          throw new IOException("Unexpected code " + response);
      }

  }
}
