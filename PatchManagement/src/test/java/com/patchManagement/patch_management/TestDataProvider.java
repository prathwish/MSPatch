package com.patchManagement.patch_management;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.DataProvider;

import com.patch.patch_management.dto.CSMDetailsDTO;
import com.patch.patch_management.dto.CustomerDetailsDTO;
import com.patch.patch_management.dto.DropDownDTO;
import com.patch.patch_management.dto.HospitalCancellation;
import com.patch.patch_management.dto.HospitalDTO;
import com.patch.patch_management.entity.CSMDetailsEntity;
import com.patch.patch_management.entity.ConsoleEntity;
import com.patch.patch_management.entity.CustomerDetailsEntity;
import com.patch.patch_management.entity.CustomerMasterDataEntity;
import com.patch.patch_management.entity.DayEntity;
import com.patch.patch_management.entity.RegionEntity;
import com.patch.patch_management.entity.SessionEntity;
import com.patch.patch_management.entity.TimeZoneDetailsEntity;
import com.patch.patch_management.entity.TimeZoneEntity;
import com.patch.patch_management.util.PatchManagementUtil;

public class TestDataProvider
{

	static ModelMapper	modelMapper	= new ModelMapper();

	@Autowired
	PatchManagementUtil	patchManagementUtil;

	@DataProvider(name = "TimeZoneEntity")
	public static Object[][] timeZoneEntity()
	{
		TimeZoneEntity timeZoneEntity = new TimeZoneEntity();
		timeZoneEntity.setId(1);
		timeZoneEntity.setName("CST");
		return new Object[][]
		{
				{ timeZoneEntity } };
	}

	@DataProvider(name = "ConsoleEntity")
	public static Object[][] consoleEntity()
	{
		ConsoleEntity consoleEntity = new ConsoleEntity();
		consoleEntity.setId(1);
		consoleEntity.setName("COSMOS-1");
		return new Object[][]
		{
				{ consoleEntity } };
	}

	@DataProvider(name = "RegionEntity")
	public static Object[][] regionEntity()
	{
		RegionEntity regionEntity = new RegionEntity();
		regionEntity.setId(1);
		regionEntity.setName("EMEA");
		return new Object[][]
		{
				{ regionEntity } };
	}

	@DataProvider(name = "DayEntity")
	public static Object[][] dayEntity()
	{
		DayEntity dayEntity = new DayEntity();
		dayEntity.setId(1);
		dayEntity.setName("Monday");
		return new Object[][]
		{
				{ dayEntity } };
	}

	@DataProvider(name = "SessionEntity")
	public static Object[][] sessionEntity()
	{
		SessionEntity sessionEntity = new SessionEntity();
		sessionEntity.setId(1);
		sessionEntity.setName("1");
		sessionEntity.setSessionEndTime("6:00 AM");
		sessionEntity.setSessionStartTime("2:00 PM");
		return new Object[][]
		{
				{ sessionEntity } };
	}

	@DataProvider(name = "CustomerMasterDataEntity")
	public static Object[][] customerMasterDataEntity() throws ParseException
	{
		CustomerMasterDataEntity customerMasterDataEntity = new CustomerMasterDataEntity();
		customerMasterDataEntity.setAddedDate(fetchDate());
		customerMasterDataEntity.setApplicationVersion("4,4,541,0");
		customerMasterDataEntity.setComment("comment");
		customerMasterDataEntity.setConsole((ConsoleEntity) consoleEntity()[0][0]);
		CSMDetailsEntity cSMDetails = modelMapper.map((CSMDetailsDTO) cSMDetailsDTO(),
				CSMDetailsEntity.class);
		customerMasterDataEntity.setCSMDetails(cSMDetails);
		customerMasterDataEntity.setCustomerDetails(Arrays.asList(
				modelMapper.map((CustomerDetailsDTO) customerDetailsDTO(), CustomerDetailsEntity.class)));
		customerMasterDataEntity.setCustomerName("Yas HealthCare");
		customerMasterDataEntity.setDay((DayEntity) dayEntity()[0][0]);
		customerMasterDataEntity.setDuration(3);
		customerMasterDataEntity.setHospitalId(1);
		customerMasterDataEntity.setRegion((RegionEntity) regionEntity()[0][0]);
		customerMasterDataEntity.setScheduledEndtime("22:00");
		customerMasterDataEntity.setScheduledStarttime("19:00");
		customerMasterDataEntity.setSession((SessionEntity) sessionEntity()[0][0]);
		customerMasterDataEntity.setSiteKey("KUL00");
		customerMasterDataEntity.setTimeZone((TimeZoneEntity) timeZoneEntity()[0][0]);
		customerMasterDataEntity
				.setTimeZoneDetailsEntity((TimeZoneDetailsEntity) timeZoneDetailsEntity()[0][0]);
		customerMasterDataEntity.setWeek(1);
		customerMasterDataEntity.setCreationTime(LocalDateTime.now());

		return new Object[][]
		{
				{ customerMasterDataEntity } };
	}

