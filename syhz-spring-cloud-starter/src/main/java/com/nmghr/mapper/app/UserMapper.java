package com.nmghr.mapper.app;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface UserMapper {

	@Select("<script> SELECT\r\n"
			+ "gu.id, IFNULL(gu.user_name, '') AS 'userName', IFNULL(gu.real_name, '') AS 'realName', IFNULL(gu.remark, '') AS remark, IFNULL(gu.user_id_number, '') AS 'userIdNumber', IFNULL(gu.user_sex, '') AS 'userSex',\r\n"
			+ "IFNULL(gu.phone, '') AS phone, IFNULL(gu.user_state, '') AS 'userState', IFNULL(gu.user_sort, '') AS 'userSort', IFNULL(gu.nation, '') AS 'nation', IFNULL(gu.politics_status, '') AS 'politicsStatus', IFNULL(gu.culture_degree, '') AS 'cultureDegree',\r\n"
			+ "IFNULL(gu.degree, '') AS 'degree', IFNULL(gu.worker_grade, '') AS 'workerGrade', IFNULL(gu.worker_duty, '') AS 'workerDuty', IFNULL(STR_TO_DATE(gu.worker_time, '%Y-%m-%d'),'') AS 'workerTime',\r\n"
			+ "IFNULL(gu.worker_post, '') AS 'workerPost', IFNULL(STR_TO_DATE(gu.join_police_time,'%Y-%m-%d'),'') AS 'joinPoliceTime',\r\n"
			+ "IFNULL(STR_TO_DATE(gu.join_hsy_time,'%Y-%m-%d'),'') AS 'joinHsyTime', IFNULL(gu.worker_phone, '') AS 'workerPhone', IFNULL(gu.pc_ip, '') AS 'ip', IFNULL( STR_TO_DATE(gu.birth_time,'%Y-%m-%d'),'') AS 'birthTime',\r\n"
			+ "IFNULL(gu.age, '') AS 'age', IFNULL(ud.id, '') AS departId, IFNULL(ud.area_code, '') AS areaCode, IFNULL(ud.depart_name, '') AS departName, IFNULL(ud.depart_code, '') AS departCode,\r\n"
			+ "IFNULL(ud.depart_type, '') AS departType, IFNULL(ud.parent_depart_id, '') AS parentDepId, IFNULL(ud.parent_depart_code, '') AS parentDepCode\r\n"
			+ "FROM g_user gu\r\n" + "LEFT JOIN u_user_depart_rel uudr ON gu.id = uudr.user_id\r\n"
			+ "LEFT JOIN u_depart ud ON uudr.depart_id = ud.id\r\n" + "WHERE user_name =#{userName} </script>")
	Map<String, Object> getUser(@Param("userName") String userName);

	@Select("<script> select d.id,d.depart_name deptName,d.depart_code deptCode,d.area_code areaCode,d.parent_depart_id parentId  from u_depart d \r\n"
			+ "where d.depart_code =#{deptCode} UNION ALL\r\n"
			+ "select u.id,u.depart_name deptName,u.depart_code deptCode,u.area_code areaCode,u.parent_depart_id parentId from u_depart u \r\n"
			+ "where u.parent_depart_id in (select t.id from u_depart t where t.depart_code =#{deptCode}) order by deptCode</script>")
	List<Map<String, Object>> getUserDept(Map<String, Object> map);

	@Select("<script> select DISTINCT gu.id,gu.real_name as realName, gu.user_name as userName from u_user_depart_rel uudr INNER JOIN g_user gu on uudr.user_id = gu.id INNER JOIN u_depart ud on uudr.depart_id = ud.id\r\n"
			+ "<where> <if test=\"departCode!=null and departCode!=''\">(ud.parent_depart_code = #{departCode} or ud.depart_code = #{departCode})</if>\r\n"
			+ "<if test=\"name!=null and name!=''\"> and (gu.real_name like '%${name}%' or  gu.user_name like '%${name}%') </if>\r\n"
			+ "<if test=\"type!=null and type!=''\"> and gu.user_type = #{type}</if></where> </script>")
	List<Map<String, Object>> getDeptUser(Map<String, Object> map);

	@Update("UPDATE `g_user` SET \r\n"
			+ "`user_state`=#{userState},  `real_name`=#{realName}, `user_id_number`=#{userIdNumber}, `user_sex`=#{userSex}, `remark`=#{remark}, `phone`=#{phone}, `nation`=#{nation}, \r\n"
			+ "`politics_status`=#{politicsStatus}, `culture_degree`=#{cultureDegree}, `degree`=#{degree}, `worker_grade`=#{workerGrade}, `worker_duty`=#{workerDuty}, `worker_time`=#{workerTime}, \r\n"
			+ "`worker_post`=#{workerPost}, `join_police_time`=#{joinPoliceTime}, `join_hsy_time`=#{joinHsyTime}, `worker_phone`=#{workerPhone}, `pc_ip`=#{ip}, `birth_time`=#{birthTime}, `age`=#{age}, `complete`=1, `last_id`=#{lastId}, `last_name`=#{lastName}, `last_time`=NOW() \r\n"
			+ "WHERE id=#{id}")
	void setUser(Map<String, Object> map);
}
