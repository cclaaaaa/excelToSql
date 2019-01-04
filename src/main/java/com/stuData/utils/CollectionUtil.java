package com.stuData.utils;

import java.util.List;
import java.util.Set;

public class CollectionUtil {

    public static boolean isEmpty(List<?> list) {
    	if(list == null || list.size() <= 0) {
    		return true;
    	}
    	return false;
    }
    
    public static boolean isNotEmpty(List<?> list) {
    	if(list != null && list.size() > 0) {
    		return true;
    	}
    	return false;
    }
    
    public static boolean isEmpty(Set<?> set) {
    	if(set == null || set.size() <= 0) {
    		return true;
    	}
    	return false;
    }
    
    public static boolean isNotEmpty(Set<?> set) {
    	if(set != null && set.size() > 0) {
    		return true;
    	}
    	return false;
    }
}
