package com.stuData.controller.sample;


import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.stuData.base.ClassInputParam;
import com.stuData.base.SimpleFlagModel;
import com.stuData.service.sample.StudentService;

@Controller
@RequestMapping("/students")
public class StudentInputController {
	
	@Autowired
	private StudentService studentService;

	@RequestMapping(value = "/input", method = RequestMethod.POST)
	@ResponseBody
	public SimpleFlagModel inputStudents(HttpServletRequest request, ClassInputParam param,
			@RequestParam(value = "file", required = false) MultipartFile file) {

		return studentService.inputStudents(file, param);
	}
	

	
	@RequestMapping(value = "/input/attrs", method = RequestMethod.POST)
	@ResponseBody
	public SimpleFlagModel inputStudentAttrs(HttpServletRequest request, ClassInputParam param,
			@RequestParam(value = "file", required = false) MultipartFile file) {

		return studentService.inputStudentAttrs(file, param);
	}
}
