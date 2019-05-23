package com.patch.patch_management.service;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.patch.patch_management.constant.IPatchManagementConstants;
import com.patch.patch_management.constant.IPatchManagementMessageCode;
import com.patch.patch_management.dto.CSMDetailsDTO;
import com.patch.patch_management.dto.CustomerDetailsDTO;
import com.patch.patch_management.dto.DropDownDTO;
import com.patch.patch_management.dto.HospitalCancellation;
import com.patch.patch_management.dto.HospitalDTO;
import com.patch.patch_management.dto.HospitalSearchDTO;
import com.patch.patch_management.entity.CSMDetailsEntity;
import com.patch.patch_management.entity.ConsoleEntity;
import com.patch.patch_management.entity.ContractCancellation;
import com.patch.patch_management.entity.CustomerDetailsEntity;
import com.patch.patch_management.entity.CustomerMasterDataEntity;
import com.patch.patch_management.entity.DayEntity;
import com.patch.patch_management.entity.RegionEntity;
import com.patch.patch_management.entity.SessionEntity;
import com.patch.patch_management.entity.TimeZoneDetailsEntity;
import com.patch.patch_management.entity.TimeZoneEntity;
import com.patch.patch_management.exception.ApplBusException;
import com.patch.patch_management.exception.ApplErrorCollection;
import com.patch.patch_management.repository.CSMRepository;
import com.patch.patch_management.repository.ConsoleRepository;
import com.patch.patch_management.repository.CustomerDetailsRepository;
import com.patch.patch_management.repository.CustomerMasterDataRepository;
import com.patch.patch_management.repository.DayRepository;
import com.patch.patch_management.repository.RegionRepository;
import com.patch.patch_management.repository.SessionRepository;
import com.patch.patch_management.repository.TimeZoneDetailsRepository;
import com.patch.patch_management.repository.TimeZoneRepository;
import com.patch.patch_management.util.MessagesUtil;
import com.patch.patch_management.util.PatchManagementUtil;
import com.patch.patch_management.util.PatchManagementValidator;
import com.patch.patch_management.util.TimeZoneConversionUtil;
import com.patch.patch_management.util.UploadUtil;

@Component
public class PatchManagementServiceImpl implements PatchManagementService
{

	static Logger					logger		= LoggerFactory.getLogger(PatchManagementServiceImpl.class);

	@Autowired
	CustomerMasterDataRepository	customerMasterDataRepository;

	@Autowired
	ConsoleRepository				consoleRepository;

	@Autowired
	DayRepository					dayRepository;

	@Autowired
	SessionRepository				sessionRepository;

	@Autowired
	TimeZoneRepository				timeZoneRepository;

	@Autowired
	RegionRepository				regionRepository;

	@Autowired
	CSMRepository					cSMRepository;

	@Autowired
	CustomerDetailsRepository		customerDetailsRepository;

	@Autowired
	TimeZoneDetailsRepository		timeZoneDetailsRepository;

	@Autowired
	UploadUtil						uploadUtil;

	@Autowired
	PatchManagementValidator		patchManagementValidator;

	@Autowired
	PatchManagementUtil				patchManagementUtil;

	ModelMapper						modelMapper	= new ModelMapper();

	@Override
	public void readAndStoreFileDetails(MultipartFile uploadedFile)
			throws ApplBusException, InvalidFormatException, IOException
	{
		String fileName = null;
		fileName = PatchManagementUtil.fetchOnlyFileName(uploadedFile.getOriginalFilename());
		ApplErrorCollection errorCollection = patchManagementValidator
				.validateFile(uploadedFile.getOriginalFilename());
		throwErrorCollection(errorCollection);
		try (Workbook workbook = WorkbookFactory.create(uploadedFile.getInputStream());)
		{
			Sheet sheet = workbook.getSheetAt(0);
			Supplier<Stream<Row>> rowStreamSupplier = uploadUtil.getRowStreamSupplier(sheet);
			Optional<Row> firstRow = rowStreamSupplier.get().findFirst();
			List<String> headerCells = firstRow.isPresent() ? uploadUtil.getStream(firstRow.get())
					.map(header -> header.toString().trim().toUpperCase()).collect(Collectors.toList())
					: null;
			errorCollection = patchManagementValidator.validateFileHeader(fileName, headerCells);
			throwErrorCollection(errorCollection);
			Stream<Row> rows = rowStreamSupplier.get().skip(1);
			saveFileRowDetails(rows, headerCells, fileName);
		}
		catch (Exception ex)
		{
			throw ex;
		}
	}

