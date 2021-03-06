package com.kh.spring.schedule.controller;

import java.text.DateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttribute;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kh.spring.member.model.dto.Member;
import com.kh.spring.schedule.model.dto.Schedule;
import com.kh.spring.schedule.model.service.ScheduleService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
@RequestMapping("schedule")
public class ScheduleController {
	
	Logger logger = LoggerFactory.getLogger(this.getClass());
	
	private final ScheduleService scheduleService;
	
	@GetMapping("calendar")
	public void calendar() {}
	
	@GetMapping("schedule-form")
	public void schduleForm() {}
	
	@GetMapping(value = "calendar-list",produces = "application/json; charset=utf-8")
	@ResponseBody		//return값을 responseBody에 담아 전달
	public String calendarList() throws JsonProcessingException {
		List<Schedule> scheduleList = scheduleService.selectAllSchedule();
		
		//json으로 브라우저 전달
		ObjectMapper mapper = new ObjectMapper();
		mapper.setDateFormat(DateFormat.getDateInstance(DateFormat.MEDIUM));
		String scheduleJson = mapper.writeValueAsString(scheduleList);
		
		return scheduleJson;

	}
	
	@PostMapping(value = "schedule-detail",produces = "application/json; charset=utf-8")
	@ResponseBody
	public String scheduleDetail(@RequestBody String json
									,@SessionAttribute(value="authentication") Member member) throws JsonMappingException, JsonProcessingException {
				
		ObjectMapper mapper = new ObjectMapper();
		Schedule scheduleObj = mapper.readValue(json, Schedule.class);
		
		Map<String,Object> schedule = new HashMap<String, Object>();
		schedule = scheduleService.selectScheduleDetail(scheduleObj.getScIdx(),member);
			
		return mapper.setDateFormat(DateFormat.getDateInstance(DateFormat.MEDIUM))
				.writeValueAsString(schedule);
	}
	
	@GetMapping("schedule-modify-form")
	public void scheduleModifyForm(Schedule schedule
									,@SessionAttribute(value="authentication", required = false) Member member
									, Model model) {
		
		Schedule scheduleByIdx = (Schedule) scheduleService.selectScheduleDetail(schedule.getScIdx(), member).get("schedule");
		model.addAttribute("schedule",scheduleByIdx);
	}
	
	@PostMapping("upload")
	public String upload(Schedule schedule, @SessionAttribute("authentication") Member member) {
		schedule.setUserId(member.getUserId());
		schedule.setNickName(member.getNickname());
		schedule.setUserInfo(member.getInfo());
		scheduleService.insertSchedule(schedule);
		
		return "redirect:/member/my-schedule";
	}
	

}
