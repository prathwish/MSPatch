package com.patch.patch_management.dto;

import java.io.Serializable;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class HospitalCancellation implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private Boolean				isPermanentCancellation;
	private String				siteKey;
	private Integer				hospitalId;
	@JsonFormat(pattern = "M-d-yyyy")
	private LocalDate				cancelFrom;
	@JsonFormat(pattern = "M-d-yyyy")
	private LocalDate				cancelTo;
}
