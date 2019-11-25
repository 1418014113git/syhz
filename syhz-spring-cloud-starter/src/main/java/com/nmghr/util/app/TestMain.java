/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nmghr.util.app;

/**
 * <功能描述/>
 *
 * @author kaven
 * @date 2019年11月25日 上午11:58:50 
 * @version 1.0   
 */
public class TestMain {

  public static void main(String[] args) {
    // TODO Auto-generated method stub
    String a = "userId = '1' and deptCode = '610000530000'";
    String[] aArr = a.split("and");
    for (String aa : aArr) {
      String aaStr = aa.replaceAll(" ", "").replaceAll("'", "");
      String[] conditionDataArr = aaStr.split("=");
      System.out.println(aaStr);
      System.out.println(conditionDataArr[0]);
      System.out.println(conditionDataArr[1]);
    }
    
    
  }

}
