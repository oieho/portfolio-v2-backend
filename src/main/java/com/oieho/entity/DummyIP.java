package com.oieho.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Table(name="dummyIPAddr")
@Builder
@AllArgsConstructor
@Getter
@Setter
@Entity
@RequiredArgsConstructor
public class DummyIP {
	@Id
	@Size(max=32)
	private String ipAddr;
}
