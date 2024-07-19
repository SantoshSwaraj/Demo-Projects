package aisaac.payload.response;

import java.math.BigInteger;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class ThreatListResponse {

	private BigInteger threatId;
	private String sourceIp;
	private String destinationIp;
	private String threatName;
	private Integer count;
	private Float score;
	private Integer destinationPort;
	private String destinationUrl;
}
