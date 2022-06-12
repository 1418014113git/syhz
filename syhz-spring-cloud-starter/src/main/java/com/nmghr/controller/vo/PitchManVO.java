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
 * @date 2019年5月6日 下午2:16:04 
 * @version 1.0   
 */
public class PitchManVO {
  
  @ExcelCell(defaultValue="单位名称", index = 0)
  private String name;
  
  @ExcelCell(defaultValue="负责人", index = 1)
  private String personName;
  
  @ExcelCell(defaultValue="身份证号码", index = 2)
  private String cardNumber;
  
  @ExcelCell(defaultValue="联系电话", index = 3)
  private String phone;
  
  //ABC
  @ExcelCell(defaultValue="分类登记", index = 4)
  private String type;
  
  @ExcelCell(defaultValue="经营项目", index = 5)
  private String manageProject;
  
  @ExcelCell(defaultValue="经营状态", index = 6)
  private String status;
  
  @ExcelCell(defaultValue="单位位置", index = 7)
  private String address;
  
  @ExcelCell(defaultValue="行政区划", index = 8)
  private String area;
  
  //高中低
  @ExcelCell(defaultValue="风险等级", index = 8)
  private String grade;
  
  @ExcelCell(defaultValue="从业人数", index = 10)
  private String personNumber;
  
  @ExcelCell(defaultValue="备案登记号", index = 11)
  private String registerNumber;
  
  @ExcelCell(defaultValue="经度", index = 12)
  private String n;
  
  @ExcelCell(defaultValue="纬度", index = 13)
  private String e;

  private String createName;
  
  private String createDept;
  
  private String id;
  
  private String createDate;
  
  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getCreateDate() {
    return createDate;
  }

  public void setCreateDate(String createDate) {
    this.createDate = createDate;
  }

  private int sourceType = 1;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPersonName() {
    return personName;
  }

  public void setPersonName(String personName) {
    this.personName = personName;
  }

  public String getCardNumber() {
    return cardNumber;
  }

  public void setCardNumber(String cardNumber) {
    this.cardNumber = cardNumber;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getManageProject() {
    return manageProject;
  }

  public void setManageProject(String manageProject) {
    this.manageProject = manageProject;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getArea() {
    return area;
  }

  public void setArea(String area) {
    this.area = area;
  }

  public String getGrade() {
    return grade;
  }

  public void setGrade(String grade) {
    this.grade = grade;
  }

  public String getPersonNumber() {
    return personNumber;
  }

  public void setPersonNumber(String personNumber) {
    this.personNumber = personNumber;
  }

  public String getRegisterNumber() {
    return registerNumber;
  }

  public void setRegisterNumber(String registerNumber) {
    this.registerNumber = registerNumber;
  }

  public String getCreateName() {
    return createName;
  }

  public void setCreateName(String createName) {
    this.createName = createName;
  }

  public String getCreateDept() {
    return createDept;
  }

  public void setCreateDept(String createDept) {
    this.createDept = createDept;
  }

  public int getSourceType() {
    return sourceType;
  }

  public void setSourceType(int sourceType) {
    this.sourceType = sourceType;
  }

  public String getN() {
    return n;
  }

  public void setN(String n) {
    this.n = n;
  }

  public String getE() {
    return e;
  }

  public void setE(String e) {
    this.e = e;
  }
  
  
}
