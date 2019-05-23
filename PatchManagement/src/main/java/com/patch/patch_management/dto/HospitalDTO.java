package com.patch.patch_management.dto;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@NoArgsConstructor
public class HospitalDTO
{
	private Integer						hospitalId;
	private String						siteKey;
	private String						customerName;
	private String						applicationVersion;
	@JsonFormat(pattern = "M-d-yyyy")
	private LocalDate					addedDate;
	private DropDownDTO					region;
	private DropDownDTO					day;
	private DropDownDTO					session;
	private DropDownDTO					console;
	private Integer						week;
	private DropDownDTO					timeZone;
	private DropDownDTO					timeZoneDetails;
	private String						scheduledStartTime;
	private Integer						duration;
	private String						scheduledEndTime;
	private String						patchTime;
	private String						comment;
	private CSMDetailsDTO				csmDetails;
	private List<CustomerDetailsDTO>	customerDetailsDTO;
	private boolean						isContractCancelled;
	@JsonFormat(pattern = "M-d-yyyy")
	private Date						creationTime;
	private HospitalCancellation		hospitalCancellation;

}
