package aisaac.payload.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class ThreatListResponse {

	private String threatName;
	private String threatSource;
	private String sourceIp;
	private Integer sourcePort;
	private String destinationIp;
	private Integer destinationPort;
	private String sourceHostName;
	private String destinationHostName;
	private String destinationUrl;
	private Long eventTime;
	private String sourceLocation;
	private String destinationLocation;
	private Integer severity;
	private String userName;
	private Float userScore;
	private String userScoreText;
}