	private void saveFileRowDetails(Stream<Row> rows, List<String> headerCells, String fileName)
			throws ApplBusException
	{
		switch (fileName.trim().toUpperCase())
		{
		case "CUSTOMER_CONTACT_MASTER":
			List<CustomerDetailsEntity> customerDetails = fetchFileCustomerRecord(rows, headerCells);
			customerDetailsRepository.saveAll(customerDetails);
			break;
		case "CSM_CONTACT_MASTER":
			List<CSMDetailsEntity> cSMDetails = fetchFileCsmRecord(rows, headerCells);
			cSMRepository.saveAll(cSMDetails);
			break;
		case "SCHEDULE_MASTER":
			List<CustomerMasterDataEntity> newHospitals = fetchFileNewHospitalRecord(rows, headerCells);
			customerMasterDataRepository.saveAll(newHospitals);
			break;
		case "SSE_SHEET":
			break;
		default:
			break;
		}
	}

	private List<CustomerMasterDataEntity> fetchFileNewHospitalRecord(Stream<Row> rows,
			List<String> headerCells) throws ApplBusException
	{
		List<String> siteKeys = new ArrayList<>();
		List<CustomerMasterDataEntity> newHospitals = new ArrayList<>();
		boolean isFileEmpty = true;
		for (Row row : rows.collect(Collectors.toList()))
		{
			if (!checkIfRowIsEmpty(row))
			{
				isFileEmpty = false;
				Map<String, String> rowDetails = fetchFileRowDetails(row, headerCells);
				CustomerMasterDataEntity customerMasterDataEntity = populateHospitalMasterDetailsEntity(
						rowDetails);
				validateScheduleMaster(customerMasterDataEntity, true, null);
				siteKeys.add(customerMasterDataEntity.getSiteKey());
				newHospitals.add(customerMasterDataEntity);
			}
		}
		validateNoData(isFileEmpty);
		validateDuplcateListItem(siteKeys);
		return newHospitals;
	}

	@Override
	public CustomerMasterDataEntity updateHospital(HospitalDTO hospitalDTO) throws ApplBusException
	{
		return persistUpdateHospital(hospitalDTO);
	}

	private CustomerMasterDataEntity persistUpdateHospital(HospitalDTO hospitalDTO) throws ApplBusException
	{
		CustomerMasterDataEntity customerMasterDataEntity = customerMasterDataRepository
				.findBySiteKey(hospitalDTO.getSiteKey());
		if (Objects.nonNull(customerMasterDataEntity))
		{
			customerMasterDataEntity.setApplicationVersion(hospitalDTO.getApplicationVersion());
			customerMasterDataEntity.setComment(hospitalDTO.getComment());
			Optional<ConsoleEntity> console = consoleRepository.findById(hospitalDTO.getConsole().getId());
			customerMasterDataEntity.setConsole(console.isPresent() ? console.get() : null);
			customerMasterDataEntity.setAddedDate(hospitalDTO.getAddedDate());
			customerMasterDataEntity
					.setCSMDetails(populateCSMDetails(hospitalDTO.getCsmDetails(), customerMasterDataEntity));

			customerMasterDataEntity.setCustomerDetails(
					populateCustomerDetails(hospitalDTO.getCustomerDetailsDTO(), customerMasterDataEntity));

			customerMasterDataEntity.setCustomerName(hospitalDTO.getCustomerName());
			Optional<DayEntity> day = dayRepository.findById(hospitalDTO.getDay().getId());
			customerMasterDataEntity.setDay(day.isPresent() ? day.get() : null);
			Integer duration = Objects.nonNull(hospitalDTO.getDuration()) ? hospitalDTO.getDuration()
					: IPatchManagementConstants.DEFAULT_DURATION;
			customerMasterDataEntity.setDuration(duration);
			Optional<RegionEntity> region = regionRepository.findById(hospitalDTO.getRegion().getId());
			customerMasterDataEntity.setRegion(region.isPresent() ? region.get() : null);
			String endTime = calculateScheduledEndTime(hospitalDTO.getScheduledStartTime(), duration);
			customerMasterDataEntity.setScheduledEndtime(endTime);
			customerMasterDataEntity.setScheduledStarttime(hospitalDTO.getScheduledStartTime());
			Optional<SessionEntity> session = sessionRepository.findById(hospitalDTO.getSession().getId());
			customerMasterDataEntity.setSession(session.isPresent() ? session.get() : null);
			Optional<TimeZoneEntity> timeZone = timeZoneRepository
					.findById(hospitalDTO.getTimeZone().getId());
			Optional<TimeZoneDetailsEntity> city = timeZoneDetailsRepository
					.findById(hospitalDTO.getTimeZoneDetails().getId());
			customerMasterDataEntity.setTimeZoneDetailsEntity(city.isPresent() ? city.get() : null);
			customerMasterDataEntity.setTimeZone(timeZone.isPresent() ? timeZone.get() : null);
			customerMasterDataEntity.setWeek(hospitalDTO.getWeek());
			validateScheduleMaster(customerMasterDataEntity, false, null);
			customerMasterDataRepository.save(customerMasterDataEntity);
		}
		else
		{
			validateScheduleMaster(customerMasterDataEntity, false, hospitalDTO.getSiteKey());
		}
		return customerMasterDataEntity;
	}

