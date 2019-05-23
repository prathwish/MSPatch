package com.patch.patch_management.exception;



public class ApplBusException extends ApplException
{
	private static final long serialVersionUID = 1L;

	/**
	 * @param exceptionId
	 *            Unique identifier of this Exception
	 * @param category
	 *            Category of this Exception
	 * @param msg
	 *            Detail message string
	 * @param exceptionDataMap
	 *            Map of data which caused this Exception
	 * @param errorCollection
	 *            Collection of errors
	 */
	public ApplBusException(String exceptionId, String msg, ApplErrorCollection errorCollection)
	{
		super(exceptionId, msg, errorCollection);
	}

}
