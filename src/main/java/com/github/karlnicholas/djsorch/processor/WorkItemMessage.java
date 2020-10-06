package com.github.karlnicholas.djsorch.processor;

import java.io.Serializable;
import java.util.Map;

import lombok.Data;

@Data
public class WorkItemMessage implements Serializable {
	private static final long serialVersionUID = 1L;
	private Map<String, Object> params;
	private Map<String, Object> results;
}
