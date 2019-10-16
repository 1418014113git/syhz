package com.nmghr.handler.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface GetCityMapper {

	@Select("<script> select area_code as areaCode, area_name AS areaName,depart_code as departCode from u_depart t  where t.parent_depart_id in\r\n"
			+ "(select id from u_depart where depart_code in ('610000000000','610000530000'))  order by depart_code; </script>")
	  List<Map<String, Object>> getCityCode();

	@Select("<script> SELECT depart_name AS departName, area_code AS areaCode,depart_code as deptCode FROM u_depart "
			+ 
			"WHERE (parent_depart_code=#{belongDepCode} or depart_code= #{belongDepCode}) and area_code <![CDATA[<> '610000']]> and depart_type<![CDATA[<> '4']]>  order by area_code</script>")
	  List<Map<String,Object>> getAreaCode(@Param("belongDepCode") String belongDepCode);
}