	@Override
	public CustomerMasterDataEntity updateContract(HospitalCancellation hospitalContractDTO)
	{
		return persistHospitalContract(hospitalContractDTO);
	}

	private CustomerMasterDataEntity persistHospitalContract(HospitalCancellation hospitalContractDTO)
	{
		CustomerMasterDataEntity customerMasterDataEntity = customerMasterDataRepository
				.findByHospitalId(hospitalContractDTO.getHospitalId());
		customerMasterDataEntity.setCancelContract(
				populateContractCancellation(customerMasterDataEntity, hospitalContractDTO));
		return customerMasterDataRepository.save(customerMasterDataEntity);
	}

	private ContractCancellation populateContractCancellation(
			CustomerMasterDataEntity customerMasterDataEntity, HospitalCancellation hospitalContractDTO)
	{
		ContractCancellation contractCancellation = Objects.nonNull(
				customerMasterDataEntity.getCancelContract()) ? customerMasterDataEntity.getCancelContract()
						: new ContractCancellation();
		if (!hospitalContractDTO.getIsPermanentCancellation())
		{
			contractCancellation.setCancelFrom(hospitalContractDTO.getCancelFrom());
			contractCancellation.setCancelTo(hospitalContractDTO.getCancelTo());
		}
		else
		{
			contractCancellation.setCancelFrom(null);
			contractCancellation.setCancelTo(null);
		}
		contractCancellation.setIsPermanentCancellation(hospitalContractDTO.getIsPermanentCancellation());
		contractCancellation.setHospitalId(customerMasterDataEntity);

		return contractCancellation;
	}

	private void validateNoData(boolean isFileEmpty) throws ApplBusException
	{
		ApplErrorCollection errorCollection = patchManagementValidator.validateNoData(isFileEmpty);
		throwErrorCollection(errorCollection);
	}

	private void validateDuplcateListItem(List<String> listWithDuplicates) throws ApplBusException
	{
		Set<String> duplicates = PatchManagementUtil.findDuplicates(listWithDuplicates);
		if (Objects.nonNull(duplicates) && !duplicates.isEmpty())
		{
			ApplErrorCollection errorCollection = patchManagementValidator
					.validateValidateDuplcateListItem(duplicates);
			throwErrorCollection(errorCollection);
		}
	}

	private void validateScheduleMaster(CustomerMasterDataEntity customerMasterDataEntity,
			boolean isBatchUpload, String siteKey) throws ApplBusException
	{
		ApplErrorCollection errorCollection = patchManagementValidator
				.validateScheduleMaster(customerMasterDataEntity, isBatchUpload, siteKey);
		throwErrorCollection(errorCollection);
	}

	private void throwErrorCollection(ApplErrorCollection errorCollection) throws ApplBusException
	{
		if (errorCollection.getSize() > 0)
		{
			throw new ApplBusException(IPatchManagementMessageCode.SERVICE_BUS_EXCEPTION,
					MessagesUtil.getString(IPatchManagementMessageCode.SERVICE_BUS_EXCEPTION),
					errorCollection);
		}

	}

