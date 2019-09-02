/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nmghr.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * <功能描述/>
 *
 * @author wujin  
 * @date 2018年10月15日 下午2:36:21 
 * @version 1.0   
 */
public class ListPageUtil<T> {
  /**原集合*/
  private List<Map<String, Object>> data;

  /** 上一页 */
  private int lastPage;

  /** 当前页 */
  private int nowPage;

  /** 下一页 */
  private int nextPage;
//    
  /** 每页条数 */
  private int pageSize;

  /** 总页数 */
  private int totalPage;

  /** 总数据条数 */
  private int totalCount;
  
  public ListPageUtil(List<Map<String, Object>> list,int nowPage,int pageSize) {
      if (list == null || list.isEmpty()) {
          throw new IllegalArgumentException("data must be not empty!");
      }

      this.data = list;
      this.pageSize = pageSize;
      /*this.totalPage = data.size()/pageSize;
      if(data.size()%pageSize!=0){
          this.totalPage++;
      }*/
      
      this.nowPage = nowPage;
      this.totalCount = list.size();
      this.totalPage = (totalCount + pageSize - 1) / pageSize;
      this.lastPage = nowPage-1>1? nowPage-1:1;
      this.nextPage = nowPage>=totalPage? totalPage: nowPage + 1;
      
  }

  /**
   * 得到分页后的数据
   *
   * @param pageNum 页码
   * @return 分页后结果
   */
  public List<Map<String, Object>> getPagedList() {
      int fromIndex = (nowPage - 1) * pageSize;
      if (fromIndex >= data.size()) {
          return Collections.emptyList();//空数组
      }
      if(fromIndex<0){
          return Collections.emptyList();//空数组
      }
      int toIndex = nowPage * pageSize;
      if (toIndex >= data.size()) {
          toIndex = data.size();
      }
      return data.subList(fromIndex, toIndex);
  }

  public int getPageSize() {
      return pageSize;
  }

  public List<Map<String, Object>> getData() {
      return data;
  }
  public int getLastPage() {
      return lastPage;
  }

  public int getNowPage() {
      return nowPage;
  }

  public int getNextPage() {
      return nextPage;
  }

  public int getTotalPage() {
      return totalPage;
  }

  public int getTotalCount() {
      return totalCount;
  }



}
