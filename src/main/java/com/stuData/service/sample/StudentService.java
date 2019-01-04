package com.stuData.service.sample;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.stuData.base.ClassInputParam;
import com.stuData.base.SimpleFlagModel;
import com.stuData.dao.ClassAttrDao;
import com.stuData.dao.StudentDao;
import com.stuData.dao.dataobject.ClassAttrDTO;
import com.stuData.dao.dataobject.StudentAttributeDO;
import com.stuData.dao.dataobject.StudentDO;
import com.stuData.dao.dataobject.StudentDTO;
import com.stuData.service.sample.enums.AttributeEnum;
import com.stuData.utils.CollectionUtil;
import com.stuData.utils.DateUtil;
import com.stuData.utils.ExcleUtil;
import com.stuData.utils.SearchUtils;

@Component("studentService")
public class StudentService {

	@Autowired
	private StudentDao studentDao;
	@Autowired
	private ClassAttrDao classAttrDao;

	public SimpleFlagModel inputStudents(MultipartFile file, ClassInputParam param) {
		SimpleFlagModel model = new SimpleFlagModel();
		Long classId = param.getClassId();
		Long userId = param.getUserId();
		if (file == null || classId == null || userId == null) {
			model.setCode("400");
			model.setMessage("样品导入失败，内容为空");
			return model;
		}

		try {
			InputStream inputStream = file.getInputStream();

			// 获取文件扩展名
			String ext = getExtName(file.getOriginalFilename());

			Workbook workbook = WorkbookFactory.create(inputStream);

			if (workbook == null) {
				model.setCode("400");
				model.setMessage("文件类型错误");
				return model;
			}

			batchInput(workbook, ext, classId, userId);

		} catch (Exception e) {
			model.setCode("400");
			model.setMessage(e.getMessage());
		}
		return model;
	}

