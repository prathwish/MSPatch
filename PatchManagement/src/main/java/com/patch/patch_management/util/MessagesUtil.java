package com.patch.patch_management.util;



import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessagesUtil
{
	private static final Logger			logger			= LoggerFactory.getLogger(MessagesUtil.class);
	private static final ResourceBundle	RESOURCE_BUNDLE	= ResourceBundle.getBundle("i18n.messages");

	private MessagesUtil()
	{
	}

	public static MessagesUtil getInstance()
	{
		return new MessagesUtil();
	}

	/**
	 * @param key
	 * @return returns string value from resource bundle messages.properties.
	 */
	public static String getString(String key)
	{
		try
		{
			return RESOURCE_BUNDLE.getString(key);
		}
		catch (MissingResourceException e)
		{
			logger.error(" MissingResourceException: ", e);
			return key;
		}
	}

	/**
	 * @param key
	 * @param params
	 * @return returns the complete user message substituted with required params.
	 */
	public static String getString(String key, Object... params)
	{
		try
		{
			return MessageFormat.format(RESOURCE_BUNDLE.getString(key), params);
		}
		catch (MissingResourceException e)
		{
			logger.error(" MissingResourceException: ", e);
			return key;
		}
	}
}
