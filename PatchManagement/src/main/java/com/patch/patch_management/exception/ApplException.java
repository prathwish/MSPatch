package com.patch.patch_management.exception;

import java.io.Serializable;

public class ApplException extends Exception implements Serializable
{
	private static final long	serialVersionUID	= 1L;
	private final String		exceptionId;
	private ApplErrorCollection	errorCollection;

	/**
	 * @param exceptionId
	 *            Unique identifier of this Exception
	 * @param category
	 *            Category of this Exception
	 * @param msg
	 *            Detail message string
	 * @param errorCollection
	 *            Collection of errors
	 * @param exceptionDataMap
	 *            Map of data which caused this Exception
	 */
	public ApplException(String exceptionId, String msg, ApplErrorCollection errorCollection)
	{
		super(msg);
		this.exceptionId = exceptionId;
		this.errorCollection = errorCollection;
	}

	/**
	 * Returns the exceptionId.
	 * 
	 * @return String
	 */
	public String getExceptionId()
	{
		return exceptionId;
	}

	/**
	 * @name : getErrorCollection
	 * 
	 * @param :
	 *            ApplException
	 *
	 *
	 * @returns : ApplErrorCollection
	 * 
	 * @throws :
	 * 
	 * @description : returns collection of errors.
	 * 
	 */
	public ApplErrorCollection getErrorCollection()
	{
		return errorCollection;
	}

	/**
	 * @name : setErrorCollection
	 * 
	 * @param :
	 *            ApplException
	 *
	 * @returns : void
	 * 
	 * @throws :
	 * 
	 * @description : sets the error collection.
	 * 
	 */
	public void setErrorCollection(ApplErrorCollection errorCollection)
	{
		this.errorCollection = errorCollection;
	}
}
