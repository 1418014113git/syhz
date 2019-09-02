/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.nmghr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <功能描述/>
 *
 * @author kaven
 * @date 2018年6月13日 下午5:17:07
 * @version 1.0
 */
@EnableJms
@SpringBootApplication
@RestController
public class SyhzApplication extends SpringBootServletInitializer{

  @GetMapping("/ping")
  public String ping() {
    return "PONG";
  }

  public static void main(String[] args) {
    SpringApplication.run(SyhzApplication.class, args);
  }
  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
      // TODO Auto-generated method stub
      return builder.sources(SyhzApplication.class);
  }
}
