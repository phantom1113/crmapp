package com.myclass.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("file")
public class UploadController {
	@Autowired
	ServletContext servletContext;

	@PostMapping(value = "upload", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object upload(@RequestParam() MultipartFile file) {
		String pathName = "D:\\CNTT\\project\\CRM project\\CrmProject\\src\\main\\resources\\static\\upload\\";
		File dir = new File(pathName);
		if (!dir.exists()) {
			dir.mkdir();
		}
		String pathSource = pathName + file.getOriginalFilename();

		File serverFile = new File(pathSource);
		FileOutputStream stream;
		try {
			stream = new FileOutputStream(serverFile);
			stream.write(file.getBytes());
			stream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Map<String, String> result = new HashMap<String, String>();
		result.put("url", file.getOriginalFilename());
		return result;
	}

	@GetMapping(value = "load", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Object upload(@RequestParam() String fileName) {
		if (!fileName.isEmpty()) {
			String pathSource = servletContext.getContextPath() + "/upload/" + fileName;
			Map<String, String> result = new HashMap<String, String>();
			result.put("name", fileName);
			result.put("url", pathSource);
			return result;
		}
		return null;
	}
}
