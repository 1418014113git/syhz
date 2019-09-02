/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nmghr.controller.vo;

import java.util.Map;

/**
 * 人案关联分数bean
 *
 * @author weber  
 * @date 2019年1月23日 下午2:47:39 
 * @version 1.0   
 */
public class XyrBean {
  //人物基本信息
  private Map<String, Object> bean;
  //总分数
  private int totalScore;
  //同出行
  private int tcx;
  //同入住
  private int trz;
  //通话记录
  private int thjl;
  //同案人
  private int tar;
  public Map<String, Object> getBean() {
    return bean;
  }
  public void setBean(Map<String, Object> bean) {
    this.bean = bean;
  }
  public int getTotalScore() {
    return totalScore;
  }
  public void setTotalScore(int totalScore) {
    this.totalScore = totalScore;
  }
  public int getTcx() {
    return tcx;
  }
  public void setTcx(int tcx) {
    this.tcx = tcx;
  }
  public int getTrz() {
    return trz;
  }
  public void setTrz(int trz) {
    this.trz = trz;
  }
  public int getThjl() {
    return thjl;
  }
  public void setThjl(int thjl) {
    this.thjl = thjl;
  }
  public int getTar() {
    return tar;
  }
  public void setTar(int tar) {
    this.tar = tar;
  }
  
  
}
