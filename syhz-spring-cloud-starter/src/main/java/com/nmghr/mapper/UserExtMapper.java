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
 * @version 1.0
 * @date 2019年4月29日 下午4:13:56
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

  @Select("<script> select area_code as areaCode,area_name as areaName,   area_code as cityCode, area_name AS cityName,depart_code as departCode ,city_code as cityCode,area_code as areaCode,parent_depart_code as parentDepartCode,1 as dtype from u_depart t  where t.parent_depart_id in\n"
      + "(select id from u_depart where depart_code in ('610000000000','610000530000'))  order by area_code</script>")
  List<Map<String, Object>> getCity();

  @Select("<script> SELECT t.depart_code as 'cityCode',t.depart_code as  deptCode,t.depart_code as 'departCode',t.depart_name as 'cityName',t.depart_type as 'departType',area_code as areaCode,depart_name as areaName ,4 as dtype FROM u_depart t \n"
      + "WHERE t.depart_code=#{departCode} \n"
      + "ORDER BY t.depart_code </script>")
  List<Map<String, Object>> getDepart(@Param("departCode") Object departCode);

  @Select("<script> SELECT t.depart_code as areaCode,t.depart_code as deptCode,t.depart_name as areaName,  t.depart_code as 'cityCode',t.depart_code as 'departCode',t.depart_name as 'cityName',t.depart_type as 'departType',area_code as areaCode,parent_depart_code as parentDepartCode, 2 as dtype FROM u_depart t \n"
      + "WHERE t.depart_type !=4 and  t.city_code=#{cityCode}\n" + "ORDER BY t.depart_code </script>")
  List<Map<String, Object>> getCityDepart(@Param("cityCode") Object cityCode);

  @Select("<script> SELECT t.depart_code as 'cityCode',t.depart_code as  deptCode,t.depart_code as 'departCode',t.depart_name as 'cityName',t.depart_type as 'departType',area_code as areaCode ,3 as dtype FROM u_depart t \n"
      + "WHERE t.depart_type !=4 and  t.regin_code=#{reginCode}\n" + "ORDER BY t.depart_code </script>")
  List<Map<String, Object>> getReginDepart(@Param("reginCode") Object reginCode);

  @Select("<script> SELECT \n" + "ifnull(count((case when d.depart_type=1 then 1 else null end ) ),0) as p1,\n"
      + "ifnull(count((case when d.depart_type=2 then 1 else null end ) ),0) as p2,\n"
      + "ifnull(count((case when d.depart_type=3 then 1 else null end ) ),0) as p3\n" + "FROM u_depart d\n"
      + "WHERE (d.parent_depart_code=#{departCode} and parent_depart_code!='610000530000') or d.depart_code=#{departCode}  </script>")
  Map<String, Object> getDepartType(@Param("departCode") Object departCode);

  @Select("<script>SELECT \n" + "ifnull(count((case when ud.depart_type=1  then 1 else null end ) ),0) as r1,\n"
      + "ifnull(count((case when ud.depart_type=2  then 1 else null end ) ),0) as r2,\n"
      + "ifnull(count((case when ud.depart_type=3  then 1 else null end ) ),0) as r3\n" + "FROM u_depart   ud\n"
      + " LEFT JOIN u_user_depart_rel udr on ud.id=udr.depart_id \n"
      + "LEFT JOIN g_user g on udr.user_id=g.id and g.user_state=1\n"
      + "WHERE \n" + " <if test=\"type==1 or type =='1' \"> \n"
      + "(ud.parent_depart_code=#{departCode} and ud.parent_depart_code!='610000530000') or \n" + "</if>\n"
      + "ud.depart_code=#{departCode} " + "union ALL\n"
      + "SELECT ifnull(count((case when d.depart_type=1 then 1 else null end ) ),0) as p1,\n"
      + "			ifnull(count((case when d.depart_type=2 then 1 else null end ) ),0) as p2,\n"
      + "		ifnull(count((case when d.depart_type=3 then 1 else null end ) ),0) as p3 \n" + "FROM u_depart d\n"
      + "WHERE \n" + " <if test=\"type==1 or type =='1' \"> \n"
      + "(d.parent_depart_code=#{departCode} and parent_depart_code!='610000530000') or \n" + "</if>\n"
      + "d.depart_code=#{departCode}  </script>")
  List<Map<String, Object>> getMyDepartType(@Param("departCode") Object departCode, @Param("type") int type);

  @Select("SELECT uur.user_id as creationId,gu.real_name as creationName FROM u_role ur \n"
      + "LEFT JOIN u_user_role_rel uur on ur.id=uur.role_id\n" + " LEFT JOIN g_user gu on uur.user_id=gu.id\n"
      + " WHERE ur.role_code in (${roleCode}) and uur.user_id in\n"
      + "(SELECT uud.user_id FROM u_depart ud LEFT JOIN u_user_depart_rel uud on uud.depart_id=ud.id WHERE ud.depart_code like '%${deptCode}%')\n"
      + "")
  List<Map<String, Object>> getRoleUser(Map<String, Object> map);

  @Select("<script>SELECT DISTINCT gu.id,gu.real_name as realName, gu.user_name as userName from u_user_depart_rel uudr INNER JOIN g_user gu on uudr.user_id = gu.id  " +
      "<where> <if test=\"receiveIds!=null\"> gu.id not in <foreach collection=\"receiveIds\" item=\"item\" open=\"(\" separator=\",\" close=\")\"> #{item} </foreach> </if> " +
      "<if test=\"deptIds!=null\"> and uudr.depart_id in <foreach collection=\"deptIds\" item=\"item\" open=\"(\" separator=\",\" close=\")\"> #{item} </foreach> </if>" +
      "<if test=\"name!=null and name!=''\"> and (gu.real_name like '%${name}%' or gu.user_name like '%${name}%')</if></where></script>")
  List<Map<String, Object>> getUserList(Map<String, Object> map);

  @Select("<script>SELECT DISTINCT gu.id,gu.real_name as realName, gu.user_name as userName from u_user_depart_rel uudr INNER JOIN g_user gu on uudr.user_id = gu.id INNER JOIN u_depart ud on uudr.depart_id = ud.id\n" +
      "INNER JOIN u_user_role_rel rr on gu.id=rr.user_id INNER JOIN u_role ur on rr.role_id=ur.id\n" +
      "WHERE ur.role_code in (select dict_code from u_dict where dict_type='mananercode') \n" +
      "and ud.id in <foreach collection=\"deptIds\" item=\"item\" open=\"(\" separator=\",\" close=\")\"> #{item} </foreach></script>")
  List<Map<String, Object>> getManagerUserList(Map<String, Object> deptIds);
}
