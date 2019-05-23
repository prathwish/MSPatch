package com.patch.patch_management.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CSMDetailsDTO
{
	private Integer	csmId;
	private String	siteKey;
	private String	csmName;
	private String	csmEmailId;
	private String	contactNumber;
	private String	additionalContacts;
}
