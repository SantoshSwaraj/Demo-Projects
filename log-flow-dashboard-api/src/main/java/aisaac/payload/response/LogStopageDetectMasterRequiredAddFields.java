package aisaac.payload.response;

import java.util.List;

import aisaac.dto.NotesFormatDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LogStopageDetectMasterRequiredAddFields {

	private Long recId;
	private Long tenantId;
	private String tenantName;
	private String productHostName;
	private String productIP;
	private String cloudResourceID;
	private String collectorIP;
	private String collectorHostName;
	private Integer productID;
	private String mdrScannerCode;
	private List<NotesFormatDto> note;
	private Long detectedDate;
	private Long lastEventReceived;
}
