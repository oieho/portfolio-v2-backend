package com.oieho.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class WorkBoard extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column
	private Long wno;

	@Column(length = 1500000)
	private String portfolioContent;

	@Column(length = 30)
	private String title;

	@Column(length = 560)
	private String description;

	@Column(name = "CATEGORY", length = 20)
	@Enumerated(EnumType.STRING)
	@NotNull
	private Category category;

	@ElementCollection
	@CollectionTable(name = "tools", joinColumns = @JoinColumn(name = "workboard_wno"))
	private List<String> tools;

	@ElementCollection
	@CollectionTable(name = "hashTag", joinColumns = @JoinColumn(name = "workboard_wno"))
	@Column(length = 15)
	private Set<String> hashTag;

	@Column(name = "hits")
	private Integer hits;

	@JsonIgnore
	@OneToMany(mappedBy = "workBoard", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
	private List<WorkComment> workComment;

	@OneToMany(mappedBy = "workBoard", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private List<WorkImage> workImages;

	public void setRegDate(LocalDateTime regDate) {
		// TODO Auto-generated method stub
		this.regDate = regDate;
	}

	public void setUpdDate(LocalDateTime regDate) {
		// TODO Auto-generated method stub
		this.regDate = regDate;
	}

	public void setUserNo(Long userNo) {
		// TODO Auto-generated method stub
	}

	public void setWorkImages(List<WorkImage> workImages) {
		if (this.workImages != workImages) {
			// 기존의 workImages와 관련된 관계를 제거
			if (this.workImages != null) {
				for (WorkImage existingWorkImage : this.workImages) {
					existingWorkImage.setWorkBoard(null);
				}
			}

			// 새로운 workImages와 관련된 관계 설정
			this.workImages = new ArrayList<>(workImages);

			if (workImages != null) {
				for (WorkImage newWorkImage : workImages) {
					if (newWorkImage.getWorkBoard() != this) {
						newWorkImage.setWorkBoard(this);
					}
				}
			}
		}
	}

	public WorkBoard(Long wno) {
	}

	public List<WorkImage> getWorkImages() {
		return workImages;
	}
}
