package aisaac.payload.response;

import java.util.List;

import aisaac.dto.NotesFormatDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AddToLFMResponse {
	
	private Long recId;
	private String tenantName;
	private String productIP;
	private String productHostName;
	private String productVendor;
	private String productName;
	private String collectorAddress;
	private String collectorHostName;
	private Long detectedDate;
	private Long lastEventReceived;
	private Integer productId;
	private String mdrScannerCode;
	
	private List<NotesFormatDto> note;
}
