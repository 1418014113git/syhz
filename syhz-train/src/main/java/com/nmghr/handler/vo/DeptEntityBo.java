/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.handler.vo;

import java.util.Map;

import com.nmghr.util.SyhzUtil;

/**
 * 部门相关信息
 *
 * @author kaven
 * @date 2019年9月23日 上午11:44:28
 * @version 1.0
 */
public class DeptEntityBo {
  private String deptCode; // 部门代码
  private String deptName; // 部门名称
  private String deptAreaCode; // 部门所属区域

  public static DeptEntityBo workflowMapToEntityBo(Object depObj) {
    if (depObj != null) {
      Map<String, Object> depMap = (Map<String, Object>) depObj;
      DeptEntityBo deptEntityBo = new DeptEntityBo();
      deptEntityBo.setDeptCode(SyhzUtil.setDate(depMap.get("deptCode")));
      deptEntityBo.setDeptName(SyhzUtil.setDate(depMap.get("deptName")));
      deptEntityBo.setDeptAreaCode(SyhzUtil.setDate(depMap.get("deptAreaCode")));
      return deptEntityBo;
    } else {
      return null;
    }

  }

  public String getDeptCode() {
    return deptCode;
  }

  public void setDeptCode(String deptCode) {
    this.deptCode = deptCode;
  }

  public String getDeptName() {
    return deptName;
  }

  public void setDeptName(String deptName) {
    this.deptName = deptName;
  }

  public String getDeptAreaCode() {
    return deptAreaCode;
  }

  public void setDeptAreaCode(String deptAreaCode) {
    this.deptAreaCode = deptAreaCode;
  }

}
