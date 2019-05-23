package com.patch.patch_management.br;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.patch.patch_management.constant.IPatchManagementMessageCode;
import com.patch.patch_management.entity.CSMDetailsEntity;
import com.patch.patch_management.entity.CustomerMasterDataEntity;
import com.patch.patch_management.exception.ApplBusException;
import com.patch.patch_management.exception.ApplErrorDTO;
import com.patch.patch_management.exception.ApplErrorDTO.Severity;
import com.patch.patch_management.repository.CSMRepository;
import com.patch.patch_management.repository.CustomerMasterDataRepository;
import com.patch.patch_management.util.MessagesUtil;

@Component("ValidateBatchCsmRecordBR")
public class ValidateBatchCsmRecordBR implements IApplValidationBR<List<ApplErrorDTO>, Map<String, Object>>
{
	@Autowired
	CustomerMasterDataRepository	customerMasterDataRepository;

	@Autowired
	CSMRepository					cSMRepository;

	@Override
	public List<ApplErrorDTO> validate(Map<String, Object> map) throws ApplBusException
	{
		CSMDetailsEntity cSMDetailsEntity = (CSMDetailsEntity) map.get("BatchCsmRecord");
		boolean batchUpload = (boolean) map.get("BatchUpload");
		List<ApplErrorDTO> errorDtoList = new ArrayList<>();
		if (Objects.nonNull(cSMDetailsEntity))
		{
			if (batchUpload)
			{
				if (Objects.isNull(cSMDetailsEntity.getSiteKey()) || cSMDetailsEntity.getSiteKey().isEmpty())
				{

					String errMsg = MessagesUtil.getString(
							IPatchManagementMessageCode.NULL_VALUE_CHECK_EXCEPTION,
							MessagesUtil.getString(IPatchManagementMessageCode.SITE_KEY));
					errorDtoList.add(new ApplErrorDTO(Severity.ERROR,
							IPatchManagementMessageCode.NULL_VALUE_CHECK_EXCEPTION, errMsg));
				}
				else
				{
					CSMDetailsEntity csm = cSMRepository.findBySiteKey(cSMDetailsEntity.getSiteKey());
					if (Objects.nonNull(csm))
					{
						String errMsg = MessagesUtil.getString(
								IPatchManagementMessageCode.DUPLICATE_VALUE_CHECK_EXCEPTION,
								MessagesUtil.getString(IPatchManagementMessageCode.SITE_KEY),
								cSMDetailsEntity.getSiteKey());
						errorDtoList.add(new ApplErrorDTO(Severity.ERROR,
								IPatchManagementMessageCode.DUPLICATE_VALUE_CHECK_EXCEPTION, errMsg));
					}

					CustomerMasterDataEntity master = customerMasterDataRepository
							.findBySiteKey(cSMDetailsEntity.getSiteKey());
					if (Objects.isNull(master))
					{
						String errMsg = MessagesUtil.getString(
								IPatchManagementMessageCode.AVAILABILITY_CHECK_EXCEPTION,
								MessagesUtil.getString(IPatchManagementMessageCode.SITE_KEY),
								cSMDetailsEntity.getSiteKey());
						errorDtoList.add(new ApplErrorDTO(Severity.ERROR,
								IPatchManagementMessageCode.AVAILABILITY_CHECK_EXCEPTION, errMsg));
					}
				}
			}
			if (Objects.isNull(cSMDetailsEntity.getCsmEmailId())
					&& Objects.isNull(cSMDetailsEntity.getSiteKey()))
			{
				String errMsg = null;
				if (batchUpload)
				{
					errMsg = MessagesUtil.getString(
							IPatchManagementMessageCode.INVALID_FIELD_VALUE_CHECK_EXCEPTION,
							MessagesUtil.getString(IPatchManagementMessageCode.CSM_EMAIL),
							MessagesUtil.getString(IPatchManagementMessageCode.SITE_KEY),
							cSMDetailsEntity.getSiteKey());
					errorDtoList.add(new ApplErrorDTO(Severity.ERROR,
							IPatchManagementMessageCode.INVALID_FIELD_VALUE_CHECK_EXCEPTION, errMsg));
				}
				else
				{
					errMsg = MessagesUtil.getString(IPatchManagementMessageCode.NULL_VALUE_CHECK_EXCEPTION,
							MessagesUtil.getString(IPatchManagementMessageCode.CSM_EMAIL));

					errorDtoList.add(new ApplErrorDTO(Severity.ERROR,
							IPatchManagementMessageCode.NULL_VALUE_CHECK_EXCEPTION, errMsg));
				}
			}
		}

		return errorDtoList;
	}
}
