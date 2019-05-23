package com.patch.patch_management.exception;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ApplErrorCollection implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public enum StatusCode
	{
		ERROR("error"), SYSTEM_ERROR("system.error"), SUB_SYSTEM_ERROR("sub.system.error");

		private final String statusCodeType;

		StatusCode(String statusCode)
		{
			this.statusCodeType = statusCode;
		}

		/**
		 * this method overrides the toString method.
		 */
		@Override
		public String toString()
		{
			return statusCodeType;
		}
	}

	private final List<ApplErrorDTO>	errors;
	private StatusCode					statusCode	= StatusCode.ERROR;

	/**
	 * Default constructor for ApplErrorCollection.
	 */
	public ApplErrorCollection()
	{
		errors = new ArrayList<>();
	}

	/**
	 * Registers a Business Error.
	 * 
	 * @param busErr
	 */
	public void add(ApplErrorDTO busErr)
	{
		errors.add(busErr);
	}

	/**
	 * Adds a List of Business Errors.
	 * 
	 * @param list
	 */

	public void addAll(List<ApplErrorDTO> list)
	{
		errors.addAll(list);
	}

	/**
	 * Remove the given Business Error.
	 * 
	 * @param busErr
	 */
	public void remove(ApplErrorDTO busErr)
	{
		errors.remove(busErr);
	}

	/**
	 * Removes all the Business Errors from the internal List.
	 */
	public void removeAll()
	{
		errors.clear();
	}

	/**
	 * Returns a List of all the Business Errors registered with this object.
	 * 
	 * @return List<ApplErrorVO>
	 */
	public List<ApplErrorDTO> getErrors()
	{
		return errors;
	}

	/**
	 * Returns a List of Business Errors registered with this object with the given severity code.
	 * 
	 * @param severity
	 * @return List
	 */
	public List<ApplErrorDTO> getErrorsBySeverity(String severity)
	{
		ApplErrorDTO busError;
		ArrayList<ApplErrorDTO> tempColl = new ArrayList<>();

		Iterator<ApplErrorDTO> listIter = errors.iterator();

		while (listIter.hasNext())
		{
			busError = listIter.next();
			if (busError != null && busError.getSeverity().equalsIgnoreCase(severity))
			{
				tempColl.add(busError);
			}
		}
		return tempColl;
	}

	/**
	 * Tells whether there exists a <code>ApplErrorVO</code> object inside with the given severity.
	 * 
	 * @param severity
	 * @return boolean
	 */

	public boolean doesSeverityExist(String severity)
	{
		ApplErrorDTO busError;
		Iterator<ApplErrorDTO> listIter = errors.iterator();

		while (listIter.hasNext())
		{
			busError = listIter.next();
			if (busError != null && busError.getSeverity().equalsIgnoreCase(severity))
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * Returns a List of Business Errors registered with this object with the given error code.
	 * 
	 * @param errorCode
	 * @return List
	 */
	public List<ApplErrorDTO> getErrorsByCode(String errorCode)
	{
		ApplErrorDTO busError;
		List<ApplErrorDTO> tempColl = new ArrayList<>();
		Iterator<ApplErrorDTO> listIter = errors.iterator();

		while (listIter.hasNext())
		{
			busError = listIter.next();
			if (busError != null && busError.getErrorCode() != null
					&& busError.getErrorCode().equalsIgnoreCase(errorCode))
			{
				tempColl.add(busError);
			}
		}

		return tempColl;
	}

	/**
	 * Returns the number of <code>ApplErrorVO</code> objects inside.
	 */
	public int getSize()
	{
		int errCollSize;

		errCollSize = this.errors.size();

		return errCollSize;
	}

	/**
	 * @return the statusCode
	 */
	public StatusCode getStatusCode()
	{
		return statusCode;
	}

	/**
	 * @param statusCode
	 *            the statusCode to set
	 */
	public void setStatusCode(StatusCode statusCode)
	{
		this.statusCode = statusCode;
	}

}
