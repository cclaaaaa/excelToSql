package com.stuData.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;

import com.stuData.dao.dataobject.StudentDTO;




public interface StudentDao {

	@MapKey("stuNo")
	Map<String, StudentDTO> getStudentDTOListByStuNOs(@Param("stuNoList") List<String> list, @Param("classId") Long classId);

	List<String> getStuNoExistedList(@Param("stuNoList") List<String> list, @Param("classId") Long classId);
}