package com.semi.learn.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.semi.learn.dto.LessonDto;
import com.semi.learn.dto.QnaDto;
import com.semi.learn.dto.QnaParam;
import com.semi.learn.service.LessonService;

@Controller
public class LessonController {
	
	@Autowired
	LessonService service;
	@GetMapping(value = "lesson")
	public String lesson(int cls_seq, QnaParam param, Model model) {
		
		// 강의 목차 가져오기
		List<LessonDto> list = service.getLessonList(cls_seq);
		
		// 클래스이름 가져오기 (navbar에 줘야됨)
		String title = service.getClsTitle(cls_seq);
		
		// 강의 하나 가져오기
		LessonDto dto = null;
		int les_seq = param.getLes_seq();
		
		if(les_seq == 0) {		// 강의 번호 못받았으면, 클래스의 첫번째 강의로 설정
			dto = list.get(les_seq);
			les_seq = dto.getSeq();
		} else {
			dto = service.getLesson(les_seq);
		}
		
		// Q&A 페이지
		int pn = param.getPageNumber();
		int start = (pn * 5) + 1;
		int end = (pn + 1) * 5;
		
		param.setStart(start);
		param.setEnd(end);
		param.setLes_seq(les_seq);
		
		// Q&A 리스트, 현재 강의의 Q&A 총 갯수 가져오기
		List<QnaDto> qnalist = service.getQnaList(param);
		int len = service.countQna(les_seq);
		
		// 현재 강의의 총 페이지 갯수
		int totalPages = len / 5;
		if((len % 5) > 0) {
			totalPages += 1;
		}
		
		model.addAttribute("cls_seq", cls_seq);
		model.addAttribute("les_seq", les_seq);
		
		model.addAttribute("title", title);
		model.addAttribute("dto", dto);
		
		model.addAttribute("list", list);
		model.addAttribute("qnalist", qnalist);
		model.addAttribute("pageNumber", pn);
		model.addAttribute("totalPages", totalPages);
		
		return "lesson";
	}
	
	@ResponseBody
	@GetMapping("qnalist")
	public List<QnaDto> qnalist(QnaParam param) {
		int pn = param.getPageNumber();
		int start = (pn * 5) + 1;
		int end = (pn + 1) * 5;
		
		param.setStart(start);
		param.setEnd(end);
		
		return service.getQnaList(param);
	}
	
	@PostMapping("writeQue")
	public String writeQue(int cls_seq, QnaDto dto) {
		
		if(!service.writeQue(dto)) {
			System.out.println("질문 작성 실패");
		}
		
		return "redirect:/lesson?cls_seq=" + cls_seq + "&les_seq=" + dto.getLes_seq();
	}
	
	@PostMapping("writeAns")
	public String writeAns(int cls_seq, QnaDto dto) {
		
		if(!service.writeAns(dto)) {
			System.out.println("답변 작성 실패");
		}
		
		return "redirect:/lesson?cls_seq=" + cls_seq + "&les_seq=" + dto.getLes_seq();
	}
}
