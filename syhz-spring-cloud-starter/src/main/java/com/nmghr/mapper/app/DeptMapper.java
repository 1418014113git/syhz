package com.nmghr.mapper.app;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DeptMapper {
	@Select("<script> SELECT org_code as cityCode ,\r\n"
			+ "(case org_code WHEN 610000 then '全部' else org_short end)as cityName\r\n"
			+ ",org_p_code  as parentId,610000 as proviceId\r\n" + "FROM t_zone\r\n" + "WHERE 1=1 \r\n"
			+ "ORDER BY org_code</script>")
	List<Map<String, Object>> getCityTree();

	@Select("<script>select id, depart_name as dep_name, depart_code as dep_code, parent_depart_code as super_dep_code,depart_type as depType from u_depart d\r\n"
			+ "where enabled = 1  and depart_code!='610000000000'\r\n"
			+ " <if test=\"provinceCode!=null and provinceCode!=''\"> and d.province_code=#{provinceCode}</if>\r\n"
			+ "<if test=\"cityCode!=null and cityCode!=''\">and d.city_code=#{cityCode}</if>\r\n"
			+ "<if test=\"reginCode!=null and reginCode!=''\">and d.regin_code=#{reginCode}</if>\r\n"
			+ "order by dep_code</script>")
	List<Map<String, Object>> getDept(Map<String, Object> map);
}
