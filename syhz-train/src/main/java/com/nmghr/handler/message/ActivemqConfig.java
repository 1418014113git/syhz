/**Copyright(C)2018 @内蒙古慧瑞.**Unless required by applicable law or agreed to in writing,software*distributed under the License is distributed on an"AS IS"BASIS,*WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,either express or implied.*See the License for the specific language governing permissions and*limitations under the License.*/

package com.nmghr.handler.message;

import javax.jms.ConnectionFactory;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;

/**
 * <功能描述/>**
 * 
 * @author brook*@date 2019 年6月17日 下午4:57:45*@version 1.0
 */
@Configuration
public class ActivemqConfig {

	/**
	 * 实现监听queue
	 *
	 * @param connectionFactory
	 * @return
	 */
	@Bean
	public JmsListenerContainerFactory<?> queueContainerFactory(ConnectionFactory connectionFactory) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setPubSubDomain(false);
		return factory;
	}

	/**
	 * 实现监听topic
	 *
	 * @param connectionFactory
	 * @return
	 */
	@Bean
	public JmsListenerContainerFactory<?> topicContainerFactory(ConnectionFactory connectionFactory) {
		DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
		factory.setConnectionFactory(connectionFactory);
		factory.setPubSubDomain(true);
		return factory;
	}
}
