package com.patch.patch_management.entity;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@Entity
@Table(name = "CANCEL_CONTRACT")
public class ContractCancellation implements Serializable
{

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Integer				id;

	@NotNull
	@Column(name = "PERMANENT_CANCELLATION", columnDefinition = "boolean default false")
	private Boolean				isPermanentCancellation;

	@Column(name = "CONTRACT_CANCEL_FROM")
	@JsonFormat(pattern = "M-d-yyyy")
	private LocalDate				cancelFrom;

	@Column(name = "CONTRACT_CANCEL_TO")
	@JsonFormat(pattern = "M-d-yyyy")
	private LocalDate				cancelTo;

	@OneToOne
	@JoinColumn(name = "HOSPITAL_ID")
	CustomerMasterDataEntity	hospitalId;

}
