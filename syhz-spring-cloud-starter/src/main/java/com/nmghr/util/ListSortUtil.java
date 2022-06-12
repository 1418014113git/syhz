package com.nmghr.util;

import java.util.*;

/**
 * [简述]:List通用排序工具类
 */
public class ListSortUtil {

    /**
     * 对结果集进行排序，目前支持日期、字符串、各种整形、各种浮点型
     * @param result 结果集
     * @param order
     * @param orderType -1降序 1升序, 下面代码假设orderType为1
     * @return
     * @author yutao
     * @date 2018年4月24日下午2:20:35
     */
    public static List<Map<String, Object>> resultOrder(List<Map<String, Object>> result, String order, Integer orderType){

        if(result == null || orderType == null){
            return result;
        }

        if(orderType != -1){
            orderType = 1;
        }

        final String orderKey = order;
        final Integer oType = orderType;

        Collections.sort(result, new Comparator<Map<String, Object>>() {

            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                Object obj1 = o1.get(orderKey);
                Object obj2 = o2.get(orderKey);

                if (obj1 == null) {
                    if(oType < 0){
                        return -oType;
                    }
                    return oType;
                }
                if (obj2 == null) {
                    if(oType < 0){
                        return oType;
                    }
                    return -oType;
                }

                if(obj1 instanceof Date && obj2 instanceof Date){
                    //日期排序
                    Date date1 = (Date)obj1;
                    Date date2 = (Date)obj2;
                    return longCompare(oType, date1.getTime(), date2.getTime());
                }else if(obj1 instanceof String && obj2 instanceof String){
                    //字符串排序
                    String str1 = obj1.toString();
                    String str2 = obj2.toString();

                    if(str1.compareTo(str2) < 0){
                        return -oType;
                    }else if(str1.compareTo(str2) == 0){
                        return 0;
                    }else if(str1.compareTo(str2) > 0){
                        return oType;
                    }
                }else if((obj1 instanceof Double || obj1 instanceof Float) && (obj2 instanceof Double || obj2 instanceof Float)){
                    //浮点型排序
                    return doubleCompare(oType, obj1, obj2);
                }else if((obj1 instanceof Long || obj1 instanceof Integer || obj1 instanceof Short || obj1 instanceof Byte) &&
                        (obj2 instanceof Long || obj2 instanceof Integer || obj2 instanceof Short || obj2 instanceof Byte)){
                    //整数型排序
                    return longCompare(oType, obj1, obj2);
                }else if((obj1.getClass() != obj2.getClass()) && (obj1 instanceof Number && obj2 instanceof Number)){
                    //这种情况可能是，既有整数又有浮点数
                    return doubleCompare(oType, obj1, obj2);
                }
                return 0;
            }
        });
        return result;
    }

    private static int longCompare(final Integer oType, Object obj1, Object obj2) {
        long d1 = Long.parseLong(obj1.toString());
        long d2 = Long.parseLong(obj2.toString());
        if(d1 < d2){
            return -oType;
        }else if(d1 == d2){
            //相同的是否进行交互
            return 0;
        }else if(d1 > d2){
            return oType;
        }
        return 0;
    }

    private static int doubleCompare(final Integer oType, Object obj1, Object obj2) {
        double d1 = Double.parseDouble(obj1.toString());
        double d2 = Double.parseDouble(obj2.toString());
        if(d1 < d2){
            return -oType;
        }else if(d1 == d2){
            return 0;
        }else if(d1 > d2){
            return oType;
        }
        return 0;
    }


}
