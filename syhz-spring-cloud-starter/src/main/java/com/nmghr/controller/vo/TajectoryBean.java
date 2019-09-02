/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.controller.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * <功能描述/>
 *
 * @author weber
 * @date 2019年1月21日 上午11:33:03
 * @version 1.0
 */
public class TajectoryBean {
  private String type;
  private String args;
  private Date dateTime;

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getArgs() {
    return args;
  }

  public void setArgs(String args) {
    this.args = args;
  }

  @JsonFormat(pattern = "yyyy年MM月dd日", timezone = "GMT+8")
  public Date getDateTime() {
    return dateTime;
  }

  public void setDateTime(Date dateTime) {
    this.dateTime = dateTime;
  }
}
