package com.patch.patch_management.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "CSM_DETAILS")
public class CSMDetailsEntity implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CSM_ID")
	private Integer				csmId;

	@NotNull
	@Column(name = "SITE_KEY", length = 254, unique = true)
	private String				siteKey;

	@Column(name = "CSM_NAME", length = 254)
	private String				csmName;

	@Column(name = "CSM_EMAIL_ID", length = 500, nullable = false)
	private String				csmEmailId;

	@Column(name = "CONTACT_NUMBER", length = 254)
	private String				contactNumber;

	@Column(name = "ADDITIONAL_CONTACTS", length = 254)
	private String				additionalContacts;

	@OneToOne
	@JoinColumn(name = "HOSPITAL_ID")
	CustomerMasterDataEntity	hospitalId;

}
