package com.oieho.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Builder
@Getter
@Setter
@ToString(exclude= {"workBoard","member"})
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkComment extends BaseEntity{
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long cno;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="wno")
	private WorkBoard workBoard;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="userNo", referencedColumnName="userNo")
	private Member member;
	
	private Long face;
	
	@Column(length = 150, nullable = false)
	private String text;
	
	@Column(nullable = false)
	private Long uid;
	
	private Long depth;
	
	private Long rnum;
	
	private Long rdepth;
	
	@CreatedDate
	@Column(name = "reg_date")
    protected LocalDateTime regDate;
	
	@LastModifiedDate
	@Column(name="updDate")
	protected LocalDateTime updDate;
	
	public void setRegDate(LocalDateTime regDate) {
		this.regDate = regDate;
	}

	public void setUpdDate(LocalDateTime updDate) {
		this.updDate = updDate;
	}
	
	public Long getWno() {
		return workBoard != null ? workBoard.getWno() : null;
	}

	public Long getUserNo() {
		return member != null ? member.getUserNo() : null;
	}

}