	private CustomerMasterDataEntity populateHospitalMasterDetailsEntity(Map<String, String> rowDetails)
	{
		CustomerMasterDataEntity newHospital = new CustomerMasterDataEntity();
		for (Map.Entry<String, String> entry : rowDetails.entrySet())
		{
			switch (entry.getKey())
			{
			case IPatchManagementConstants.SITE_KEY:
				newHospital.setSiteKey(entry.getValue());
				break;
			case IPatchManagementConstants.APPLICATION_VERSION:
				newHospital.setApplicationVersion(entry.getValue());
				break;
			case IPatchManagementConstants.CUSTOMER_NAME:
				newHospital.setCustomerName(entry.getValue());
				break;
			case IPatchManagementConstants.WEEK:
				newHospital.setWeek((Objects.nonNull(entry.getValue()) && !entry.getValue().isEmpty()
						&& isNumber(entry.getValue())) ? new Double(entry.getValue()).intValue() : null);
				break;
			case IPatchManagementConstants.DAY:
				newHospital.setDay(dayRepository.findByName(entry.getValue()));
				break;
			case IPatchManagementConstants.SESSION:
				newHospital.setSession((Objects.nonNull(entry.getValue())
						&& !entry.getValue().isEmpty() && isNumber(entry.getValue()))
								? sessionRepository.findByName(
										String.valueOf(new Double(entry.getValue()).intValue()))
								: null);
				break;
			case IPatchManagementConstants.CONSOLE:
				newHospital.setConsole(consoleRepository.findByName(entry.getValue()));
				break;
			case IPatchManagementConstants.REGION:
				newHospital.setRegion(regionRepository.findByName(entry.getValue()));
				break;
			case IPatchManagementConstants.SCHEDULE_START_TIME:
				newHospital.setScheduledStarttime(entry.getValue());
				break;
			case IPatchManagementConstants.TIME_ZONE:
				newHospital.setTimeZone(timeZoneRepository.findByName(entry.getValue()));
				break;
			case IPatchManagementConstants.DURATION_OF_MAINTENANCE_WINDOW:
				newHospital.setDuration((Objects.nonNull(entry.getValue()) && !entry.getValue().isEmpty()
						&& isNumber(entry.getValue())) ? new Double(entry.getValue()).intValue()
								: IPatchManagementConstants.DEFAULT_DURATION);
				break;
			case IPatchManagementConstants.COMMENTS:
				newHospital.setComment(entry.getValue());
				break;
			case IPatchManagementConstants.ADDED_TO_MASTER_SHEET_ON:
				newHospital.setAddedDate(patchManagementUtil.convertStringToDate(entry.getValue()));
				break;
			case IPatchManagementConstants.CITY:
				newHospital.setTimeZoneDetailsEntity(timeZoneDetailsRepository.findByName(entry.getValue()));
				break;
			default:
				break;
			}
		}
		newHospital.setScheduledEndtime(
				calculateScheduledEndTime(newHospital.getScheduledStarttime(), newHospital.getDuration()));
		return newHospital;
	}

	private boolean isNumber(String value)
	{
		boolean numeric = true;
		try
		{
			Double.parseDouble(value);
		}
		catch (NumberFormatException e)
		{
			numeric = false;
		}
		return numeric;
	}

	private List<CustomerDetailsEntity> fetchFileCustomerRecord(Stream<Row> rows, List<String> headerCells)
			throws ApplBusException
	{
		List<CustomerDetailsEntity> customerDetails = new ArrayList<>();
		boolean isFileEmpty = true;
		for (Row row : rows.collect(Collectors.toList()))
		{
			if (!checkIfRowIsEmpty(row))
			{
				isFileEmpty = false;
				Map<String, String> rowDetails = fetchFileRowDetails(row, headerCells);

				CustomerDetailsEntity customerDetailsEntity = populateCustomerDetailsEntity(rowDetails);
				validateBatchUploadedCustomerRecord(customerDetailsEntity);
				customerDetailsEntity.setHospitalId(
						customerMasterDataRepository.findBySiteKey(customerDetailsEntity.getSiteKey()));
				customerDetails.add(customerDetailsEntity);
			}
		}
		validateNoData(isFileEmpty);
		return customerDetails;
	}

	private void validateBatchUploadedCustomerRecord(CustomerDetailsEntity customerDetailsEntity)
			throws ApplBusException
	{
		ApplErrorCollection errorCollection = patchManagementValidator
				.validateBatchUploadedCustomerRecord(customerDetailsEntity);
		throwErrorCollection(errorCollection);

	}

	private CustomerDetailsEntity populateCustomerDetailsEntity(Map<String, String> rowDetails)
	{
		CustomerDetailsEntity customerDetailsEntity = new CustomerDetailsEntity();
		for (Map.Entry<String, String> entry : rowDetails.entrySet())
		{
			switch (entry.getKey())
			{
			case IPatchManagementConstants.SITE_KEY:
				customerDetailsEntity.setSiteKey(entry.getValue());
				break;
			case IPatchManagementConstants.CUSTOMER_CONTACT_NAME:
				customerDetailsEntity.setCustomerName(entry.getValue());
				break;
			case IPatchManagementConstants.CUSTOMER_EMAIL_ID:
				customerDetailsEntity.setCustomerEmailId(entry.getValue());
				break;
			case IPatchManagementConstants.CONTACT_NUMBER:
				customerDetailsEntity.setContactNumber(entry.getValue());
				break;
			default:
				break;
			}
		}
		return customerDetailsEntity;
	}

