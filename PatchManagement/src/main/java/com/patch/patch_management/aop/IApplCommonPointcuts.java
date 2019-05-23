package com.patch.patch_management.aop;

import org.aspectj.lang.annotation.Pointcut;

public interface IApplCommonPointcuts
{
	/**
	 * @name : archCapClasses
	 * 
	 * @returns : void
	 * 
	 * @description : This method is for creating point cut for classes
	 * 
	 */
	@Pointcut("within(com.patch..*)")
	public default void archCapClasses()
	{
	}

}
