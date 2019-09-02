/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nmghr;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 服务启动执行
 *
 * @author kaven  
 * @date 2018年6月13日 下午5:34:19 
 * @version 1.0   
 */
@Component
@Order(value=1)
public class LoadStartupRunner implements CommandLineRunner{

  public void run(String... args) throws Exception {
    System.out.println("启动执行1-----");
    
  }

}
