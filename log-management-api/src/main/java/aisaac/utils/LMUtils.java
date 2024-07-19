package aisaac.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LMUtils {
	
	public static final String USER_SESSION = "userSession";
	public static final Integer USERSTATUS = 359;

	public static String convertDateToStr(Date since, String dateFormate) {
		String dateStr = "";
		if (since != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(dateFormate);
			dateStr = sdf.format(since);
		}
		return dateStr;
	}
	
	/**
	 * Method to convert Server time to local.
	 * 
	 * @param input
	 * @param offset
	 * @return
	 */
	public static Date convertServertoLocal(Date input, Integer offset) {

		if (input == null || offset == null) {
			return input;
		}

		Calendar date = Calendar.getInstance();
		date.setTime(input);
		date.add(Calendar.MINUTE, -1 * offset);
		return date.getTime();
	}
	
	/*
	 * Method for converting date which coming from Client to server Time
	 * 
	*/
	public static Date getConvertedDateForLocalAndServer(Date dateTime, boolean isUtcTimeZone, Integer timezoneOffset){
	    Date convertedDateTime = null;
	    try {
	    	
	         if (isUtcTimeZone) {
	    	    convertedDateTime = convertServertoLocal(dateTime, timezoneOffset);
	         }
	         else{
	    	    convertedDateTime = dateTime;
	         }
	         
		} catch (Exception e) {
			log.error("Error : Simutils : while processing Date from Server to Localor vice versa "+e.getMessage());
			e.printStackTrace();
		}
	    
	    return convertedDateTime;
	}
	
	public static String removeNonUtf8Characters(String subject) {
		if (StringUtils.isBlank(subject)) {
			return subject;
		}

		return subject.replaceAll("[^\\x0A\\x20-\\x7e]", "");
	}
		
	public static String getTimeZoneName(Integer timezoneOffset) {
		String timezoneName = " UTC";
		String sign = timezoneOffset < 0 ? "+" : "-";
		
		if(timezoneOffset == 0) {
			return timezoneName;
		}
		return timezoneName + " " + sign + (Math.abs(timezoneOffset) / 60) + ":" + ( Math.abs(timezoneOffset) % 60);
	}
	
	public static Date getDateTimeByEpochMilli(Long value) {
		return new Date(value);
	}
	
	public static String getDisplayUsername(String displayName, String displayrole, Boolean isDeleted,
			Integer isDisabled) {

		String displayNameStr = displayName;
		if (displayName==null)
			return null;

		if (isDeleted == null)
			isDeleted = false;

		if (isDisabled == null)
			isDisabled = 0;

		if (isDeleted) {
			displayNameStr = "Deleted User";
		} else if ((USERSTATUS).equals(isDisabled)) {
			displayNameStr += " (Disabled User)";
		} else if (StringUtils.isNotEmpty(displayrole))
			displayNameStr += "(" + displayrole + ")";

		return displayNameStr;
	}
}
