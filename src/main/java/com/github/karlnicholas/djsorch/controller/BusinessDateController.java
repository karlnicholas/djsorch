package com.github.karlnicholas.djsorch.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.karlnicholas.djsorch.service.BusinessDateService;

@RestController
@RequestMapping("businessdate")
public class BusinessDateController {
	private final BusinessDateService businessDateService;
	public BusinessDateController(BusinessDateService businessDateService) {
		this.businessDateService = businessDateService;
	}
	@PostMapping("/{businessDate}")
	public ResponseEntity<Void> setBusinessDate(@PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate businessDate) {
		businessDateService.setBusinessDate(businessDate);
		return ResponseEntity.ok().build();
	}
}
