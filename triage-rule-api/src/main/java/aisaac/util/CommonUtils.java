package aisaac.util;

import java.util.Date;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CommonUtils {

	public static Date parseEpochToDate(Long epochTime) {
		Date convertedDate=null;
		try {
			if(null!=epochTime) {
				convertedDate = new Date(epochTime);
			}
		}catch(Exception e) {
			log.error("@@Exception in parseEpochToDate()::",e);
		}
		return convertedDate;
	}

	public static Long parseDateToEpoch(Date date) {
		long epochTime=0L;
		try {
			if(null!=date) {
				epochTime = date.getTime();
			}
		}catch(Exception e) {
			log.error("@@Exception in parseDateToEpoch()::",e);
		}
		return epochTime;
	}


}
