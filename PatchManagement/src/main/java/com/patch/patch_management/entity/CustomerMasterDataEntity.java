package com.patch.patch_management.entity;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Entity
@Table(name = "CUSTOMER_MASTER_DATA")
public class CustomerMasterDataEntity implements Serializable
{

	/**
	 * 
	 */
	private static final long			serialVersionUID	= 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "HOSPITAL_ID")
	private Integer						hospitalId;

	@NotNull
	@Column(name = "SITE_KEY", length = 20, unique = true)
	private String						siteKey;

	@Column(name = "ADDED_DATE")
	@JsonFormat(pattern = "M-d-yyyy")
	private LocalDate					addedDate;

	@Column(name = "APPLICATION_VERSION", length = 20)
	private String						applicationVersion;

	@Column(name = "CUSTOMER_NAME", length = 100)
	private String						customerName;

	@Column(name = "WEEK", nullable = false)
	private Integer						week;

	@ManyToOne
	@JoinColumn(name = "DAY_ID", nullable = false)
	private DayEntity					day;

	@ManyToOne
	@JoinColumn(name = "SESSION_ID", nullable = false)
	private SessionEntity				session;

	@ManyToOne
	@JoinColumn(name = "CONSOLE_ID", nullable = false)
	private ConsoleEntity				console;

	@ManyToOne
	@JoinColumn(name = "REGION_ID", nullable = false)
	private RegionEntity				region;

	@Column(name = "SCHEDULED_START_TIME", length = 20, nullable = false)
	private String						scheduledStarttime;

	@Column(name = "SCHEDULED_END_TIME", length = 20)
	private String						scheduledEndtime;

	@Column(name = "DURATION")
	private Integer						duration;

	@ManyToOne
	@JoinColumn(name = "TIMEZONE_DETAILS_ID", nullable = false)
	private TimeZoneDetailsEntity		timeZoneDetailsEntity;

	@ManyToOne
	@JoinColumn(name = "TIMEZONE_ID", nullable = false)
	private TimeZoneEntity				timeZone;

	@Lob
	@Type(type = "org.hibernate.type.TextType")
	@Column(name = "COMMENT")
	private String						comment;

	@OneToOne(cascade = CascadeType.ALL, mappedBy = "hospitalId")
	private CSMDetailsEntity			cSMDetails;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "hospitalId", fetch = FetchType.EAGER)
	private List<CustomerDetailsEntity>	customerDetails		= new ArrayList<>();

	@CreationTimestamp
	@Column(name = "CREATION_TIME")
	private LocalDateTime				creationTime;

	@OneToOne(cascade = CascadeType.ALL, mappedBy = "hospitalId")
	private ContractCancellation		cancelContract;
}
