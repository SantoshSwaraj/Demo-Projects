package aisaac.service;

import aisaac.payload.request.AddToWatchListRequest;
import aisaac.payload.request.AssetsListRequest;
import aisaac.payload.request.EntitiesTileListRequest;
import aisaac.payload.request.ThreatListRequest;
import aisaac.payload.request.TicketListRequest;

public interface EBAService {

	public Object getEntitiesTileList(EntitiesTileListRequest request);

	public Object getThreatList(ThreatListRequest request);

	public Object updateWatchListDetails(AddToWatchListRequest addToWatchListRequest);

	public Object getWatchListAuditTrail(Long recId);

	public Object getEBASettings();

	public Object getAssetsByUserEntityId(AssetsListRequest request);

	public Object exportThreatListToCSV(ThreatListRequest request, Integer limit, String path);
	
	public Object exportThreatList(ThreatListRequest request, Integer limit, String path);
	
	public Object getRiskScoreDonutList(Long tenantId);
	
	public Object getWatchlistedEntities(Long tenantId);
	
	public Object getRiskEntitiesList(Long tenantId);
	
	public Object getTicketList(TicketListRequest ticketListRequest);
	
	public Object getMappedAssetExport(AssetsListRequest request, Integer limit, String path);
}
