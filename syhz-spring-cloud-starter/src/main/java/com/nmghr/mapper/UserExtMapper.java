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

	@Select("<script> select u.id as userId, u.uk, ud.depart_name as depName "
			+ "from g_user u INNER JOIN u_user_depart_rel udr on udr.user_id = u.id "
			+ "INNER JOIN u_depart ud on udr.depart_id = ud.id  "
			+ "where u.id in  <foreach collection=\"ids\" item=\"item\" open=\"(\" separator=\",\" close=\")\"> #{item} </foreach>  </script>")
	List<Map<String, Object>> getList(@Param("ids") List<Object> ids);

	@Select("<script> select id as receiverDeptId, depart_name as receiverDeptName,depart_code as receiverDeptCode "
			+ "from u_depart where enabled = 1 and id in  <foreach collection=\"ids\" item=\"item\" open=\"(\" separator=\",\" close=\")\"> #{item} </foreach>  </script>")
	List<Map<String, Object>> getDeptNameList(@Param("ids") List<Object> ids);

	@Select("<script> select d.user_id as userId from u_user_depart_rel d INNER JOIN u_user_role_rel r on d.user_id = r.user_id INNER JOIN u_role role on r.role_id = role.id\n"
			+ "where d.depart_id = #{deptId} and role_code in (${roleCodes}) </script>")
	List<Map<String, Object>> getManagerUserId(@Param("deptId") Object deptId, @Param("roleCodes") Object roleCodes);

	@Select("<script> SELECT id,dict_code as `dictCode`  FROM u_dict WHERE app_id=1  and dict_type=#{dictType}  and enabled=1 </script>")
	List<Map<String, Object>> getDictCode(@Param("dictType") Object dictType);

	@Select("<script> select area_code as areaCode, area_name AS areaName,depart_code as departCode ,city_code as cityCode,area_code as areaCode from u_depart t  where t.parent_depart_id in\n"
			+ "(select id from u_depart where depart_code in ('610000000000','610000530000'))  order by area_code</script>")
	List<Map<String, Object>> getCity();

	@Select("<script> SELECT t.depart_code as 'deptCode',t.depart_code as 'departCode',t.depart_name as 'areaName',t.depart_type as 'departType',area_code as areaCode FROM u_depart t \n"
			+ "WHERE t.depart_code=#{departCode} or (t.parent_depart_code=#{departCode} AND t.parent_depart_code!=610000530000 and t.depart_type != 4)\n"
			+ "ORDER BY t.depart_code </script>")
	List<Map<String, Object>> getDepart(@Param("departCode") Object departCode);

	@Select("<script> SELECT t.depart_code as 'deptCode',t.depart_code as 'departCode',t.depart_name as 'areaName',t.depart_type as 'departType',area_code as areaCode FROM u_depart t \n"
			+ "WHERE t.depart_type !=4 and  t.city_code=#{cityCode}\n" + "ORDER BY t.depart_code </script>")
	List<Map<String, Object>> getCityDepart(@Param("cityCode") Object cityCode);

	@Select("<script> SELECT t.depart_code as 'deptCode',t.depart_code as 'departCode',t.depart_name as 'areaName',t.depart_type as 'departType',area_code as areaCode FROM u_depart t \n"
			+ "WHERE t.depart_type !=4 and  t.regin_code=#{reginCode}\n" + "ORDER BY t.depart_code </script>")
	List<Map<String, Object>> getReginDepart(@Param("reginCode") Object reginCode);

	@Select("<script> SELECT \n" + "ifnull(count((case when d.depart_type=1 then 1 else null end ) ),0) as p1,\n"
			+ "ifnull(count((case when d.depart_type=2 then 1 else null end ) ),0) as p2,\n"
			+ "ifnull(count((case when d.depart_type=3 then 1 else null end ) ),0) as p3\n" + "FROM u_depart d\n"
			+ "WHERE d.parent_depart_code=#{departCode} or d.depart_code=#{departCode}  </script>")
	Map<String, Object> getDepartType(@Param("departCode") Object departCode);

	@Select("<script> SELECT \n" + "ifnull(count((case when d.depart_type=1 then 1 else null end ) ),0) as p1,\n"
			+ "ifnull(count((case when d.depart_type=2 then 1 else null end ) ),0) as p2,\n"
			+ "ifnull(count((case when d.depart_type=3 then 1 else null end ) ),0) as p3\n" + "FROM u_depart d\n"
			+ "WHERE d.depart_code=#{departCode} </script>")
	Map<String, Object> getMyDepartType(@Param("departCode") Object departCode);

}
