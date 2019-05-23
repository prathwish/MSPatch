package com.patch.patch_management.dto;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class HospitalSearchDTO implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;
	private Integer	hospitalId;
	private String	siteKey;
	private String	customerName;
	private String	session;
	private String	console;
	private String	region;

}