	private List<CSMDetailsEntity> fetchFileCsmRecord(Stream<Row> rows, List<String> headerCells)
			throws ApplBusException
	{
		boolean isFileEmpty = true;
		List<CSMDetailsEntity> cSMDetails = new ArrayList<>();
		List<String> siteKeys = new ArrayList<>();
		for (Row row : rows.collect(Collectors.toList()))
		{
			if (!checkIfRowIsEmpty(row))
			{
				isFileEmpty = false;
				Map<String, String> rowDetails = fetchFileRowDetails(row, headerCells);
				CSMDetailsEntity cSMDetailsEntity = populateCSMDetailsEntity(rowDetails);
				validateBatchCsmRecord(cSMDetailsEntity);
				siteKeys.add(cSMDetailsEntity.getSiteKey());
				cSMDetailsEntity.setHospitalId(
						customerMasterDataRepository.findBySiteKey(cSMDetailsEntity.getSiteKey()));
				cSMDetails.add(cSMDetailsEntity);
			}
		}
		validateNoData(isFileEmpty);
		validateDuplcateListItem(siteKeys);
		return cSMDetails;

	}

	private void validateBatchCsmRecord(CSMDetailsEntity cSMDetailsEntity) throws ApplBusException
	{
		ApplErrorCollection errorCollection = patchManagementValidator
				.validateBatchCsmRecord(cSMDetailsEntity);
		throwErrorCollection(errorCollection);
	}

	private Map<String, String> fetchFileRowDetails(Row row, List<String> headerCells)
	{
		int colCount = headerCells.size();
		List<String> cellList = uploadUtil.getStream(row).map(String::valueOf).collect(Collectors.toList());
		return uploadUtil.cellIteratorSupplier(colCount).get()
				.collect(Collectors.toMap(headerCells::get, cellList::get));
	}

	private boolean checkIfRowIsEmpty(Row row)
	{
		boolean isEmptyRow = true;
		for (int cellNum = row.getFirstCellNum(); cellNum < row.getLastCellNum(); cellNum++)
		{
			Cell cell = row.getCell(cellNum);
			if (cell != null && cell.getCellType() != Cell.CELL_TYPE_BLANK
					&& !StringUtils.isEmpty(cell.toString()))
			{
				isEmptyRow = false;
			}
		}
		return isEmptyRow;
	}

	private CSMDetailsEntity populateCSMDetailsEntity(Map<String, String> map)
	{
		CSMDetailsEntity cSMDetailsEntity = new CSMDetailsEntity();
		for (Map.Entry<String, String> entry : map.entrySet())
		{
			switch (entry.getKey())
			{
			case IPatchManagementConstants.SITE_KEY:
				cSMDetailsEntity.setSiteKey(entry.getValue());
				break;
			case IPatchManagementConstants.CSM_NAME:
				cSMDetailsEntity.setCsmName(entry.getValue());
				break;
			case IPatchManagementConstants.CSM_EMAIL_ID:
				cSMDetailsEntity.setCsmEmailId(entry.getValue());
				break;

			case IPatchManagementConstants.CONTACT_NUMBER:
				cSMDetailsEntity.setContactNumber(entry.getValue());
				break;
			case IPatchManagementConstants.ADDITIONAL_CONTACTS:
				cSMDetailsEntity.setAdditionalContacts(entry.getValue());
				break;
			default:
				break;
			}
		}
		return cSMDetailsEntity;

	}

	@Override
	public List<HospitalSearchDTO> getAllCustomerDetails()
	{
		List<CustomerMasterDataEntity> customerMasterDataList = customerMasterDataRepository
				.findAllByOrderByCustomerName();
		return populateHospitalSearchDTO(customerMasterDataList);
	}

	private List<HospitalSearchDTO> populateHospitalSearchDTO(
			List<CustomerMasterDataEntity> customerMasterDataList)
	{
		if (Objects.nonNull(customerMasterDataList) && !customerMasterDataList.isEmpty())
		{
			List<HospitalSearchDTO> hospitalSearchDTOs = new ArrayList<>();
			customerMasterDataList.stream().forEach(hospital -> {
				HospitalSearchDTO hospitalSearchDTO = new HospitalSearchDTO();
				hospitalSearchDTO.setConsole(hospital.getConsole().getName());
				hospitalSearchDTO.setCustomerName(hospital.getCustomerName());
				hospitalSearchDTO.setHospitalId(hospital.getHospitalId());
				hospitalSearchDTO.setRegion(hospital.getRegion().getName());
				hospitalSearchDTO.setSession(hospital.getSession().getName());
				hospitalSearchDTO.setSiteKey(hospital.getSiteKey());

				hospitalSearchDTOs.add(hospitalSearchDTO);

			});
			return hospitalSearchDTOs;
		}
		return null;
	}

