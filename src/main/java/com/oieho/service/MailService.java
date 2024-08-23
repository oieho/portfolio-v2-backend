package com.oieho.service;

import java.io.UnsupportedEncodingException;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.oieho.dto.MailDTO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MailService {

    private final JavaMailSender javaMailSender;
    MimeMessageHelper mimeMessageHelper = null;

    public ResponseEntity<String> sendMail(MailDTO mailDto) throws Exception{

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true, "UTF-8"); // use multipart (true)

            mimeMessageHelper.setSubject(mailDto.getSubject());
            mimeMessageHelper.setFrom(new InternetAddress(mailDto.getEmail(), mailDto.getSender(),"UTF-8"));
            mimeMessageHelper.setTo("oiehomail@gmail.com");
            String htmlContent = "<div>보낸사람 : " + mailDto.getEmail() +"<div><br>"+mailDto.getContent();
            mimeMessageHelper.setText(htmlContent,true);
            if(!(mailDto.getMultipartFile() == null) && !mailDto.getMultipartFile().isEmpty()) {
            MultipartFile multipartFile = mailDto.getMultipartFile();
            mimeMessageHelper.addAttachment(multipartFile.getOriginalFilename(), mailDto.getMultipartFile());}
            javaMailSender.send(mimeMessage);
            return new ResponseEntity<String>("Mail was sent.", HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<String>("Failed to send mail.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
    
    public void setFrom(String email,String name) throws 
	UnsupportedEncodingException, MessagingException {
    	mimeMessageHelper.setFrom(email, name);
}
}