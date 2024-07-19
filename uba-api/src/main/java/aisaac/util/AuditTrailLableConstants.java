package aisaac.util;

import java.time.format.DateTimeFormatter;

public class AuditTrailLableConstants {

	public static final String adUserId="AD User ID";
	public static final String tenantId="Tenant ID";
	public static final String performedBy="Performed By";
	public static final String performedOn="Performed On";
	
	public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");
}
