/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nmghr.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nmghr.basic.common.Constant;
import com.nmghr.basic.core.common.LocalThreadStorage;
import com.nmghr.basic.core.service.IBaseService;
import com.nmghr.basic.rmdb.datasource.TargetDataSource;
import com.nmghr.mapper.UserExtMapper;

/**
 * <功能描述/>
 *
 * @author weber
 * @date 2019年4月29日 下午3:19:43
 * @version 1.0
 */
@Service("userextService")
public class UserExtService implements IBaseService {

	@Autowired
	private UserExtMapper userExtMapper;

	@Override
	public Object save(Map<String, Object> requestMap) throws Exception {
		return null;
	}

	@Override
	public Object update(String id, Map<String, Object> requestBody) throws Exception {
		return null;
	}

	@Override
	public Object update(Map<String, Object> requestParam, Map<String, Object> requestBody) throws Exception {
		return null;
	}

	@Override
	public void remove(Map<String, Object> requestMap) throws Exception {
	}

	@Override
	public void remove(String id) throws Exception {
	}

	@Override
	@TargetDataSource(value = "hrupms")
	public Object get(Map<String, Object> requestMap) throws Exception {
		return userExtMapper.getRoleUser(requestMap);
	}

	@Override
	public Object get(String id) throws Exception {
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	@TargetDataSource(value = "hrupms")
	public Object list(Map<String, Object> map) throws Exception {
		if (map.get("ids") == null) {
			return null;
		}
		List<Object> ids = (List<Object>) map.get("ids");
		if (ids.size() == 0) {
			return null;
		}
		LocalThreadStorage.put(Constant.CONTROLLER_ALIAS, "USEREXTBYIDS");
		return userExtMapper.getList(ids);
	}

	@Override
	public Object page(Map<String, Object> requestMap, int currentPage, int pageSize) throws Exception {
		return null;
	}

	@Override
	public Object findAll() throws Exception {
		return null;
	}

	@Override
	public Object getSequence(String seqName) throws Exception {
		return null;
	}

}
