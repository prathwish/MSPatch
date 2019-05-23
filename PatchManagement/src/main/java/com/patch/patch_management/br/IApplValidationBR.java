package com.patch.patch_management.br;

import com.patch.patch_management.exception.ApplBusException;

/**
 * @param <R>
 * @param <P>
 * @Description : The implementation classes of this BR are expected to perform some sort of validations based
 *              on a set of given inputs. For example validate if contract is editable, validate required
 *              factor dimensions, validate address, validate pricing tolerance etc.
 * 
 *              Note: The BRs should be independent and reusable meaning it should be provided with all the
 *              details required to accomplish its task.
 */
public interface IApplValidationBR<R, P>
{
	/**
	 * @name : validate
	 * 
	 * @param p
	 *
	 * @returns : R
	 * 
	 * @throws :
	 *             ApplBusException
	 * 
	 * @description : This method is for validating the passed parameter.
	 * 
	 */
	R validate(P p) throws ApplBusException;
}
