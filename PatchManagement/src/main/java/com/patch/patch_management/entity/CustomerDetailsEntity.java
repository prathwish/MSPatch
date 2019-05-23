package com.patch.patch_management.entity;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "CUSTOMER_DETAILS")
public class CustomerDetailsEntity implements Serializable
{
	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "CUSTOMER_ID")
	private Integer				customerId;

	@NotNull
	@Column(name = "SITE_KEY", length = 20)
	private String				siteKey;

	@Column(name = "CUSTOMER_NAME", length = 254)
	private String				customerName;

	@Column(name = "CUSTOMER_MAIL_ID", length = 254)
	private String				customerEmailId;

	@Column(name = "CONTACT_NUMBER", length = 20)
	private String				contactNumber;

	@ManyToOne
	@JoinColumn(name = "HOSPITAL_ID")
	CustomerMasterDataEntity	hospitalId;
}
