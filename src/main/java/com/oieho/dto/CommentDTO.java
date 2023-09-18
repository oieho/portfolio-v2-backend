package com.oieho.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
	
	private String title;
	private LocalDateTime boardRegDate;
	private String description;
    private Long userNo;
    private String userName;
    private Long cno;
    private LocalDateTime regDate;
    private String text;
    private Long face;
    private Long uid;
    private Long depth;
    private Long rnum;
    private Long rdepth;
    private Long wno;

}