	@Override
	public CustomerMasterDataEntity saveNewHospital(HospitalDTO hospitalDTO) throws ApplBusException
	{
		return persisteNewHospital(hospitalDTO);
	}

	private HospitalDTO populateHospitalDTO(CustomerMasterDataEntity customerMasterData)
	{
		HospitalDTO hospitalDTO = new HospitalDTO();
		hospitalDTO.setAddedDate(customerMasterData.getAddedDate());
		hospitalDTO.setApplicationVersion(customerMasterData.getApplicationVersion());
		hospitalDTO.setComment(customerMasterData.getComment());
		hospitalDTO.setConsole(populateDropDown(customerMasterData.getConsole().getId(),
				customerMasterData.getConsole().getName()));
		hospitalDTO.setCreationTime(
				Date.from(customerMasterData.getCreationTime().atZone(ZoneId.systemDefault()).toInstant()));
		hospitalDTO.setCsmDetails(populateCSMDetailsDTO(customerMasterData.getCSMDetails()));
		hospitalDTO
				.setCustomerDetailsDTO(populateCustomerDetailsDTO(customerMasterData.getCustomerDetails()));
		hospitalDTO.setCustomerName(customerMasterData.getCustomerName());
		hospitalDTO.setDay(
				populateDropDown(customerMasterData.getDay().getId(), customerMasterData.getDay().getName()));
		hospitalDTO.setDuration(customerMasterData.getDuration());
		hospitalDTO.setHospitalId(customerMasterData.getHospitalId());
		String date = TimeZoneConversionUtil.calculateDate(customerMasterData.getWeek(),
				customerMasterData.getDay().getName());
		hospitalDTO.setPatchTime(TimeZoneConversionUtil.timeZoneConversion(
				customerMasterData.getTimeZoneDetailsEntity().getName(), date,
				customerMasterData.getScheduledStarttime()));
		hospitalDTO.setRegion(populateDropDown(customerMasterData.getRegion().getId(),
				customerMasterData.getRegion().getName()));
		hospitalDTO.setScheduledEndTime(customerMasterData.getScheduledEndtime());
		hospitalDTO.setScheduledStartTime(customerMasterData.getScheduledStarttime());
		hospitalDTO.setSession(populateDropDown(customerMasterData.getSession().getId(),
				customerMasterData.getSession().getName()));
		hospitalDTO.setSiteKey(customerMasterData.getSiteKey());
		hospitalDTO.setTimeZone(populateDropDown(customerMasterData.getTimeZone().getId(),
				customerMasterData.getTimeZone().getName()));
		hospitalDTO.setTimeZoneDetails(populateDropDown(customerMasterData.getTimeZoneDetailsEntity().getId(),
				customerMasterData.getTimeZoneDetailsEntity().getName()));
		hospitalDTO.setWeek(customerMasterData.getWeek());
		hospitalDTO.setContractCancelled(checkIsContractCancelled(customerMasterData));
		hospitalDTO.setHospitalCancellation(populateHospitalCancellationDTO(customerMasterData));
		return hospitalDTO;
	}

	private boolean checkIsContractCancelled(CustomerMasterDataEntity customerMasterData)
	{
		boolean isContractCancelled = false;
		if (Objects.nonNull(customerMasterData.getCancelContract()))
		{
			if (customerMasterData.getCancelContract().getIsPermanentCancellation())
				return true;
			else
			{
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("M-d-yyyy");
				LocalDate localDate = LocalDate.now();
				LocalDate currentDate = patchManagementUtil.convertStringToDate(dtf.format(localDate));
				if (currentDate.compareTo(customerMasterData.getCancelContract().getCancelFrom()) >= 0
						&& currentDate.compareTo(customerMasterData.getCancelContract().getCancelTo()) <= 0)
				{
					return true;
				}
			}
		}
		return isContractCancelled;
	}

	private HospitalCancellation populateHospitalCancellationDTO(CustomerMasterDataEntity customerMasterData)
	{
		HospitalCancellation hospitalConcellationDTO = new HospitalCancellation();
		ContractCancellation contractCancellation = customerMasterData.getCancelContract();
		if (Objects.nonNull(contractCancellation))
		{
			hospitalConcellationDTO.setCancelFrom(contractCancellation.getCancelFrom());
			hospitalConcellationDTO.setCancelTo(contractCancellation.getCancelTo());
			hospitalConcellationDTO
					.setIsPermanentCancellation(contractCancellation.getIsPermanentCancellation());
		}
		hospitalConcellationDTO.setHospitalId(customerMasterData.getHospitalId());
		hospitalConcellationDTO.setSiteKey(customerMasterData.getSiteKey());
		return hospitalConcellationDTO;
	}

