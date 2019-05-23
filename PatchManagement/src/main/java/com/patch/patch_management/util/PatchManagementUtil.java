package com.patch.patch_management.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.patch.patch_management.constant.IPatchManagementConstants;
import com.patch.patch_management.exception.ApplErrorCollection;
import com.patch.patch_management.exception.ApplErrorDTO;

@Component
public class PatchManagementUtil
{

	static Logger					logger						= LoggerFactory
			.getLogger(PatchManagementUtil.class);

	DateFormat						ddMMyyyy				= new SimpleDateFormat("dd-MMM-yyyy");
	DateFormat						mdyyyy				= new SimpleDateFormat("M-d-yyyy");
	public final List<DateFormat>	defaultDateFormatterList	= Arrays.asList( mdyyyy,ddMMyyyy

	);

	DateFormat						dateFormatter				= new SimpleDateFormat(
			MessagesUtil.getString(IPatchManagementConstants.DATE_FORMAT));

	/**
	 * @name : getCurrentTimestamp
	 *
	 *
	 * @returns : Timestamp
	 *
	 * @throws :
	 *
	 * @description : This method will return the current Timestamp to the calling method.
	 *
	 */
	public static Timestamp getCurrentTimestamp()
	{
		return new Timestamp(System.currentTimeMillis());
	}

	public LocalDate convertStringToDate(String inputDate)
	{
		LocalDate localDate = null;

		if (Objects.nonNull(inputDate) && !inputDate.trim().equals(""))
		{

			for (DateFormat formatter : defaultDateFormatterList)
			{
				try
				{
					Date date = mdyyyy.parse(dateFormatter.format(formatter.parse(inputDate)));
					localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					break;
				}
				catch (Exception e)
				{
					localDate = null;
					logger.error("Error while parsing the time. " + inputDate);
				}
			}

		}
		return localDate;
	}

	public String convertDateToString(Date inputDate)
	{
		String date = null;

		if (Objects.nonNull(inputDate))
		{
			try
			{
				date = dateFormatter.format(inputDate);
			}
			catch (Exception e)
			{
				logger.error("Error while formating the time. " + inputDate);
				return null;
			}
		}
		return date;
	}

	public static void populateErrorList(List<ApplErrorDTO> errorDtoList, ApplErrorCollection errorCollection)
	{
		if (!errorDtoList.isEmpty())
		{
			errorCollection.addAll(errorDtoList);
		}

	}

	public static String fetchOnlyFileName(String originalFilename)
	{
		int pos = originalFilename.lastIndexOf('.');
		if (pos > 0 && pos < (originalFilename.length() - 1))
		{ // If '.' is not the first or last character.
			int pos1 = originalFilename.lastIndexOf('-');
			if (pos1 > 0)
			{
				originalFilename = originalFilename.substring(0, pos1);
			}
			else
			{
				originalFilename = originalFilename.substring(0, pos);
			}
		}
		return originalFilename.toUpperCase().trim();
	}

	public static String fetchFileExt(String originalFilename)
	{
		return (originalFilename.substring(originalFilename.lastIndexOf('.') + 1)).toUpperCase();
	}

	public static Set<String> findDuplicates(List<String> listContainingDuplicates)
	{

		Set<String> setToReturn = new HashSet<>();
		Set<String> siteKeys = new HashSet<>();

		for (String yourInt : listContainingDuplicates)
		{
			if (!siteKeys.add(yourInt))
			{
				setToReturn.add(yourInt);
			}
		}
		return setToReturn;
	}
}
