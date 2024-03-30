package com.oieho.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;

import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;

@MappedSuperclass
@EntityListeners(value = { AuditingEntityListener.class})
@Getter
abstract class BaseEntity {

	@Column(name="regDate",updatable=false)
	protected LocalDateTime regDate;

	@LastModifiedDate
	@Column(name="updDate")
	protected LocalDateTime updDate;
}
