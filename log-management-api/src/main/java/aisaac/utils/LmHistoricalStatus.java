package aisaac.utils;

public enum LmHistoricalStatus {
	
	ACCEPTED("ACCEPTED"),
	RUNNING("RUNNING"),
	SUCCESS("SUCCESS"),
	FAILED("FAILED");
	
	private final String status;
	
	LmHistoricalStatus(String status) {
        this.status = status;
    }
	
	 public String getStatus() {
	        return status;
	 }
	 
	 public static LmHistoricalStatus fromString(String text) {
        for (LmHistoricalStatus status : LmHistoricalStatus.values()) {
            if (status.status.equalsIgnoreCase(text)) {
                return status;
            }
        }
		return null;
    }
}
