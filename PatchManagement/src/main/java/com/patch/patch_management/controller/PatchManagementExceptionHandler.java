package com.patch.patch_management.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.patch.patch_management.constant.IPatchManagementMessageCode;
import com.patch.patch_management.exception.ApplBusException;
import com.patch.patch_management.exception.ApplErrorCollection;
import com.patch.patch_management.exception.ApplErrorDTO;
import com.patch.patch_management.exception.ApplErrorDTO.Severity;
import com.patch.patch_management.util.MessagesUtil;

@ControllerAdvice
public class PatchManagementExceptionHandler extends ResponseEntityExceptionHandler
{
	static Logger log = LoggerFactory.getLogger(PatchManagementExceptionHandler.class);

	/**
	 * @name : generalException
	 *
	 * @param :
	 *            e
	 *
	 * @returns : ResponseEntity<ApplErrorCollection>
	 *
	 * @description : This method is used to handle generic Exceptions.
	 *
	 */
	@ExceptionHandler(value = ApplBusException.class)
	public ResponseEntity<ApplErrorCollection> applBusException(ApplBusException e)
	{
		return new ResponseEntity<>(e.getErrorCollection(), HttpStatus.BAD_REQUEST);
	}

	/**
	 * @name : generalException
	 *
	 * @param :
	 *            e
	 *
	 * @returns : ResponseEntity<ApplErrorCollection>
	 *
	 * @description : This method is used to handle generic Exceptions.
	 *
	 */

	@ExceptionHandler(value = Exception.class)
	public ResponseEntity<ApplErrorCollection> generalException(Exception e)
	{
		ApplErrorCollection errorCollection = new ApplErrorCollection();
		if (e.getCause() instanceof ApplBusException)
		{
			errorCollection = ((ApplBusException) e.getCause()).getErrorCollection();
		}
		else
		{
			log.error(e.getMessage(), e);
			ApplErrorDTO applErrorDTO = new ApplErrorDTO(Severity.FATAL,
					IPatchManagementMessageCode.SYS_EXCEPTION,
					MessagesUtil.getString(IPatchManagementMessageCode.SYS_EXCEPTION));

			errorCollection.add(applErrorDTO);

		}
		return new ResponseEntity<>(errorCollection, HttpStatus.SERVICE_UNAVAILABLE);
	}

}
