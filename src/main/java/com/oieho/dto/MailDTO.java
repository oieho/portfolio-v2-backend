package com.oieho.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Setter
@ToString
public class MailDTO {
    private String sender;
    private String email;
    private String subject;
    private String content;
    private MultipartFile multipartFile;
}