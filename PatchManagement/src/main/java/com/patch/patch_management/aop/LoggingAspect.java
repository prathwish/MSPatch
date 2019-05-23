package com.patch.patch_management.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
public class LoggingAspect implements IApplCommonPointcuts
{
	private final Logger		logger			= LoggerFactory.getLogger(LoggingAspect.class);

	private static final String	ENTERING_METHOD	= "Entering method : ";
	private static final String	EXITING_METHOD	= "Exiting method : ";

	/**
	 * @name : logAround
	 * 
	 * @param joinPoint
	 *
	 * @returns : Object
	 * 
	 * @throws :
	 *             Throwable
	 * 
	 * @description : This method is for logging.
	 * 
	 */
	@Around("archCapClasses()")
	public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable
	{
		String className = joinPoint.getSignature().getDeclaringTypeName();
		String methodName = joinPoint.getSignature().getName();

		String fullMethodName = className + "." + methodName;

		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		logger.info(ENTERING_METHOD + fullMethodName);

		Object result = null;
		try
		{
			result = joinPoint.proceed();
		}
		catch (Exception e)
		{
			logger.error("Exception in method " + fullMethodName);
			throw e;
		}
		finally
		{
			stopWatch.stop();
		}

		logger.info(EXITING_METHOD + fullMethodName);
		logger.debug("Method Execution Time : " + methodName + " : " + stopWatch.getTotalTimeMillis() + " ms");

		return result;
	}
}
