package com.patch.patch_management.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ResponseMessageDTO
{
	private Integer	hospitalId;
	private String	message;
}
