package com.patch.patch_management.br;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.patch.patch_management.constant.IPatchManagementMessageCode;
import com.patch.patch_management.entity.CustomerDetailsEntity;
import com.patch.patch_management.entity.CustomerMasterDataEntity;
import com.patch.patch_management.exception.ApplBusException;
import com.patch.patch_management.exception.ApplErrorDTO;
import com.patch.patch_management.exception.ApplErrorDTO.Severity;
import com.patch.patch_management.repository.CustomerDetailsRepository;
import com.patch.patch_management.repository.CustomerMasterDataRepository;
import com.patch.patch_management.util.MessagesUtil;

@Component("ValidateHospitalCustomerBR")
public class ValidateHospitalCustomerBR implements IApplValidationBR<List<ApplErrorDTO>, Map<String, Object>>
{
	@Autowired
	CustomerMasterDataRepository	customerMasterDataRepository;

	@Autowired
	CustomerDetailsRepository		customerDetailsRepository;

	@Override
	public List<ApplErrorDTO> validate(Map<String, Object> map) throws ApplBusException
	{
		CustomerDetailsEntity customerDetailsEntity = (CustomerDetailsEntity) map
				.get("BatchUploadedCustomerRecord");
		List<ApplErrorDTO> errorDtoList = new ArrayList<>();
		if (Objects.nonNull(customerDetailsEntity))
		{
			if (Objects.isNull(customerDetailsEntity.getSiteKey())
					|| customerDetailsEntity.getSiteKey().isEmpty())
			{
				String errMsg = MessagesUtil.getString(IPatchManagementMessageCode.NULL_VALUE_CHECK_EXCEPTION,
						MessagesUtil.getString(IPatchManagementMessageCode.SITE_KEY));
				errorDtoList.add(new ApplErrorDTO(Severity.ERROR,
						IPatchManagementMessageCode.NULL_VALUE_CHECK_EXCEPTION, errMsg));
			}
			else if (Objects.isNull(customerDetailsEntity.getCustomerId()))
			{
				CustomerMasterDataEntity master = customerMasterDataRepository
						.findBySiteKey(customerDetailsEntity.getSiteKey());
				if (Objects.isNull(master))
				{
					String errMsg = MessagesUtil.getString(
							IPatchManagementMessageCode.AVAILABILITY_CHECK_EXCEPTION,
							MessagesUtil.getString(IPatchManagementMessageCode.SITE_KEY),
							customerDetailsEntity.getSiteKey());
					errorDtoList.add(new ApplErrorDTO(Severity.ERROR,
							IPatchManagementMessageCode.AVAILABILITY_CHECK_EXCEPTION, errMsg));
				}
			}

		}
		return errorDtoList;
	}

}
