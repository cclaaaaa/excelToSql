package com.stuData.dao;

import java.util.List;

import com.stuData.dao.dataobject.ClassAttrDTO;


public interface ClassAttrDao {

	List<ClassAttrDTO> getClassAttrList(Long classId);
}
