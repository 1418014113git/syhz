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
public class QBXSVO {
 
  @ExcelCell(defaultValue="情报线索标题", index = 0)
  private String BT;
  
  @ExcelCell(defaultValue="涉事类型", index = 1)
  private String  SSLB_NAME;
  
  @ExcelCell(defaultValue="紧急程度", index = 2)
  private String  JJCD_NAME;
  
  @ExcelCell(defaultValue="正文", index = 3)
  private String XXZW;
  
  @ExcelCell(defaultValue="涉事诱因", index = 4)
  private String SSYY;
  
  @ExcelCell(defaultValue="报告次序", index = 5)
  private String BGCX_NAME;
  
  @ExcelCell(defaultValue="表现形式", index = 6)
  private String BXXS_NAME;
  
  @ExcelCell(defaultValue="地点类别", index = 7)
  private String ASDDLB_NAME;
  
  @ExcelCell(defaultValue="情报线索案(事)时间", index = 8)
  private String ASSJ;
  
  @ExcelCell(defaultValue="情报线索填报人", index = 9)
  private String TBR;
  
  @ExcelCell(defaultValue="情报线索采集人", index = 10)
  private String CJR;
  
  @ExcelCell(defaultValue="情报线索审批人", index = 11)
  private String SHR;
  
  @ExcelCell(defaultValue="情报线索类型", index = 12)
  private String XSLX;

public String getBT() {
	return BT;
}

public void setBT(String bT) {
	BT = bT;
}

public String getSSLB_NAME() {
	return SSLB_NAME;
}

public void setSSLB_NAME(String sSLB_NAME) {
	SSLB_NAME = sSLB_NAME;
}

public String getJJCD_NAME() {
	return JJCD_NAME;
}

public void setJJCD_NAME(String jJCD_NAME) {
	JJCD_NAME = jJCD_NAME;
}

public String getXXZW() {
	return XXZW;
}

public void setXXZW(String xXZW) {
	XXZW = xXZW;
}

public String getSSYY() {
	return SSYY;
}

public void setSSYY(String sSYY) {
	SSYY = sSYY;
}

public String getBGCX_NAME() {
	return BGCX_NAME;
}

public void setBGCX_NAME(String bGCX_NAME) {
	BGCX_NAME = bGCX_NAME;
}

public String getBXXS_NAME() {
	return BXXS_NAME;
}

public void setBXXS_NAME(String bXXS_NAME) {
	BXXS_NAME = bXXS_NAME;
}

public String getASDDLB_NAME() {
	return ASDDLB_NAME;
}

public void setASDDLB_NAME(String aSDDLB_NAME) {
	ASDDLB_NAME = aSDDLB_NAME;
}

public String getASSJ() {
	return ASSJ;
}

public void setASSJ(String aSSJ) {
	ASSJ = aSSJ;
}

public String getTBR() {
	return TBR;
}

public void setTBR(String tBR) {
	TBR = tBR;
}

public String getCJR() {
	return CJR;
}

public void setCJR(String cJR) {
	CJR = cJR;
}

public String getSHR() {
	return SHR;
}

public void setSHR(String sHR) {
	SHR = sHR;
}

public String getXSLX() {
	return XSLX;
}

public void setXSLX(String xSLX) {
	XSLX = xSLX;
}
  
  
  
}
