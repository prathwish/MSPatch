package com.patch.patch_management.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.patch.patch_management.dto.HospitalCancellation;
import com.patch.patch_management.dto.HospitalDTO;
import com.patch.patch_management.dto.HospitalSearchDTO;
import com.patch.patch_management.dto.ResponseMessageDTO;
import com.patch.patch_management.entity.CustomerMasterDataEntity;
import com.patch.patch_management.exception.ApplBusException;
import com.patch.patch_management.service.PatchManagementService;

@Async
@RestController
@RequestMapping("/api")
@CrossOrigin
public class PatchManagementController
{

	static Logger			logger	= LoggerFactory.getLogger(PatchManagementController.class);

	@Autowired
	PatchManagementService	patchManagementService;

	// 20th of every month at 4 AM
	/**
	 * @name : createRoaster
	 * 
	 *
	 * @returns : ResponseEntity<String>
	 * 
	 * @throws :
	 *             Exception
	 * 
	 * @description : Schedule to create roaster for all the active hospital in the system.
	 * 
	 */
	@Scheduled(cron = "0 0 4 20 1/1 *")
	public CompletableFuture<ResponseEntity<String>> createRoaster() throws Exception
	{
		logger.info("cron job started...");
		return CompletableFuture.completedFuture(new ResponseEntity<>("Success", HttpStatus.OK));
	}

	@PostMapping(value = "/patchmanagement/fileupload", consumes =
	{ "multipart/form-data" })
	public CompletableFuture<ResponseEntity<Void>> batchUpload(
			@RequestParam("uploadedFile") MultipartFile uploadedFile)
			throws ApplBusException, InvalidFormatException, IOException
	{
		patchManagementService.readAndStoreFileDetails(uploadedFile);
		return CompletableFuture.completedFuture(new ResponseEntity<>(HttpStatus.OK));

	}

	@GetMapping(value = "/patchmanagement/dropdown")
	public CompletableFuture<ResponseEntity<Map<String, Object>>> getDropDownValues(
			@RequestParam(value = "label", defaultValue = "All") String label,
			@RequestParam(value = "id", required = false) Integer value)
	{
		return CompletableFuture.completedFuture(
				new ResponseEntity<>(patchManagementService.getDropDownValues(label, value), HttpStatus.OK));
	}

	@GetMapping(value = "/patchmanagement/hospital/search")
	public CompletableFuture<ResponseEntity<List<HospitalSearchDTO>>> getAllHospitalDetails()
	{
		return CompletableFuture.completedFuture(
				new ResponseEntity<>(patchManagementService.getAllCustomerDetails(), HttpStatus.OK));
	}

	@GetMapping(value = "/patchmanagement/hospital/{siteKey}")
	public CompletableFuture<ResponseEntity<HospitalDTO>> getHospitalBySiteKey(@PathVariable String siteKey)
			throws ApplBusException
	{

		return CompletableFuture.completedFuture(new ResponseEntity<>(
				patchManagementService.getHospitalDetailsBySiteKey(siteKey), HttpStatus.OK));

	}

	@PostMapping(value = "/patchmanagement/onboard/hospital")
	public CompletableFuture<ResponseEntity<ResponseMessageDTO>> createHospital(
			@RequestBody HospitalDTO hospitalDTO) throws ApplBusException
	{
		CustomerMasterDataEntity customerMasterDataEntity = patchManagementService
				.saveNewHospital(hospitalDTO);
		return CompletableFuture.completedFuture(new ResponseEntity<>(
				populateSuccessMessage(customerMasterDataEntity.getHospitalId(), "Successfully created."),
				HttpStatus.OK));

	}

	@PutMapping(value = "/patchmanagement/onboard/hospital")
	public CompletableFuture<ResponseEntity<ResponseMessageDTO>> updateHospital(
			@RequestBody HospitalDTO hospitalDTO) throws ApplBusException
	{
		CustomerMasterDataEntity customerMasterDataEntity = patchManagementService
				.updateHospital(hospitalDTO);
		return CompletableFuture.completedFuture(new ResponseEntity<>(
				populateSuccessMessage(customerMasterDataEntity.getHospitalId(), "Successfully Updated."),
				HttpStatus.OK));

	}

	@PutMapping(value = "/patchmanagement/hospital/contract")
	public CompletableFuture<ResponseEntity<ResponseMessageDTO>> contractHospital(
			@RequestBody HospitalCancellation hospitalContractDTO)
	{
		CustomerMasterDataEntity customerMasterDataEntity = patchManagementService
				.updateContract(hospitalContractDTO);
		return CompletableFuture.completedFuture(new ResponseEntity<>(
				populateSuccessMessage(customerMasterDataEntity.getHospitalId(), "Successfully Updated."),
				HttpStatus.OK));

	}

	private ResponseMessageDTO populateSuccessMessage(Integer id, String message)
	{
		ResponseMessageDTO responseMessageDTO = new ResponseMessageDTO();
		responseMessageDTO.setHospitalId(id);
		responseMessageDTO.setMessage(message);
		return responseMessageDTO;
	}

}
