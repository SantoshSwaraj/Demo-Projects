package aisaac.payload.response;

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
public class PolicyManagementRemediationRefreshResponse {
	
	private Boolean success;
	private String status;
    private String message;
    private String complianceStatus; 

}
