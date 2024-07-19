package aisaac.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class LogManagementApiGetSavedQueriesRequest {
	private List<Integer> tenantIds;
	private String roleName;
	private Boolean isPaladionUser;
	
	public LogManagementApiGetSavedQueriesRequest(List<Integer> tenantIds, String roleName, Boolean isPaladionUser) {
		this.tenantIds = tenantIds;
		this.roleName = roleName;
		this.isPaladionUser = isPaladionUser;
	}
}