	public void batchInput(Workbook workbook, String ext, Long classId, Long userId) throws IOException {

		FileWriter errorWriter = new FileWriter("F:/ccl/studentError.txt");
		FileWriter sqlWriter = new FileWriter("F:/ccl/studentInsert.sql");

		List<StudentDO> studentList = convertSampleList(workbook, classId, userId);
		if (CollectionUtil.isEmpty(studentList)) {
			errorWriter.close();
			sqlWriter.close();
			return;
		}

		// 循环sampleList，生成sql
		List<String> inputStuNoList = new ArrayList<String>();
		for (StudentDO student : studentList) {
			inputStuNoList.add(student.getStuNo());
		}

		List<String> stuNOExistedList = studentDao.getStuNoExistedList(inputStuNoList, classId);

		for (int i = 0; i < studentList.size(); i++) {

			StudentDO student = studentList.get(i);

			String stuNo = student.getStuNo();

			if (StringUtils.isBlank(stuNo)) {
				errorWriter.write("失败 学号为空！rowNum:" + (i + 1));
				errorWriter.write("\r\n");
				continue;
			}

			if (stuNOExistedList.contains(stuNo)) {
				errorWriter.write("失败！学号重复！rowNum: " + (i + 1) + ",stuNO:" + stuNo);
				errorWriter.write("\r\n");
				continue;
			}

			stuNOExistedList.add(stuNo);

			sqlWriter.write(
					"insert into `student`(`class_id`,`creator_id`,`stu_no`,`name`,`class`,`height`,`weight`,`major`,`sex`,`gmt_created`,`gmt_modified`)values(");
			sqlWriter.write("'" + classId + "'");
			sqlWriter.write(",'" + userId + "'");
			sqlWriter.write("," + getSqlStringValue(stuNo));
			sqlWriter.write("," + getSqlStringValue(student.getName()));
			sqlWriter.write("," + getSqlStringValue(student.getsClass()));
			sqlWriter.write("," + getSqlStringValue(student.getHeight()));
			sqlWriter.write("," + getSqlStringValue(student.getWeight()));
			sqlWriter.write("," + getSqlStringValue(student.getMajor()));
			sqlWriter.write("," + getSqlStringValue(student.getSex()));
			String now = DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_YMDHMS);
			sqlWriter.write(",'" + now + "'");
			sqlWriter.write(",'" + now + "'");
			sqlWriter.write(");");
			sqlWriter.write("\r\n");
		}
		errorWriter.close();
		sqlWriter.close();
	}

	private String getSqlStringValue(String value) {
		if (StringUtils.isBlank(value)) {
			return "null";
		}
		return "'" + value + "'";
	}

	private String getSqlDoubleValue(Double value) {
		if (value == null) {
			return "null";
		}
		return "'" + value + "'";
	}

	private List<StudentDO> convertSampleList(Workbook workbook, Long classId, Long userId) throws IOException {

		List<StudentDO> result = new ArrayList<StudentDO>();

		Sheet sheet = workbook.getSheetAt(0); // 获得第一个表单
		Iterator<Row> rows = sheet.rowIterator(); // 获得第一个表单的迭代器
		Row firstRow = rows.next(); // 获得第一行数据

		// 获取列号对应字段ID的map
		Map<Integer, Long> cellNumLinkAttrMap = getCellIndexLinkAttrMap(firstRow, classId);

		int columnCount = firstRow.getPhysicalNumberOfCells(); // 获取总列数

		while (rows.hasNext()) {
			Row row = rows.next(); // 获得行数据
			if (row.getRowNum() > 0) {
				StudentDO sample = new StudentDO();
				sample.setClassId(classId);
				sample.setCreatorId(userId);

				for (int i = 0; i < columnCount; i++) {
					// 获取Excel单元格
					Cell cell = row.getCell(i, MissingCellPolicy.RETURN_BLANK_AS_NULL);
					Long attrId = cellNumLinkAttrMap.get(i);
					if (cell != null && attrId != null) {

						String value = ExcleUtil.getStringCellValue(cell);

						if (StringUtils.isBlank(value)) {
							continue;
						}
						if (AttributeEnum.STU_NO.getAttrId().equals(attrId)) {
							sample.setStuNo(value.trim());
							System.out.println("样品编号itemNO:" + value);
							continue;
						}
						if (AttributeEnum.NAME.getAttrId().equals(attrId)) {
							sample.setName(value);
							continue;
						}
						if (AttributeEnum.SCLASS.getAttrId().equals(attrId)) {
							sample.setsClass(value);
							continue;
						}
						if (AttributeEnum.HEIGHT.getAttrId().equals(attrId)) {
							String width = value;
							if (StringUtils.isNumeric(width)) {
								width = width + " cm";
							}
							sample.setHeight(width);
							if (row.getRowNum() > 1887) {
								continue;
							}
							continue;
						}
						if (AttributeEnum.WEIGHT.getAttrId().equals(attrId)) {
							String weight = value;
							if (StringUtils.isNumeric(weight)) {
								weight = weight + " kg";
							}
							sample.setWeight(weight);
							Double[] weightParams = SearchUtils.getSampleSearchParams(weight);

							continue;
						}
						if (AttributeEnum.MAJOR.getAttrId().equals(attrId)) {
							sample.setMajor(value);
							continue;
						}
						if (AttributeEnum.SEX.getAttrId().equals(attrId)) {
							sample.setSex(value);
						}
					}
				}
				result.add(sample);
			}
		}
		return result;
	}

	// 获取列号对应字段ID的map
	private Map<Integer, Long> getCellIndexLinkAttrMap(Row firstRow, Long classId) {
		Map<Integer, Long> map = new HashMap<Integer, Long>();
		int columnCount = firstRow.getPhysicalNumberOfCells(); // 获取总列数
		if (columnCount > 0) {

			List<ClassAttrDTO> classAttrs = classAttrDao.getClassAttrList(classId);
			if (CollectionUtil.isEmpty(classAttrs)) {
				return map;
			}
			Map<String, ClassAttrDTO> classAttrMap = new HashMap<String, ClassAttrDTO>();
			for (ClassAttrDTO dto : classAttrs) {
				classAttrMap.put(dto.getPrettyName(), dto);
			}

			for (int i = 0; i < columnCount; i++) {
				Cell cell = firstRow.getCell(i, MissingCellPolicy.RETURN_BLANK_AS_NULL);
				if (cell != null) {
					ClassAttrDTO attr = classAttrMap.get(ExcleUtil.getStringCellValue(cell).trim());
					if (attr == null) {
						System.out.println("MAP key:" + i + ", value:null");
						continue;
					}
					map.put(i, attr.getAttributeId());
					System.out.println("MAP key:" + i + ", value:" + attr.getPrettyName());
				}
			}
		}
		return map;
	}

	/**
	 * 获得文件扩展名
	 * 
	 * @param fileName
	 * @return
	 */
	private String getExtName(String fileName) {
		if (StringUtils.isBlank(fileName)) {
			return "";
		}
		return fileName.substring(fileName.lastIndexOf(".") + 1);
	}

	public SimpleFlagModel inputStudentAttrs(MultipartFile file, ClassInputParam param) {
		SimpleFlagModel model = new SimpleFlagModel();
		Long classId = param.getClassId();
		if (file == null || classId == null) {
			model.setCode("400");
			model.setMessage("学号导入失败，内容为空");
			return model;
		}

		try {
			InputStream inputStream = file.getInputStream();

			Workbook workbook = null;
			// 检验文件类型
			String ext = getExtName(file.getOriginalFilename());
			if (ext.equalsIgnoreCase("xls")) {
				workbook = new HSSFWorkbook(inputStream);
			} else if (ext.equalsIgnoreCase("xlsx")) {
				workbook = new XSSFWorkbook(inputStream);
			}

			if (workbook == null) {
				model.setCode("400");
				model.setMessage("文件类型错误");
				return model;
			}

			batchInputAttrs(workbook, ext, classId);

		} catch (Exception e) {
			model.setCode("400");
			model.setMessage(e.getMessage());
		}
		return model;
	}

	public void batchInputAttrs(Workbook workbook, String ext, Long classId) throws IOException {

		FileWriter errorWriter = new FileWriter("F:/ccl/studentAttrError.txt");
		FileWriter sqlWriter = new FileWriter("F:/ccl/studentAttrInsert.sql");

		Map<String, List<StudentAttributeDO>> stuNoAttrMap = convertItemNoAttrMap(workbook, classId);
		if (stuNoAttrMap.isEmpty()) {
			errorWriter.close();
			sqlWriter.close();
			return;
		}

		List<String> stuNoList = new ArrayList<String>();
		stuNoList.addAll(stuNoAttrMap.keySet());

		Map<String, StudentDTO> studentMap = studentDao.getStudentDTOListByStuNOs(stuNoList, classId);
		if (studentMap.isEmpty()) {
			errorWriter.close();
			sqlWriter.close();
			return;
		}

		List<StudentAttributeDO> studentAttrList = new ArrayList<StudentAttributeDO>();

		for (Entry entry : studentMap.entrySet()) {
			String stuNo = (String) entry.getKey();
			StudentDTO student = (StudentDTO) entry.getValue();
			Long studentId = student.getStudentId();
			List<StudentAttributeDO> thisStudentsAttrs = stuNoAttrMap.get(stuNo);
			if (CollectionUtil.isNotEmpty(thisStudentsAttrs)) {
				for (StudentAttributeDO studentAttr : thisStudentsAttrs) {
					studentAttr.setStudentId(studentId);
					studentAttrList.add(studentAttr);
				}
			}
		}

		for (StudentAttributeDO sampleAttr : studentAttrList) {
			sqlWriter.write(
					"insert into `student_attribute`(`student_id`,`attribute_id`,`attribute_value`,`gmt_created`,`gmt_modified`)values(");
			sqlWriter.write("'" + sampleAttr.getStudentId() + "'");
			sqlWriter.write(",'" + sampleAttr.getAttributeId() + "'");
			sqlWriter.write(",'" + sampleAttr.getAttributeValue() + "'");
			String now = DateUtil.formatDate(new Date(), DateUtil.DATE_FORMAT_YMDHMS);
			sqlWriter.write(",'" + now + "'");
			sqlWriter.write(",'" + now + "'");
			sqlWriter.write(");");
			sqlWriter.write("\r\n");
		}
		errorWriter.close();
		sqlWriter.close();
	}

	private Map<String, List<StudentAttributeDO>> convertItemNoAttrMap(Workbook workbook, Long classId)
			throws IOException {

		Map<String, List<StudentAttributeDO>> result = new HashMap<String, List<StudentAttributeDO>>();

		Sheet sheet = workbook.getSheetAt(0); // 获得第一个表单
		Iterator<Row> rows = sheet.rowIterator(); // 获得第一个表单的迭代器
		Row firstRow = rows.next(); // 获得第一行数据

		// 获取列号对应字段ID的map
		Map<Integer, Long> cellNumLinkAttrMap = getCellIndexLinkAttrMap(firstRow, classId);

		int columnCount = firstRow.getPhysicalNumberOfCells(); // 获取总列数

		while (rows.hasNext()) {
			Row row = rows.next(); // 获得行数据
			if (row.getRowNum() > 0) {

				String stuNo = "";
				List<StudentAttributeDO> studentAttrs = new ArrayList<StudentAttributeDO>();

				for (int i = 0; i < columnCount; i++) {
					Cell cell = row.getCell(i, MissingCellPolicy.RETURN_BLANK_AS_NULL);
					Long attrId = cellNumLinkAttrMap.get(i);
					if (cell != null && attrId != null) {

						String value = ExcleUtil.getStringCellValue(cell);

						if (StringUtils.isBlank(value)) {
							continue;
						}
						if (AttributeEnum.STU_NO.getAttrId().equals(attrId)) {
							stuNo = value;
							continue;
						}
						List<Long> sysDefaultAttrIds = AttributeEnum.getSysDefaultAttrIds();
						if (sysDefaultAttrIds.contains(attrId)) {
							continue;
						}
						StudentAttributeDO studentAttr = new StudentAttributeDO();
						studentAttr.setAttributeId(attrId);
						studentAttr.setAttributeValue(value);
						studentAttrs.add(studentAttr);
					}
				}
				if (StringUtils.isNotBlank(stuNo) && CollectionUtil.isNotEmpty(studentAttrs)
						&& !result.containsKey(stuNo)) {
					result.put(stuNo, studentAttrs);
				}
			}
		}
		return result;
	}

	

}
