/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.entity.query;

import java.util.Map;

import com.nmghr.util.SyhzUtil;

/**
 * 分页对象，由第三方应用服务或第三方数据请求服务确定是否支持分页查询。
 * 
 * @author kaven
 * @date 2019年11月23日 下午2:19:44
 * @version 1.0
 */
public class Page {
  private int pageSize;// 每页大小，可为空，默认为 10，最小为 1
  private int pageNo;// 页码，可为空，默认为 1，最小为 1
  private int total;// 总数

  public static Page dataToVo(Map<String, Object> requestBody) {
    Map<String, Object> pageMap = (Map<String, Object>) requestBody.get("page");
    Page page = new Page();
		if (pageMap != null) {
			int pageSize = SyhzUtil.setDateInt(pageMap.get("pageSize"));
			int pageNo = SyhzUtil.setDateInt(pageMap.get("pageNo"));
			page.setPageSize(pageSize);
			page.setPageNo(pageNo);
		}

    return page;
  }

  public int getPageSize() {
    return pageSize;
  }

  public void setPageSize(int pageSize) {
    this.pageSize = pageSize;
  }

  public int getPageNo() {
    return pageNo;
  }

  public void setPageNo(int pageNo) {
    this.pageNo = pageNo;
  }

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Page [pageSize=");
    builder.append(pageSize);
    builder.append(", pageNo=");
    builder.append(pageNo);
    builder.append(", total=");
    builder.append(total);
    builder.append(']');
    return builder.toString();
  }
}
