/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
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
 * @date 2019年4月18日 下午6:39:03
 * @version 1.0
 */
@Mapper
public interface PhoneByDeptMapper {
  /**
   * 查询某角色 部门下的用户电话号码
   * @param ids
   * @param roleCode
   * @return
   */
  @Select("<script> select u.id, u.real_name, u.phone, udr.depart_id from g_user u INNER JOIN u_user_depart_rel udr on udr.user_id = u.id " + 
      "where u.id in (select user_id from u_user_role_rel where role_id in (select id from u_role where role_code = #{roleCode})) " + 
      "and udr.depart_id in <foreach collection=\"ids\" item=\"item\" open=\"(\" separator=\",\" close=\")\"> #{item} </foreach> </script>")
  List<Map<String, Object>> getList(@Param("ids")List<Object> ids, @Param("roleCode")String roleCode);

  /**
   * 根据手机号码查询用户部门
   * @param phone
   * @return
   */
  @Select("select u.id, u.user_name, u.real_name, u.phone, udr.depart_id from g_user u INNER JOIN u_user_depart_rel udr on udr.user_id = u.id where u.phone = #{phone}")
  List<Map<String, Object>> getUserDep(@Param("phone")String phone);
  
}
