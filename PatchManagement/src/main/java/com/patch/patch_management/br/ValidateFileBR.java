package com.patch.patch_management.br;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.patch.patch_management.constant.IPatchManagementMessageCode;
import com.patch.patch_management.exception.ApplBusException;
import com.patch.patch_management.exception.ApplErrorDTO;
import com.patch.patch_management.exception.ApplErrorDTO.Severity;
import com.patch.patch_management.util.MessagesUtil;
import com.patch.patch_management.util.PatchManagementUtil;

@Component("ValidateFileBR")
public class ValidateFileBR implements IApplValidationBR<List<ApplErrorDTO>, Map<String, Object>>
{

	@Override
	public List<ApplErrorDTO> validate(Map<String, Object> map) throws ApplBusException
	{
		String uploadedFileName = map.containsKey("UploadFileName") ? (String) map.get("UploadFileName")
				: null;
		boolean result = false;
		String fileName = Objects.nonNull(uploadedFileName)
				? PatchManagementUtil.fetchOnlyFileName(uploadedFileName)
				: null;
		List<ApplErrorDTO> errorDtoList = new ArrayList<>();
		boolean validateHeader = map.containsKey("validateHeader") ? (boolean) map.get("validateHeader")
				: result;
		boolean validateFile = map.containsKey("validateFile") ? (boolean) map.get("validateFile") : result;
		boolean noData = map.containsKey("NoData") ? (boolean) map.get("NoData") : result;

		if (noData)
		{
			String errMsg = MessagesUtil.getString(IPatchManagementMessageCode.NO_FILE_DATA);
			errorDtoList.add(
					new ApplErrorDTO(Severity.ERROR, IPatchManagementMessageCode.NO_FILE_DATA, errMsg));
		}
		else if (validateHeader)
		{
			List<String> sheetHeaders = (List<String>) map.get("fileHeader");
			List<String> missedHeader = validateHeaders(fileName, sheetHeaders);
			if (!missedHeader.isEmpty())
			{
				String errMsg = MessagesUtil
						.getString(IPatchManagementMessageCode.MISSING_FILE_HEADER_EXCEPTION, missedHeader);
				errorDtoList.add(new ApplErrorDTO(Severity.ERROR,
						IPatchManagementMessageCode.MISSING_FILE_HEADER_EXCEPTION, errMsg));
			}
		}
		else if (validateFile)
		{
			if (!Arrays.asList("SCHEDULE_MASTER", "CUSTOMER_CONTACT_MASTER", "CSM_CONTACT_MASTER")
					.contains(fileName))
			{
				String errMsg = MessagesUtil
						.getString(IPatchManagementMessageCode.INVALID_FILENAME_EXCEPTION);
				errorDtoList.add(new ApplErrorDTO(Severity.ERROR,
						IPatchManagementMessageCode.INVALID_FILENAME_EXCEPTION, errMsg));
			}
			else if (!Arrays.asList("XLSX", "XLX")
					.contains(PatchManagementUtil.fetchFileExt(uploadedFileName)))
			{
				String errMsg = MessagesUtil.getString(IPatchManagementMessageCode.INVALID_FILEEXT_EXCEPTION);
				errorDtoList.add(new ApplErrorDTO(Severity.ERROR,
						IPatchManagementMessageCode.INVALID_FILEEXT_EXCEPTION, errMsg));
			}
		}

		return errorDtoList;
	}

	private List<String> validateHeaders(String fileName, List<String> sheetHeaders)
	{

		List<String> fileHeader = Arrays.stream(MessagesUtil.getString(fileName).split(","))
				.collect(Collectors.toList());
		if (Objects.nonNull(sheetHeaders))
		{
			fileHeader.removeAll(sheetHeaders);
		}
		return fileHeader;

	}

}
