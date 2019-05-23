package com.patch.patch_management.entity;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
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
@Table(name = "SESSION")
public class SessionEntity implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "SESSION_ID")
	private Integer	id;

	@NotNull
	@Column(name = "SESSION",length = 20)
	private String	name;

	@NotNull
	@Column(name = "SESSION_START_TIME", length = 20)
	private String	sessionStartTime;

	@NotNull
	@Column(name = "SESSION_END_TIME", length = 20)
	private String	sessionEndTime;

}
