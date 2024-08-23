package com.oieho.controller;

import java.util.List;
import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.oieho.entity.Count;
import com.oieho.entity.DummyIP;
import com.oieho.repository.CountRepository;
import com.oieho.repository.DummyIPRepository;
import com.oieho.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RequiredArgsConstructor
@RestController

public class CountController {
	
	private final MemberService memberService; 
	private final CountRepository countRepository;
	private final DummyIPRepository dummyRepository;

	@Transactional
	@GetMapping("/counts")
	public ResponseEntity<List<Count>> list() throws Exception {
		String clientIP = getUserIp();
		Count thisis = new Count();
		Integer criteriaVar = 1;
		thisis.setCriteriaVar(criteriaVar);
		Optional<DummyIP> chkIp = memberService.readCnt(clientIP);
		Optional<Count> chkCriteriaVar = memberService.readCriteriaVar(criteriaVar);
		if(chkIp.isPresent()!=true) {
			if(chkCriteriaVar.isPresent()!=true) {
				countRepository.save(thisis);
				countRepository.updateCount();
			} else {
			countRepository.updateCount();
			}
		}
		DummyIP ip = DummyIP.builder().ipAddr(clientIP).build();
		dummyRepository.save(ip);
		List<Count> countInfo = memberService.list();
		return new ResponseEntity<>(countInfo, HttpStatus.OK);
	}
	
	public String getUserIp() throws Exception {
		
        String ip = null;
        HttpServletRequest request = 
        ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();

        ip = request.getHeader("X-Forwarded-For");
        
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
            ip = request.getHeader("Proxy-Client-IP"); 
        } 
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
            ip = request.getHeader("WL-Proxy-Client-IP"); 
        } 
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
            ip = request.getHeader("HTTP_CLIENT_IP"); 
        } 
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
            ip = request.getHeader("HTTP_X_FORWARDED_FOR"); 
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
            ip = request.getHeader("X-Real-IP"); 
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
            ip = request.getHeader("X-RealIP"); 
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
            ip = request.getHeader("REMOTE_ADDR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) { 
            ip = request.getRemoteAddr(); 
        }
		
		return ip;
	}
}
