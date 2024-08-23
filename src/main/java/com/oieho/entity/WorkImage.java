package com.oieho.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Builder
//@EqualsAndHashCode(of="inum")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
//@ToString(exclude="workBoard")
public class WorkImage {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long inum;
	
	private String uuid;
	
	private String imgName;
	
	private String path;
	
	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name="workBoard_wno")
	@JsonIgnore
	private WorkBoard workBoard;

	public WorkImage getWorkImage() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void setWorkBoard(WorkBoard workBoard) {
	    if (this.workBoard != workBoard) {
	        if (this.workBoard != null && this.workBoard.getWorkImages() != null) {
	            if (this.workBoard.getWorkImages().contains(this)) {
	                this.workBoard.getWorkImages().remove(this);
	            }
	        }
	        this.workBoard = workBoard;
	        if (workBoard != null && !workBoard.getWorkImages().contains(this)) {
	            workBoard.getWorkImages().add(this);
	        }
	    }
	}

}
