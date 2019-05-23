package com.patch.patch_management.br;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.patch.patch_management.constant.IPatchManagementMessageCode;
import com.patch.patch_management.entity.CustomerMasterDataEntity;
import com.patch.patch_management.entity.TimeZoneDetailsEntity;
import com.patch.patch_management.entity.TimeZoneEntity;
import com.patch.patch_management.exception.ApplBusException;
import com.patch.patch_management.exception.ApplErrorDTO;
import com.patch.patch_management.exception.ApplErrorDTO.Severity;
import com.patch.patch_management.repository.CustomerMasterDataRepository;
import com.patch.patch_management.repository.TimeZoneDetailsRepository;
import com.patch.patch_management.repository.TimeZoneRepository;
import com.patch.patch_management.util.MessagesUtil;

@Component("ValidateScheduleMasterBR")
public class ValidateScheduleMasterBR implements IApplValidationBR<List<ApplErrorDTO>, Map<String, Object>>
{
	@Autowired
	CustomerMasterDataRepository										customerMasterDataRepository;

	@Autowired
	@Qualifier("ValidateBatchCsmRecordBR")
	private IApplValidationBR<List<ApplErrorDTO>, Map<String, Object>>	validateBatchCsmRecordBR;

	@Autowired
	TimeZoneRepository													timeZoneRepository;

	@Autowired
	TimeZoneDetailsRepository											timeZoneDetailsRepository;

