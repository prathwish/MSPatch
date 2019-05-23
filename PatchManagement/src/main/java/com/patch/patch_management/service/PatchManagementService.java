package com.patch.patch_management.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.springframework.web.multipart.MultipartFile;

import com.patch.patch_management.dto.HospitalCancellation;
import com.patch.patch_management.dto.HospitalDTO;
import com.patch.patch_management.dto.HospitalSearchDTO;
import com.patch.patch_management.entity.CustomerMasterDataEntity;
import com.patch.patch_management.exception.ApplBusException;

public interface PatchManagementService
{

	void readAndStoreFileDetails(MultipartFile uploadedFile)
			throws ApplBusException, InvalidFormatException, IOException;

	List<HospitalSearchDTO> getAllCustomerDetails();

	CustomerMasterDataEntity saveNewHospital(HospitalDTO hospitalDTO) throws ApplBusException;

	HospitalDTO getHospitalDetailsBySiteKey(String siteKey) throws ApplBusException;

	Map<String, Object> getDropDownValues(String label, Integer value);

	CustomerMasterDataEntity updateHospital(HospitalDTO hospitalDTO) throws ApplBusException;

	CustomerMasterDataEntity updateContract(HospitalCancellation hospitalContractDTO);
}
