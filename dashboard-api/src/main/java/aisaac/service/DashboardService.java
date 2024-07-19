package aisaac.service;

import java.math.BigInteger;

import aisaac.payload.request.CountryMapAlertRequest;
import aisaac.payload.request.DashboardCountRequest;

public interface DashboardService {

	public Long getAssetCountByTenantId(Long tenantId);

	public Object getTicketsDetails(DashboardCountRequest dashboardCountRequest);

	public Object getAssetByProductNamesAPI(Long tenantId);

	public Object getOpenTicketsCountDetails(DashboardCountRequest dashboardCountRequest);

	public Object getReOpenedTicketsCountDetails(DashboardCountRequest dashboardCountRequest);

	public Object getTicketsByCategoryAPI(DashboardCountRequest dashboardCountRequest);

	public Object getTicketsList(DashboardCountRequest dashboardCountRequest);

	public Object getThreatAlertDetails(BigInteger threatId);

	public Object getThreatAlertListByTicketId(Long ticketId);

	public Object getAssetByCriticalityDetails(Long tenantId);

	public Object getAgeOfTicketsDetails(Long tenantId);
	
	public Object getClosedTicketsCountDetails(DashboardCountRequest dashboardCountRequest);
	
	public Object getThreatCountDetails(DashboardCountRequest dashboardCountRequest);
	
	public Object getThreatCountByProductName(DashboardCountRequest dashboardCountRequest);
	
	public Object getAssetByTicketPriorityCount(DashboardCountRequest dashboardCountRequest);
	
	public Object getAssetByTicketPriorityLevelCount(DashboardCountRequest dashboardCountRequest);
	
	public Object getThreatCountriesData(DashboardCountRequest dashboardCountRequest);
	
	public Object getCountryMapDetailsForAlert(CountryMapAlertRequest alertRequest);
	
	public Object getAssetByLogFlowStatusDetails(Long tenantId);
	
	public Object getAverageResponseTimeForTicketCategory(Long tenantId);
	
	public Object getAverageResponseTime(Long tenantId);
	
	public Object getDefaultSettings();
}
