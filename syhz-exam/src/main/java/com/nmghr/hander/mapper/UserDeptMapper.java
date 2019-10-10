/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nmghr.hander.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * <功能描述/>
 *
 * @author weber  
 * @date 2019年4月29日 下午4:13:56 
 * @version 1.0   
 */

@Mapper
public interface UserDeptMapper {

  @Select("<script> select count(1) as totalNum from u_user_depart_rel r inner join g_user t\n" +
          "  on r.user_id = t.id where  r.depart_id in  (${depts}) </script>")
  Map<String, Object> getTotalNum(@Param("depts") String depts);


  @Select("<script>select t.id deptId, t.depart_name deptName,t.area_code areaCode,t.depart_code deptCode,\n" +
          "(select count(1) from u_user_depart_rel r where r.depart_id = t.id) as userCount,t.area_name as areaName from u_depart t  where t.parent_depart_id =\n" +
          " 1011 and area_code <![CDATA[<> '610000']]> order by area_code</script>")
  List<Map<String,Object>> getCitys();

  @Select("<script> select t.id deptId,t.parent_depart_id parentId, t.depart_name deptName,t.area_code areaCode,t.depart_code deptCode from u_depart t where t.parent_depart_id = #{deptId}</script>")
  List<Map<String,Object>> getCityChild(@Param("deptId") String deptId);

  @Select("<script> select t.id deptId,t.parent_depart_id parentId,t.parent_depart_code parentCode, t.depart_name deptName,t.area_code areaCode,t.depart_code deptCode from u_depart t where t.parent_depart_code = #{deptCode}</script>")
  List<Map<String,Object>> getChildByDeptCode(@Param("deptCode") String deptCode);


  /*
  查某一地区的应考人数
  */
  @Select("<script>select t.depart_code as departCode,count(1) as totalNum from u_depart t \n" +
          "inner join u_user_depart_rel r on r.depart_id = t.id where t.id = #{deptId}</script>")
  Map<String,Object> getCityChildTotalNumByDeptId(@Param("deptId") String deptId);


  @Select("<script>select t.id deptId, t.depart_name deptName,t.area_code areaCode,t.depart_code deptCode,\n" +
          "          (select count(1) from u_user_depart_rel r where r.depart_id = t.id) as userCount,t.area_name as areaName from u_depart t  where t.parent_depart_id in\n" +
          "           (1011,-1)  order by area_code</script>")
  List<Map<String,Object>> getAllCitys();

}

