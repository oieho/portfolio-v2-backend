package com.oieho.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oieho.entity.DummyIP;

public interface DummyIPRepository extends JpaRepository<DummyIP, String> {

}