	public static Object[][] timeZoneDetailsEntity()
	{
		TimeZoneDetailsEntity timeZoneDetailsEntity = new TimeZoneDetailsEntity();
		timeZoneDetailsEntity.setId(1);
		timeZoneDetailsEntity.setName("Eirunepe");
		return new Object[][]
		{
				{ timeZoneDetailsEntity } };
	}

	private static LocalDate fetchDate() throws ParseException
	{
		String pattern = "M-d-yyyy";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		return simpleDateFormat.parse("05-09-1993").toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	}

	@DataProvider(name = "CSMDetailsEntity")
	public static Object[][] cSMDataEntity() throws ParseException
	{
		CSMDetailsEntity cSMDetails = modelMapper.map((CSMDetailsDTO) cSMDetailsDTO(),
				CSMDetailsEntity.class);
		return new Object[][]
		{
				{ cSMDetails } };
	}

	private static DropDownDTO dropDownDTO(Integer id, String name)
	{
		DropDownDTO dropDownDTO = new DropDownDTO();
		dropDownDTO.setId(id);
		dropDownDTO.setName(name);
		return dropDownDTO;
	}

	@DataProvider(name = "HospitalDTO")
	public static Object[][] hospitalDTO() throws ParseException
	{
		HospitalDTO hospitalDTO = new HospitalDTO();
		hospitalDTO.setSiteKey("KUL00");
		hospitalDTO.setCustomerName("Yas HealthCare");
		hospitalDTO.setApplicationVersion("4,4,541,0");
		hospitalDTO.setAddedDate(null);
		hospitalDTO.setRegion(dropDownDTO(1, "EMEA"));
		hospitalDTO.setDay(dropDownDTO(1, "Monday"));
		hospitalDTO.setSession(dropDownDTO(1, "1"));
		hospitalDTO.setConsole(dropDownDTO(1, "COSMOS-1"));
		hospitalDTO.setWeek(1);
		hospitalDTO.setTimeZone(dropDownDTO(1, "CET"));
		hospitalDTO.setTimeZoneDetails(dropDownDTO(1, "Eirunepe"));
		hospitalDTO.setScheduledStartTime("19:00");
		hospitalDTO.setComment("comment");
		hospitalDTO.setCsmDetails(cSMDetailsDTO());
		hospitalDTO.setCustomerDetailsDTO(Arrays.asList((CustomerDetailsDTO) customerDetailsDTO()));
		return new Object[][]
		{
				{ hospitalDTO } };
	}

	@DataProvider(name = "HospitalConcellationDTO")
	public static HospitalCancellation hospitalConcellationDTO()
	{
		HospitalCancellation hospitalConcellationDTO = new HospitalCancellation();
		hospitalConcellationDTO.setIsPermanentCancellation(true);
		hospitalConcellationDTO.setHospitalId(1);
		hospitalConcellationDTO.setSiteKey("KUL00");
		return hospitalConcellationDTO;
	}

	@DataProvider(name = "CSMDetailsDTO")
	public static CSMDetailsDTO cSMDetailsDTO()
	{
		CSMDetailsDTO csm = new CSMDetailsDTO();
		csm.setCsmName("David Berg");
		csm.setSiteKey("KUL00");
		csm.setCsmEmailId("david.berg@philips.com");
		csm.setContactNumber("+1 813 743 2894");
		csm.setAdditionalContacts("thomas.conger@philips.com; abhijeet.waykar@philips.com");
		return csm;
	}

	@DataProvider(name = "CSMDetailsDTO")
	public static CustomerDetailsDTO customerDetailsDTO()
	{
		CustomerDetailsDTO customerDetails = new CustomerDetailsDTO();
		customerDetails.setSiteKey("KUL00");
		customerDetails.setCustomerName("Dr. Wolfram Dölken");
		customerDetails.setCustomerEmailId("w.doelken@marienkrankenhaus.com");
		customerDetails.setContactNumber("+49 40 882152560");
		return customerDetails;
	}

}
