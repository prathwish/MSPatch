package com.patch.patch_management.dto;




import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CustomerDetailsDTO
{
	private Integer	customerId;
	private String	siteKey;
	private String	customerName;
	private String	customerEmailId;
	private String	contactNumber;
}
