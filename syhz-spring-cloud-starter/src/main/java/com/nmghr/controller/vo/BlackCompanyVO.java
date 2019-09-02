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

import com.sargeraswang.util.ExcelUtil.ExcelCell;

/**
 * <功能描述/>
 *
 * @author weber  
 * @date 2019年1月26日 下午7:51:26 
 * @version 1.0   
 */
public class BlackCompanyVO {
  @ExcelCell(defaultValue="企业ID", index = 0)
  private Double companyId;
  
  @ExcelCell(defaultValue="企业名称", index = 1)
  private String dwmc;
  
  @ExcelCell(defaultValue="加入黑名单原因", index = 2)
  private String blacklistReason;
  
  @ExcelCell(defaultValue="主要违法行为", index = 3)
  private String unlawAct;
  
  @ExcelCell(defaultValue="处罚依据", index = 4)
  private String according;
  
  @ExcelCell(defaultValue="处罚结果", index = 5)
  private String accordingMessage;
  
  @ExcelCell(defaultValue="涉案产品系统", index = 6)
  private String relevanceSystem;

  public Double getCompanyId() {
    return companyId;
  }

  public void setCompanyId(Double companyId) {
    this.companyId = companyId;
  }

  public String getDwmc() {
    return dwmc;
  }

  public void setDwmc(String dwmc) {
    this.dwmc = dwmc;
  }

  public String getBlacklistReason() {
    return blacklistReason;
  }

  public void setBlacklistReason(String blacklistReason) {
    this.blacklistReason = blacklistReason;
  }

  public String getUnlawAct() {
    return unlawAct;
  }

  public void setUnlawAct(String unlawAct) {
    this.unlawAct = unlawAct;
  }

  public String getAccording() {
    return according;
  }

  public void setAccording(String according) {
    this.according = according;
  }

  public String getAccordingMessage() {
    return accordingMessage;
  }

  public void setAccordingMessage(String accordingMessage) {
    this.accordingMessage = accordingMessage;
  }

  public String getRelevanceSystem() {
    return relevanceSystem;
  }

  public void setRelevanceSystem(String relevanceSystem) {
    this.relevanceSystem = relevanceSystem;
  }

  
}
