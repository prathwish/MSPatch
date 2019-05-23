package com.patch.patch_management.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.patch.patch_management.br.IApplValidationBR;
import com.patch.patch_management.entity.CSMDetailsEntity;
import com.patch.patch_management.entity.CustomerDetailsEntity;
import com.patch.patch_management.entity.CustomerMasterDataEntity;
import com.patch.patch_management.exception.ApplBusException;
import com.patch.patch_management.exception.ApplErrorCollection;
import com.patch.patch_management.exception.ApplErrorDTO;

@Component
public class PatchManagementValidator
{
	static Logger														logger	= LoggerFactory
			.getLogger(PatchManagementValidator.class);

	@Autowired
	@Qualifier("ValidateScheduleMasterBR")
	private IApplValidationBR<List<ApplErrorDTO>, Map<String, Object>>	validateScheduleMasterBR;

	@Autowired
	@Qualifier("ValidateHospitalCustomerBR")
	private IApplValidationBR<List<ApplErrorDTO>, Map<String, Object>>	validateHospitalCustomerBR;

	@Autowired
	@Qualifier("ValidateBatchCsmRecordBR")
	private IApplValidationBR<List<ApplErrorDTO>, Map<String, Object>>	validateBatchCsmRecordBR;

	@Autowired
	@Qualifier("ValidateFileBR")
	private IApplValidationBR<List<ApplErrorDTO>, Map<String, Object>>	validateFileBR;

	@Autowired
	@Qualifier("ValidateBatchDuplicateFileRecordBR")
	private IApplValidationBR<List<ApplErrorDTO>, Map<String, Object>>	validateBatchDuplicateFileRecordBR;

	public ApplErrorCollection validateScheduleMaster(CustomerMasterDataEntity customerMasterDataEntity,
			boolean isBatchUpload,String siteKey) throws ApplBusException
	{
		ApplErrorCollection errorCollection = new ApplErrorCollection();
		Map<String, Object> entityMap = new HashMap<>();
		entityMap.put("CustomerMasterData", customerMasterDataEntity);
		entityMap.put("BatchUpload", isBatchUpload);
		entityMap.put("SiteKey", siteKey);
		PatchManagementUtil.populateErrorList(validateScheduleMasterBR.validate(entityMap), errorCollection);
		return errorCollection;
	}

	public ApplErrorCollection validateBatchUploadedCustomerRecord(
			CustomerDetailsEntity customerDetailsEntity) throws ApplBusException
	{
		ApplErrorCollection errorCollection = new ApplErrorCollection();
		Map<String, Object> entityMap = new HashMap<>();
		entityMap.put("BatchUploadedCustomerRecord", customerDetailsEntity);
		PatchManagementUtil.populateErrorList(validateHospitalCustomerBR.validate(entityMap),
				errorCollection);
		return errorCollection;
	}

	public ApplErrorCollection validateBatchCsmRecord(CSMDetailsEntity cSMDetailsEntity)
			throws ApplBusException
	{
		ApplErrorCollection errorCollection = new ApplErrorCollection();
		Map<String, Object> entityMap = new HashMap<>();
		entityMap.put("BatchCsmRecord", cSMDetailsEntity);
		
		entityMap.put("BatchUpload", true);
		
		PatchManagementUtil.populateErrorList(validateBatchCsmRecordBR.validate(entityMap), errorCollection);
		return errorCollection;
	}

	public ApplErrorCollection validateFile(String originalFilename) throws ApplBusException
	{
		ApplErrorCollection errorCollection = new ApplErrorCollection();
		Map<String, Object> entityMap = new HashMap<>();
		entityMap.put("UploadFileName", originalFilename);
		entityMap.put("validateFile", true);
		PatchManagementUtil.populateErrorList(validateFileBR.validate(entityMap), errorCollection);
		return errorCollection;
	}

	public ApplErrorCollection validateFileHeader(String fileName, List<String> headerCells)
			throws ApplBusException
	{
		ApplErrorCollection errorCollection = new ApplErrorCollection();
		Map<String, Object> entityMap = new HashMap<>();
		entityMap.put("UploadFileName", fileName);
		entityMap.put("validateHeader", true);
		entityMap.put("fileHeader", headerCells);
		PatchManagementUtil.populateErrorList(validateFileBR.validate(entityMap), errorCollection);
		return errorCollection;
	}

	public ApplErrorCollection validateValidateDuplcateListItem(Set<String> duplicates)
			throws ApplBusException
	{
		ApplErrorCollection errorCollection = new ApplErrorCollection();
		Map<String, Object> entityMap = new HashMap<>();
		entityMap.put("DuplicateRecords", duplicates);
		PatchManagementUtil.populateErrorList(validateBatchDuplicateFileRecordBR.validate(entityMap),
				errorCollection);
		return errorCollection;
	}

	public ApplErrorCollection validateNoData(boolean isFileEmpty) throws ApplBusException
	{
		ApplErrorCollection errorCollection = new ApplErrorCollection();
		Map<String, Object> entityMap = new HashMap<>();
		entityMap.put("NoData", isFileEmpty);
		PatchManagementUtil.populateErrorList(validateFileBR.validate(entityMap), errorCollection);
		return errorCollection;
	}

}
