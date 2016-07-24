package com.nivalsoul.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.nivalsoul.service.FileService;

@Controller  
@RequestMapping("/v1/docdive") 
public class SimpleController {
	
	@Autowired
	private FileService fileService;
	
	@RequestMapping(value ="/test", method = RequestMethod.GET)
    @ResponseBody
    public Object test(HttpServletRequest request){
		String path = request.getServletContext().getRealPath("/");
		File file =  new File(path, "files");
		file.mkdirs();
		try {
			File ff = new File(file, "a.txt");
			ff.createNewFile();
			new PrintStream(ff).println("abcdefg");
			System.out.println("==="+ff.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
		}
        return "http://"+request.getLocalAddr()+":"+request.getLocalPort()
          +"/files/a.txt";
    }
	
	@RequestMapping(value ="/convert/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Object convert(HttpServletRequest request,
			@PathVariable("id") String documentId){
		fileService.sendMessage(documentId);
		return null;
	}
	
	

}
