package com.patch.patch_management.br;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.patch.patch_management.constant.IPatchManagementMessageCode;
import com.patch.patch_management.exception.ApplBusException;
import com.patch.patch_management.exception.ApplErrorDTO;
import com.patch.patch_management.exception.ApplErrorDTO.Severity;
import com.patch.patch_management.util.MessagesUtil;

@Component("ValidateBatchDuplicateFileRecordBR")
public class ValidateBatchDuplicateFileRecordBR
		implements IApplValidationBR<List<ApplErrorDTO>, Map<String, Object>>
{

	@Override
	public List<ApplErrorDTO> validate(Map<String, Object> map) throws ApplBusException
	{
		Set<String> duplicateRecords = ((Set<String>) map.get("DuplicateRecords"));
		List<ApplErrorDTO> errorDtoList = new ArrayList<>();
		if (Objects.nonNull(duplicateRecords) && !duplicateRecords.isEmpty())
		{
			String errMsg = MessagesUtil.getString(
					IPatchManagementMessageCode.DUPLICATE_BATCH_RECORD_EXCEPTION,
					MessagesUtil.getString(IPatchManagementMessageCode.SITE_KEY), duplicateRecords);
			errorDtoList.add(new ApplErrorDTO(Severity.ERROR,
					IPatchManagementMessageCode.DUPLICATE_BATCH_RECORD_EXCEPTION, errMsg));
		}
		return errorDtoList;
	}

}
