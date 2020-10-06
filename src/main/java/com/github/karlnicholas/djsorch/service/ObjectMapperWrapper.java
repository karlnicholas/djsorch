package com.github.karlnicholas.djsorch.service;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ObjectMapperWrapper {
	private ObjectMapper objectMapper;
	@PostConstruct
	public void postConstruct() {
		this.objectMapper = new ObjectMapper();
	}
	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}
}
