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
 * @author brook  
 * @date 2018年8月11日 上午10:22:45 
 * @version 1.0   
 */
public class CompanyVO {

  @ExcelCell(defaultValue="企业名称", index = 0)
  private String dwmc;
  
  @ExcelCell(defaultValue="企业统一信用代码", index = 1)
  private String  tyshxydm;
  
  @ExcelCell(defaultValue="企业性质", index = 2)
  private String  dwxz;
  
  @ExcelCell(defaultValue="企业状态", index = 3)
  private String djzt;
  
  @ExcelCell(defaultValue="登记机关", index = 4)
  private String djdw;
  
  @ExcelCell(defaultValue="法人名称", index = 5)
  private String frdbxm;
  
  @ExcelCell(defaultValue="法人身份证号码", index = 6)
  private String frdbzjhm;
  
  @ExcelCell(defaultValue="注册日期", index = 7)
  private String zcrq;
  
  @ExcelCell(defaultValue="企业详址", index = 8)
  private String mlxz;
  
  public String getDwmc() {
    return dwmc;
  }
  public void setDwmc(String dwmc) {
    this.dwmc = dwmc;
  }
  public String getTyshxydm() {
    return tyshxydm;
  }
  public void setTyshxydm(String tyshxydm) {
    this.tyshxydm = tyshxydm;
  }
  public String getDwxz() {
    return dwxz;
  }
  public void setDwxz(String dwxz) {
    this.dwxz = dwxz;
  }
  public String getDjzt() {
    return djzt;
  }
  public void setDjzt(String djzt) {
    this.djzt = djzt;
  }
  public String getDjdw() {
    return djdw;
  }
  public void setDjdw(String djdw) {
    this.djdw = djdw;
  }
  public String getFrdbxm() {
    return frdbxm;
  }
  public void setFrdbxm(String frdbxm) {
    this.frdbxm = frdbxm;
  }
  public String getFrdbzjhm() {
    return frdbzjhm;
  }
  public void setFrdbzjhm(String frdbzjhm) {
    this.frdbzjhm = frdbzjhm;
  }
  public String getZcrq() {
    return zcrq;
  }
  public void setZcrq(String zcrq) {
    this.zcrq = zcrq;
  }
  public String getMlxz() {
    return mlxz;
  }
  public void setMlxz(String mlxz) {
    this.mlxz = mlxz;
  }

}
