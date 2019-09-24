/*
 * Copyright (C) 2018 @内蒙古慧瑞.
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.nmghr.util;

/**
 * <功能描述/>
 *
 * @author kaven
 * @date 2018年12月4日 上午11:46:28
 * @version 1.0
 */
public class SyhzUtil {

	public static String setDate(Object obj) {
		if (null != obj) {
			return obj.toString();
		}
		return "";
	}

	public static int setDateInt(Object obj) {
		if (null != obj) {
			return Integer.parseInt(obj.toString());
		}
		return 0;
	}

	public static boolean notNull(Object obj) {
		boolean bo = false;
		if (null != obj) {
			bo = true;
		}
		return bo;
	}

}
