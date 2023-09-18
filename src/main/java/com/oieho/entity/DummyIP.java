package com.oieho.entity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Size;

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