	private List<CustomerDetailsDTO> populateCustomerDetailsDTO(List<CustomerDetailsEntity> customerDetails)
	{
		List<CustomerDetailsDTO> customerDetailsDTOs = new ArrayList<>();
		if (Objects.nonNull(customerDetails) && !customerDetails.isEmpty())
		{
			customerDetails.parallelStream()
					.forEach(cd -> customerDetailsDTOs.add(modelMapper.map(cd, CustomerDetailsDTO.class)));
		}
		return customerDetailsDTOs.isEmpty() ? null : customerDetailsDTOs;
	}

	private CSMDetailsDTO populateCSMDetailsDTO(CSMDetailsEntity getcSMDetails)
	{
		return Objects.nonNull(getcSMDetails) ? modelMapper.map(getcSMDetails, CSMDetailsDTO.class)
				: new CSMDetailsDTO();
	}

	private DropDownDTO populateDropDown(Integer id, String value)
	{
		DropDownDTO dropDownDTO = new DropDownDTO();
		dropDownDTO.setId(id);
		dropDownDTO.setName(value);
		return dropDownDTO;
	}

	private CustomerMasterDataEntity persisteNewHospital(HospitalDTO hospitalDTO) throws ApplBusException
	{
		CustomerMasterDataEntity customerMasterDataEntity = new CustomerMasterDataEntity();
		customerMasterDataEntity.setAddedDate(hospitalDTO.getAddedDate());
		customerMasterDataEntity.setApplicationVersion(hospitalDTO.getApplicationVersion());
		customerMasterDataEntity.setComment(hospitalDTO.getComment());
		Optional<ConsoleEntity> console = consoleRepository.findById(hospitalDTO.getConsole().getId());
		customerMasterDataEntity.setConsole(console.isPresent() ? console.get() : null);
		customerMasterDataEntity
				.setCSMDetails(populateCSMDetails(hospitalDTO.getCsmDetails(), customerMasterDataEntity));

		customerMasterDataEntity.setCustomerDetails(
				populateCustomerDetails(hospitalDTO.getCustomerDetailsDTO(), customerMasterDataEntity));

		customerMasterDataEntity.setCustomerName(hospitalDTO.getCustomerName());
		Optional<DayEntity> day = dayRepository.findById(hospitalDTO.getDay().getId());
		customerMasterDataEntity.setDay(day.isPresent() ? day.get() : null);
		Integer duration = Objects.nonNull(hospitalDTO.getDuration()) ? hospitalDTO.getDuration()
				: IPatchManagementConstants.DEFAULT_DURATION;
		customerMasterDataEntity.setDuration(duration);
		Optional<RegionEntity> region = regionRepository.findById(hospitalDTO.getRegion().getId());
		customerMasterDataEntity.setRegion(region.isPresent() ? region.get() : null);
		String endTime = calculateScheduledEndTime(hospitalDTO.getScheduledStartTime(), duration);
		customerMasterDataEntity.setScheduledEndtime(endTime);
		customerMasterDataEntity.setScheduledStarttime(hospitalDTO.getScheduledStartTime());
		Optional<SessionEntity> session = sessionRepository.findById(hospitalDTO.getSession().getId());
		customerMasterDataEntity.setSession(session.isPresent() ? session.get() : null);
		customerMasterDataEntity.setSiteKey(hospitalDTO.getSiteKey());
		Optional<TimeZoneEntity> timeZone = timeZoneRepository.findById(hospitalDTO.getTimeZone().getId());
		customerMasterDataEntity.setTimeZone(timeZone.isPresent() ? timeZone.get() : null);
		customerMasterDataEntity.setWeek(hospitalDTO.getWeek());
		Optional<TimeZoneDetailsEntity> city = timeZoneDetailsRepository
				.findById(hospitalDTO.getTimeZoneDetails().getId());
		customerMasterDataEntity.setTimeZoneDetailsEntity(city.isPresent() ? city.get() : null);
		validateScheduleMaster(customerMasterDataEntity, false, null);
		return customerMasterDataRepository.save(customerMasterDataEntity);
	}

