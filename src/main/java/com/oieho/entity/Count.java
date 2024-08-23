package com.oieho.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(value="hibernateLazyInitializer")
@Builder
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(of="criteriaVar")
@ToString
@Entity
@Table(name="calcCount")
@RequiredArgsConstructor
public class Count {
	@Id
	private int criteriaVar;
	
	private int todayVar;
	
	private int totalVar; 
}
