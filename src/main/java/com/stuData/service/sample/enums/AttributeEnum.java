package com.stuData.service.sample.enums;

import java.util.ArrayList;
import java.util.List;

public enum AttributeEnum {
	
	//系统默认的字段
	STU_NO("stu_no", 1L, "学号", "STU NO", true),
	NAME("name", 2L, "名称", "NAME", false),
	SCLASS("class", 3L, "班级", "CLASS", false),
	HEIGHT("height", 4L, "身高", "HEIGHT", true),
	WEIGHT("weight", 5L, "体重", "WEIGHT", true),
	MAJOR("major", 6L, "专业", "MAJOR.", false),
	SEX("sex", 7L, "性别", "SEX", false);
	
	private AttributeEnum(String attrKey, Long attrId, String prettyName, String prettyNameEn, Boolean isWaterMarkParam) {
		this.attrKey = attrKey;
		this.attrId = attrId;
		this.prettyName = prettyName;
		this.prettyNameEn = prettyNameEn;
		this.isWaterMarkParam = isWaterMarkParam;
	}
	
	public static List<Long> getSysDefaultAttrIds() {
		List<Long> attrIds = new ArrayList<Long>();
		for (AttributeEnum itemEnum : AttributeEnum.values()) {
			attrIds.add(itemEnum.getAttrId());
		}
		return attrIds;
	}
	
	// 字段关键字，和标签保持一致
	private String attrKey;
	
	// 字段ID
	private Long attrId;
	
	private String prettyName;
	
	private String prettyNameEn;
	
	// 是否是水印参数
	private Boolean isWaterMarkParam;
	
	public String getAttrKey() {
		return attrKey;
	}

	public Long getAttrId() {
		return attrId;
	}

	public String getPrettyName() {
		return prettyName;
	}

	public String getPrettyNameEn() {
		return prettyNameEn;
	}

	public Boolean getIsWaterMarkParam() {
		return isWaterMarkParam;
	}
}
