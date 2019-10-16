/**Copyright(C)2018 @内蒙古慧瑞.**Unless required by applicable law or agreed to in writing,software*distributed under the License is distributed on an"AS IS"BASIS,*WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.*See the License for the specific language governing permissions and*limitations under the License.*/

package com.nmghr.handler.message;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

/***
 * <功能描述/>**
 * 
 * @author zhanghang*@date 2019 年6月18日 14:21*@version 1.0
 */
@Component
public class ConsumerServiceImpl {
	Logger logger = LoggerFactory.getLogger(ConsumerServiceImpl.class);

	// 个人接收报名消息
	@JmsListener(destination = QueueConfig.KNOWLEDGE, containerFactory = "queueContainerFactory")
	public void consumeSignUpQueue(String message) throws Exception {
		Map<String, Object> map = JSON.parseObject(message);
		logger.info("knowledge", map);
		OkHttpUtil.post(JSON.toJSONString(map));
	}

}