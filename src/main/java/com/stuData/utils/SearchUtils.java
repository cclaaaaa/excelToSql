package com.stuData.utils;

import org.apache.commons.lang3.StringUtils;

import com.google.common.base.CharMatcher;


public class SearchUtils {
	
	/**
	 * 获取搜索参数
	 * @param param
	 * 
	 * @return
	 */
	public static Double[] getSampleSearchParams(String param) {
		if(StringUtils.isBlank(param)) {
			return null;
		}
		Double min = null;
		Double max = null;
		param = CharMatcher.anyOf("0123456789+-/=.").retainFrom(param);
		String[] splitChars = new String[]{"/2", "/", "-"};
		for(int i = 0; i < splitChars.length; i++) {
			for(String splitChar : splitChars) {
				if(param.endsWith(splitChar)) {
					param = param.substring(0, param.length() - splitChar.length());
				}
			}
		}
		if(StringUtils.isBlank(param)) {
			return null;
		}
		try {
			if(param.contains("=")) {
				String[] paramArray = param.split("=");
				if(paramArray.length < 2) {
					return null;
				}
				param = paramArray[1];
			}
			if(param.contains("+/-")) {
				String[] paramArray = param.split("\\+/-");
				if(paramArray[0].length() > 12) {
					return null;
				}
				if(paramArray.length >= 2) {
					if(paramArray[1].length() > 12) {
						return null;
					}
					double middle = Double.parseDouble(paramArray[0]);
					double range = Double.parseDouble(paramArray[1]);
					min = middle - range;
					max = middle + range;
				}
				return new Double[]{min,max};
			}
			if(param.contains("-") || param.contains("/")) {
				String[] paramArray ;
				if(param.contains("-")) {
					paramArray = param.split("-");
				} else {
					paramArray = param.split("/");
				} 
				min = Double.parseDouble(paramArray[0]);
				if(paramArray[0].length() > 12) {
					return null;
				}
				if(paramArray.length >= 2) {
					if(paramArray[1].length() > 12) {
						return null;
					}
					max = Double.parseDouble(paramArray[1]);
					if(max < min) {
						double temp = max;
						max = min;
						min = temp;
					}
				} else {
					max = min;
				}
				return new Double[]{min,max};
			}
			if(param.length() > 12) {
				return null;
			}
			Double paramInt = Double.parseDouble(param);
			min = paramInt;
			max = paramInt;
			return new Double[]{min,max};
		} catch (NumberFormatException e) {
			return null;
		}
	}
}
