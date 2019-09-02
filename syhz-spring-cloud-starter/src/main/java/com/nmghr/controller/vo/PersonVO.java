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
public class PersonVO {
 
  @ExcelCell(defaultValue="姓名", index = 0)
  private String xm;
  
  @ExcelCell(defaultValue="性别", index = 1)
  private String  xbname;//
  
  @ExcelCell(defaultValue="民族", index = 2)
  private String  mzname;//
  
  @ExcelCell(defaultValue="出生日期", index = 3)
  private String csrq;
  
  @ExcelCell(defaultValue="籍贯", index = 4)
  private String hjdssxq;
  
  @ExcelCell(defaultValue="身份证号码", index = 5)
  private String zjhm;
  
  @ExcelCell(defaultValue="婚姻", index = 6)
  private String hyzkname;//
  
  @ExcelCell(defaultValue="职业", index = 7)
  private String zylbname;//
  
  @ExcelCell(defaultValue="现住址", index = 8)
  private String zzxz;
  
  @ExcelCell(defaultValue="户籍地址", index = 9)
  private String hjdxz;
  
  @ExcelCell(defaultValue="文化程度", index = 10)
  private String whcdname;//
  
  @ExcelCell(defaultValue="联系方式", index = 11)
  private String lxdh;

public String getXm() {
	return xm;
}

public void setXm(String xm) {
	this.xm = xm;
}

public String getXbname() {
	return xbname;
}

public void setXbname(String xbname) {
	this.xbname = xbname;
}

public String getMzname() {
	return mzname;
}

public void setMzname(String mzname) {
	this.mzname = mzname;
}

public String getCsrq() {
	return csrq;
}

public void setCsrq(String csrq) {
	this.csrq = csrq;
}

public String getHjdssxq() {
	return hjdssxq;
}

public void setHjdssxq(String hjdssxq) {
	this.hjdssxq = hjdssxq;
}

public String getZjhm() {
	return zjhm;
}

public void setZjhm(String zjhm) {
	this.zjhm = zjhm;
}

public String getHyzkname() {
	return hyzkname;
}

public void setHyzkname(String hyzkname) {
	this.hyzkname = hyzkname;
}

public String getZylbname() {
	return zylbname;
}

public void setZylbname(String zylbname) {
	this.zylbname = zylbname;
}

public String getZzxz() {
	return zzxz;
}

public void setZzxz(String zzxz) {
	this.zzxz = zzxz;
}

public String getHjdxz() {
	return hjdxz;
}

public void setHjdxz(String hjdxz) {
	this.hjdxz = hjdxz;
}

public String getWhcdname() {
	return whcdname;
}

public void setWhcdname(String whcdname) {
	this.whcdname = whcdname;
}

public String getLxdh() {
	return lxdh;
}

public void setLxdh(String lxdh) {
	this.lxdh = lxdh;
}
  
  
  
}
