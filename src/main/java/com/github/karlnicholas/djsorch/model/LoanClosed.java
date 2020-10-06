package com.github.karlnicholas.djsorch.model;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoanClosed implements Persistable<Long>{
	@Id private Long id;
	private Long accountClosedId;
	// funding specific 
	private BigDecimal principal;
	private LocalDate inceptionDate;
	private BigDecimal interestRate;
	private BigDecimal fixedMindue;
	private Integer termMonths;
	@Override
	public boolean isNew() {
		// TODO Auto-generated method stub
		return false;
	}
	
}
