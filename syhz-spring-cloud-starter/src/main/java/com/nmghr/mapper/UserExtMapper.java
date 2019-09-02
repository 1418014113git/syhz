/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nmghr.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * <功能描述/>
 *
 * @author weber  
 * @date 2019年4月29日 下午4:13:56 
 * @version 1.0   
 */
@Mapper
public interface UserExtMapper {
  
  @Select("<script> select u.id as userId, u.uk, ud.depart_name as depName " + 
      "from g_user u INNER JOIN u_user_depart_rel udr on udr.user_id = u.id " + 
      "INNER JOIN u_depart ud on udr.depart_id = ud.id  " + 
      "where u.id in  <foreach collection=\"ids\" item=\"item\" open=\"(\" separator=\",\" close=\")\"> #{item} </foreach>  </script>")
  List<Map<String, Object>> getList(@Param("ids")List<Object> ids);
}
