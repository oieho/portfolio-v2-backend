package com.oieho.controller;

import java.net.URLDecoder;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.oieho.dto.MailDTO;
import com.oieho.entity.Member;
import com.oieho.entity.RecoverPassword;
import com.oieho.jwt.JwtTokenProvider;
import com.oieho.jwt.SecurityConstants;
import com.oieho.repository.RecoverPasswordRepository;
import com.oieho.service.MailService;
import com.oieho.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {
	private final PasswordEncoder passwordEncoder;
	private final MemberService memberService;
	private final MailService mailService;
    private final JavaMailSender javaMailSender;
	private final RecoverPasswordRepository recoverRepository;
	private final JwtTokenProvider jwtTokenProvider;
	
	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public void exAdmin() {
		log.info("exAdmin..................");
	}

	@PostMapping("/myinfo")
	public ResponseEntity<?> getMyInfo(HttpServletRequest request,HttpServletResponse response) throws Exception {
		String header = request.getHeader(SecurityConstants.TOKEN_HEADER);
		String header2 = request.getHeader(SecurityConstants.REFRESH_HEADER);
		System.out.println("header1::"+header);
		System.out.println("header2::"+header2);
		if((header == null || header2 == null) || (header.equals("Bearer undefined") && header2.equals("Bearer undefined"))) {
			System.out.println("isn't authorized.");
			return new ResponseEntity<Boolean>(false, HttpStatus.OK);
		}

		String token = header.substring(7);
		if(header.equals("Bearer undefined")) {
			token = header2.substring(7);
		}
		
		Map<String, Object> extractedJwtClaims = jwtTokenProvider.extractJWTClaims(token);
	    long userName =	Long.parseLong(String.valueOf(extractedJwtClaims.get("uno")));
		Member member = memberService.readMyInfo(userName);
		member.setUserPw("");
		
		return new ResponseEntity<>(member, HttpStatus.OK);
	}
	
	@PostMapping("/logout")
	public ResponseEntity<?> logout()  throws Exception{
		return ResponseEntity.status(HttpStatus.NO_CONTENT).body("logout");
	}
	
	// Join
	@PostMapping("/joinUs")
	public ResponseEntity<Member> register(@Validated @RequestBody Member member) throws Exception {
		String inputPassword = member.getUserPw();
		member.setUserPw(passwordEncoder.encode(inputPassword));
		
		
		memberService.register(member);
		return new ResponseEntity<>(member, HttpStatus.OK);
	}
	
	@PostMapping("/idChk")
	public ResponseEntity<Boolean> idchk(@RequestBody String userId) throws Exception{
		String extractUserId = userId.substring(0, userId.length() - 1);
		boolean chkResult = memberService.chkDuplicatedId(extractUserId);

		return new ResponseEntity<Boolean>(chkResult,HttpStatus.OK);
	}
	@PostMapping("/nameChk")
	public ResponseEntity<Boolean> namechk(@RequestBody String userName) throws Exception {
	    String decodedUserName = URLDecoder.decode(userName, "UTF-8");
	    String extractUserName = decodedUserName.substring(0, decodedUserName.length() - 1);
	    boolean chkResult = memberService.chkDuplicatedName(extractUserName);
	    System.out.println(chkResult);
	    return new ResponseEntity<Boolean>(chkResult, HttpStatus.OK);
	}
	@PostMapping("/emailChk")
	public ResponseEntity<Boolean> emailchk(@RequestBody String userEmail) throws Exception{
		String extractUserEmail = userEmail.substring(0, userEmail.length() - 1);
		String replaceEmail = extractUserEmail.replace("%40", "@");
		boolean chkResult = memberService.chkDuplicatedEmail(replaceEmail);

		return new ResponseEntity<Boolean>(chkResult,HttpStatus.OK);
	}
	
	// Member Modify
	@GetMapping("/fetchModify/{userNo}")
	public ResponseEntity<Member> fetchModify(@PathVariable("userNo") Long userNo) throws Exception {
		Member member = memberService.readMemberByUserId(userNo);
		return new ResponseEntity<Member>(member,HttpStatus.OK);
	}
	
	@PostMapping("/pwChk")
	public ResponseEntity<Boolean> pwchk(@RequestBody Member member) throws Exception{
		String userId = member.getUserId();
		String userPw = member.getUserPw();
		Boolean pwchk = memberService.pwchk(userId, userPw);
		
		return new ResponseEntity<Boolean>(pwchk,HttpStatus.OK);
	}
	@PostMapping("/withdrawalconfirm")
	public ResponseEntity<Boolean> confirm(@RequestBody Member member) throws Exception {
		String userId = member.getUserId();
		String password = member.getUserPw();
		Boolean confirmResult = memberService.confirm(userId,password);
		
		return new ResponseEntity<>(confirmResult, HttpStatus.OK);
		
	}
	@PutMapping("/modify")
	public ResponseEntity<Boolean> modify(@RequestBody Member member) throws Exception{
		String userId = member.getUserId();
		String userPw = member.getUserPw();
		Boolean pwchk = memberService.pwchk(userId, userPw);
		String userName = member.getUserName();
		String userEmail = member.getUserEmail();
		memberService.modify(userId, userName, userEmail);
		
		return new ResponseEntity<Boolean>(pwchk,HttpStatus.OK);
	}
	@DeleteMapping("/{userNo}")
	public ResponseEntity<Void> remove(@PathVariable("userNo") Long userNo) throws Exception {
		memberService.remove(userNo);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	} 
	
	// Member Modify Password
	@PutMapping("/changePw/")
	public ResponseEntity<Boolean> changePw(@RequestBody Member member) throws Exception{
		String userId = member.getUserId();
		String userPw = member.getUserPw();
		String password = passwordEncoder.encode(userPw);
		memberService.changePw(userId, password);
		Boolean chkResult = true;
		return new ResponseEntity<Boolean>(chkResult,HttpStatus.OK);
	}
	

	// Mail Sender
	@PostMapping(value="/mail", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<String> execMail(@ModelAttribute MailDTO mailDto)  throws Exception{
		System.out.println("mailDto:::"+mailDto);
		mailService.sendMail(mailDto);
        return new ResponseEntity<String>("Success",HttpStatus.OK);
    }
	
	
	// Member Find ID
	@PostMapping("/nameChkOnFindId")
	public ResponseEntity<Boolean> nameChkOnFindId(@RequestBody Map userName) throws Exception{ // @RequestBody에서 userId는 key,value 이기 때문에 Map으로 받음 예)String으로 받으면 value가 null.
		String name = (String) userName.get("userName");
		boolean chkName = memberService.chkDuplicatedName(name);

		return new ResponseEntity<Boolean>(chkName,HttpStatus.OK);
	}
	@PostMapping("/emailChkOnFindId")
	public ResponseEntity<Boolean> emailchkonFindId(@RequestBody Map userEmail) throws Exception{
		String email = (String) userEmail.get("userEmail");
		boolean chkEmail = memberService.chkDuplicatedEmail(email);
		
		return new ResponseEntity<Boolean>(chkEmail,HttpStatus.OK);
	}
	@PostMapping("/ifIdMatchesEmail")
	public ResponseEntity<Boolean> ifIdMatchesEmail(@RequestBody Member user) throws Exception{
		String name = (String) user.getUserName();
		String email = (String) user.getUserEmail();
		boolean chkNameAndEmail = memberService.chkNameAndEmail(name,email);
		return new ResponseEntity<Boolean>(chkNameAndEmail,HttpStatus.OK);
	}
	@PostMapping("/sendEmailOnFindId")
	public ResponseEntity<?> sendEmailOnFindId(@RequestBody Member user) throws Exception {
		String name = (String) user.getUserName();
		String email = (String) user.getUserEmail();
		Member extractsId = memberService.findUserIdByUserName(name);
		String id = extractsId.getUserId();
		try {
	        MimeMessage message = javaMailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message, true);
	        helper.setTo(email);
	        helper.setSubject("OIEHO 아이디 찾기 메일입니다.");
	        helper.setText("<p>아이디 찾기를 요청하셨습니다. 아이디는 다음과 같습니다.</p>"
	            + "<br><span style='font-size:2rem;font-weight:bold;'>"+id+"</span>", true);
	        javaMailSender.send(message);
	        return new ResponseEntity<Boolean>(true,HttpStatus.OK);
	    } catch (MessagingException e) {
	        e.printStackTrace();
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
		
	}
	
	
	// Member Find Password
	@PostMapping("/emailchkOnFindPassword")
	public ResponseEntity<?> extractsEmail(@RequestBody Member user) throws Exception {
		String id = (String) user.getUserId();
		String email = (String) user.getUserEmail();
		Boolean validEmail = memberService.existsByUserIdAndUserEmail(id, email);
		if (!validEmail) {
	        return new ResponseEntity<Boolean>(false, HttpStatus.BAD_REQUEST);
	    }
		String token = UUID.randomUUID().toString();
		RecoverPassword rpass = new RecoverPassword();
		rpass.setResetToken(token);
	    recoverRepository.save(rpass);
	    
	    try {
	        MimeMessage message = javaMailSender.createMimeMessage();
	        MimeMessageHelper helper = new MimeMessageHelper(message, true);
	        helper.setTo(email);
	        helper.setSubject("OIEHO 비밀번호 찾기 인증 메일입니다.");
	        helper.setText("<p>비밀번호 찾기를 요청하셨을 경우 아래에 있는 인증버튼을 누르시기 바랍니다.</p><p>인증완료 후 <b>홈페이지로 돌아간 후 비밀번호를 변경</b>하시기 바랍니다.</p>"
	            + "<a href='http://52.78.70.226:8088/members/password/verify/"+token+"'><br><span style=\"width:120%;height:100%;border: 1px solid #000; padding: 0.6rem;border-radius:1rem;background-color:black;color:#fff;font-size: 0.85rem;\">인증하기</span></a>", true);
	        javaMailSender.send(message);
	    } catch (MessagingException e) {
	        e.printStackTrace();
	        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	    System.out.println("validEmail:::"+validEmail);
	    HashMap<String, Object> chkResult = new HashMap<>();
		chkResult.put("token",token);
		chkResult.put("booleanResult", true);
		return new ResponseEntity<HashMap>(chkResult,HttpStatus.OK);
	}
	
	@PostMapping("/idChkOnFindPassword")
	public ResponseEntity<HashMap<String,Object>> idChkOnFindPassword(@RequestBody Map userId) throws Exception{ // @RequestBody에서 userId는 key,value 이기 때문에 Map으로 받음 예)String으로 받으면 value가 null.
		String id = (String) userId.get("userId");
		HashMap<String,Object> chkResult = memberService.chkDuplicatedIdOnFindPassword(id);
		return new ResponseEntity<HashMap<String,Object>>(chkResult,HttpStatus.OK);
	}
	
	@GetMapping("/password/verify/{token}")
	public ResponseEntity<Object> verifyToken(@PathVariable String token)  throws Exception{
		RecoverPassword resetToken = recoverRepository.findByResetToken(token);
		recoverRepository.delete(resetToken);
	    if (resetToken == null) {
	        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    }
	    return new ResponseEntity<>("<html><body><h1>인증이 완료되었습니다.</h1><p>홈페이지로 돌아간 후 비밀번호를 변경하시기 바랍니다.</p></body></html>",HttpStatus.OK);
	}
	@GetMapping("/password/authorization/{token}")
	public ResponseEntity<Boolean> authorization(@PathVariable("token") String token) throws Exception {
		String validToken = memberService.readSearchPasswordTokenByToken(token);
		if (validToken == null) {
			return new ResponseEntity<Boolean>(true,HttpStatus.OK);
		}
		return new ResponseEntity<Boolean>(false,HttpStatus.BAD_REQUEST);
	}
	
	@DeleteMapping("/password/authorization/delete/{token}")
	public ResponseEntity<Void> removeFindPasswordToken(@PathVariable("token") String token) throws Exception {
		memberService.removeFindPasswordToken(token);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	} 
}