	private String calculateScheduledEndTime(String scheduledStartTime, Integer duration)
	{
		Double endTime = null;
		DecimalFormat df = new DecimalFormat("##0.00");
		try
		{
			if (Objects.nonNull(scheduledStartTime) && !scheduledStartTime.isEmpty())
			{
				String[] split = null;
				if (scheduledStartTime.contains(":"))
				{
					split = scheduledStartTime.split(":", 3);
					endTime = (Double.valueOf(split[0]) + Double.valueOf("." + split[1]))
							+ Double.valueOf(duration);
				}
				else
				{
					endTime = Double.valueOf(scheduledStartTime) + Double.valueOf(duration);
				}
			}
		}
		catch (Exception e)
		{
			logger.error("Exception while calculating the Scheduled End Time and the scheduledStartTime is"
					+ scheduledStartTime);
			return null;
		}
		return Objects.nonNull(endTime)
				? ((endTime >= 24) ? df.format(endTime - 24.0).replace(".", ":")
						: df.format(endTime).replace(".", ":"))
				: null;
	}

	private List<CustomerDetailsEntity> populateCustomerDetails(List<CustomerDetailsDTO> customerDetails,
			CustomerMasterDataEntity customerMasterDataEntity)
	{
		if (Objects.nonNull(customerDetails) && !customerDetails.isEmpty())
		{
			List<CustomerDetailsEntity> customerDetailsEntities = new ArrayList<>();
			customerDetails.parallelStream().forEach(cd -> {
				CustomerDetailsEntity customerDetailsEntity = modelMapper.map(cd,
						CustomerDetailsEntity.class);
				customerDetailsEntity.setHospitalId(customerMasterDataEntity);
				customerDetailsEntities.add(customerDetailsEntity);
			});
			return customerDetailsEntities;
		}
		return null;

	}

	private CSMDetailsEntity populateCSMDetails(CSMDetailsDTO csmDetailsDTO,
			CustomerMasterDataEntity customerMasterDataEntity)
	{
		CSMDetailsEntity cSMDetailsEntity = null;
		if (Objects.nonNull(csmDetailsDTO))
		{
			cSMDetailsEntity = modelMapper.map(csmDetailsDTO, CSMDetailsEntity.class);
			cSMDetailsEntity.setHospitalId(customerMasterDataEntity);
		}
		return cSMDetailsEntity;
	}

	@Override
	public HospitalDTO getHospitalDetailsBySiteKey(String siteKey) throws ApplBusException
	{
		CustomerMasterDataEntity customerMasterDataEntity = customerMasterDataRepository
				.findBySiteKey(siteKey);
		HospitalDTO hospitalDTO = null;
		if (Objects.isNull(customerMasterDataEntity))
		{
			validateScheduleMaster(customerMasterDataEntity, false, siteKey);
		}
		hospitalDTO = populateHospitalDTO(customerMasterDataEntity);
		return hospitalDTO;
	}

	@Override
	public Map<String, Object> getDropDownValues(String label, Integer value)
	{

		Map<String, Object> dropDown = new HashMap<>();
		List<DropDownDTO> dropDownList = null;
		switch (label)
		{
		case "Console":
			dropDown.put("consoles", consoleRepository.findAll());
			break;
		case "TimeZone":
			dropDownList = new ArrayList<>();
			for (TimeZoneEntity timeZoneEntity : timeZoneRepository.findByRegionEntityId_Id(value))
			{
				dropDownList.add(populateDropDown(timeZoneEntity.getId(), timeZoneEntity.getName()));
			}
			dropDown.put("timezones", dropDownList);
			break;
		case "City":
			dropDownList = new ArrayList<>();
			for (TimeZoneDetailsEntity timeZoneDetailsEntity : timeZoneDetailsRepository
					.findByTimeZoneEntityId_Id(value))
			{
				dropDownList.add(
						populateDropDown(timeZoneDetailsEntity.getId(), timeZoneDetailsEntity.getName()));
			}
			dropDown.put("timeZoneDetails", dropDownList);
			break;
		case "Region":
			dropDown.put("regions", regionRepository.findAll());
			break;
		case "Day":
			dropDown.put("days", dayRepository.findAll());
			break;
		case "Session":
			dropDownList = new ArrayList<>();
			for (SessionEntity session : sessionRepository.findAll())
			{
				dropDownList.add(populateDropDown(session.getId(), session.getName()));
			}
			dropDown.put("sessions", dropDownList);
			break;
		case "All":
			dropDown.put("consoles", consoleRepository.findAll());
			dropDown.put("regions", regionRepository.findAll());
			dropDown.put("days", dayRepository.findAll());
			dropDownList = new ArrayList<>();
			for (SessionEntity session : sessionRepository.findAll())
			{
				dropDownList.add(populateDropDown(session.getId(), session.getName()));
			}
			dropDown.put("sessions", dropDownList);
			break;
		default:
			break;
		}
		return dropDown;
	}

}
