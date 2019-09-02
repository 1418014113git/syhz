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

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;

import com.nmghr.entity.BusinessSign;

/**
 * <功能描述/>
 *
 * @author brook  
 * @date 2018年8月23日 下午2:17:36
 * @version 1.0   
 */
@Mapper
public interface CaseManagerMapper {

  
  //根据案件编号，获取签收表是否存在
  @Select("select * from t_business_sign where business_value = #{ajbh} and business_table = 'aj_jbxx_etl' and business_property='AJBH' ")
  @Results({ @Result(property = "id", column = "id"), @Result(property = "singUserId", column = "sign_user_id"),
    @Result(property = "signTime", column = "sign_time"), @Result(property = "businessTable", column = "business_table"),
    @Result(property = "businessProperty", column = "business_property"), @Result(property = "bussinessValue", column = "business_value"),
    @Result(property = "noticeOrgId", column = "notice_org_id"),@Result(property = "noticeRoleId", column = "notice_role_id"),
    @Result(property = "noticeTime", column = "notice_time"),@Result(property = "noticeUserId", column = "notice_user_id"),
    @Result(property = "qsStatus", column = "qs_status"),@Result(property = "parentId", column = "parent_id"),
    @Result(property = "noticeLx", column = "notice_lx"),@Result(property = "updateTime", column = "update_time"),
    @Result(property = "updateUserId", column = "update_user_id"),@Result(property = "businessType", column = "business_type"),
    @Result(property = "deadlineTime", column = "deadline_time"),@Result(property = "status", column = "status"),
    @Result(property = "revokeReason", column = "revoke_reason")})
  List<BusinessSign> getBusinessSignByAJBH(String ajbh);
}
