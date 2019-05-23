package com.patchManagement.patch_management;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.FileInputStream;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Optional;

import org.json.JSONObject;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.gson.Gson;
import com.patch.patch_management.PatchManagementApplication;
import com.patch.patch_management.br.ValidateBatchCsmRecordBR;
import com.patch.patch_management.br.ValidateBatchDuplicateFileRecordBR;
import com.patch.patch_management.br.ValidateFileBR;
import com.patch.patch_management.br.ValidateHospitalCustomerBR;
import com.patch.patch_management.br.ValidateScheduleMasterBR;
import com.patch.patch_management.controller.PatchManagementController;
import com.patch.patch_management.controller.PatchManagementExceptionHandler;
import com.patch.patch_management.dto.HospitalDTO;
import com.patch.patch_management.entity.CSMDetailsEntity;
import com.patch.patch_management.entity.ConsoleEntity;
import com.patch.patch_management.entity.CustomerMasterDataEntity;
import com.patch.patch_management.entity.DayEntity;
import com.patch.patch_management.entity.RegionEntity;
import com.patch.patch_management.entity.SessionEntity;
import com.patch.patch_management.entity.TimeZoneDetailsEntity;
import com.patch.patch_management.entity.TimeZoneEntity;
import com.patch.patch_management.repository.CSMRepository;
import com.patch.patch_management.repository.ConsoleRepository;
import com.patch.patch_management.repository.CustomerDetailsRepository;
import com.patch.patch_management.repository.CustomerMasterDataRepository;
import com.patch.patch_management.repository.DayRepository;
import com.patch.patch_management.repository.RegionRepository;
import com.patch.patch_management.repository.SessionRepository;
import com.patch.patch_management.repository.TimeZoneDetailsRepository;
import com.patch.patch_management.repository.TimeZoneRepository;
import com.patch.patch_management.service.PatchManagementService;
import com.patch.patch_management.service.PatchManagementServiceImpl;
import com.patch.patch_management.util.PatchManagementUtil;
import com.patch.patch_management.util.PatchManagementValidator;
import com.patch.patch_management.util.UploadUtil;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = PatchManagementApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PatchManagementApplicationTest
{
	protected transient MockMvc	mvc;

	@LocalServerPort
	private static int			port;

	@BeforeMethod
	@BeforeClass
	public void setUp()
	{
		MockitoAnnotations.initMocks(this);
		ReflectionTestUtils.setField(patchManagementService, "timeZoneRepository", timeZoneRepository);
		ReflectionTestUtils.setField(patchManagementService, "regionRepository", regionRepository);
		ReflectionTestUtils.setField(patchManagementService, "sessionRepository", sessionRepository);
		ReflectionTestUtils.setField(patchManagementService, "dayRepository", dayRepository);
		ReflectionTestUtils.setField(patchManagementService, "consoleRepository", consoleRepository);
		ReflectionTestUtils.setField(patchManagementService, "patchManagementValidator",
				patchManagementValidator);
		ReflectionTestUtils.setField(patchManagementService, "patchManagementValidator",
				patchManagementValidator);
		ReflectionTestUtils.setField(patchManagementService, "customerMasterDataRepository",
				customerMasterDataRepository);
		ReflectionTestUtils.setField(patchManagementService, "uploadUtil", uploadUtil);

		ReflectionTestUtils.setField(patchManagementValidator, "validateHospitalCustomerBR",
				validateHospitalCustomerBR);
		ReflectionTestUtils.setField(patchManagementValidator, "validateBatchCsmRecordBR",
				validateBatchCsmRecordBR);
		ReflectionTestUtils.setField(patchManagementValidator, "validateScheduleMasterBR",
				validateScheduleMasterBR);
		ReflectionTestUtils.setField(patchManagementValidator, "validateFileBR", validateFileBR);
		ReflectionTestUtils.setField(patchManagementValidator, "validateBatchDuplicateFileRecordBR",
				validateBatchDuplicateFileRecordBR);
		ReflectionTestUtils.setField(validateScheduleMasterBR, "customerMasterDataRepository",
				customerMasterDataRepository);
		ReflectionTestUtils.setField(validateScheduleMasterBR, "timeZoneRepository", timeZoneRepository);
		ReflectionTestUtils.setField(validateScheduleMasterBR, "timeZoneDetailsRepository",
				timeZoneDetailsRepository);
		ReflectionTestUtils.setField(validateHospitalCustomerBR, "customerMasterDataRepository",
				customerMasterDataRepository);
		ReflectionTestUtils.setField(validateBatchCsmRecordBR, "customerMasterDataRepository",
				customerMasterDataRepository);
		ReflectionTestUtils.setField(validateScheduleMasterBR, "validateBatchCsmRecordBR",
				validateBatchCsmRecordBR);
		ReflectionTestUtils.setField(validateBatchCsmRecordBR, "cSMRepository", cSMRepository);
		ReflectionTestUtils.setField(patchManagementService, "cSMRepository", cSMRepository);
		ReflectionTestUtils.setField(patchManagementService, "customerDetailsRepository",
				customerDetailsRepository);
		ReflectionTestUtils.setField(patchManagementService, "timeZoneDetailsRepository",
				timeZoneDetailsRepository);

		ReflectionTestUtils.setField(patchManagementService, "patchManagementUtil", patchManagementUtil);

		mvc = MockMvcBuilders.standaloneSetup(patchManagementController)
				.setControllerAdvice(PatchManagementExceptionHandler).build();
	}

	@Mock
	TimeZoneRepository						timeZoneRepository;

	@Mock
	RegionRepository						regionRepository;

	@Mock
	SessionRepository						sessionRepository;

	@Mock
	DayRepository							dayRepository;

	@Mock
	ConsoleRepository						consoleRepository;

	@Spy
	PatchManagementService					patchManagementService	= new PatchManagementServiceImpl();

	@InjectMocks
	private PatchManagementController		patchManagementController;

	@Spy
	PatchManagementValidator				patchManagementValidator;

	@Spy
	ValidateHospitalCustomerBR				validateHospitalCustomerBR;

	@Spy
	ValidateBatchCsmRecordBR				validateBatchCsmRecordBR;

	@Spy
	ValidateScheduleMasterBR				validateScheduleMasterBR;

	@Mock
	CustomerMasterDataRepository			customerMasterDataRepository;

	@Spy
	ValidateFileBR							validateFileBR;

	@Mock
	CSMRepository							cSMRepository;

	@Mock
	CustomerDetailsRepository				customerDetailsRepository;

	@Mock
	TimeZoneDetailsRepository				timeZoneDetailsRepository;

	@Spy
	UploadUtil								uploadUtil;

	@Spy
	PatchManagementUtil						patchManagementUtil;

	@Spy
	ValidateBatchDuplicateFileRecordBR		validateBatchDuplicateFileRecordBR;

	@Spy
	private PatchManagementExceptionHandler	PatchManagementExceptionHandler;

	///////////////////////////////////////////////////////////////////////////////////////////////////////////

	private static String getRootUrl()
	{
		return "http://localhost:" + port + "/api/patchmanagement/";
	}

	private static String getFileLocation()
	{
		return ".\\src\\test\\java\\com\\patchManagement\\patch_management\\testFiles\\";
	}

	private String jsonConverter(Object o)
	{
		String json = null;
		Gson gson = new Gson();
		json = gson.toJson(o);
		return json;
	}

	@Test
	private void testValidGetAllDropDown() throws Exception
	{
		when(timeZoneRepository.findAll())
				.thenReturn(Arrays.asList((TimeZoneEntity) TestDataProvider.timeZoneEntity()[0][0]));
		when(regionRepository.findAll())
				.thenReturn(Arrays.asList((RegionEntity) TestDataProvider.regionEntity()[0][0]));
		when(sessionRepository.findAll())
				.thenReturn(Arrays.asList((SessionEntity) TestDataProvider.sessionEntity()[0][0]));
		when(dayRepository.findAll())
				.thenReturn(Arrays.asList((DayEntity) TestDataProvider.dayEntity()[0][0]));
		when(consoleRepository.findAll())
				.thenReturn(Arrays.asList((ConsoleEntity) TestDataProvider.consoleEntity()[0][0]));
		this.mvc.perform(get(getRootUrl() + "dropdown").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(status().isOk());
	}

	@Test(priority = 0)
	private void testValidCreatePatient() throws ParseException, Exception
	{
		String inputJson = jsonConverter((HospitalDTO) TestDataProvider.hospitalDTO()[0][0]);
		JSONObject jObject = new JSONObject(inputJson);
		jObject.put("addedDate", "09-09-2019");
		inputJson = jObject.toString();
		getDropDownById();
		when(customerMasterDataRepository.findBySiteKey(any(String.class))).thenReturn(null);

		when(customerMasterDataRepository.save(any(CustomerMasterDataEntity.class)))
				.thenReturn((CustomerMasterDataEntity) TestDataProvider.customerMasterDataEntity()[0][0]);
		when(timeZoneRepository.findByRegionEntityId_Id(any(Integer.class)))
				.thenReturn(Arrays.asList((TimeZoneEntity) TestDataProvider.timeZoneEntity()[0][0]));

		when(timeZoneDetailsRepository.findByTimeZoneEntityId_Id(any(Integer.class))).thenReturn(
				Arrays.asList((TimeZoneDetailsEntity) TestDataProvider.timeZoneDetailsEntity()[0][0]));

		this.mvc.perform(post(getRootUrl() + "onboard/hospital")
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(inputJson))
				.andExpect(status().isOk());
	}

	@Test(dataProviderClass = TestDataProvider.class, dataProvider = "HospitalDTO", priority = 0)
	private void testInValidCreatePatient(HospitalDTO hospitalDTO) throws Exception
	{
		hospitalDTO.setCsmDetails(null);
		hospitalDTO.setCustomerDetailsDTO(null);
		String inputJson = jsonConverter(hospitalDTO);
		JSONObject jObject = new JSONObject(inputJson);
		jObject.put("addedDate", "09-09-2019");
		inputJson = jObject.toString();
		getDropDownById();
		when(customerMasterDataRepository.findBySiteKey(any(String.class)))
				.thenReturn((CustomerMasterDataEntity) TestDataProvider.customerMasterDataEntity()[0][0]);
		when(timeZoneRepository.findByRegionEntityId_Id(any(Integer.class)))
				.thenReturn(Arrays.asList((TimeZoneEntity) TestDataProvider.timeZoneEntity()[0][0]));

		when(timeZoneDetailsRepository.findByTimeZoneEntityId_Id(any(Integer.class))).thenReturn(
				Arrays.asList((TimeZoneDetailsEntity) TestDataProvider.timeZoneDetailsEntity()[0][0]));
		this.mvc.perform(post(getRootUrl() + "onboard/hospital")
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(inputJson))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0].userMessage",
						equalTo("Site Key-KUL00 is already present in the system.")))
				.andExpect(jsonPath("$.errors[1].userMessage", equalTo("CSM Contact Detail is required.")))
				.andExpect(
						jsonPath("$.errors[2].userMessage", equalTo("Customer Contact Detail is required.")));

	}

	@Test(dataProviderClass = TestDataProvider.class, dataProvider = "HospitalDTO", priority = 0)
	private void testInValidCreatePatientNoSiteKey(HospitalDTO hospitalDTO) throws Exception
	{
		hospitalDTO.setSiteKey(null);
		String inputJson = jsonConverter(hospitalDTO);
		getDropDownById();
		this.mvc.perform(post(getRootUrl() + "onboard/hospital")
				.contentType(MediaType.APPLICATION_JSON_UTF8_VALUE).content(inputJson))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0].userMessage", equalTo("Site Key is required.")));

	}

	@Test
	private void testValidGetAllHospital() throws Exception
	{

		when(customerMasterDataRepository.findAllByOrderByCustomerName()).thenReturn(
				Arrays.asList((CustomerMasterDataEntity) TestDataProvider.customerMasterDataEntity()[0][0]));

		this.mvc.perform(
				get(getRootUrl() + "hospital/search").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(status().isOk());
	}

	@Test
	private void testValidGetHospitalBySiteKey() throws Exception
	{

		when(customerMasterDataRepository.findBySiteKey(any(String.class)))
				.thenReturn((CustomerMasterDataEntity) TestDataProvider.customerMasterDataEntity()[0][0]);

		this.mvc.perform(
				get(getRootUrl() + "hospital/pqr").contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
				.andExpect(status().isOk());
	}

	@Test
	private void testInValidUploadFileName() throws Exception
	{

		FileInputStream inputFile = new FileInputStream(getFileLocation() + "InValidFileName.xlsx");
		MockMultipartFile file = new MockMultipartFile("uploadedFile", "InValidFileName.xlsx",
				"multipart/form-data", inputFile);

		this.mvc.perform(multipart(getRootUrl() + "fileupload").file(file)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0].userMessage", equalTo("Invalid file name.")));
	}

	@Test
	private void testInValidUploadFileExt() throws Exception
	{
		FileInputStream inputFile = new FileInputStream(
				getFileLocation() + "SCHEDULE_MASTER-invalidFileExt.ext");
		MockMultipartFile file = new MockMultipartFile("uploadedFile", "SCHEDULE_MASTER-invalidFileExt.ext",
				"multipart/form-data", inputFile);

		this.mvc.perform(multipart(getRootUrl() + "fileupload").file(file)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0].userMessage", equalTo("Invalid file type.")));
	}

	@Test
	private void testInValidScheduleMasterUploadFileHeader() throws Exception
	{
		FileInputStream inputFile = new FileInputStream(
				getFileLocation() + "SCHEDULE_MASTER-invalidHeader.xlsx");
		MockMultipartFile file = new MockMultipartFile("uploadedFile", "SCHEDULE_MASTER-invalidHeader.xlsx",
				"multipart/form-data", inputFile);

		this.mvc.perform(multipart(getRootUrl() + "fileupload").file(file)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0].userMessage",
						equalTo("Missing header [APPLICATION VERSION, CUSTOMER NAME, DAY, REGION, CITY].")));
	}

	@Test
	private void testInValidScheduleMasterUploadFileRecord() throws Exception
	{
		FileInputStream inputFile = new FileInputStream(
				getFileLocation() + "SCHEDULE_MASTER-invalidRecord1.xlsx");
		MockMultipartFile file = new MockMultipartFile("uploadedFile", "SCHEDULE_MASTER-invalidRecord1.xlsx",
				"multipart/form-data", inputFile);

		this.mvc.perform(multipart(getRootUrl() + "fileupload").file(file)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors.[1].userMessage", equalTo("Invalid Day is provided for Site Key-ABC")))
				.andExpect(jsonPath("$.errors.[2].userMessage", equalTo("Invalid Region is provided for Site Key-ABC")))
				.andExpect(jsonPath("$.errors.[3].userMessage", equalTo("Invalid Scheduled start time is provided for Site Key-ABC")))
				.andExpect(jsonPath("$.errors.[4].userMessage", equalTo("Invalid Session is provided for Site Key-ABC")))
				.andExpect(jsonPath("$.errors.[5].userMessage", equalTo("Invalid TimeZone is provided for Site Key-ABC")))
				.andExpect(jsonPath("$.errors.[6].userMessage", equalTo("Invalid Week is provided for Site Key-ABC")))
				.andExpect(jsonPath("$.errors.[7].userMessage", equalTo("Invalid City is provided for Site Key-ABC")));
	}

	@Test
	private void testInValidScheduleMasterUploadFileRecordNoSiteKey() throws Exception
	{
		FileInputStream inputFile = new FileInputStream(
				getFileLocation() + "SCHEDULE_MASTER-invalidRecord2.xlsx");
		MockMultipartFile file = new MockMultipartFile("uploadedFile", "SCHEDULE_MASTER-invalidRecord2.xlsx",
				"multipart/form-data", inputFile);

		this.mvc.perform(multipart(getRootUrl() + "fileupload").file(file)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors.[0].userMessage", equalTo("Site Key is required.")));
	}

	@Test
	private void testValidScheduleMasterUploadFileRecord() throws Exception
	{
		FileInputStream inputFile = new FileInputStream(getFileLocation() + "SCHEDULE_MASTER-validFile.xlsx");
		MockMultipartFile file = new MockMultipartFile("uploadedFile", "SCHEDULE_MASTER-validFile.xlsx",
				"multipart/form-data", inputFile);
		when(timeZoneRepository.findByRegionEntityId_Id(any(Integer.class)))
				.thenReturn(Arrays.asList((TimeZoneEntity) TestDataProvider.timeZoneEntity()[0][0]));

		when(timeZoneDetailsRepository.findByTimeZoneEntityId_Id(any(Integer.class))).thenReturn(
				Arrays.asList((TimeZoneDetailsEntity) TestDataProvider.timeZoneDetailsEntity()[0][0]));
		getDropDownByName();

		this.mvc.perform(multipart(getRootUrl() + "fileupload").file(file)).andExpect(status().isOk());
	}

	private void getDropDownByName()
	{
		when(regionRepository.findByName(any(String.class)))
				.thenReturn((RegionEntity) TestDataProvider.regionEntity()[0][0]);
		when(dayRepository.findByName(any(String.class)))
				.thenReturn((DayEntity) TestDataProvider.dayEntity()[0][0]);
		when(timeZoneRepository.findByName(any(String.class)))
				.thenReturn((TimeZoneEntity) TestDataProvider.timeZoneEntity()[0][0]);
		when(consoleRepository.findByName(any(String.class)))
				.thenReturn((ConsoleEntity) TestDataProvider.consoleEntity()[0][0]);
		when(sessionRepository.findByName(any(String.class)))
				.thenReturn((SessionEntity) TestDataProvider.sessionEntity()[0][0]);
		when(timeZoneDetailsRepository.findByName(any(String.class)))
				.thenReturn((TimeZoneDetailsEntity) TestDataProvider.timeZoneDetailsEntity()[0][0]);
	}

	private void getDropDownById()
	{
		when(timeZoneRepository.findById(any(Integer.class)))
				.thenReturn(Optional.of((TimeZoneEntity) TestDataProvider.timeZoneEntity()[0][0]));
		when(regionRepository.findById(any(Integer.class)))
				.thenReturn(Optional.of((RegionEntity) TestDataProvider.regionEntity()[0][0]));
		when(sessionRepository.findById(any(Integer.class)))
				.thenReturn(Optional.of((SessionEntity) TestDataProvider.sessionEntity()[0][0]));
		when(dayRepository.findById(any(Integer.class)))
				.thenReturn(Optional.of((DayEntity) TestDataProvider.dayEntity()[0][0]));
		when(consoleRepository.findById(any(Integer.class)))
				.thenReturn(Optional.of((ConsoleEntity) TestDataProvider.consoleEntity()[0][0]));
		when(timeZoneDetailsRepository.findById(any(Integer.class))).thenReturn(
				Optional.of((TimeZoneDetailsEntity) TestDataProvider.timeZoneDetailsEntity()[0][0]));

	}

	@Test
	private void testInValidUploadCSMFileHeader() throws Exception
	{
		FileInputStream inputFile = new FileInputStream(
				getFileLocation() + "CSM_CONTACT_MASTER-invalidHeader.xlsx");
		MockMultipartFile file = new MockMultipartFile("uploadedFile",
				"CSM_CONTACT_MASTER-invalidHeader.xlsx", "multipart/form-data", inputFile);

		this.mvc.perform(multipart(getRootUrl() + "fileupload").file(file)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0].userMessage",
						equalTo("Missing header [SITE KEY, CONTACT NUMBER, ADDITIONAL CONTACTS].")));
	}

	@Test
	private void testInValidUploadCSMFileRecord() throws Exception
	{
		FileInputStream inputFile = new FileInputStream(
				getFileLocation() + "CSM_CONTACT_MASTER-invalidRecord.xlsx");
		MockMultipartFile file = new MockMultipartFile("uploadedFile",
				"CSM_CONTACT_MASTER-invalidRecord.xlsx", "multipart/form-data", inputFile);

		this.mvc.perform(multipart(getRootUrl() + "fileupload").file(file)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0].userMessage", equalTo("Missing header [SITE KEY].")));
	}

	@Test
	private void testValidUploadCSMFileRecord() throws Exception
	{
		FileInputStream inputFile = new FileInputStream(getFileLocation() + "CSM_CONTACT_MASTER.xlsx");
		MockMultipartFile file = new MockMultipartFile("uploadedFile", "CSM_CONTACT_MASTER.xlsx",
				"multipart/form-data", inputFile);

		when(customerMasterDataRepository.findBySiteKey(any(String.class)))
				.thenReturn((CustomerMasterDataEntity) TestDataProvider.customerMasterDataEntity()[0][0]);

		this.mvc.perform(multipart(getRootUrl() + "fileupload").file(file)).andExpect(status().isOk());
	}

	@Test
	private void testInvalidDuplicateSiteInUploadedCSMFileRecord() throws Exception
	{
		FileInputStream inputFile = new FileInputStream(getFileLocation() + "CSM_CONTACT_MASTER.xlsx");
		MockMultipartFile file = new MockMultipartFile("uploadedFile", "CSM_CONTACT_MASTER.xlsx",
				"multipart/form-data", inputFile);

		when(customerMasterDataRepository.findBySiteKey(any(String.class)))
				.thenReturn((CustomerMasterDataEntity) TestDataProvider.customerMasterDataEntity()[0][0]);

		when(cSMRepository.findBySiteKey(any(String.class)))
				.thenReturn((CSMDetailsEntity) TestDataProvider.cSMDataEntity()[0][0]);

		this.mvc.perform(multipart(getRootUrl() + "fileupload").file(file)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0].userMessage",
						equalTo("Site Key-CRMC1 is already present in the system.")));
	}

	@Test
	private void testInvalidUploadedCSMFileRecordNoHospital() throws Exception
	{
		FileInputStream inputFile = new FileInputStream(getFileLocation() + "CSM_CONTACT_MASTER.xlsx");
		MockMultipartFile file = new MockMultipartFile("uploadedFile", "CSM_CONTACT_MASTER.xlsx",
				"multipart/form-data", inputFile);

		this.mvc.perform(multipart(getRootUrl() + "fileupload").file(file)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0].userMessage",
						equalTo("No Hospital is available for Site Key-CRMC1.")));
	}

	@Test
	private void testInValidUploadCustomerFileHeader() throws Exception
	{
		FileInputStream inputFile = new FileInputStream(
				getFileLocation() + "CUSTOMER_CONTACT_MASTER-invalidHeader.xlsx");
		MockMultipartFile file = new MockMultipartFile("uploadedFile",
				"CUSTOMER_CONTACT_MASTER-invalidHeader.xlsx", "multipart/form-data", inputFile);

		this.mvc.perform(multipart(getRootUrl() + "fileupload").file(file)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0].userMessage",
						equalTo("Missing header [CUSTOMER CONTACT NAME, CUSTOMER EMAIL ID].")));
	}

	@Test
	private void testInValidUploadCustomerFileRecord() throws Exception
	{
		FileInputStream inputFile = new FileInputStream(
				getFileLocation() + "Customer_Contact_Master-invalidRecord.xlsx");
		MockMultipartFile file = new MockMultipartFile("uploadedFile",
				"Customer_Contact_Master-invalidRecord.xlsx", "multipart/form-data", inputFile);

		this.mvc.perform(multipart(getRootUrl() + "fileupload").file(file)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0].userMessage", equalTo("Missing header [SITE KEY].")));
	}

	@Test
	private void testInValidUploadCustomerFileNoHospital() throws Exception
	{
		FileInputStream inputFile = new FileInputStream(getFileLocation() + "Customer_Contact_Master.xlsx");
		MockMultipartFile file = new MockMultipartFile("uploadedFile", "Customer_Contact_Master.xlsx",
				"multipart/form-data", inputFile);

		this.mvc.perform(multipart(getRootUrl() + "fileupload").file(file)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0].userMessage",
						equalTo("No Hospital is available for Site Key-CRMC1.")));
	}

	@Test
	private void testValidUploadCustomerFileRecord() throws Exception
	{
		FileInputStream inputFile = new FileInputStream(getFileLocation() + "Customer_Contact_Master.xlsx");
		MockMultipartFile file = new MockMultipartFile("uploadedFile", "Customer_Contact_Master.xlsx",
				"multipart/form-data", inputFile);
		when(customerMasterDataRepository.findBySiteKey(any(String.class)))
				.thenReturn((CustomerMasterDataEntity) TestDataProvider.customerMasterDataEntity()[0][0]);

		this.mvc.perform(multipart(getRootUrl() + "fileupload").file(file)).andExpect(status().isOk());
	}

	@Test
	private void testEmptyFile() throws Exception
	{
		FileInputStream inputFile = new FileInputStream(
				getFileLocation() + "CSM_CONTACT_MASTER-emptyFile.xlsx");
		MockMultipartFile file = new MockMultipartFile("uploadedFile", "CSM_CONTACT_MASTER-emptyFile.xlsx",
				"multipart/form-data", inputFile);

		this.mvc.perform(multipart(getRootUrl() + "fileupload").file(file)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0].userMessage", equalTo(
						"Missing header [SITE KEY, CSM NAME, CSM EMAIL ID, CONTACT NUMBER, ADDITIONAL CONTACTS].")));
	}

	@Test
	private void testDuplicateSiteKeyInScheduleMasterFile() throws Exception
	{
		FileInputStream inputFile = new FileInputStream(
				getFileLocation() + "SCHEDULE_MASTER-duplRecord.xlsx");
		when(timeZoneRepository.findByRegionEntityId_Id(any(Integer.class)))
				.thenReturn(Arrays.asList((TimeZoneEntity) TestDataProvider.timeZoneEntity()[0][0]));

		when(timeZoneDetailsRepository.findByTimeZoneEntityId_Id(any(Integer.class))).thenReturn(
				Arrays.asList((TimeZoneDetailsEntity) TestDataProvider.timeZoneDetailsEntity()[0][0]));
		getDropDownByName();

		MockMultipartFile file = new MockMultipartFile("uploadedFile", "SCHEDULE_MASTER-duplRecord.xlsx",
				"multipart/form-data", inputFile);

		this.mvc.perform(multipart(getRootUrl() + "fileupload").file(file)).andExpect(status().isBadRequest())
				.andExpect(
						jsonPath("$.errors[0].userMessage", equalTo("Duplicate Site Key-[CRMC1, R4S00].")));
	}

	@Test
	private void testDuplicateSiteKeyInCSMFile() throws Exception
	{

		FileInputStream inputFile = new FileInputStream(
				getFileLocation() + "CSM_CONTACT_MASTER -duplicateSiteKey.xlsx");

		when(customerMasterDataRepository.findBySiteKey(any(String.class)))
				.thenReturn((CustomerMasterDataEntity) TestDataProvider.customerMasterDataEntity()[0][0]);

		MockMultipartFile file = new MockMultipartFile("uploadedFile",
				"CSM_CONTACT_MASTER -duplicateSiteKey.xlsx", "multipart/form-data", inputFile);

		this.mvc.perform(multipart(getRootUrl() + "fileupload").file(file)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errors[0].userMessage", equalTo("Duplicate Site Key-[CRMC1].")));
	}

}
