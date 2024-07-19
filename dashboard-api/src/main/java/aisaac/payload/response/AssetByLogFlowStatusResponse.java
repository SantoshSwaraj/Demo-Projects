package aisaac.payload.response;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AssetByLogFlowStatusResponse {

	private String timeDuration;
	private Long stopped;
	private Long running;
	private Long total;
	private Long minLogRunningTime;
	private Long maxLogRunningTime;
	private Long minLogStoppedTime;
	private Long maxLogStoppedTime;
}