	@Override
	public List<ApplErrorDTO> validate(Map<String, Object> map) throws ApplBusException
	{

		CustomerMasterDataEntity customerMasterDataEntity = (CustomerMasterDataEntity) map
				.get("CustomerMasterData");
		Boolean isBatchUpload = (Boolean) map.get("BatchUpload");
		String siteKey = map.containsKey("SiteKey") ? (String) map.get("SiteKey") : null;
		List<ApplErrorDTO> errorDtoList = new ArrayList<>();
		if (Objects.nonNull(customerMasterDataEntity))
		{
			if (Objects.isNull(customerMasterDataEntity.getSiteKey())
					|| customerMasterDataEntity.getSiteKey().isEmpty())
			{
				String errMsg = MessagesUtil.getString(IPatchManagementMessageCode.NULL_VALUE_CHECK_EXCEPTION,
						MessagesUtil.getString(IPatchManagementMessageCode.SITE_KEY));
				errorDtoList.add(new ApplErrorDTO(Severity.ERROR,
						IPatchManagementMessageCode.NULL_VALUE_CHECK_EXCEPTION, errMsg));
			}
			else
			{
				CustomerMasterDataEntity master = customerMasterDataRepository
						.findBySiteKey(customerMasterDataEntity.getSiteKey());
				if (Objects.nonNull(master))
				{
					if (Objects.isNull(customerMasterDataEntity.getHospitalId()))
					{
						String errMsg = MessagesUtil.getString(
								IPatchManagementMessageCode.DUPLICATE_VALUE_CHECK_EXCEPTION,
								MessagesUtil.getString(IPatchManagementMessageCode.SITE_KEY),
								customerMasterDataEntity.getSiteKey());
						errorDtoList.add(new ApplErrorDTO(Severity.ERROR,
								IPatchManagementMessageCode.DUPLICATE_VALUE_CHECK_EXCEPTION, errMsg));
					}
					else if (!customerMasterDataEntity.getHospitalId().equals(master.getHospitalId()))
					{
						String errMsg = MessagesUtil.getString(
								IPatchManagementMessageCode.DUPLICATE_VALUE_CHECK_EXCEPTION,
								MessagesUtil.getString(IPatchManagementMessageCode.SITE_KEY),
								customerMasterDataEntity.getSiteKey());
						errorDtoList.add(new ApplErrorDTO(Severity.ERROR,
								IPatchManagementMessageCode.DUPLICATE_VALUE_CHECK_EXCEPTION, errMsg));
					}
				}

				if (Objects.isNull(customerMasterDataEntity.getConsole()))
				{
					String errMsg = null;
					if (isBatchUpload)
					{
						errMsg = MessagesUtil.getString(
								IPatchManagementMessageCode.INVALID_FIELD_VALUE_CHECK_EXCEPTION,
								MessagesUtil.getString(IPatchManagementMessageCode.CONSOLE),
								MessagesUtil.getString(IPatchManagementMessageCode.SITE_KEY),
								customerMasterDataEntity.getSiteKey());
						errorDtoList.add(new ApplErrorDTO(Severity.ERROR,
								IPatchManagementMessageCode.INVALID_FIELD_VALUE_CHECK_EXCEPTION, errMsg));
					}
					else
					{
						errMsg = MessagesUtil.getString(
								IPatchManagementMessageCode.NULL_VALUE_CHECK_EXCEPTION,
								MessagesUtil.getString(IPatchManagementMessageCode.CONSOLE));
						errorDtoList.add(new ApplErrorDTO(Severity.ERROR,
								IPatchManagementMessageCode.NULL_VALUE_CHECK_EXCEPTION, errMsg));
					}

				}
				if (Objects.isNull(customerMasterDataEntity.getDay()))
				{
					String errMsg = null;
					if (isBatchUpload)
					{
						errMsg = MessagesUtil.getString(
								IPatchManagementMessageCode.INVALID_FIELD_VALUE_CHECK_EXCEPTION,
								MessagesUtil.getString(IPatchManagementMessageCode.DAY),
								MessagesUtil.getString(IPatchManagementMessageCode.SITE_KEY),
								customerMasterDataEntity.getSiteKey());
						errorDtoList.add(new ApplErrorDTO(Severity.ERROR,
								IPatchManagementMessageCode.INVALID_FIELD_VALUE_CHECK_EXCEPTION, errMsg));
					}
					else
					{
						errMsg = MessagesUtil.getString(
								IPatchManagementMessageCode.NULL_VALUE_CHECK_EXCEPTION,
								MessagesUtil.getString(IPatchManagementMessageCode.DAY));
						errorDtoList.add(new ApplErrorDTO(Severity.ERROR,
								IPatchManagementMessageCode.NULL_VALUE_CHECK_EXCEPTION, errMsg));
					}
				}
				if (Objects.isNull(customerMasterDataEntity.getRegion()))
				{
					String errMsg = null;
					if (isBatchUpload)
					{
						errMsg = MessagesUtil.getString(
								IPatchManagementMessageCode.INVALID_FIELD_VALUE_CHECK_EXCEPTION,
								MessagesUtil.getString(IPatchManagementMessageCode.REGION),
								MessagesUtil.getString(IPatchManagementMessageCode.SITE_KEY),
								customerMasterDataEntity.getSiteKey());
						errorDtoList.add(new ApplErrorDTO(Severity.ERROR,
								IPatchManagementMessageCode.INVALID_FIELD_VALUE_CHECK_EXCEPTION, errMsg));
					}
					else
					{
						errMsg = MessagesUtil.getString(
								IPatchManagementMessageCode.NULL_VALUE_CHECK_EXCEPTION,
								MessagesUtil.getString(IPatchManagementMessageCode.REGION));
						errorDtoList.add(new ApplErrorDTO(Severity.ERROR,
								IPatchManagementMessageCode.NULL_VALUE_CHECK_EXCEPTION, errMsg));
					}
				}
				if (Objects.isNull(customerMasterDataEntity.getScheduledStarttime())
						|| customerMasterDataEntity.getScheduledStarttime().isEmpty())
				{
					String errMsg = null;
					if (isBatchUpload)
					{
						errMsg = MessagesUtil.getString(
								IPatchManagementMessageCode.INVALID_FIELD_VALUE_CHECK_EXCEPTION,
								MessagesUtil.getString(IPatchManagementMessageCode.SCHEDULEDSTARTTIME),
								MessagesUtil.getString(IPatchManagementMessageCode.SITE_KEY),
								customerMasterDataEntity.getSiteKey());
						errorDtoList.add(new ApplErrorDTO(Severity.ERROR,
								IPatchManagementMessageCode.INVALID_FIELD_VALUE_CHECK_EXCEPTION, errMsg));
					}
					else
					{
						errMsg = MessagesUtil.getString(
								IPatchManagementMessageCode.NULL_VALUE_CHECK_EXCEPTION,
								MessagesUtil.getString(IPatchManagementMessageCode.SCHEDULEDSTARTTIME));
						errorDtoList.add(new ApplErrorDTO(Severity.ERROR,
								IPatchManagementMessageCode.NULL_VALUE_CHECK_EXCEPTION, errMsg));
					}
				}
				if (Objects.isNull(customerMasterDataEntity.getSession()))
				{
					String errMsg = null;
					if (isBatchUpload)
					{
						errMsg = MessagesUtil.getString(
								IPatchManagementMessageCode.INVALID_FIELD_VALUE_CHECK_EXCEPTION,
								MessagesUtil.getString(IPatchManagementMessageCode.SESSION),
								MessagesUtil.getString(IPatchManagementMessageCode.SITE_KEY),
								customerMasterDataEntity.getSiteKey());
						errorDtoList.add(new ApplErrorDTO(Severity.ERROR,
								IPatchManagementMessageCode.INVALID_FIELD_VALUE_CHECK_EXCEPTION, errMsg));
					}
					else
					{
						errMsg = MessagesUtil.getString(
								IPatchManagementMessageCode.NULL_VALUE_CHECK_EXCEPTION,
								MessagesUtil.getString(IPatchManagementMessageCode.SESSION));
						errorDtoList.add(new ApplErrorDTO(Severity.ERROR,
								IPatchManagementMessageCode.NULL_VALUE_CHECK_EXCEPTION, errMsg));
					}
				}
				if (Objects.isNull(customerMasterDataEntity.getTimeZone()))
				{
					String errMsg = null;
					if (isBatchUpload)
					{
						errMsg = MessagesUtil.getString(
								IPatchManagementMessageCode.INVALID_FIELD_VALUE_CHECK_EXCEPTION,
								MessagesUtil.getString(IPatchManagementMessageCode.TIMEZONE),
								MessagesUtil.getString(IPatchManagementMessageCode.SITE_KEY),
								customerMasterDataEntity.getSiteKey());
						errorDtoList.add(new ApplErrorDTO(Severity.ERROR,
								IPatchManagementMessageCode.INVALID_FIELD_VALUE_CHECK_EXCEPTION, errMsg));
					}
					else
					{
						errMsg = MessagesUtil.getString(
								IPatchManagementMessageCode.NULL_VALUE_CHECK_EXCEPTION,
								MessagesUtil.getString(IPatchManagementMessageCode.TIMEZONE));
						errorDtoList.add(new ApplErrorDTO(Severity.ERROR,
								IPatchManagementMessageCode.NULL_VALUE_CHECK_EXCEPTION, errMsg));
					}
				}
				else
				{
					if (Objects.nonNull(customerMasterDataEntity.getRegion()))
					{
						List<TimeZoneEntity> timeZones = timeZoneRepository
								.findByRegionEntityId_Id(customerMasterDataEntity.getRegion().getId());
						if (!(timeZones.contains(customerMasterDataEntity.getTimeZone())))
						{
							customerMasterDataEntity.setTimeZone(null);
							String errMsg = MessagesUtil.getString(
									IPatchManagementMessageCode.INVALID_VALUE_CHECK_EXCEPTION,
									MessagesUtil.getString(IPatchManagementMessageCode.TIMEZONE),
									MessagesUtil.getString(IPatchManagementMessageCode.REGION),
									customerMasterDataEntity.getRegion().getName(),
									MessagesUtil.getString(IPatchManagementMessageCode.SITE_KEY),
									customerMasterDataEntity.getSiteKey());
							errorDtoList.add(new ApplErrorDTO(Severity.ERROR,
									IPatchManagementMessageCode.INVALID_VALUE_CHECK_EXCEPTION, errMsg));
						}
					}
				}
				if (Objects.isNull(customerMasterDataEntity.getAddedDate()))
				{
					String errMsg = null;
					if (isBatchUpload)
					{
						errMsg = MessagesUtil.getString(
								IPatchManagementMessageCode.INVALID_FIELD_VALUE_CHECK_EXCEPTION,
								MessagesUtil.getString(IPatchManagementMessageCode.ADDED_DATE),
								MessagesUtil.getString(IPatchManagementMessageCode.SITE_KEY),
								customerMasterDataEntity.getSiteKey());
						errorDtoList.add(new ApplErrorDTO(Severity.ERROR,
								IPatchManagementMessageCode.INVALID_FIELD_VALUE_CHECK_EXCEPTION, errMsg));
					}
					else
					{
						errMsg = MessagesUtil.getString(
								IPatchManagementMessageCode.NULL_VALUE_CHECK_EXCEPTION,
								MessagesUtil.getString(IPatchManagementMessageCode.ADDED_DATE));
						errorDtoList.add(new ApplErrorDTO(Severity.ERROR,
								IPatchManagementMessageCode.NULL_VALUE_CHECK_EXCEPTION, errMsg));
					}
				}
				if (Objects.isNull(customerMasterDataEntity.getWeek()))
				{
					String errMsg = null;
					if (isBatchUpload)
					{
						errMsg = MessagesUtil.getString(
								IPatchManagementMessageCode.INVALID_FIELD_VALUE_CHECK_EXCEPTION,
								MessagesUtil.getString(IPatchManagementMessageCode.WEEK),
								MessagesUtil.getString(IPatchManagementMessageCode.SITE_KEY),
								customerMasterDataEntity.getSiteKey());
						errorDtoList.add(new ApplErrorDTO(Severity.ERROR,
								IPatchManagementMessageCode.INVALID_FIELD_VALUE_CHECK_EXCEPTION, errMsg));
					}
					else
					{
						errMsg = MessagesUtil.getString(
								IPatchManagementMessageCode.NULL_VALUE_CHECK_EXCEPTION,
								MessagesUtil.getString(IPatchManagementMessageCode.WEEK));
						errorDtoList.add(new ApplErrorDTO(Severity.ERROR,
								IPatchManagementMessageCode.NULL_VALUE_CHECK_EXCEPTION, errMsg));
					}
				}
				if (Objects.isNull(customerMasterDataEntity.getTimeZoneDetailsEntity()))
				{
					String errMsg = null;
					if (isBatchUpload)
					{
						errMsg = MessagesUtil.getString(
								IPatchManagementMessageCode.INVALID_FIELD_VALUE_CHECK_EXCEPTION,
								MessagesUtil.getString(IPatchManagementMessageCode.CITY),
								MessagesUtil.getString(IPatchManagementMessageCode.SITE_KEY),
								customerMasterDataEntity.getSiteKey());
						errorDtoList.add(new ApplErrorDTO(Severity.ERROR,
								IPatchManagementMessageCode.INVALID_FIELD_VALUE_CHECK_EXCEPTION, errMsg));
					}
					else
					{
						errMsg = MessagesUtil.getString(
								IPatchManagementMessageCode.NULL_VALUE_CHECK_EXCEPTION,
								MessagesUtil.getString(IPatchManagementMessageCode.CITY));
						errorDtoList.add(new ApplErrorDTO(Severity.ERROR,
								IPatchManagementMessageCode.NULL_VALUE_CHECK_EXCEPTION, errMsg));
					}
				}
				else
				{
					if (Objects.nonNull(customerMasterDataEntity.getRegion())
							&& Objects.nonNull(customerMasterDataEntity.getTimeZone()))
					{
						List<TimeZoneDetailsEntity> cities = timeZoneDetailsRepository
								.findByTimeZoneEntityId_Id(customerMasterDataEntity.getTimeZone().getId());
						if (!(cities.contains(customerMasterDataEntity.getTimeZoneDetailsEntity())))
						{
							String errMsg = MessagesUtil.getString(
									IPatchManagementMessageCode.INVALID_VALUE_CHECK_EXCEPTION,
									MessagesUtil.getString(IPatchManagementMessageCode.CITY),
									MessagesUtil.getString(IPatchManagementMessageCode.TIMEZONE),
									customerMasterDataEntity.getTimeZone().getName(),
									MessagesUtil.getString(IPatchManagementMessageCode.SITE_KEY),
									customerMasterDataEntity.getSiteKey());
							errorDtoList.add(new ApplErrorDTO(Severity.ERROR,
									IPatchManagementMessageCode.INVALID_VALUE_CHECK_EXCEPTION, errMsg));
						}
					}
				}
				if (!isBatchUpload)
				{
					if (Objects.isNull(customerMasterDataEntity.getCSMDetails()))
					{
						String errMsg = MessagesUtil.getString(
								IPatchManagementMessageCode.NULL_VALUE_CHECK_EXCEPTION,
								MessagesUtil.getString(IPatchManagementMessageCode.CSM));
						errorDtoList.add(new ApplErrorDTO(Severity.ERROR,
								IPatchManagementMessageCode.NULL_VALUE_CHECK_EXCEPTION, errMsg));
					}
					else
					{
						map.put("BatchCsmRecord", customerMasterDataEntity.getCSMDetails());
						map.put("BatchUpload", false);
						errorDtoList.addAll(validateBatchCsmRecordBR.validate(map));
					}
				}
				if (!isBatchUpload && (Objects.isNull(customerMasterDataEntity.getCustomerDetails())
						|| customerMasterDataEntity.getCustomerDetails().isEmpty()))
				{
					String errMsg = MessagesUtil.getString(
							IPatchManagementMessageCode.NULL_VALUE_CHECK_EXCEPTION,
							MessagesUtil.getString(IPatchManagementMessageCode.CUSTOMER_DETAILS));
					errorDtoList.add(new ApplErrorDTO(Severity.ERROR,
							IPatchManagementMessageCode.NULL_VALUE_CHECK_EXCEPTION, errMsg));
				}

			}
		}
		else
		{
			String errMsg = MessagesUtil.getString(IPatchManagementMessageCode.AVAILABILITY_CHECK_EXCEPTION,
					MessagesUtil.getString(IPatchManagementMessageCode.SITE_KEY), siteKey);
			errorDtoList.add(new ApplErrorDTO(Severity.ERROR,
					IPatchManagementMessageCode.AVAILABILITY_CHECK_EXCEPTION, errMsg));
		}
		return errorDtoList;
	}

}
