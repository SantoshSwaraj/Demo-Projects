package aisaac.service;

import aisaac.payload.request.AddToWatchListRequest;
import aisaac.payload.request.ThreatListRequest;
import aisaac.payload.request.TicketListRequest;
import aisaac.payload.request.UserTileListRequest;

public interface UbaService {

	public Object getAdUserDetailDepartmentList(Long tenantId);
	
	public Object getThreatList(ThreatListRequest request);
	
	public Object addOrDeleteWatchListDetails(AddToWatchListRequest addToWatchListRequest);
	
	public Object getWatchListAuditTrail(Long createdBy);
	
	public Object getUserTileList(UserTileListRequest userTileListRequest);
	
	public Object exportThreatListToCSV(ThreatListRequest request, Integer limit, String path);
	
	public Object exportThreatList(ThreatListRequest request, Integer limit, String path);
	
	public Object getRiskScoreDonutDetails(Long tenantId);
	
	public Object getWatchlistedUsers(Long tenantId);
	
	public Object getRiskUsersList(Long tenantId);
	
	public Object getTicketList(TicketListRequest ticketListRequest);
}
