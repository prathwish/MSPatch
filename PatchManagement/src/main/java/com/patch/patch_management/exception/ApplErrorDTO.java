package com.patch.patch_management.exception;

import java.io.Serializable;

public class ApplErrorDTO implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * This class gives the enum Severity to set the severity type in error scenario.
	 *
	 */
	public enum Severity
	{
		INFO("INFO"), WARNING("WARNING"), ERROR("ERROR"), FATAL("FATAL");

		private final String severityType;

		Severity(String severity)
		{
			this.severityType = severity;
		}

		/**
		 * this method overrides the toString method.
		 */
		@Override
		public String toString()
		{
			return severityType;
		}
	}

	private Severity	severity;
	private String		errorCode;
	private String		userMessage;

	/**
	 * Creates this object with the specified error code, severity, user message, technical message, error
	 * class name and method name
	 * 
	 * @param severity
	 * @param errorCode
	 * @param userMessage
	 */

	public ApplErrorDTO(Severity severity, String errorCode, String userMessage)
	{
		this.setSeverity(severity);
		this.setErrorCode(errorCode);
		this.setUserMessage(userMessage);
	}

	/**
	 * @return the severity
	 */
	public String getSeverity()
	{
		return severity.toString();
	}

	/**
	 * @param severity
	 *            the severity to set
	 */
	public void setSeverity(Severity severity)
	{
		this.severity = severity;
	}

	/**
	 * @return the errorCode
	 */
	public String getErrorCode()
	{
		return errorCode;
	}

	/**
	 * @param errorCode
	 *            the errorCode to set
	 */
	public void setErrorCode(String errorCode)
	{
		this.errorCode = errorCode;
	}

	/**
	 * @return the userMessage
	 */
	public String getUserMessage()
	{
		return userMessage;
	}

	/**
	 * @param userMessage
	 *            the userMessage to set
	 */
	public void setUserMessage(String userMessage)
	{
		this.userMessage = userMessage;
	}

}
