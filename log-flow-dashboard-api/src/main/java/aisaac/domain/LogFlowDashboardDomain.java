package aisaac.domain;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.springframework.data.jpa.domain.Specification;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import aisaac.domain.datatable.Order;
import aisaac.domain.datatable.Search;
import aisaac.dto.AllowListAuditTrailDto;
import aisaac.dto.AllowListAuditTrailJsonResponse;
import aisaac.dto.AuditTrailDetailsOnly;
import aisaac.dto.AuditTrailNewOldValueDto;
import aisaac.dto.CustomTimeFields;
import aisaac.dto.DetectedDevicesAuditTrailDto;
import aisaac.dto.DetectedDevicesAuditTrailJsonResponse;
import aisaac.dto.LogStopageAuditTrailDto;
import aisaac.dto.LogStopageAuditTrailJsonResponse;
import aisaac.dto.NotesFormatDto;
import aisaac.entities.AuditTrail;
import aisaac.entities.LogStopageMaster;
import aisaac.entities.LogStoppageDetectedDevices;
import aisaac.entities.LogStoppageWhitelistDevices;
import aisaac.entities.Notes;
import aisaac.entities.ProductMaster;
import aisaac.entities.ProductTypeMaster;
import aisaac.entities.SysParameterValue;
import aisaac.entities.Tenant;
import aisaac.payload.request.AuditTrailListRequest;
import aisaac.payload.request.LogFlowDashboardDetailsRequest;
import aisaac.util.AppConstants;
import aisaac.util.AuditTrailLableConstans;
import aisaac.util.LogFlowDashboardUtils;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

public class LogFlowDashboardDomain {

	public static Integer getExportLimit() {
		return 100;
	}

	public static <T> Specification<T> getAdvanceOrFullSearchFilterSpecification(boolean isDeleted,
			String productVendor, String productName, String productType, String productIP, String productHostName,
			String cloudResourceID, String collectorAddress, String collectorHostName, String toEmailAddress,
			String ccEmailAddress, boolean useLogicalOr, LogFlowDashboardDetailsRequest request, Class<T> EntityName) {

		List<Integer> assetType = request.getSearchAssetTypeList(),
				deviceStatus = request.getSearchAisaacCurrentFlowStatusList();
		List<Long> tenantId = request.getSearchOrgIds(), productId = request.getProductId(),
				productTypeIdList = request.getProductTypeId();
		List<String> monitorStatus = request.getSearchMonitoringStatus(), severity = request.getSearchSeverityList();
		Integer fromLogStoppage = request.getSearchLogStoppageThresholdFrom(),
				toLogStoppage = request.getSearchLogStoppageThresholdTo(),
				fromEmailFrequency = request.getSearchEmailNotificationFrequencyFrom(),
				toEmailFrequency = request.getSearchEmailNotificationFrequencyTo();
		LocalDateTime aisaacUpdateBetweenFrom = request.getSearchCurrentFlowStateFrom(),
				aisaacUpdateBetweenTo = request.getSearchCurrentFlowStateUpdateTo(),
				lastEventReceivedFrom = request.getSearchLastEventReceivedFrom(),
				lastEventReceivedTo = request.getSearchLastEventReceivedTo(),
				createdFromDate = request.getSearchCreatedBetweenFrom(),
				createdToDate = request.getSearchCreatedBetweenTo(),
				updatedFromDate = request.getSearchUpdatedBetweenFrom(),
				updatedToDate = request.getSearchUpdatedBetweenTo(),
				detectedFromDate = request.getSearchDetectedBetweenFrom(),
				detectedToDate = request.getSearchDetectedBetweenTo(),
				allowlistFromDate = request.getSearchAllowlistedBetweenFrom(),
				allowlistToDate = request.getSearchAllowlistedBetweenTo();
		String mdrScannerCode = request.getSearchMdrScannerCode(), description = request.getSearchDescription();

		return (root, query, criteriaBuilder) -> {
			Specification<T> specification = Specification.where(null);
			Specification<T> deleteSpecification = Specification.where(null);

			deleteSpecification = (subroot, subquery, builder) -> criteriaBuilder.equal(subroot.get("deleted"),
					isDeleted);
			if (CollectionUtils.isNotEmpty(tenantId)) {
				Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder.in(subroot.get("tenantId"))
						.value(tenantId);
				deleteSpecification = createCondition(deleteSpecification, condition, false);
			}
			if (EntityName.equals(LogStoppageDetectedDevices.class)) {
				Specification<T> condition  = (subroot, subquery, builder) -> criteriaBuilder.equal(subroot.get("whitelist"),
						AppConstants.LOG_STOPPAGE_DETECTED_DEVICE_DELETE_COLUMN_DEFAULT);
				deleteSpecification = createCondition(deleteSpecification, condition, false);
			}

			if (StringUtils.isNotBlank(productVendor)) {
//				Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
//						.like(subroot.get("productVendor"), "%" + productVendor + "%");
//				specification = createCondition(specification, condition, useLogicalOr);

				// for full search
				if (useLogicalOr) {
					if (NumberUtils.isCreatable(productVendor) && !productVendor.contains(".")
							&& EntityName.equals(LogStopageMaster.class)) {
						Specification<T> assetTypeCondition = (subroot, subquery, builder) -> criteriaBuilder
								.equal(subroot.get("assetType"), productVendor);
						specification = createCondition(specification, assetTypeCondition, useLogicalOr);

						Specification<T> currentFlowStatusCondition = (subroot, subquery, builder) -> criteriaBuilder
								.equal(subroot.get("deviceStatus"), productVendor);
						specification = createCondition(specification, currentFlowStatusCondition, useLogicalOr);

						Specification<T> logStoppateThreshlodCondition = (subroot, subquery, builder) -> criteriaBuilder
								.equal(subroot.get("emailThresholdReached"), productVendor);
						specification = createCondition(specification, logStoppateThreshlodCondition, useLogicalOr);

						Specification<T> emailNotificationFrequencyCondition = (subroot, subquery,
								builder) -> criteriaBuilder.equal(subroot.get("emailAlertFrequency"), productVendor);
						specification = createCondition(specification, emailNotificationFrequencyCondition,
								useLogicalOr);
					}
					if (EntityName.equals(LogStoppageWhitelistDevices.class)) {
						Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
								.like(subroot.get("description"), "%" + productVendor + "%");
						specification = createCondition(specification, condition, useLogicalOr);
					}
					if (EntityName.equals(LogStopageMaster.class)) {
						Specification<T> severityCondition = (subroot, subquery, builder) -> criteriaBuilder
								.like(subroot.get("severity"), "%" + productVendor + "%");
						specification = createCondition(specification, severityCondition, useLogicalOr);
					}
					
				}
			}
//			if (useLogicalOr) {
				if (StringUtils.isNotBlank(productIP) && !EntityName.equals(LogStoppageWhitelistDevices.class)) {
					Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
							.like(subroot.get("productIP"), "%" + productIP + "%");
					specification = createCondition(specification, condition, useLogicalOr);
				}
				if (StringUtils.isNotBlank(productHostName) && !EntityName.equals(LogStoppageWhitelistDevices.class)) {
					Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
							.like(subroot.get("productHostName"), "%" + productHostName + "%");
					specification = createCondition(specification, condition, useLogicalOr);
				}
//			}else {
//				if (StringUtils.isNotBlank(productIP) && !EntityName.equals(LogStoppageWhitelistDevices.class)) {
//					Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
//							.equal(subroot.get("productIP"), productIP);
//					specification = createCondition(specification, condition, useLogicalOr);
//				}
//				if (StringUtils.isNotBlank(productHostName) && !EntityName.equals(LogStoppageWhitelistDevices.class)) {
//					Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
//							.equal(subroot.get("productHostName"), productHostName);
//					specification = createCondition(specification, condition, useLogicalOr);
//				}
//			
//			}
			
			if (StringUtils.isNotBlank(cloudResourceID) && !EntityName.equals(LogStoppageWhitelistDevices.class)) {
				Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
						.like(subroot.get("cloudResourceID"), "%" + cloudResourceID + "%");
				specification = createCondition(specification, condition, useLogicalOr);
			}
			if (StringUtils.isNotBlank(collectorAddress) && !EntityName.equals(LogStoppageWhitelistDevices.class)) {
				Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
						.like(subroot.get("collectorAddress"), "%" + collectorAddress + "%");
				specification = createCondition(specification, condition, useLogicalOr);
			}
			if (StringUtils.isNotBlank(collectorHostName) && !EntityName.equals(LogStoppageWhitelistDevices.class)) {
				Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
						.like(subroot.get("collectorHostName"), "%" + collectorHostName + "%");
				specification = createCondition(specification, condition, useLogicalOr);
			}
			if (StringUtils.isNotBlank(toEmailAddress) && EntityName.equals(LogStopageMaster.class)) {
				Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
						.like(subroot.get("toEmailAddress"), "%" + toEmailAddress + "%");
				specification = createCondition(specification, condition, useLogicalOr);
			}
			if (StringUtils.isNotBlank(ccEmailAddress) && EntityName.equals(LogStopageMaster.class)) {
				Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
						.like(subroot.get("ccEmailAddress"), "%" + ccEmailAddress + "%");
				specification = createCondition(specification, condition, useLogicalOr);
			}
			if (StringUtils.isNotBlank(mdrScannerCode) && !EntityName.equals(LogStoppageWhitelistDevices.class)) {
				Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
						.like(subroot.get("mdrScannerCode"), "%" + mdrScannerCode + "%");
				specification = createCondition(specification, condition, useLogicalOr);
			}
			if (CollectionUtils.isNotEmpty(assetType) && EntityName.equals(LogStopageMaster.class)) {
				Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
						.in(subroot.get("assetType")).value(assetType);
				specification = createCondition(specification, condition, useLogicalOr);
			}
			if (Objects.nonNull(productTypeIdList) && EntityName.equals(LogStopageMaster.class)) {
				Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
						.in(subroot.get("productTypeId")).value(productTypeIdList);
				specification = createCondition(specification, condition, useLogicalOr);
			}
			if (Objects.nonNull(productId)) {
				Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
						.in(subroot.get("productId")).value(productId);
				specification = createCondition(specification, condition, useLogicalOr);
			}
			
			//Monitor Status Condition Start
			if (CollectionUtils.isNotEmpty(monitorStatus) && EntityName.equals(LogStopageMaster.class)) {
				Specification<T> monitorSpecification = Specification.where(null);
				if (monitorStatus.contains(AppConstants.MONITOR_STATUS_ACTIVE)
						&& monitorStatus.contains(AppConstants.MONITOR_STATUS_DISABLED)
						&& monitorStatus.contains(AppConstants.MONITOR_STATUS_SUPPRESSED)) {
					// Ignore the conditon if all the status is selected
				} else if (monitorStatus.contains(AppConstants.MONITOR_STATUS_ACTIVE)
						&& monitorStatus.contains(AppConstants.MONITOR_STATUS_DISABLED)) {
					Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
							.notEqual(subroot.get("suppressed"), AppConstants.LOG_FLOW_MONITOR_USER_ACTION_SUPPRESS);
					monitorSpecification = createCondition(monitorSpecification, condition, false);

				} else if (monitorStatus.contains(AppConstants.MONITOR_STATUS_ACTIVE)
						&& monitorStatus.contains(AppConstants.MONITOR_STATUS_SUPPRESSED)) {
					Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
							.notEqual(subroot.get("disabled"), AppConstants.LOG_FLOW_MONITOR_USER_ACTION_DISABLE);
					monitorSpecification = createCondition(monitorSpecification, condition, false);

				} else if (monitorStatus.contains(AppConstants.MONITOR_STATUS_DISABLED)
						&& monitorStatus.contains(AppConstants.MONITOR_STATUS_SUPPRESSED)) {
					Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
							.equal(subroot.get("disabled"), AppConstants.LOG_FLOW_MONITOR_USER_ACTION_DISABLE);
					monitorSpecification = createCondition(monitorSpecification, condition, true);
					Specification<T> condition1 = (subroot, subquery, builder) -> criteriaBuilder
							.equal(subroot.get("suppressed"), AppConstants.LOG_FLOW_MONITOR_USER_ACTION_SUPPRESS);
					monitorSpecification = createCondition(monitorSpecification, condition1, true);
				} else if (monitorStatus.contains(AppConstants.MONITOR_STATUS_ACTIVE)) {
					Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
							.equal(subroot.get("disabled"), AppConstants.LOG_FLOW_MONITOR_USER_ACTION_ENABLE);
					monitorSpecification = createCondition(monitorSpecification, condition, true);
					Specification<T> condition1 = (subroot, subquery, builder) -> criteriaBuilder
							.equal(subroot.get("suppressed"), AppConstants.LOG_FLOW_MONITOR_USER_ACTION_RESUME);
					monitorSpecification = createCondition(monitorSpecification, condition1, false);
				} else if (monitorStatus.contains(AppConstants.MONITOR_STATUS_DISABLED)) {
					Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
							.equal(subroot.get("disabled"), AppConstants.LOG_FLOW_MONITOR_USER_ACTION_DISABLE);
					monitorSpecification = createCondition(monitorSpecification, condition, true);
				} else if (monitorStatus.contains(AppConstants.MONITOR_STATUS_SUPPRESSED)) {
					Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
							.equal(subroot.get("suppressed"), AppConstants.LOG_FLOW_MONITOR_USER_ACTION_SUPPRESS);
					monitorSpecification = createCondition(monitorSpecification, condition, true);
					Specification<T> condition1 = (subroot, subquery, builder) -> criteriaBuilder
							.equal(subroot.get("disabled"), AppConstants.LOG_FLOW_MONITOR_USER_ACTION_ENABLE);
					monitorSpecification = createCondition(monitorSpecification, condition1, false);
				}
				specification = createCondition(specification, monitorSpecification, useLogicalOr);
			}
			//End of Monitor Status Condition
			if (CollectionUtils.isNotEmpty(severity) && EntityName.equals(LogStopageMaster.class)) {
				Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder.in(subroot.get("severity"))
						.value(severity);
				specification = createCondition(specification, condition, useLogicalOr);
			}
			if (fromLogStoppage != null && toLogStoppage != null && EntityName.equals(LogStopageMaster.class)) {
				Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
						.between(subroot.get("logStopageThresholdTime"), fromLogStoppage, toLogStoppage);
				specification = createCondition(specification, condition, useLogicalOr);
			}
			if (fromEmailFrequency != null && toEmailFrequency != null && EntityName.equals(LogStopageMaster.class)) {
				Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
						.between(subroot.get("emailAlertFrequency"), fromEmailFrequency, toEmailFrequency);
				specification = createCondition(specification, condition, useLogicalOr);
			}
			
			//Device status condition start
			if (CollectionUtils.isNotEmpty(deviceStatus) && EntityName.equals(LogStopageMaster.class)
					&& Objects.isNull(aisaacUpdateBetweenFrom) && Objects.isNull(aisaacUpdateBetweenTo)) {
				Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
						.in(subroot.get("deviceStatus")).value(deviceStatus);
				specification = createCondition(specification, condition, useLogicalOr);
			} else if (CollectionUtils.isNotEmpty(deviceStatus) && aisaacUpdateBetweenFrom != null
					&& aisaacUpdateBetweenTo != null && EntityName.equals(LogStopageMaster.class)) {
				Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
						.in(subroot.get("deviceStatus")).value(deviceStatus);
				specification = createCondition(specification, condition, useLogicalOr);
				
				if(deviceStatus.size()==2) {
					Specification<T> loggerSpecification = Specification.where(null);
					loggerSpecification = (subroot, subquery, builder) -> criteriaBuilder
							.between(subroot.get("latestRecivedTime"), aisaacUpdateBetweenFrom, aisaacUpdateBetweenTo);
					
					Specification<T> logStoppageTime = (subroot, subquery, builder) -> criteriaBuilder
							.between(subroot.get("logStoppageTime"), aisaacUpdateBetweenFrom, aisaacUpdateBetweenTo);
					
					loggerSpecification = createCondition(loggerSpecification, logStoppageTime, true);
					
					specification = createCondition(specification, loggerSpecification, useLogicalOr);
				}else {
					if(deviceStatus.get(0).equals(1)) {
						Specification<T> loggerSpecification = (subroot, subquery, builder) -> criteriaBuilder
								.between(subroot.get("latestRecivedTime"), aisaacUpdateBetweenFrom, aisaacUpdateBetweenTo);
						specification = createCondition(specification, loggerSpecification, useLogicalOr);
						
					}else {
						Specification<T> logStoppageTime = (subroot, subquery, builder) -> criteriaBuilder
								.between(subroot.get("logStoppageTime"), aisaacUpdateBetweenFrom, aisaacUpdateBetweenTo);
						specification = createCondition(specification, logStoppageTime, useLogicalOr);
					}
					
				}
				
			} else if (aisaacUpdateBetweenFrom != null && aisaacUpdateBetweenTo != null
					&& EntityName.equals(LogStopageMaster.class) && CollectionUtils.isEmpty(deviceStatus)) {
				
				Specification<T> loggerSpecification = Specification.where(null);

				loggerSpecification = (subroot, subquery, builder) -> criteriaBuilder
						.between(subroot.get("latestRecivedTime"), aisaacUpdateBetweenFrom, aisaacUpdateBetweenTo);
				
				Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
						.between(subroot.get("logStoppageTime"), aisaacUpdateBetweenFrom, aisaacUpdateBetweenTo);
				
				loggerSpecification = createCondition(loggerSpecification, condition, true);
				
				specification = createCondition(specification, loggerSpecification, useLogicalOr);
			}
			//End of Device Status Condition
			
			if (lastEventReceivedFrom != null && lastEventReceivedTo != null
					&& !EntityName.equals(LogStoppageWhitelistDevices.class)) {
				Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
						.between(subroot.get("lastEventReceived"), lastEventReceivedFrom, lastEventReceivedTo);
				specification = createCondition(specification, condition, useLogicalOr);
			}
			if (createdFromDate != null && createdToDate != null && EntityName.equals(LogStopageMaster.class)) {
				Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
						.between(subroot.get("createdDate"), createdFromDate, createdToDate);
				specification = createCondition(specification, condition, useLogicalOr);
			}
			if (updatedFromDate != null && updatedToDate != null && EntityName.equals(LogStopageMaster.class)) {
				Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
						.between(subroot.get("updatedDate"), updatedFromDate, updatedToDate);
				specification = createCondition(specification, condition, useLogicalOr);
			}
			if (detectedFromDate != null && detectedToDate != null
					&& EntityName.equals(LogStoppageDetectedDevices.class)) {
				Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
						.between(subroot.get("detectedDate"), detectedFromDate, detectedToDate);
				specification = createCondition(specification, condition, useLogicalOr);
			}
			if (allowlistFromDate != null && allowlistToDate != null
					&& EntityName.equals(LogStoppageWhitelistDevices.class)) {
				Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
						.between(subroot.get("allowListedDate"), allowlistFromDate, allowlistToDate);
				specification = createCondition(specification, condition, useLogicalOr);
			}
			if (StringUtils.isNotBlank(description) && EntityName.equals(LogStoppageWhitelistDevices.class)) {
				Specification<T> condition = (subroot, subquery, builder) -> criteriaBuilder
						.like(subroot.get("description"), "%" + description + "%");
				specification = createCondition(specification, condition, useLogicalOr);
			}
			specification = createCondition(deleteSpecification, specification, false);
			
			
			//sorting logic if the column is not present in same table
			String sortColumn = "", sortOrderBy = "";
			List<Map<Order, String>> orderList = request.getOrder();

			if (orderList != null && orderList.size() > 0) {
				Map<Order, String> orderMap = orderList.get(0);
				sortColumn = orderMap.get(Order.name);
				sortOrderBy = orderMap.get(Order.dir);
			}
			if (AppConstants.ASSET_TYPE.equalsIgnoreCase(sortColumn)
					|| AppConstants.PRODUCT_MASTER_FIELDS.contains(sortColumn)
					|| AppConstants.PRODUCT_TYPE.equalsIgnoreCase(sortColumn)) {
				Subquery<String> subquery = query.subquery(String.class);
				if (AppConstants.ASSET_TYPE.equalsIgnoreCase(sortColumn)) {
					Root<SysParameterValue> subRoot = subquery.from(SysParameterValue.class);
					subquery.select(subRoot.get("paramValue"))
							.where(criteriaBuilder.equal(root.get("assetType"), subRoot.get("paramValueId")));

				} else if (AppConstants.PRODUCT_MASTER_FIELDS.contains(sortColumn)) {
					Root<ProductMaster> subRoot = subquery.from(ProductMaster.class);
					subquery.select(subRoot.get(sortColumn))
							.where(criteriaBuilder.equal(root.get("productId"), subRoot.get("productId")));

				} else if (AppConstants.PRODUCT_TYPE.equalsIgnoreCase(sortColumn)) {
					Root<ProductTypeMaster> subRoot = subquery.from(ProductTypeMaster.class);
					subquery.select(subRoot.get(sortColumn))
							.where(criteriaBuilder.equal(root.get("productTypeId"), subRoot.get("productTypeId")));
				}
				if (AppConstants.SORTING_ORDER_ASC.equalsIgnoreCase(sortOrderBy)) {
					query.orderBy(criteriaBuilder.asc(subquery));
				} else {
					query.orderBy(criteriaBuilder.desc(subquery));
				}
				criteriaBuilder.conjunction();
			} else if ((EntityName.equals(LogStoppageWhitelistDevices.class)
					|| EntityName.equals(LogStoppageDetectedDevices.class))
					&& AppConstants.TENANT_NAME.equalsIgnoreCase(sortColumn)) {
				Subquery<String> subquery = query.subquery(String.class);
				Root<Tenant> subRoot = subquery.from(Tenant.class);
				subquery.select(subRoot.get("tenantName"))
						.where(criteriaBuilder.equal(root.get("tenantId"), subRoot.get("tenantId")));
				if (AppConstants.SORTING_ORDER_ASC.equalsIgnoreCase(sortOrderBy)) {
					query.orderBy(criteriaBuilder.asc(subquery));
				} else {
					query.orderBy(criteriaBuilder.desc(subquery));
				}
				criteriaBuilder.conjunction();
			}
			return specification.toPredicate(root, query, criteriaBuilder);
		};
	}

	public static String getAuditTrailActionsForLogStoppageMaster(String actionType, int length) {
		if (length > 1) {
			switch (actionType) {
			case AppConstants.LOG_FLOW_MONITOR_USER_ACTION_RESUME_VALUE: {
				return AppConstants.LFD_ACTION_TYPE_BULK_RESUME;
			}
			case AppConstants.LOG_FLOW_MONITOR_USER_ACTION_SUPPRESS_VALUE: {
				return AppConstants.LFD_ACTION_TYPE_BULK_SUPRESS;
			}
			case AppConstants.LOG_FLOW_MONITOR_USER_ACTION_ENABLE_VALUE: {
				return AppConstants.LFD_ACTION_TYPE_BULK_ENABLE;
			}
			case AppConstants.LOG_FLOW_MONITOR_USER_ACTION_DISABLE_VALUE: {
				return AppConstants.LFD_ACTION_TYPE_BULK_DISABLE;
			}
			case AppConstants.LOG_FLOW_MONITOR_USER_ACTION_ADD_NOTE: {
				return AppConstants.LFD_ACTION_TYPE_NOTE_BULK;
			}
			case AppConstants.LOG_FLOW_MONITOR_USER_ACTION_DELETE_VALUE: {
				return AppConstants.LFD_ACTION_TYPE_DELETED_BULK;
			}
			default:
				return actionType;
			}
		} else {
			switch (actionType) {
			case AppConstants.LOG_FLOW_MONITOR_USER_ACTION_RESUME_VALUE: {
				return AppConstants.LFD_ACTION_TYPE_RESUMED;
			}
			case AppConstants.LOG_FLOW_MONITOR_USER_ACTION_SUPPRESS_VALUE: {
				return AppConstants.LFD_ACTION_TYPE_SUPPRESSED;
			}
			case AppConstants.LOG_FLOW_MONITOR_USER_ACTION_ADD_NOTE: {
				return AppConstants.LFD_ACTION_TYPE_NOTE;
			}
			default:
				return actionType;
			}
		}
	}

	public static String getAuditTrailActionsForDetectedDevices(Integer length, String userAction) {
		if (length > 1) {
			switch (userAction) {
			case AppConstants.LOG_FLOW_MONITOR_USER_ACTION_ADD_NOTE: {
				return AppConstants.LFD_ACTION_TYPE_NOTE_BULK_ADDED_TO_ADD_TO_LOG_FLOW_MONITORING;
			}
			case AppConstants.LOG_FLOW_MONITOR_USER_ACTION_DELETE_VALUE: {
				return AppConstants.LFD_ACTION_TYPE_DELETED_ADD_TO_LOG_FLOW_MONITORING_BULK;
			}
			default:
				return userAction;
			}
		} else {
			switch (userAction) {
			case AppConstants.LOG_FLOW_MONITOR_USER_ACTION_ADD_NOTE: {
				return AppConstants.LFD_ACTION_TYPE_NOTE_ADDED_TO_ADD_TO_LOG_FLOW_MONITORING;
			}
			case AppConstants.LOG_FLOW_MONITOR_USER_ACTION_DELETE_VALUE: {
				return AppConstants.LFD_ACTION_TYPE_DELETED_ADD_TO_LOG_FLOW_MONITORING;
			}
			default:
				return userAction;
			}
		}
	}

	private static <T> Specification<T> createCondition(Specification<T> specification, Specification<T> condition,
			boolean useLogicalOr) {
		if (useLogicalOr) {
			return specification.or(condition);
		} else {
			return specification.and(condition);
		}
	}

	public static <T> Specification<T> dynamicWhereConditionGenerator(String columnName, String value) {
		return (root, query, criteriaBuilder) -> {

			List<Predicate> predicates = new ArrayList<>();
			predicates.add(criteriaBuilder.like(root.get(columnName), '%' + value + '%'));
			predicates
					.add(criteriaBuilder.equal(root.get(AppConstants.LOG_STOPPAGE_MASTER_DELETE_COLUMN_NAME_IN_ENTITY),
							AppConstants.LOG_STOPPAGE_MASTER_DELETE_COLUMN_DEFAULT));
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}

	public static List<LogStoppageDetectedDevices> getDetectedDevicesEntityByUserAction(String actionType,
			List<LogStoppageDetectedDevices> data) {
		return data.stream().map(o -> mapLogStoppageDetectedDevicesFields(o, actionType)).collect(Collectors.toList());
	}

	private static LogStoppageDetectedDevices mapLogStoppageDetectedDevicesFields(
			LogStoppageDetectedDevices logDetectedDevices, String userAction) {
		switch (userAction) {
		case AppConstants.LOG_FLOW_MONITOR_USER_ACTION_ADD_NOTE: {
			logDetectedDevices.setUpdatedDate(LocalDateTime.now());
			break;
		}
		case AppConstants.LOG_FLOW_MONITOR_USER_ACTION_DELETE_VALUE: {
			logDetectedDevices.setDeleted(AppConstants.LOG_FLOW_MONITOR_USER_ACTION_DELETE);
			logDetectedDevices.setUpdatedDate(LocalDateTime.now());
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + userAction);
		}
		return logDetectedDevices;
	}

	public static LogStopageMaster getLogStopageMasterByUserAction(String note, String userAction,
			LogStopageMaster logStopageMaster, Long userId) {
		switch (userAction) {
		case AppConstants.LOG_FLOW_MONITOR_USER_ACTION_ENABLE_VALUE: {
			//45134 bug
//			if (logStopageMaster.isDisabled() && !logStopageMaster.isSuppressed()) {
			if (logStopageMaster.isDisabled()) {
				logStopageMaster.setDisabled(AppConstants.LOG_FLOW_MONITOR_USER_ACTION_ENABLE);
				break;
			} else
				return null;
		}
		case AppConstants.LOG_FLOW_MONITOR_USER_ACTION_DISABLE_VALUE: {
			if (!logStopageMaster.isDisabled()) {
				logStopageMaster.setDisabled(AppConstants.LOG_FLOW_MONITOR_USER_ACTION_DISABLE);
				break;
			} else
				return null;
		}
		case AppConstants.LOG_FLOW_MONITOR_USER_ACTION_RESUME_VALUE: {
			if (!logStopageMaster.isDisabled() && logStopageMaster.isSuppressed()) {
				logStopageMaster.setSuppressed(AppConstants.LOG_FLOW_MONITOR_USER_ACTION_RESUME);
				break;
			} else
				return null;
		}
		case AppConstants.LOG_FLOW_MONITOR_USER_ACTION_SUPPRESS_VALUE: {
			if (!logStopageMaster.isDisabled() && !logStopageMaster.isSuppressed()) {
				logStopageMaster.setSuppressed(AppConstants.LOG_FLOW_MONITOR_USER_ACTION_SUPPRESS);
				break;
			} else
				return null;
		}
		case AppConstants.LOG_FLOW_MONITOR_USER_ACTION_ADD_NOTE: {
			if (!logStopageMaster.isDisabled()) {
				break;
			} else
				return null;
		}
		case AppConstants.LOG_FLOW_MONITOR_USER_ACTION_DELETE_VALUE: {
			logStopageMaster.setDeleted(AppConstants.LOG_FLOW_MONITOR_USER_ACTION_DELETE);
			break;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + userAction);
		}

		logStopageMaster.setUpdatedBy(userId);
		logStopageMaster.setUpdatedDate(LocalDateTime.now());
		return logStopageMaster;
	}

	public static LogStopageMaster getLogStopageMasterFromRow(Row row, List<String> columns,
			Map<String, Long> tenateMap, Integer userId) {
		LogStopageMaster log = new LogStopageMaster();
		if (columns.indexOf("organization") > -1) {
			Cell cell = row.getCell(columns.indexOf("organization"), MissingCellPolicy.RETURN_NULL_AND_BLANK);
			Long customerId = tenateMap.get(ObjectUtils.isNotEmpty(cell) ? cell.toString() : null);
			if (ObjectUtils.isNotEmpty(customerId)) {
				log.setTenantName(ObjectUtils.isNotEmpty(cell) ? cell.toString() : log.getTenantName());
				log.setTenantId(customerId);
			}

		}

		if (columns.indexOf("asset type") > -1) {
			Cell cell = row.getCell(columns.indexOf("asset type"), MissingCellPolicy.RETURN_NULL_AND_BLANK);
			log.setAssetType(
					ObjectUtils.isNotEmpty(cell) ? Double.valueOf(cell.toString()).intValue() : log.getAssetType());

		}
		if (columns.indexOf("monitoring status") > -1) {
			Cell cell = row.getCell(columns.indexOf("monitoring status"), MissingCellPolicy.RETURN_NULL_AND_BLANK);
			log.setSuppressed(AppConstants.MONITOR_STATUS_SUPPRESSED
					.equalsIgnoreCase(ObjectUtils.isNotEmpty(cell) ? cell.toString() : ""));
			log.setDisabled(AppConstants.MONITOR_STATUS_DISABLED
					.equalsIgnoreCase(ObjectUtils.isNotEmpty(cell) ? cell.toString() : ""));

		}
		if (columns.indexOf("product ip") > -1) {
			Cell cell = row.getCell(columns.indexOf("product ip"), MissingCellPolicy.RETURN_NULL_AND_BLANK);
			log.setProductIP(ObjectUtils.isNotEmpty(cell) ? cell.toString() : log.getProductIP());

		}
		if (columns.indexOf("product hostname") > -1) {
			Cell cell = row.getCell(columns.indexOf("product hostname"), MissingCellPolicy.RETURN_NULL_AND_BLANK);
			log.setProductHostName(ObjectUtils.isNotEmpty(cell) ? cell.toString() : log.getProductHostName());

		}
		if (columns.indexOf("product vendor") > -1) {
			Cell cell = row.getCell(columns.indexOf("product vendor"), MissingCellPolicy.RETURN_NULL_AND_BLANK);
			log.setProductVendor(ObjectUtils.isNotEmpty(cell) ? cell.toString() : log.getProductVendor());

		}
		if (columns.indexOf("product name") > -1) {
			Cell cell = row.getCell(columns.indexOf("product name"), MissingCellPolicy.RETURN_NULL_AND_BLANK);
			log.setProductName(ObjectUtils.isNotEmpty(cell) ? cell.toString() : log.getProductName());

		}
		if (columns.indexOf("cloud resource id") > -1) {
			Cell cell = row.getCell(columns.indexOf("cloud resource id"), MissingCellPolicy.RETURN_NULL_AND_BLANK);
			log.setCloudResourceID(ObjectUtils.isNotEmpty(cell) ? cell.toString() : log.getCloudResourceID());

		}
		if (columns.indexOf("severity") > -1) {
			Cell cell = row.getCell(columns.indexOf("severity"), MissingCellPolicy.RETURN_NULL_AND_BLANK);
			log.setSeverity(ObjectUtils.isNotEmpty(cell) ? cell.toString() : log.getSeverity());

		}
		if (columns.indexOf("collector ip") > -1) {
			Cell cell = row.getCell(columns.indexOf("collector ip"), MissingCellPolicy.RETURN_NULL_AND_BLANK);
			log.setCollectorAddress(ObjectUtils.isNotEmpty(cell) ? cell.toString() : log.getCollectorAddress());

		}
		if (columns.indexOf("collector hostname") > -1) {
			Cell cell = row.getCell(columns.indexOf("collector hostname"), MissingCellPolicy.RETURN_NULL_AND_BLANK);
			log.setCollectorHostName(ObjectUtils.isNotEmpty(cell) ? cell.toString() : log.getCollectorHostName());

		}
		if (columns.indexOf("log stoppage threshold") > -1) {
			Cell cell = row.getCell(columns.indexOf("log stoppage threshold"), MissingCellPolicy.RETURN_NULL_AND_BLANK);
			log.setLogStopageThresholdTime(ObjectUtils.isNotEmpty(cell) ? Double.valueOf(cell.toString()).intValue()
					: log.getLogStopageThresholdTime());
			CustomTimeFields customTimeFields = new CustomTimeFields();
			customTimeFields.setIscustom(false);
			customTimeFields.setValue(cell.toString());
			log.setLogStoppageThresholdJson(LogFlowDashboardUtils.CustomTimeFieldsEntityToString(customTimeFields));
		}
		if (columns.indexOf("email notification frequency") > -1) {
			Cell cell = row.getCell(columns.indexOf("email notification frequency"),
					MissingCellPolicy.RETURN_NULL_AND_BLANK);
			log.setEmailAlertFrequency(ObjectUtils.isNotEmpty(cell) ? Double.valueOf(cell.toString()).intValue()
					: log.getEmailAlertFrequency());
			CustomTimeFields customTimeFields = new CustomTimeFields();
			customTimeFields.setIscustom(false);
			customTimeFields.setValue(cell.toString());
			log.setEmailNotificationFrequencyJson(
					LogFlowDashboardUtils.CustomTimeFieldsEntityToString(customTimeFields));

		}
		if (columns.indexOf("send email notification for log stoppage") > -1) {
			Cell cell = row.getCell(columns.indexOf("send email notification for log stoppage"),
					MissingCellPolicy.RETURN_NULL_AND_BLANK);
			log.setSendMail(AppConstants.IS_MAIL_SEND_DEFALUT_VALUE
					.equalsIgnoreCase(ObjectUtils.isNotEmpty(cell) ? cell.toString() : ""));

		}
		if (columns.indexOf("email address (to email)") > -1) {
			Cell cell = row.getCell(columns.indexOf("email address (to email)"),
					MissingCellPolicy.RETURN_NULL_AND_BLANK);
			log.setToEmailAddress(ObjectUtils.isNotEmpty(cell) ? cell.toString() : null);

		}
		if (columns.indexOf("email address (cc email)") > -1) {
			Cell cell = row.getCell(columns.indexOf("email address (cc email)"),
					MissingCellPolicy.RETURN_NULL_AND_BLANK);
			log.setCcEmailAddress(ObjectUtils.isNotEmpty(cell) ? cell.toString() : null);

		}
		if (columns.indexOf("note") > -1) {
			Cell cell = row.getCell(columns.indexOf("note"), MissingCellPolicy.RETURN_NULL_AND_BLANK);
			log.setNote(ObjectUtils.isNotEmpty(cell) ? cell.toString() : null);
		}
		if (columns.indexOf("mdr scanner code") > -1) {
			Cell cell = row.getCell(columns.indexOf("mdr scanner code"), MissingCellPolicy.RETURN_NULL_AND_BLANK);
			log.setMdrScannerCode(ObjectUtils.isNotEmpty(cell) ? cell.toString() : null);
		}
		log.setCreatedDate(LocalDateTime.now());
		log.setCreatedBy(userId);
		log.setDeviceStatus(0);

		if ((ObjectUtils.isEmpty(log.getTenantName()) && ObjectUtils.isEmpty(log.getAssetType())
				&& ObjectUtils.isEmpty(log.isSuppressed()) && ObjectUtils.isEmpty(log.isDisabled())
				&& StringUtils.isBlank(log.getProductIP()) && StringUtils.isBlank(log.getProductHostName())
				&& StringUtils.isBlank(log.getProductVendor()) && StringUtils.isBlank(log.getProductName())
				&& StringUtils.isBlank(log.getCloudResourceID()) && StringUtils.isBlank(log.getSeverity())
				&& StringUtils.isBlank(log.getCollectorAddress()) && StringUtils.isBlank(log.getCollectorHostName())
				&& ObjectUtils.isEmpty(log.getEmailAlertFrequency())
				&& ObjectUtils.isEmpty(log.getEmailThresholdReached()) && StringUtils.isBlank(log.getToEmailAddress())
				&& StringUtils.isBlank(log.getCcEmailAddress())) || ObjectUtils.isEmpty(log.getTenantId())) {
			log = null;
		}
		return log;
	}

	public static NotesFormatDto getNotesFormatDtoByNote(Notes note, Map<Long, String> createdByAndIdMap) {
		return NotesFormatDto.builder().note(note.getNote())
				.createdDate(LogFlowDashboardUtils.getLocalDateTimeInMilliSec(note.getCreatedDate()))
				.createdBy(createdByAndIdMap.get(note.getCreatedBy() != null ? note.getCreatedBy().longValue() : 0))
				.build();
	}

	public static String getUserCustomizationTabSectionName(String tabName) {
		switch (tabName) {
		case AppConstants.LOG_FLOW_MONITORING_TAB: {
			return AppConstants.LOGFLOW_LISTING;
		}
		case AppConstants.DETECTED_DEVICES_TAB: {
			return AppConstants.ADDTOLOGFLOW_LISTING;
		}
		case AppConstants.ALLOWLIST_TAB: {
			return AppConstants.ALLOWLIST_LISTING;
		}
		default:
			throw new IllegalArgumentException("Unexpected value: " + tabName);
		}
	}

	public static <T> Specification<T> getCustomizationListWhereCondition(Integer userId, String tabName) {
		Specification<T> mainCondition = (root, query, criteriaBuilder) -> {

			String customeTabName = getUserCustomizationTabSectionName(tabName);

			List<Predicate> predicates = new ArrayList<>();
			predicates.add(criteriaBuilder.equal(root.get("moduleName"), AppConstants.LOG_FLOW_DASHBOARD_NAME));
			predicates.add(criteriaBuilder.equal(root.get("sectionName"), customeTabName));
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
		Specification<T> subCondition = (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(criteriaBuilder.isNull(root.get("createdBy")));
			predicates.add(criteriaBuilder.equal(root.get("createdBy"), userId));
			query.orderBy(criteriaBuilder.desc(root.get("createdBy")));
			return criteriaBuilder.or(predicates.toArray(new Predicate[0]));
		};
		return mainCondition = mainCondition.and(subCondition);
	}

	public static String getAuditTrailJsonResponseByLogStopageMaster(String action, LogStopageMaster newLog,
			Map<Long, String> tenantMap, Map<Long, String> assetTypeMap, Map<Long, ProductMaster> productMasterMap,
			AuditTrailDetailsOnly auditTrailDetailsOnly, Map<String, String> notesMap,
			LogStopageMaster oldLogStopageMaster, String oldNotes) {
		LogStopageAuditTrailJsonResponse detailsJson = new LogStopageAuditTrailJsonResponse();

		ProductMaster productMaster = productMasterMap.getOrDefault(newLog.getProductId().longValue(),
				new ProductMaster());
		LogStopageAuditTrailDto auditTrailDto = null;

		boolean isExist = false;
		if (Objects.nonNull(auditTrailDetailsOnly) && Objects.nonNull(auditTrailDetailsOnly.details())) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				detailsJson = mapper.readValue(auditTrailDetailsOnly.details(), LogStopageAuditTrailJsonResponse.class);
			} catch (JsonParseException | JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			auditTrailDto = detailsJson.getData();
		} else {
			isExist = true;
		}

		if (AppConstants.LFD_ACTION_TYPE_ADDED.equalsIgnoreCase(action)
				|| AppConstants.LFD_ACTION_TYPE_ADDED_LOG_FLOW_MONITORING.equalsIgnoreCase(action)
				|| AppConstants.LFD_ACTION_TYPE_ADDED_LOG_FLOW_MONITORING_BULK.equalsIgnoreCase(action)
				|| AppConstants.LFD_ACTION_TYPE_ADDED_BULK.equalsIgnoreCase(action)
				|| AppConstants.LOG_FLOW_MONITOR_USER_ACTION_DELETE_VALUE.equalsIgnoreCase(action)
				|| AppConstants.LFD_ACTION_TYPE_DELETED_BULK.equalsIgnoreCase(action)
				|| AppConstants.LFD_ACTION_TYPE_NOTE.equalsIgnoreCase(action)
				|| AppConstants.LFD_ACTION_TYPE_NOTE_BULK.equalsIgnoreCase(action)) {
			auditTrailDto = new LogStopageAuditTrailDto();
			AuditTrailNewOldValueDto auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.ORGNIZATION);
			auditTrailNewOldValueDto.setNewValue(tenantMap.get(newLog.getTenantId()));
			auditTrailDto.setCustomerName(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.ASSET_TYPE);
			auditTrailNewOldValueDto.setNewValue(assetTypeMap.get(newLog.getAssetType().longValue()));
			auditTrailDto.setAssetType(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.MONITORING_STATUS);
			auditTrailNewOldValueDto
					.setNewValue(LogFlowDashboardUtils.decideMonitorStatus(newLog.isSuppressed(), newLog.isDisabled()));
			auditTrailDto.setMonitorStatus(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.PRODUCT_VENDOR);
			auditTrailNewOldValueDto.setNewValue(productMaster.getProductVendor());
			auditTrailDto.setDeviceVendor(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.PRODUCT_NAME);
			auditTrailNewOldValueDto.setNewValue(productMaster.getProductName());
			auditTrailDto.setDeviceProduct(auditTrailNewOldValueDto);
			;

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.PRODUCT_TYPE);
			auditTrailNewOldValueDto.setNewValue(productMaster.getProductType());
			auditTrailDto.setDeviceGroup(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.PRODUCT_IP);
			auditTrailNewOldValueDto.setNewValue(newLog.getProductIP());
			auditTrailDto.setDeviceAddress(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.PRODUCT_HOST_NAME);
			auditTrailNewOldValueDto.setNewValue(newLog.getProductHostName());
			auditTrailDto.setDeviceHostName(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.CLOUD_RESOURCE_ID);
			auditTrailNewOldValueDto.setNewValue(newLog.getCloudResourceID());
			auditTrailDto.setCloudResourceID(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.SEVERITY);
			auditTrailNewOldValueDto.setNewValue(newLog.getSeverity());
			auditTrailDto.setSeverity(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.COLLECTOR_IP);
			auditTrailNewOldValueDto.setNewValue(newLog.getCollectorAddress());
			auditTrailDto.setCollectorIp(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.COLLECTOR_HOSTNAME);
			auditTrailNewOldValueDto.setNewValue(newLog.getCollectorHostName());
			auditTrailDto.setCollectorHostName(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.LOG_STOPPAGE_THRESHOLD);
			auditTrailNewOldValueDto.setNewValue(String.valueOf(newLog.getLogStopageThresholdTime()));
			auditTrailDto.setStoppageThreshold(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.EMAIL_NOTIFICATION_FREQUENCY);
			auditTrailNewOldValueDto.setNewValue(String.valueOf(newLog.getEmailAlertFrequency()));
			auditTrailDto.setEmailNotificationFrequency(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.TO_EMAIL_ADDRESS);
			auditTrailNewOldValueDto.setNewValue(newLog.getToEmailAddress());
			auditTrailDto.setToEmailAddress(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.CC_EMAIL_ADDRESS);
			auditTrailNewOldValueDto.setNewValue(newLog.getCcEmailAddress());
			auditTrailDto.setCcEmailAddress(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.IS_MAIL_SEND);
			auditTrailNewOldValueDto.setNewValue((Objects.nonNull(newLog.isSendMail()) && newLog.isSendMail())
					? AuditTrailLableConstans.ENABLE_STRING_VALUE
					: AuditTrailLableConstans.DISABLED_STRING_VALUE);
			auditTrailDto.setIsMailSend(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.NOTE);
			auditTrailNewOldValueDto.setNewValue(notesMap.get(newLog.getProductId() + newLog.getTenantId()
					+ newLog.getProductIP() + newLog.getProductHostName()));
			auditTrailDto.setNote(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.PRODUCT_ID);
			auditTrailNewOldValueDto.setNewValue(String.valueOf(newLog.getProductId()));
			auditTrailDto.setProductId(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.MDR_SCANNER_CODE);
			auditTrailNewOldValueDto.setNewValue(newLog.getMdrScannerCode());
			auditTrailDto.setMdrScannerCode(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.ASSET_ENTRY_METHOD_ID);
			auditTrailNewOldValueDto.setNewValue(String.valueOf(newLog.getAssetEntryMethod()));
			auditTrailDto.setAssetEntryMethodId(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.ASSET_ID);
			auditTrailNewOldValueDto.setNewValue(String.valueOf(newLog.getAssetId()));
			auditTrailDto.setAssetId(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.LAST_EVENT_RECEIVED);
			auditTrailNewOldValueDto.setNewValue(Objects.nonNull(newLog.getLastEventReceived())
					? newLog.getLastEventReceived().format(AuditTrailLableConstans.formatter)
					: null);
			auditTrailDto.setLastEventReceived(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.TENANT_ID);
			auditTrailNewOldValueDto.setNewValue(String.valueOf(newLog.getTenantId()));
			auditTrailDto.setCustomerId(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.DEVICE_STATUS);
			auditTrailNewOldValueDto.setNewValue(String.valueOf(newLog.getDeviceStatus()));
			auditTrailDto.setDeviceStatus(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.LOG_STOPPAGE_TIME);
			auditTrailNewOldValueDto.setNewValue(Objects.nonNull(newLog.getLogStoppageTime())
					? newLog.getLogStoppageTime().format(AuditTrailLableConstans.formatter)
					: null);
			auditTrailDto.setLogStopageTime(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.LOG_RECIEVED_TIME);
			auditTrailNewOldValueDto.setNewValue(Objects.nonNull(newLog.getLatestRecivedTime())
					? newLog.getLatestRecivedTime().format(AuditTrailLableConstans.formatter)
					: null);
			auditTrailDto.setLogRecieveTime(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.DISABLED);
			auditTrailNewOldValueDto.setNewValue((Objects.nonNull(newLog.isDisabled()) && newLog.isDisabled())
					? AuditTrailLableConstans.BOOLEN_TRUE_STRING_VALUE
					: AuditTrailLableConstans.BOOLEN_FALSE_STRING_VALUE);
			auditTrailDto.setDisabled(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.SUPPRESSED);
			auditTrailNewOldValueDto.setNewValue((Objects.nonNull(newLog.isSuppressed()) && newLog.isSuppressed())
					? AuditTrailLableConstans.BOOLEN_TRUE_STRING_VALUE
					: AuditTrailLableConstans.BOOLEN_FALSE_STRING_VALUE);
			auditTrailDto.setSuppressed(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.LOGGER_STATUS);
			auditTrailNewOldValueDto.setNewValue((Objects.nonNull(newLog.getLoggerStatus()) && newLog.getLoggerStatus())
					? AuditTrailLableConstans.BOOLEN_TRUE_STRING_VALUE
					: AuditTrailLableConstans.BOOLEN_FALSE_STRING_VALUE);
			auditTrailDto.setLoggerStatus(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.LOGGER_DATE);
			auditTrailNewOldValueDto.setNewValue(Objects.nonNull(newLog.getLoggerDate())
					? newLog.getLoggerDate().format(AuditTrailLableConstans.formatter)
					: null);
			auditTrailDto.setLoggerDate(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.DELETED);
			auditTrailNewOldValueDto.setNewValue(newLog.isDeleted() ? AuditTrailLableConstans.BOOLEN_TRUE_STRING_VALUE
					: AuditTrailLableConstans.BOOLEN_FALSE_STRING_VALUE);
			auditTrailDto.setDeleted(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.CREATED_DATE);
			auditTrailNewOldValueDto.setNewValue(Objects.nonNull(newLog.getCreatedDate())
					? newLog.getCreatedDate().format(AuditTrailLableConstans.formatter)
					: null);
			auditTrailDto.setCreatedDate(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.UPDATED_DATE);
			auditTrailNewOldValueDto.setNewValue(Objects.nonNull(newLog.getUpdatedDate())
					? newLog.getUpdatedDate().format(AuditTrailLableConstans.formatter)
					: null);
			auditTrailDto.setUpdatedDate(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.CREATED_BY);
			auditTrailNewOldValueDto.setNewValue(String.valueOf(newLog.getCreatedBy()));
			auditTrailDto.setCreatedId(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.UPDATED_BY);
			auditTrailNewOldValueDto.setNewValue(String.valueOf(newLog.getUpdatedBy()));
			auditTrailDto.setUpdatedId(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.EMAIL_STATUS);
			auditTrailNewOldValueDto.setNewValue(newLog.getEmailStatus());
			auditTrailDto.setEmailStatus(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.EMAIL_THREASHOLD_REACHED);
			auditTrailNewOldValueDto.setNewValue(newLog.getEmailThresholdReached());
			auditTrailDto.setEmailThreasholdReached(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.EMAIL_TIME);
			auditTrailNewOldValueDto.setNewValue(Objects.nonNull(newLog.getEmailTime())
					? newLog.getEmailTime().format(AuditTrailLableConstans.formatter)
					: null);
			auditTrailDto.setEmailTime(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.DEVICE_RECEIPT_TIME);
			auditTrailNewOldValueDto.setNewValue(Objects.nonNull(newLog.getDeviceReceiptTime())
					? newLog.getDeviceReceiptTime().format(AuditTrailLableConstans.formatter)
					: null);
			auditTrailDto.setDeviceReceiptTime(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.SEND_EMAIL_ALERT_AUDIT);
			auditTrailNewOldValueDto.setNewValue(newLog.getSendToEmailAlertAudit());
			auditTrailDto.setSendEmailAlertToAudit(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.LOG_STOPPAGE_THRESHOLD_JSON);
			auditTrailNewOldValueDto.setNewValue(newLog.getLogStoppageThresholdJson());
			auditTrailDto.setLogStoppageThresholdJson(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.EMAIL_NOTIFICATION_FREQUENCY_JSON);
			auditTrailNewOldValueDto.setNewValue(newLog.getEmailNotificationFrequencyJson());
			auditTrailDto.setEmailNotificationFrequencyJson(auditTrailNewOldValueDto);
			
			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.PRODUCT_TYPE_ID);
			if(Objects.nonNull(newLog.getProductTypeId()))
					auditTrailNewOldValueDto.setNewValue(newLog.getProductTypeId().toString());
			auditTrailDto.setProductTypeId(auditTrailNewOldValueDto);

		} else if (AppConstants.LFD_ACTION_TYPE_EDITED.equalsIgnoreCase(action)) {
			if (isExist) {
				auditTrailDto = new LogStopageAuditTrailDto();
			}

			AuditTrailNewOldValueDto organization = Objects.nonNull(auditTrailDto.getCustomerName())
					? auditTrailDto.getCustomerName()
					: new AuditTrailNewOldValueDto();
			organization.setLabel(AuditTrailLableConstans.ORGNIZATION);
			if (Objects.nonNull(organization.getNewValue())) {
				organization.setOldValue(organization.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStopageMaster)) {
				organization.setOldValue(tenantMap.get(oldLogStopageMaster.getTenantId()));
			}
			organization.setNewValue(tenantMap.get(newLog.getTenantId()));
			if (Objects.nonNull(organization.getNewValue()))
				organization.setEdited(!organization.getNewValue().equals(organization.getOldValue()));
			else
				organization.setEdited(false);
			auditTrailDto.setCustomerName(organization);

			AuditTrailNewOldValueDto assetType = Objects.nonNull(auditTrailDto.getAssetType())
					? auditTrailDto.getAssetType()
					: new AuditTrailNewOldValueDto();
			assetType.setLabel(AuditTrailLableConstans.ASSET_TYPE);
			if (Objects.nonNull(assetType.getNewValue())) {
				assetType.setOldValue(assetType.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStopageMaster)
					&& Objects.nonNull(oldLogStopageMaster.getAssetType())) {
				assetType.setOldValue(assetTypeMap.get(oldLogStopageMaster.getAssetType().longValue()));
			}
			if (Objects.nonNull(newLog.getAssetType()))
				assetType.setNewValue(assetTypeMap.get(newLog.getAssetType().longValue()));
			if (Objects.nonNull(assetType.getNewValue()))
				assetType.setEdited(!assetType.getNewValue().equals(assetType.getOldValue()));
			else
				assetType.setEdited(false);
			auditTrailDto.setAssetType(assetType);

			AuditTrailNewOldValueDto monitorStatus = Objects.nonNull(auditTrailDto.getMonitorStatus())
					? auditTrailDto.getMonitorStatus()
					: new AuditTrailNewOldValueDto();
			monitorStatus.setLabel(AuditTrailLableConstans.MONITORING_STATUS);
			if (Objects.nonNull(monitorStatus.getNewValue())) {
				monitorStatus.setOldValue(monitorStatus.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStopageMaster)) {
				monitorStatus.setOldValue(LogFlowDashboardUtils.decideMonitorStatus(oldLogStopageMaster.isSuppressed(),
						oldLogStopageMaster.isDisabled()));
			}
			monitorStatus
					.setNewValue(LogFlowDashboardUtils.decideMonitorStatus(newLog.isSuppressed(), newLog.isDisabled()));
			if (Objects.nonNull(monitorStatus.getNewValue()))
				monitorStatus.setEdited(!monitorStatus.getNewValue().equals(monitorStatus.getOldValue()));
			else
				monitorStatus.setEdited(false);
			auditTrailDto.setMonitorStatus(monitorStatus);

			AuditTrailNewOldValueDto productVendor = Objects.nonNull(auditTrailDto.getDeviceVendor())
					? auditTrailDto.getDeviceVendor()
					: new AuditTrailNewOldValueDto();
			productVendor.setLabel(AuditTrailLableConstans.PRODUCT_VENDOR);
			if (Objects.nonNull(productVendor.getNewValue())) {
				productVendor.setOldValue(productVendor.getNewValue());
			}
			if (isExist && Objects.nonNull(newLog)) {
				productVendor.setOldValue(productMaster.getProductVendor());
			}
			productVendor.setNewValue(productMaster.getProductVendor());
			if (Objects.nonNull(productVendor.getNewValue()))
				productVendor.setEdited(!productVendor.getNewValue().equals(productVendor.getOldValue()));
			else
				productVendor.setEdited(false);
			auditTrailDto.setDeviceVendor(productVendor);

			AuditTrailNewOldValueDto productName = Objects.nonNull(auditTrailDto.getDeviceProduct())
					? auditTrailDto.getDeviceProduct()
					: new AuditTrailNewOldValueDto();
			productName.setLabel(AuditTrailLableConstans.PRODUCT_NAME);
			if (Objects.nonNull(productName.getNewValue())) {
				productName.setOldValue(productName.getNewValue());
			}
			if (isExist && Objects.nonNull(newLog)) {
				productName.setOldValue(productMaster.getProductName());
			}
			productName.setNewValue(productMaster.getProductName());
			if (Objects.nonNull(productName.getNewValue()))
				productName.setEdited(!productName.getNewValue().equals(productName.getOldValue()));
			else
				productName.setEdited(false);
			auditTrailDto.setDeviceProduct(productName);

			AuditTrailNewOldValueDto productType = Objects.nonNull(auditTrailDto.getDeviceGroup())
					? auditTrailDto.getDeviceGroup()
					: new AuditTrailNewOldValueDto();
			productType.setLabel(AuditTrailLableConstans.PRODUCT_TYPE);
			if (Objects.nonNull(productType.getNewValue())) {
				productType.setOldValue(productType.getNewValue());
			}
			if (isExist && Objects.nonNull(newLog)) {
				productType.setOldValue(productMaster.getProductType());
			}
			productType.setNewValue(productMaster.getProductType());
			if (Objects.nonNull(productType.getNewValue()))
				productType.setEdited(!productType.getNewValue().equals(productType.getOldValue()));
			else
				productType.setEdited(false);
			auditTrailDto.setDeviceGroup(productType);

			AuditTrailNewOldValueDto productIp = Objects.nonNull(auditTrailDto.getDeviceAddress())
					? auditTrailDto.getDeviceAddress()
					: new AuditTrailNewOldValueDto();
			productIp.setLabel(AuditTrailLableConstans.PRODUCT_IP);
			if (Objects.nonNull(productIp.getNewValue())) {
				productIp.setOldValue(productIp.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStopageMaster)) {
				productIp.setOldValue(oldLogStopageMaster.getProductIP());
			}
			productIp.setNewValue(newLog.getProductIP());
			if (Objects.nonNull(productIp.getNewValue()))
				productIp.setEdited(!productIp.getNewValue().equals(productIp.getOldValue()));
			else
				productIp.setEdited(false);
			auditTrailDto.setDeviceAddress(productIp);

			AuditTrailNewOldValueDto productHostName = Objects.nonNull(auditTrailDto.getDeviceHostName())
					? auditTrailDto.getDeviceHostName()
					: new AuditTrailNewOldValueDto();
			productHostName.setLabel(AuditTrailLableConstans.PRODUCT_HOST_NAME);
			if (Objects.nonNull(productHostName.getNewValue())) {
				productHostName.setOldValue(productHostName.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStopageMaster)) {
				productHostName.setOldValue(oldLogStopageMaster.getProductHostName());
			}
			productHostName.setNewValue(newLog.getProductHostName());
			if (Objects.nonNull(productHostName.getNewValue()))
				productHostName.setEdited(!productHostName.getNewValue().equals(productHostName.getOldValue()));
			else
				productHostName.setEdited(false);
			auditTrailDto.setDeviceHostName(productHostName);

			AuditTrailNewOldValueDto cloudResourceId = Objects.nonNull(auditTrailDto.getCloudResourceID())
					? auditTrailDto.getCloudResourceID()
					: new AuditTrailNewOldValueDto();
			cloudResourceId.setLabel(AuditTrailLableConstans.CLOUD_RESOURCE_ID);
			if (Objects.nonNull(cloudResourceId.getNewValue())) {
				cloudResourceId.setOldValue(cloudResourceId.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStopageMaster)) {
				cloudResourceId.setOldValue(oldLogStopageMaster.getCloudResourceID());
			}
			cloudResourceId.setNewValue(newLog.getCloudResourceID());
			if (Objects.nonNull(cloudResourceId.getNewValue()))
				cloudResourceId.setEdited(!cloudResourceId.getNewValue().equals(cloudResourceId.getOldValue()));
			else
				cloudResourceId.setEdited(false);
			auditTrailDto.setCloudResourceID(cloudResourceId);

			AuditTrailNewOldValueDto severity = Objects.nonNull(auditTrailDto.getSeverity())
					? auditTrailDto.getSeverity()
					: new AuditTrailNewOldValueDto();
			severity.setLabel(AuditTrailLableConstans.SEVERITY);
			if (Objects.nonNull(severity.getNewValue())) {
				severity.setOldValue(severity.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStopageMaster)) {
				severity.setOldValue(oldLogStopageMaster.getSeverity());
			}
			severity.setNewValue(newLog.getSeverity());
			if (Objects.nonNull(severity.getNewValue()))
				severity.setEdited(!severity.getNewValue().equals(severity.getOldValue()));
			else
				severity.setEdited(false);
			auditTrailDto.setSeverity(severity);

			AuditTrailNewOldValueDto collectorIP = Objects.nonNull(auditTrailDto.getCollectorIp())
					? auditTrailDto.getCollectorIp()
					: new AuditTrailNewOldValueDto();
			collectorIP.setLabel(AuditTrailLableConstans.COLLECTOR_IP);
			if (Objects.nonNull(collectorIP.getNewValue())) {
				collectorIP.setOldValue(collectorIP.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStopageMaster)) {
				collectorIP.setOldValue(oldLogStopageMaster.getCollectorAddress());
			}
			collectorIP.setNewValue(newLog.getCollectorAddress());
			if (Objects.nonNull(collectorIP.getNewValue()))
				collectorIP.setEdited(!collectorIP.getNewValue().equals(collectorIP.getOldValue()));
			else
				collectorIP.setEdited(false);
			auditTrailDto.setCollectorIp(collectorIP);

			AuditTrailNewOldValueDto collectorHostName = Objects.nonNull(auditTrailDto.getCollectorHostName())
					? auditTrailDto.getCollectorHostName()
					: new AuditTrailNewOldValueDto();
			collectorHostName.setLabel(AuditTrailLableConstans.COLLECTOR_HOSTNAME);
			if (Objects.nonNull(collectorHostName.getNewValue())) {
				collectorHostName.setOldValue(collectorHostName.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStopageMaster)) {
				collectorHostName.setOldValue(oldLogStopageMaster.getCollectorHostName());
			}
			collectorHostName.setNewValue(newLog.getCollectorHostName());
			if (Objects.nonNull(collectorHostName.getNewValue()))
				collectorHostName.setEdited(!collectorHostName.getNewValue().equals(collectorHostName.getOldValue()));
			else
				collectorHostName.setEdited(false);
			auditTrailDto.setCollectorHostName(collectorHostName);

			AuditTrailNewOldValueDto logStopageThresholdTime = Objects.nonNull(auditTrailDto.getStoppageThreshold())
					? auditTrailDto.getStoppageThreshold()
					: new AuditTrailNewOldValueDto();
			logStopageThresholdTime.setLabel(AuditTrailLableConstans.LOG_STOPPAGE_THRESHOLD);
			if (Objects.nonNull(logStopageThresholdTime.getNewValue())) {
				logStopageThresholdTime.setOldValue(logStopageThresholdTime.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStopageMaster)) {
				logStopageThresholdTime.setOldValue(String.valueOf(oldLogStopageMaster.getLogStopageThresholdTime()));
			}
			logStopageThresholdTime.setNewValue(String.valueOf(newLog.getLogStopageThresholdTime()));
			if (Objects.nonNull(logStopageThresholdTime.getNewValue()))
				logStopageThresholdTime.setEdited(
						!logStopageThresholdTime.getNewValue().equals(logStopageThresholdTime.getOldValue()));
			else
				logStopageThresholdTime.setEdited(false);
			auditTrailDto.setStoppageThreshold(logStopageThresholdTime);

			AuditTrailNewOldValueDto emailNotificationFrequency = Objects.nonNull(
					auditTrailDto.getEmailNotificationFrequency()) ? auditTrailDto.getEmailNotificationFrequency()
							: new AuditTrailNewOldValueDto();
			emailNotificationFrequency.setLabel(AuditTrailLableConstans.EMAIL_NOTIFICATION_FREQUENCY);
			if (Objects.nonNull(emailNotificationFrequency.getNewValue())) {
				emailNotificationFrequency.setOldValue(emailNotificationFrequency.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStopageMaster)) {
				emailNotificationFrequency.setOldValue(String.valueOf(oldLogStopageMaster.getEmailAlertFrequency()));
			}
			emailNotificationFrequency.setNewValue(String.valueOf(newLog.getEmailAlertFrequency()));
			if (Objects.nonNull(emailNotificationFrequency.getNewValue()))
				emailNotificationFrequency.setEdited(
						!emailNotificationFrequency.getNewValue().equals(emailNotificationFrequency.getOldValue()));
			else
				emailNotificationFrequency.setEdited(false);
			auditTrailDto.setEmailNotificationFrequency(emailNotificationFrequency);

			AuditTrailNewOldValueDto toEmail = Objects.nonNull(auditTrailDto.getToEmailAddress())
					? auditTrailDto.getToEmailAddress()
					: new AuditTrailNewOldValueDto();
			toEmail.setLabel(AuditTrailLableConstans.TO_EMAIL_ADDRESS);
			if (Objects.nonNull(toEmail.getNewValue())) {
				toEmail.setOldValue(toEmail.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStopageMaster)) {
				toEmail.setOldValue(oldLogStopageMaster.getToEmailAddress());
			}
			toEmail.setNewValue(newLog.getToEmailAddress());
			if (Objects.nonNull(toEmail.getNewValue()))
				toEmail.setEdited(!toEmail.getNewValue().equals(toEmail.getOldValue()));
			else
				toEmail.setEdited(false);
			auditTrailDto.setToEmailAddress(toEmail);

			AuditTrailNewOldValueDto ccEmail = Objects.nonNull(auditTrailDto.getCcEmailAddress())
					? auditTrailDto.getCcEmailAddress()
					: new AuditTrailNewOldValueDto();
			ccEmail.setLabel(AuditTrailLableConstans.CC_EMAIL_ADDRESS);
			if (Objects.nonNull(ccEmail.getNewValue())) {
				ccEmail.setOldValue(ccEmail.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStopageMaster)) {
				ccEmail.setOldValue(oldLogStopageMaster.getCcEmailAddress());
			}
			ccEmail.setNewValue(newLog.getCcEmailAddress());
			if (Objects.nonNull(ccEmail.getNewValue()))
				ccEmail.setEdited(!ccEmail.getNewValue().equals(ccEmail.getOldValue()));
			else
				ccEmail.setEdited(false);
			auditTrailDto.setCcEmailAddress(ccEmail);

			AuditTrailNewOldValueDto mailSend = Objects.nonNull(auditTrailDto.getIsMailSend())
					? auditTrailDto.getIsMailSend()
					: new AuditTrailNewOldValueDto();
			mailSend.setLabel(AuditTrailLableConstans.IS_MAIL_SEND);
			if (Objects.nonNull(mailSend.getNewValue())) {
				mailSend.setOldValue(mailSend.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStopageMaster)) {
				mailSend.setOldValue(
						String.valueOf(oldLogStopageMaster.isSendMail() ? AuditTrailLableConstans.ENABLE_STRING_VALUE
								: AuditTrailLableConstans.DISABLED_STRING_VALUE));
			}
			mailSend.setNewValue((Objects.nonNull(newLog.isSendMail()) && newLog.isSendMail())
					? AuditTrailLableConstans.ENABLE_STRING_VALUE
					: AuditTrailLableConstans.DISABLED_STRING_VALUE);
			if (Objects.nonNull(mailSend.getNewValue()))
				mailSend.setEdited(!mailSend.getNewValue().equals(mailSend.getOldValue()));
			else
				mailSend.setEdited(false);
			auditTrailDto.setIsMailSend(mailSend);

			AuditTrailNewOldValueDto note = Objects.nonNull(auditTrailDto.getNote()) ? auditTrailDto.getNote()
					: new AuditTrailNewOldValueDto();
			note.setLabel(AuditTrailLableConstans.NOTE);
			if (Objects.nonNull(note.getNewValue())) {
				note.setOldValue(note.getNewValue());
			}
			if (isExist && Objects.nonNull(oldNotes)) {
				note.setOldValue(oldNotes);
			}
			note.setNewValue(notesMap.get(String.valueOf(newLog.getRecId())));
			if (Objects.nonNull(note.getNewValue()))
				note.setEdited(!note.getNewValue().equals(note.getOldValue()));
			else
				note.setEdited(false);
			auditTrailDto.setNote(note);

			AuditTrailNewOldValueDto productId = Objects.nonNull(auditTrailDto.getProductId())
					? auditTrailDto.getProductId()
					: new AuditTrailNewOldValueDto();
			productId.setLabel(AuditTrailLableConstans.PRODUCT_ID);
			if (Objects.nonNull(productId.getNewValue())) {
				productId.setOldValue(productId.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStopageMaster)) {
				productId.setOldValue(String.valueOf(oldLogStopageMaster.getProductId()));
			}
			productId.setNewValue(String.valueOf(newLog.getProductId()));
			if (Objects.nonNull(productId.getNewValue()))
				productId.setEdited(!productId.getNewValue().equals(productId.getOldValue()));
			else
				productId.setEdited(false);
			auditTrailDto.setProductId(productId);

			AuditTrailNewOldValueDto mdrScannerCode = Objects.nonNull(auditTrailDto.getMdrScannerCode())
					? auditTrailDto.getMdrScannerCode()
					: new AuditTrailNewOldValueDto();
			mdrScannerCode.setLabel(AuditTrailLableConstans.MDR_SCANNER_CODE);
			if (Objects.nonNull(mdrScannerCode.getNewValue())) {
				mdrScannerCode.setOldValue(mdrScannerCode.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStopageMaster)) {
				mdrScannerCode.setOldValue(oldLogStopageMaster.getMdrScannerCode());
			}
			mdrScannerCode.setNewValue(newLog.getMdrScannerCode());
			if (Objects.nonNull(mdrScannerCode.getNewValue()))
				mdrScannerCode.setEdited(!mdrScannerCode.getNewValue().equals(mdrScannerCode.getOldValue()));
			else
				mdrScannerCode.setEdited(false);
			auditTrailDto.setMdrScannerCode(mdrScannerCode);

			AuditTrailNewOldValueDto assetEntryMethod = Objects.nonNull(auditTrailDto.getAssetEntryMethodId())
					? auditTrailDto.getAssetEntryMethodId()
					: new AuditTrailNewOldValueDto();
			assetEntryMethod.setLabel(AuditTrailLableConstans.ASSET_ENTRY_METHOD_ID);
			if (Objects.nonNull(assetEntryMethod.getNewValue())) {
				assetEntryMethod.setOldValue(assetEntryMethod.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStopageMaster)) {
				assetEntryMethod.setOldValue(String.valueOf(oldLogStopageMaster.getAssetEntryMethod()));
			}
			assetEntryMethod.setNewValue(String.valueOf(newLog.getAssetEntryMethod()));
			if (Objects.nonNull(assetEntryMethod.getNewValue()))
				assetEntryMethod.setEdited(!assetEntryMethod.getNewValue().equals(assetEntryMethod.getOldValue()));
			else
				assetEntryMethod.setEdited(false);
			auditTrailDto.setAssetEntryMethodId(assetEntryMethod);

			AuditTrailNewOldValueDto assetId = Objects.nonNull(auditTrailDto.getAssetId()) ? auditTrailDto.getAssetId()
					: new AuditTrailNewOldValueDto();
			assetId.setLabel(AuditTrailLableConstans.ASSET_ID);
			if (Objects.nonNull(assetId.getNewValue())) {
				assetId.setOldValue(assetId.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStopageMaster)) {
				assetId.setOldValue(String.valueOf(oldLogStopageMaster.getAssetId()));
			}
			assetId.setNewValue(String.valueOf(newLog.getAssetId()));
			if (Objects.nonNull(assetId.getNewValue()))
				assetId.setEdited(!assetId.getNewValue().equals(assetId.getOldValue()));
			else
				assetId.setEdited(false);
			auditTrailDto.setAssetId(assetId);

			AuditTrailNewOldValueDto lastEvenetReceived = Objects.nonNull(auditTrailDto.getLastEventReceived())
					? auditTrailDto.getLastEventReceived()
					: new AuditTrailNewOldValueDto();
			lastEvenetReceived.setLabel(AuditTrailLableConstans.LAST_EVENT_RECEIVED);
			if (Objects.nonNull(lastEvenetReceived.getNewValue())) {
				lastEvenetReceived.setOldValue(lastEvenetReceived.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStopageMaster)) {
				lastEvenetReceived.setOldValue(Objects.nonNull(oldLogStopageMaster.getLastEventReceived())
						? oldLogStopageMaster.getLastEventReceived().format(AuditTrailLableConstans.formatter)
						: null);
			}
			lastEvenetReceived.setNewValue(Objects.nonNull(newLog.getLastEventReceived())
					? newLog.getLastEventReceived().format(AuditTrailLableConstans.formatter)
					: null);
			if (Objects.nonNull(lastEvenetReceived.getNewValue()))
				lastEvenetReceived
						.setEdited(!lastEvenetReceived.getNewValue().equals(lastEvenetReceived.getOldValue()));
			else
				lastEvenetReceived.setEdited(false);
			auditTrailDto.setLastEventReceived(lastEvenetReceived);

			AuditTrailNewOldValueDto tenantId = Objects.nonNull(auditTrailDto.getCustomerId())
					? auditTrailDto.getCustomerId()
					: new AuditTrailNewOldValueDto();
			tenantId.setLabel(AuditTrailLableConstans.TENANT_ID);
			if (Objects.nonNull(tenantId.getNewValue())) {
				tenantId.setOldValue(tenantId.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStopageMaster)) {
				tenantId.setOldValue(String.valueOf(oldLogStopageMaster.getTenantId()));
			}
			tenantId.setNewValue(String.valueOf(newLog.getTenantId()));
			if (Objects.nonNull(tenantId.getNewValue()))
				tenantId.setEdited(!tenantId.getNewValue().equals(tenantId.getOldValue()));
			else
				tenantId.setEdited(false);
			auditTrailDto.setCustomerId(tenantId);

			AuditTrailNewOldValueDto deviceStatus = Objects.nonNull(auditTrailDto.getDeviceStatus())
					? auditTrailDto.getDeviceStatus()
					: new AuditTrailNewOldValueDto();
			deviceStatus.setLabel(AuditTrailLableConstans.DEVICE_STATUS);
			if (Objects.nonNull(deviceStatus.getNewValue())) {
				deviceStatus.setOldValue(deviceStatus.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStopageMaster)) {
				deviceStatus.setOldValue(String.valueOf(oldLogStopageMaster.getDeviceStatus()));
			}
			deviceStatus.setNewValue(String.valueOf(newLog.getDeviceStatus()));
			if (Objects.nonNull(deviceStatus.getNewValue()))
				deviceStatus.setEdited(!deviceStatus.getNewValue().equals(deviceStatus.getOldValue()));
			else
				deviceStatus.setEdited(false);
			auditTrailDto.setDeviceStatus(deviceStatus);

			AuditTrailNewOldValueDto logStopageTime = Objects.nonNull(auditTrailDto.getLogStopageTime())
					? auditTrailDto.getLogStopageTime()
					: new AuditTrailNewOldValueDto();
			logStopageTime.setLabel(AuditTrailLableConstans.LOG_STOPPAGE_TIME);
			if (Objects.nonNull(logStopageTime.getNewValue())) {
				logStopageTime.setOldValue(logStopageTime.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStopageMaster)) {
				logStopageTime.setOldValue(Objects.nonNull(oldLogStopageMaster.getLogStoppageTime())
						? oldLogStopageMaster.getLogStoppageTime().format(AuditTrailLableConstans.formatter)
						: null);
			}
			logStopageTime.setNewValue(Objects.nonNull(newLog.getLogStoppageTime())
					? newLog.getLogStoppageTime().format(AuditTrailLableConstans.formatter)
					: null);
			if (Objects.nonNull(logStopageTime.getNewValue()))
				logStopageTime.setEdited(!logStopageTime.getNewValue().equals(logStopageTime.getOldValue()));
			else
				logStopageTime.setEdited(false);
			auditTrailDto.setLogStopageTime(logStopageTime);

			AuditTrailNewOldValueDto logReceiveTime = Objects.nonNull(auditTrailDto.getLogRecieveTime())
					? auditTrailDto.getLogRecieveTime()
					: new AuditTrailNewOldValueDto();
			logReceiveTime.setLabel(AuditTrailLableConstans.LOG_RECIEVED_TIME);
			if (Objects.nonNull(logReceiveTime.getNewValue())) {
				logReceiveTime.setOldValue(logReceiveTime.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStopageMaster)) {
				logReceiveTime.setOldValue(Objects.nonNull(oldLogStopageMaster.getLatestRecivedTime())
						? oldLogStopageMaster.getLatestRecivedTime().format(AuditTrailLableConstans.formatter)
						: null);
			}
			logReceiveTime.setNewValue(Objects.nonNull(newLog.getLatestRecivedTime())
					? newLog.getLatestRecivedTime().format(AuditTrailLableConstans.formatter)
					: null);
			if (Objects.nonNull(logReceiveTime.getNewValue()))
				logReceiveTime.setEdited(!logReceiveTime.getNewValue().equals(logReceiveTime.getOldValue()));
			else
				logReceiveTime.setEdited(false);
			auditTrailDto.setLogRecieveTime(logReceiveTime);

			AuditTrailNewOldValueDto disabled = Objects.nonNull(auditTrailDto.getDisabled())
					? auditTrailDto.getDisabled()
					: new AuditTrailNewOldValueDto();
			disabled.setLabel(AuditTrailLableConstans.DISABLED);
			if (Objects.nonNull(disabled.getNewValue())) {
				disabled.setOldValue(disabled.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStopageMaster)) {
				disabled.setOldValue((Objects.nonNull(oldLogStopageMaster.isDisabled()) && oldLogStopageMaster.isDisabled())
						? AuditTrailLableConstans.BOOLEN_TRUE_STRING_VALUE
						: AuditTrailLableConstans.BOOLEN_FALSE_STRING_VALUE);
			}
			disabled.setNewValue((Objects.nonNull(newLog.isDisabled()) && newLog.isDisabled())
					? AuditTrailLableConstans.BOOLEN_TRUE_STRING_VALUE
					: AuditTrailLableConstans.BOOLEN_FALSE_STRING_VALUE);
			if (Objects.nonNull(disabled.getNewValue()))
				disabled.setEdited(!disabled.getNewValue().equals(disabled.getOldValue()));
			else
				disabled.setEdited(false);
			auditTrailDto.setDisabled(disabled);

			AuditTrailNewOldValueDto suppressed = Objects.nonNull(auditTrailDto.getSuppressed())
					? auditTrailDto.getSuppressed()
					: new AuditTrailNewOldValueDto();
			suppressed.setLabel(AuditTrailLableConstans.SUPPRESSED);
			if (Objects.nonNull(suppressed.getNewValue())) {
				suppressed.setOldValue(suppressed.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStopageMaster)) {
				suppressed.setOldValue((Objects.nonNull(oldLogStopageMaster.isSuppressed()) && oldLogStopageMaster.isSuppressed())
						? AuditTrailLableConstans.BOOLEN_TRUE_STRING_VALUE
						: AuditTrailLableConstans.BOOLEN_FALSE_STRING_VALUE);
			}
			suppressed.setNewValue((Objects.nonNull(newLog.isSuppressed()) && newLog.isSuppressed())
					? AuditTrailLableConstans.BOOLEN_TRUE_STRING_VALUE
					: AuditTrailLableConstans.BOOLEN_FALSE_STRING_VALUE);
			if (Objects.nonNull(suppressed.getNewValue()))
				suppressed.setEdited(!suppressed.getNewValue().equals(suppressed.getOldValue()));
			else
				suppressed.setEdited(false);
			auditTrailDto.setSuppressed(suppressed);

			AuditTrailNewOldValueDto loggerStatus = Objects.nonNull(auditTrailDto.getLoggerStatus())
					? auditTrailDto.getLoggerStatus()
					: new AuditTrailNewOldValueDto();
			loggerStatus.setLabel(AuditTrailLableConstans.LOGGER_STATUS);
			if (Objects.nonNull(loggerStatus.getNewValue())) {
				loggerStatus.setOldValue(loggerStatus.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStopageMaster)) {
				loggerStatus.setOldValue((Objects.nonNull(oldLogStopageMaster.getLoggerStatus()) && oldLogStopageMaster.getLoggerStatus())
						? AuditTrailLableConstans.BOOLEN_TRUE_STRING_VALUE
						: AuditTrailLableConstans.BOOLEN_FALSE_STRING_VALUE);
			}
			loggerStatus.setNewValue((Objects.nonNull(newLog.getLoggerStatus()) && newLog.getLoggerStatus())
					? AuditTrailLableConstans.BOOLEN_TRUE_STRING_VALUE
					: AuditTrailLableConstans.BOOLEN_FALSE_STRING_VALUE);
			if (Objects.nonNull(loggerStatus.getNewValue()))
				loggerStatus.setEdited(!loggerStatus.getNewValue().equals(loggerStatus.getOldValue()));
			else
				loggerStatus.setEdited(false);
			auditTrailDto.setLoggerStatus(loggerStatus);

			AuditTrailNewOldValueDto loggerDate = Objects.nonNull(auditTrailDto.getLoggerDate())
					? auditTrailDto.getLoggerDate()
					: new AuditTrailNewOldValueDto();
			loggerDate.setLabel(AuditTrailLableConstans.LOGGER_DATE);
			if (Objects.nonNull(loggerDate.getNewValue())) {
				loggerDate.setOldValue(loggerDate.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStopageMaster)) {
				loggerDate.setOldValue(Objects.nonNull(oldLogStopageMaster.getLoggerDate())
						? oldLogStopageMaster.getLoggerDate().format(AuditTrailLableConstans.formatter)
						: null);
			}
			loggerDate.setNewValue(Objects.nonNull(newLog.getLoggerDate())
					? newLog.getLoggerDate().format(AuditTrailLableConstans.formatter)
					: null);
			if (Objects.nonNull(loggerDate.getNewValue()))
				loggerDate.setEdited(!loggerDate.getNewValue().equals(loggerDate.getOldValue()));
			else
				loggerDate.setEdited(false);
			auditTrailDto.setLoggerDate(loggerDate);

			AuditTrailNewOldValueDto deleted = Objects.nonNull(auditTrailDto.getDeleted()) ? auditTrailDto.getDeleted()
					: new AuditTrailNewOldValueDto();
			deleted.setLabel(AuditTrailLableConstans.DELETED);
			if (Objects.nonNull(deleted.getNewValue())) {
				deleted.setOldValue(deleted.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStopageMaster)) {
				deleted.setOldValue(oldLogStopageMaster.isDeleted() ? AuditTrailLableConstans.BOOLEN_TRUE_STRING_VALUE
						: AuditTrailLableConstans.BOOLEN_FALSE_STRING_VALUE);
			}
			deleted.setNewValue(newLog.isDeleted() ? AuditTrailLableConstans.BOOLEN_TRUE_STRING_VALUE
					: AuditTrailLableConstans.BOOLEN_FALSE_STRING_VALUE);
			if (Objects.nonNull(deleted.getNewValue()))
				deleted.setEdited(!deleted.getNewValue().equals(deleted.getOldValue()));
			else
				deleted.setEdited(false);
			auditTrailDto.setDeleted(deleted);

			AuditTrailNewOldValueDto cratedDate = Objects.nonNull(auditTrailDto.getCreatedDate())
					? auditTrailDto.getCreatedDate()
					: new AuditTrailNewOldValueDto();
			cratedDate.setLabel(AuditTrailLableConstans.CREATED_DATE);
			if (Objects.nonNull(cratedDate.getNewValue())) {
				cratedDate.setOldValue(cratedDate.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStopageMaster)) {
				cratedDate.setOldValue(Objects.nonNull(oldLogStopageMaster.getCreatedDate())
						? oldLogStopageMaster.getCreatedDate().format(AuditTrailLableConstans.formatter)
						: null);
			}
			cratedDate.setNewValue(Objects.nonNull(newLog.getCreatedDate())
					? newLog.getCreatedDate().format(AuditTrailLableConstans.formatter)
					: null);
			if (Objects.nonNull(cratedDate.getNewValue()))
				cratedDate.setEdited(!cratedDate.getNewValue().equals(cratedDate.getOldValue()));
			else
				cratedDate.setEdited(false);
			auditTrailDto.setCreatedDate(cratedDate);

			AuditTrailNewOldValueDto updatedDate = Objects.nonNull(auditTrailDto.getUpdatedDate())
					? auditTrailDto.getUpdatedDate()
					: new AuditTrailNewOldValueDto();
			updatedDate.setLabel(AuditTrailLableConstans.UPDATED_DATE);
			if (Objects.nonNull(updatedDate.getNewValue())) {
				updatedDate.setOldValue(updatedDate.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStopageMaster)) {
				updatedDate.setOldValue(Objects.nonNull(oldLogStopageMaster.getUpdatedDate())
						? oldLogStopageMaster.getUpdatedDate().format(AuditTrailLableConstans.formatter)
						: null);
			}
			updatedDate.setNewValue(Objects.nonNull(newLog.getUpdatedDate())
					? newLog.getUpdatedDate().format(AuditTrailLableConstans.formatter)
					: null);
			if (Objects.nonNull(updatedDate.getNewValue()))
				updatedDate.setEdited(!updatedDate.getNewValue().equals(updatedDate.getOldValue()));
			else
				updatedDate.setEdited(false);
			auditTrailDto.setUpdatedDate(updatedDate);

			AuditTrailNewOldValueDto createdBy = Objects.nonNull(auditTrailDto.getCreatedId())
					? auditTrailDto.getCreatedId()
					: new AuditTrailNewOldValueDto();
			createdBy.setLabel(AuditTrailLableConstans.CREATED_BY);
			if (Objects.nonNull(createdBy.getNewValue())) {
				createdBy.setOldValue(createdBy.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStopageMaster)) {
				createdBy.setOldValue(String.valueOf(oldLogStopageMaster.getCreatedBy()));
			}
			createdBy.setNewValue(String.valueOf(newLog.getCreatedBy()));
			if (Objects.nonNull(createdBy.getNewValue()))
				createdBy.setEdited(!createdBy.getNewValue().equals(createdBy.getOldValue()));
			else
				createdBy.setEdited(false);
			auditTrailDto.setCreatedId(createdBy);

			AuditTrailNewOldValueDto updatedBy = Objects.nonNull(auditTrailDto.getUpdatedId())
					? auditTrailDto.getUpdatedId()
					: new AuditTrailNewOldValueDto();
			updatedBy.setLabel(AuditTrailLableConstans.UPDATED_BY);
			if (Objects.nonNull(updatedBy.getNewValue())) {
				updatedBy.setOldValue(updatedBy.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStopageMaster)) {
				updatedBy.setOldValue(String.valueOf(oldLogStopageMaster.getUpdatedBy()));
			}
			updatedBy.setNewValue(String.valueOf(newLog.getUpdatedBy()));
			if (Objects.nonNull(updatedBy.getNewValue()))
				updatedBy.setEdited(!updatedBy.getNewValue().equals(updatedBy.getOldValue()));
			else
				updatedBy.setEdited(false);
			auditTrailDto.setUpdatedId(updatedBy);

			AuditTrailNewOldValueDto emailStatus = Objects.nonNull(auditTrailDto.getEmailStatus())
					? auditTrailDto.getEmailStatus()
					: new AuditTrailNewOldValueDto();
			emailStatus.setLabel(AuditTrailLableConstans.EMAIL_STATUS);
			if (Objects.nonNull(emailStatus.getNewValue())) {
				emailStatus.setOldValue(emailStatus.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStopageMaster)) {
				emailStatus.setOldValue(oldLogStopageMaster.getEmailStatus());
			}
			emailStatus.setNewValue(newLog.getEmailStatus());
			if (Objects.nonNull(emailStatus.getNewValue()))
				emailStatus.setEdited(!emailStatus.getNewValue().equals(emailStatus.getOldValue()));
			else
				emailStatus.setEdited(false);
			auditTrailDto.setEmailStatus(emailStatus);

			AuditTrailNewOldValueDto emailThreasholdReached = Objects.nonNull(auditTrailDto.getEmailThreasholdReached())
					? auditTrailDto.getEmailThreasholdReached()
					: new AuditTrailNewOldValueDto();
			emailThreasholdReached.setLabel(AuditTrailLableConstans.EMAIL_THREASHOLD_REACHED);
			if (Objects.nonNull(emailThreasholdReached.getNewValue())) {
				emailThreasholdReached.setOldValue(emailThreasholdReached.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStopageMaster)) {
				emailThreasholdReached.setOldValue(oldLogStopageMaster.getEmailThresholdReached());
			}
			emailThreasholdReached.setNewValue(newLog.getEmailThresholdReached());
			if (Objects.nonNull(emailThreasholdReached.getNewValue()))
				emailThreasholdReached
						.setEdited(!emailThreasholdReached.getNewValue().equals(emailThreasholdReached.getOldValue()));
			else
				emailThreasholdReached.setEdited(false);
			auditTrailDto.setEmailThreasholdReached(emailThreasholdReached);

			AuditTrailNewOldValueDto emailTime = Objects.nonNull(auditTrailDto.getEmailTime())
					? auditTrailDto.getEmailTime()
					: new AuditTrailNewOldValueDto();
			emailTime.setLabel(AuditTrailLableConstans.EMAIL_TIME);
			if (Objects.nonNull(emailTime.getNewValue())) {
				emailTime.setOldValue(emailTime.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStopageMaster)) {
				emailTime.setOldValue(Objects.nonNull(oldLogStopageMaster.getEmailTime())
						? oldLogStopageMaster.getEmailTime().format(AuditTrailLableConstans.formatter)
						: null);
			}
			emailTime.setNewValue(Objects.nonNull(newLog.getEmailTime())
					? newLog.getEmailTime().format(AuditTrailLableConstans.formatter)
					: null);
			if (Objects.nonNull(emailTime.getNewValue()))
				emailTime.setEdited(!emailTime.getNewValue().equals(emailTime.getOldValue()));
			else
				emailTime.setEdited(false);
			auditTrailDto.setEmailTime(emailTime);

			AuditTrailNewOldValueDto deviceReceiptTime = Objects.nonNull(auditTrailDto.getDeviceReceiptTime())
					? auditTrailDto.getDeviceReceiptTime()
					: new AuditTrailNewOldValueDto();
			deviceReceiptTime.setLabel(AuditTrailLableConstans.DEVICE_RECEIPT_TIME);
			if (Objects.nonNull(deviceReceiptTime.getNewValue())) {
				deviceReceiptTime.setOldValue(deviceReceiptTime.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStopageMaster)) {
				deviceReceiptTime.setOldValue(Objects.nonNull(oldLogStopageMaster.getDeviceReceiptTime())
						? oldLogStopageMaster.getDeviceReceiptTime().format(AuditTrailLableConstans.formatter)
						: null);
			}
			deviceReceiptTime.setNewValue(Objects.nonNull(newLog.getDeviceReceiptTime())
					? newLog.getDeviceReceiptTime().format(AuditTrailLableConstans.formatter)
					: null);
			if (Objects.nonNull(deviceReceiptTime.getNewValue()))
				deviceReceiptTime.setEdited(!deviceReceiptTime.getNewValue().equals(deviceReceiptTime.getOldValue()));
			else
				deviceReceiptTime.setEdited(false);
			auditTrailDto.setDeviceReceiptTime(deviceReceiptTime);

			AuditTrailNewOldValueDto sendEmailAlertAudit = Objects.nonNull(auditTrailDto.getSendEmailAlertToAudit())
					? auditTrailDto.getSendEmailAlertToAudit()
					: new AuditTrailNewOldValueDto();
			sendEmailAlertAudit.setLabel(AuditTrailLableConstans.SEND_EMAIL_ALERT_AUDIT);
			if (Objects.nonNull(sendEmailAlertAudit.getNewValue())) {
				sendEmailAlertAudit.setOldValue(sendEmailAlertAudit.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStopageMaster)) {
				sendEmailAlertAudit.setOldValue(oldLogStopageMaster.getSendToEmailAlertAudit());
			}
			sendEmailAlertAudit.setNewValue(newLog.getSendToEmailAlertAudit());
			if (Objects.nonNull(sendEmailAlertAudit.getNewValue()))
				sendEmailAlertAudit
						.setEdited(!sendEmailAlertAudit.getNewValue().equals(sendEmailAlertAudit.getOldValue()));
			else
				sendEmailAlertAudit.setEdited(false);
			auditTrailDto.setSendEmailAlertToAudit(sendEmailAlertAudit);

			AuditTrailNewOldValueDto logStopageJson = Objects.nonNull(auditTrailDto.getLogStoppageThresholdJson())
					? auditTrailDto.getLogStoppageThresholdJson()
					: new AuditTrailNewOldValueDto();
			logStopageJson.setLabel(AuditTrailLableConstans.LOG_STOPPAGE_THRESHOLD_JSON);
			if (Objects.nonNull(logStopageJson.getNewValue())) {
				logStopageJson.setOldValue(logStopageJson.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStopageMaster)) {
				logStopageJson.setOldValue(oldLogStopageMaster.getLogStoppageThresholdJson());
			}
			logStopageJson.setNewValue(newLog.getLogStoppageThresholdJson());
			if (Objects.nonNull(logStopageJson.getNewValue()))
				logStopageJson.setEdited(!logStopageJson.getNewValue().equals(logStopageJson.getOldValue()));
			else
				logStopageJson.setEdited(false);
			auditTrailDto.setLogStoppageThresholdJson(logStopageJson);

			AuditTrailNewOldValueDto emailFrequencyJson = Objects
					.nonNull(auditTrailDto.getEmailNotificationFrequencyJson())
							? auditTrailDto.getEmailNotificationFrequencyJson()
							: new AuditTrailNewOldValueDto();
			emailFrequencyJson.setLabel(AuditTrailLableConstans.EMAIL_NOTIFICATION_FREQUENCY_JSON);
			if (Objects.nonNull(emailFrequencyJson.getNewValue())) {
				emailFrequencyJson.setOldValue(emailFrequencyJson.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStopageMaster)) {
				emailFrequencyJson.setOldValue(oldLogStopageMaster.getEmailNotificationFrequencyJson());
			}
			emailFrequencyJson.setNewValue(newLog.getEmailNotificationFrequencyJson());
			if (Objects.nonNull(emailFrequencyJson.getNewValue()))
				emailFrequencyJson
						.setEdited(!emailFrequencyJson.getNewValue().equals(emailFrequencyJson.getOldValue()));
			else
				emailFrequencyJson.setEdited(false);
			auditTrailDto.setEmailNotificationFrequencyJson(emailFrequencyJson);
			
			AuditTrailNewOldValueDto productTypeId = Objects.nonNull(auditTrailDto.getProductTypeId())
					? auditTrailDto.getProductTypeId()
					: new AuditTrailNewOldValueDto();
			productTypeId.setLabel(AuditTrailLableConstans.PRODUCT_TYPE_ID);
			if (Objects.nonNull(productTypeId.getNewValue())) {
				productTypeId.setOldValue(productTypeId.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStopageMaster)) {
				productTypeId.setOldValue(
						Objects.nonNull(oldLogStopageMaster.getProductTypeId()) ? oldLogStopageMaster.getProductTypeId().toString() : null);
			}
			productTypeId.setNewValue(
					Objects.nonNull(newLog.getProductTypeId()) ? newLog.getProductTypeId().toString() : null);
			if (Objects.nonNull(productTypeId.getNewValue()))
				productTypeId.setEdited(!productTypeId.getNewValue().equals(productTypeId.getOldValue()));
			else
				productTypeId.setEdited(false);
			auditTrailDto.setProductTypeId(productTypeId);

		}else {
			auditTrailDto = new LogStopageAuditTrailDto();
			
			AuditTrailNewOldValueDto auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.ORGNIZATION);
			auditTrailNewOldValueDto.setNewValue(tenantMap.get(newLog.getTenantId()));
			auditTrailDto.setCustomerName(auditTrailNewOldValueDto);
			
			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.PRODUCT_ID);
			auditTrailNewOldValueDto.setNewValue(String.valueOf(newLog.getProductId()));
			auditTrailDto.setProductId(auditTrailNewOldValueDto);
			
			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.PRODUCT_VENDOR);
			auditTrailNewOldValueDto.setNewValue(productMaster.getProductVendor());
			auditTrailDto.setDeviceVendor(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.PRODUCT_NAME);
			auditTrailNewOldValueDto.setNewValue(productMaster.getProductName());
			auditTrailDto.setDeviceProduct(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.PRODUCT_TYPE);
			auditTrailNewOldValueDto.setNewValue(productMaster.getProductType());
			auditTrailDto.setDeviceGroup(auditTrailNewOldValueDto);
			
			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.PRODUCT_TYPE_ID);
			if(Objects.nonNull(newLog.getProductTypeId()))
					auditTrailNewOldValueDto.setNewValue(newLog.getProductTypeId().toString());
			auditTrailDto.setProductTypeId(auditTrailNewOldValueDto);
			
		}
		detailsJson.setType(action);
		detailsJson.setData(auditTrailDto);
		ObjectMapper objectMapper = new ObjectMapper();

		try {
			return objectMapper.writeValueAsString(detailsJson);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getAuditTrailJsonResponseByAllowlist(String action,
			LogStoppageWhitelistDevices logStoppageWhitelistDevices, Map<Long, String> tenantMap,
			Map<Long, ProductMaster> productMasterMap, AuditTrailDetailsOnly auditTrailDetailsOnly, String deleteNote,
			LogStoppageWhitelistDevices oldLogStoppageWhitelistDevices) {

		AllowListAuditTrailJsonResponse detailsJson = new AllowListAuditTrailJsonResponse();
		ProductMaster productMaster = productMasterMap.getOrDefault(logStoppageWhitelistDevices.getProductId(),
				new ProductMaster());
		ProductMaster oldProductMaster=new ProductMaster();
		if(Objects.nonNull(oldLogStoppageWhitelistDevices)){
			oldProductMaster = productMasterMap.getOrDefault(oldLogStoppageWhitelistDevices.getProductId(),
					new ProductMaster());
		}
		AllowListAuditTrailDto auditTrailDto = null;

		boolean isExist = false;
		if (Objects.nonNull(auditTrailDetailsOnly) && Objects.nonNull(auditTrailDetailsOnly.details())) {
			ObjectMapper mapper = new ObjectMapper();
			try {
				detailsJson = mapper.readValue(auditTrailDetailsOnly.details(), AllowListAuditTrailJsonResponse.class);
			} catch (JsonParseException | JsonMappingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			auditTrailDto = detailsJson.getData();
		} else {
			isExist = true;
		}

		if (AppConstants.LFD_ACTION_TYPE_ADDED_TO_ALLOWLIST.equalsIgnoreCase(action)
				|| AppConstants.LFD_ACTION_TYPE_ADD_TO_ALLOWLIST_FROM_ADD_TO_LOG_FLOW_MONITORING
						.equalsIgnoreCase(action)) {
			auditTrailDto = new AllowListAuditTrailDto();
			AuditTrailNewOldValueDto auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.ORGNIZATION);
			auditTrailNewOldValueDto.setNewValue(tenantMap.get(logStoppageWhitelistDevices.getTenantId()));
			auditTrailDto.setCustomerName(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.PRODUCT_VENDOR);
			auditTrailNewOldValueDto.setNewValue(productMaster.getProductVendor());
			auditTrailDto.setDeviceVendor(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.PRODUCT_NAME);
			auditTrailNewOldValueDto.setNewValue(productMaster.getProductName());
			auditTrailDto.setDeviceProduct(auditTrailNewOldValueDto);
			;

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.DESCRIPTION);
			auditTrailNewOldValueDto.setNewValue(logStoppageWhitelistDevices.getDescription());
			auditTrailDto.setDescription(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.PRODUCT_ID);
			auditTrailNewOldValueDto.setNewValue(String.valueOf(logStoppageWhitelistDevices.getProductId()));
			auditTrailDto.setProductId(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.TENANT_ID);
			auditTrailNewOldValueDto.setNewValue(String.valueOf(logStoppageWhitelistDevices.getTenantId()));
			auditTrailDto.setTenantId(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.DELETED);
			auditTrailNewOldValueDto.setNewValue(
					logStoppageWhitelistDevices.isDeleted() ? AuditTrailLableConstans.BOOLEN_TRUE_STRING_VALUE
							: AuditTrailLableConstans.BOOLEN_FALSE_STRING_VALUE);
			auditTrailDto.setDeleted(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.CREATED_DATE);
			auditTrailNewOldValueDto.setNewValue(Objects.nonNull(logStoppageWhitelistDevices.getAllowListedDate())
					? logStoppageWhitelistDevices.getAllowListedDate().format(AuditTrailLableConstans.formatter)
					: null);
			auditTrailDto.setCreatedDate(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.UPDATED_DATE);
			auditTrailNewOldValueDto.setNewValue(Objects.nonNull(logStoppageWhitelistDevices.getUpdatedDate())
					? logStoppageWhitelistDevices.getUpdatedDate().format(AuditTrailLableConstans.formatter)
					: null);
			auditTrailDto.setUpdatedDate(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.CREATED_BY);
			auditTrailNewOldValueDto.setNewValue(String.valueOf(logStoppageWhitelistDevices.getCreatedById()));
			auditTrailDto.setCreatedId(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.UPDATED_BY);
			auditTrailNewOldValueDto.setNewValue(String.valueOf(logStoppageWhitelistDevices.getUpdatedById()));
			auditTrailDto.setUpdatedId(auditTrailNewOldValueDto);

		} else if (AppConstants.LFD_ACTION_TYPE_EDITED_IN_ALLOWLIST.equalsIgnoreCase(action)) {
			if (isExist) {
				auditTrailDto = new AllowListAuditTrailDto();
			}

			AuditTrailNewOldValueDto organization = Objects.nonNull(auditTrailDto.getCustomerName())
					? auditTrailDto.getCustomerName()
					: new AuditTrailNewOldValueDto();
			organization.setLabel(AuditTrailLableConstans.ORGNIZATION);
			if (Objects.nonNull(organization.getNewValue())) {
				organization.setOldValue(organization.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStoppageWhitelistDevices)) {
				organization.setOldValue(tenantMap.get(oldLogStoppageWhitelistDevices.getTenantId()));
			}
			organization.setNewValue(tenantMap.get(logStoppageWhitelistDevices.getTenantId()));
			if (Objects.nonNull(organization.getNewValue()))
				organization.setEdited(!organization.getNewValue().equals(organization.getOldValue()));
			else
				organization.setEdited(false);
			auditTrailDto.setCustomerName(organization);

			AuditTrailNewOldValueDto productVendor = Objects.nonNull(auditTrailDto.getDeviceVendor())
					? auditTrailDto.getDeviceVendor()
					: new AuditTrailNewOldValueDto();
			productVendor.setLabel(AuditTrailLableConstans.PRODUCT_VENDOR);
			if (Objects.nonNull(productVendor.getNewValue())) {
				productVendor.setOldValue(productVendor.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStoppageWhitelistDevices)) {
				productVendor.setOldValue(oldProductMaster.getProductVendor());
			}
			productVendor.setNewValue(productMaster.getProductVendor());
			if (Objects.nonNull(productVendor.getNewValue()))
				productVendor.setEdited(!productVendor.getNewValue().equals(productVendor.getOldValue()));
			else
				productVendor.setEdited(false);
			auditTrailDto.setDeviceVendor(productVendor);

			AuditTrailNewOldValueDto productName = Objects.nonNull(auditTrailDto.getDeviceProduct())
					? auditTrailDto.getDeviceProduct()
					: new AuditTrailNewOldValueDto();
			productName.setLabel(AuditTrailLableConstans.PRODUCT_NAME);
			if (Objects.nonNull(productName.getNewValue())) {
				productName.setOldValue(productName.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStoppageWhitelistDevices)) {
				productName.setOldValue(oldProductMaster.getProductName());
			}
			productName.setNewValue(productMaster.getProductName());
			if (Objects.nonNull(productName.getNewValue()))
				productName.setEdited(!productName.getNewValue().equals(productName.getOldValue()));
			else
				productName.setEdited(false);
			auditTrailDto.setDeviceProduct(productName);

			AuditTrailNewOldValueDto description = Objects.nonNull(auditTrailDto.getDescription())
					? auditTrailDto.getDescription()
					: new AuditTrailNewOldValueDto();
			description.setLabel(AuditTrailLableConstans.DESCRIPTION);
			if (Objects.nonNull(description.getNewValue())) {
				description.setOldValue(description.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStoppageWhitelistDevices)) {
				description.setOldValue(oldLogStoppageWhitelistDevices.getDescription());
			}
			description.setNewValue(logStoppageWhitelistDevices.getDescription());
			if (Objects.nonNull(description.getNewValue()))
				description.setEdited(!description.getNewValue().equals(description.getOldValue()));
			else
				description.setEdited(false);
			auditTrailDto.setDescription(description);

			AuditTrailNewOldValueDto productId = Objects.nonNull(auditTrailDto.getProductId())
					? auditTrailDto.getProductId()
					: new AuditTrailNewOldValueDto();
			productId.setLabel(AuditTrailLableConstans.PRODUCT_ID);
			if (Objects.nonNull(productId.getNewValue())) {
				productId.setOldValue(productId.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStoppageWhitelistDevices)) {
				productId.setOldValue(String.valueOf(oldLogStoppageWhitelistDevices.getProductId()));
			}
			productId.setNewValue(String.valueOf(logStoppageWhitelistDevices.getProductId()));
			if (Objects.nonNull(productId.getNewValue()))
				productId.setEdited(!productId.getNewValue().equals(productId.getOldValue()));
			else
				productId.setEdited(false);
			auditTrailDto.setProductId(productId);

			AuditTrailNewOldValueDto tenantId = Objects.nonNull(auditTrailDto.getTenantId())
					? auditTrailDto.getTenantId()
					: new AuditTrailNewOldValueDto();
			tenantId.setLabel(AuditTrailLableConstans.TENANT_ID);
			if (Objects.nonNull(tenantId.getNewValue())) {
				tenantId.setOldValue(tenantId.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStoppageWhitelistDevices)) {
				tenantId.setOldValue(String.valueOf(oldLogStoppageWhitelistDevices.getTenantId()));
			}
			tenantId.setNewValue(String.valueOf(logStoppageWhitelistDevices.getTenantId()));
			if (Objects.nonNull(tenantId.getNewValue()))
				tenantId.setEdited(!tenantId.getNewValue().equals(tenantId.getOldValue()));
			else
				tenantId.setEdited(false);
			auditTrailDto.setTenantId(tenantId);

			AuditTrailNewOldValueDto deleted = Objects.nonNull(auditTrailDto.getDeleted()) ? auditTrailDto.getDeleted()
					: new AuditTrailNewOldValueDto();
			deleted.setLabel(AuditTrailLableConstans.DELETED);
			if (Objects.nonNull(deleted.getNewValue())) {
				deleted.setOldValue(deleted.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStoppageWhitelistDevices)) {
				deleted.setOldValue(
						oldLogStoppageWhitelistDevices.isDeleted() ? AuditTrailLableConstans.BOOLEN_TRUE_STRING_VALUE
								: AuditTrailLableConstans.BOOLEN_FALSE_STRING_VALUE);
			}
			deleted.setNewValue(
					logStoppageWhitelistDevices.isDeleted() ? AuditTrailLableConstans.BOOLEN_TRUE_STRING_VALUE
							: AuditTrailLableConstans.BOOLEN_FALSE_STRING_VALUE);
			if (Objects.nonNull(deleted.getNewValue()))
				deleted.setEdited(!deleted.getNewValue().equals(deleted.getOldValue()));
			else
				deleted.setEdited(false);
			auditTrailDto.setDeleted(deleted);

			AuditTrailNewOldValueDto cratedDate = Objects.nonNull(auditTrailDto.getCreatedDate())
					? auditTrailDto.getCreatedDate()
					: new AuditTrailNewOldValueDto();
			cratedDate.setLabel(AuditTrailLableConstans.CREATED_DATE);
			if (Objects.nonNull(cratedDate.getNewValue())) {
				cratedDate.setOldValue(cratedDate.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStoppageWhitelistDevices)) {
				cratedDate.setOldValue(Objects.nonNull(oldLogStoppageWhitelistDevices.getAllowListedDate())
						? oldLogStoppageWhitelistDevices.getAllowListedDate().format(AuditTrailLableConstans.formatter)
						: null);
			}
			cratedDate.setNewValue(Objects.nonNull(logStoppageWhitelistDevices.getAllowListedDate())
					? logStoppageWhitelistDevices.getAllowListedDate().format(AuditTrailLableConstans.formatter)
					: null);
			if (Objects.nonNull(cratedDate.getNewValue()))
				cratedDate.setEdited(!cratedDate.getNewValue().equals(cratedDate.getOldValue()));
			else
				cratedDate.setEdited(false);
			auditTrailDto.setCreatedDate(cratedDate);

			AuditTrailNewOldValueDto updatedDate = Objects.nonNull(auditTrailDto.getUpdatedDate())
					? auditTrailDto.getUpdatedDate()
					: new AuditTrailNewOldValueDto();
			updatedDate.setLabel(AuditTrailLableConstans.UPDATED_DATE);
			if (Objects.nonNull(updatedDate.getNewValue())) {
				updatedDate.setOldValue(updatedDate.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStoppageWhitelistDevices)) {
				updatedDate.setOldValue(Objects.nonNull(oldLogStoppageWhitelistDevices.getUpdatedDate())
						? oldLogStoppageWhitelistDevices.getUpdatedDate().format(AuditTrailLableConstans.formatter)
						: null);
			}
			updatedDate.setNewValue(Objects.nonNull(logStoppageWhitelistDevices.getUpdatedDate())
					? logStoppageWhitelistDevices.getUpdatedDate().format(AuditTrailLableConstans.formatter)
					: null);
			if (Objects.nonNull(updatedDate.getNewValue()))
				updatedDate.setEdited(!updatedDate.getNewValue().equals(updatedDate.getOldValue()));
			else
				updatedDate.setEdited(false);
			auditTrailDto.setUpdatedDate(updatedDate);

			AuditTrailNewOldValueDto createdBy = Objects.nonNull(auditTrailDto.getCreatedId())
					? auditTrailDto.getCreatedId()
					: new AuditTrailNewOldValueDto();
			createdBy.setLabel(AuditTrailLableConstans.CREATED_BY);
			if (Objects.nonNull(createdBy.getNewValue())) {
				createdBy.setOldValue(createdBy.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStoppageWhitelistDevices)) {
				createdBy.setOldValue(String.valueOf(oldLogStoppageWhitelistDevices.getCreatedById()));
			}
			createdBy.setNewValue(String.valueOf(logStoppageWhitelistDevices.getCreatedById()));
			if (Objects.nonNull(createdBy.getNewValue()))
				createdBy.setEdited(!createdBy.getNewValue().equals(createdBy.getOldValue()));
			else
				createdBy.setEdited(false);
			auditTrailDto.setCreatedId(createdBy);

			AuditTrailNewOldValueDto updatedBy = Objects.nonNull(auditTrailDto.getUpdatedId())
					? auditTrailDto.getUpdatedId()
					: new AuditTrailNewOldValueDto();
			updatedBy.setLabel(AuditTrailLableConstans.UPDATED_BY);
			if (Objects.nonNull(updatedBy.getNewValue())) {
				updatedBy.setOldValue(updatedBy.getNewValue());
			}
			if (isExist && Objects.nonNull(oldLogStoppageWhitelistDevices)) {
				updatedBy.setOldValue(String.valueOf(oldLogStoppageWhitelistDevices.getUpdatedById()));
			}
			updatedBy.setNewValue(String.valueOf(logStoppageWhitelistDevices.getUpdatedById()));
			if (Objects.nonNull(updatedBy.getNewValue()))
				updatedBy.setEdited(!updatedBy.getNewValue().equals(updatedBy.getOldValue()));
			else
				updatedBy.setEdited(false);
			auditTrailDto.setUpdatedId(updatedBy);

		}else if (AppConstants.LFD_ACTION_TYPE_DELETED_FROM_ALLOWLIST.equalsIgnoreCase(action)
				|| AppConstants.LFD_ACTION_TYPE_BULK_DELETED_FROM_ALLOWLIST
				.equalsIgnoreCase(action)) {
			auditTrailDto = new AllowListAuditTrailDto();
			AuditTrailNewOldValueDto auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.ORGNIZATION);
			auditTrailNewOldValueDto.setNewValue(tenantMap.get(logStoppageWhitelistDevices.getTenantId()));
			auditTrailDto.setCustomerName(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.PRODUCT_VENDOR);
			auditTrailNewOldValueDto.setNewValue(productMaster.getProductVendor());
			auditTrailDto.setDeviceVendor(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.PRODUCT_NAME);
			auditTrailNewOldValueDto.setNewValue(productMaster.getProductName());
			auditTrailDto.setDeviceProduct(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.DESCRIPTION);
			auditTrailNewOldValueDto.setNewValue(logStoppageWhitelistDevices.getDescription());
			auditTrailDto.setDescription(auditTrailNewOldValueDto);
			
			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.NOTE);
			auditTrailNewOldValueDto.setNewValue(deleteNote);
			auditTrailDto.setNote(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.PRODUCT_ID);
			auditTrailNewOldValueDto.setNewValue(String.valueOf(logStoppageWhitelistDevices.getProductId()));
			auditTrailDto.setProductId(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.TENANT_ID);
			auditTrailNewOldValueDto.setNewValue(String.valueOf(logStoppageWhitelistDevices.getTenantId()));
			auditTrailDto.setTenantId(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.DELETED);
			auditTrailNewOldValueDto.setNewValue(
					logStoppageWhitelistDevices.isDeleted() ? AuditTrailLableConstans.BOOLEN_TRUE_STRING_VALUE
							: AuditTrailLableConstans.BOOLEN_FALSE_STRING_VALUE);
			auditTrailDto.setDeleted(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.CREATED_DATE);
			auditTrailNewOldValueDto.setNewValue(Objects.nonNull(logStoppageWhitelistDevices.getAllowListedDate())
					? logStoppageWhitelistDevices.getAllowListedDate().format(AuditTrailLableConstans.formatter)
					: null);
			auditTrailDto.setCreatedDate(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.UPDATED_DATE);
			auditTrailNewOldValueDto.setNewValue(Objects.nonNull(logStoppageWhitelistDevices.getUpdatedDate())
					? logStoppageWhitelistDevices.getUpdatedDate().format(AuditTrailLableConstans.formatter)
					: null);
			auditTrailDto.setUpdatedDate(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.CREATED_BY);
			auditTrailNewOldValueDto.setNewValue(String.valueOf(logStoppageWhitelistDevices.getCreatedById()));
			auditTrailDto.setCreatedId(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.UPDATED_BY);
			auditTrailNewOldValueDto.setNewValue(String.valueOf(logStoppageWhitelistDevices.getUpdatedById()));
			auditTrailDto.setUpdatedId(auditTrailNewOldValueDto);

		}
		detailsJson.setType(action);
		detailsJson.setData(auditTrailDto);
		ObjectMapper objectMapper = new ObjectMapper();

		try {
			return objectMapper.writeValueAsString(detailsJson);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;

	}
	
	public static String getAuditTrailJsonResponseByDetectedDevices(String action,
			LogStoppageDetectedDevices logStoppageDetectedDevices, Map<Long, String> tenantMap,
			Map<Long, ProductMaster> productMasterMap, String note) {

		DetectedDevicesAuditTrailJsonResponse detailsJson = new DetectedDevicesAuditTrailJsonResponse();

		ProductMaster productMaster = productMasterMap
				.getOrDefault(Objects.nonNull(logStoppageDetectedDevices.getProductId())
						? logStoppageDetectedDevices.getProductId().longValue()
						: null, new ProductMaster());
		DetectedDevicesAuditTrailDto auditTrailDto = null;

		if (AppConstants.LFD_ACTION_TYPE_DELETED_ADD_TO_LOG_FLOW_MONITORING.equalsIgnoreCase(action)
				|| AppConstants.LFD_ACTION_TYPE_DELETED_ADD_TO_LOG_FLOW_MONITORING_BULK.equalsIgnoreCase(action)
				|| AppConstants.LFD_ACTION_TYPE_NOTE_ADDED_TO_ADD_TO_LOG_FLOW_MONITORING.equalsIgnoreCase(action)
				|| AppConstants.LFD_ACTION_TYPE_NOTE_BULK_ADDED_TO_ADD_TO_LOG_FLOW_MONITORING
						.equalsIgnoreCase(action)) {

			auditTrailDto = new DetectedDevicesAuditTrailDto();

			AuditTrailNewOldValueDto auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.ORGNIZATION);
			auditTrailNewOldValueDto.setNewValue(tenantMap.get(logStoppageDetectedDevices.getTenantId()));
			auditTrailDto.setCustomerName(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.PRODUCT_VENDOR);
			auditTrailNewOldValueDto.setNewValue(productMaster.getProductVendor());
			auditTrailDto.setDeviceVendor(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.PRODUCT_NAME);
			auditTrailNewOldValueDto.setNewValue(productMaster.getProductName());
			auditTrailDto.setDeviceProduct(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.PRODUCT_TYPE);
			auditTrailNewOldValueDto.setNewValue(productMaster.getProductType());
			auditTrailDto.setDeviceGroup(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.PRODUCT_IP);
			auditTrailNewOldValueDto.setNewValue(logStoppageDetectedDevices.getProductIP());
			auditTrailDto.setDeviceAddress(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.PRODUCT_HOST_NAME);
			auditTrailNewOldValueDto.setNewValue(logStoppageDetectedDevices.getProductHostName());
			auditTrailDto.setDeviceHostName(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.COLLECTOR_IP);
			auditTrailNewOldValueDto.setNewValue(logStoppageDetectedDevices.getCollectorAddress());
			auditTrailDto.setCollectorIp(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.COLLECTOR_HOSTNAME);
			auditTrailNewOldValueDto.setNewValue(logStoppageDetectedDevices.getCollectorHostName());
			auditTrailDto.setCollectorHostName(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.MDR_SCANNER_CODE);
			auditTrailNewOldValueDto.setNewValue(logStoppageDetectedDevices.getMdrScannerCode());
			auditTrailDto.setMdrScannerCode(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.NOTE);
			auditTrailNewOldValueDto.setNewValue(note);
			auditTrailDto.setNote(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.PRODUCT_ID);
			auditTrailNewOldValueDto.setNewValue(String.valueOf(logStoppageDetectedDevices.getProductId()));
			auditTrailDto.setProductId(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.TENANT_ID);
			auditTrailNewOldValueDto.setNewValue(String.valueOf(logStoppageDetectedDevices.getTenantId()));
			auditTrailDto.setTenantId(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.DELETED);
			auditTrailNewOldValueDto.setNewValue(
					logStoppageDetectedDevices.isDeleted() ? AuditTrailLableConstans.BOOLEN_TRUE_STRING_VALUE
							: AuditTrailLableConstans.BOOLEN_FALSE_STRING_VALUE);
			auditTrailDto.setDeleted(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.WHILTELIST);
			auditTrailNewOldValueDto.setNewValue(
					logStoppageDetectedDevices.isWhitelist() ? AuditTrailLableConstans.BOOLEN_TRUE_STRING_VALUE
							: AuditTrailLableConstans.BOOLEN_FALSE_STRING_VALUE);
			auditTrailDto.setWhitelist(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.LAST_EVENT_RECEIVED);
			auditTrailNewOldValueDto.setNewValue(Objects.nonNull(logStoppageDetectedDevices.getLastEventReceived())
					? logStoppageDetectedDevices.getLastEventReceived().format(AuditTrailLableConstans.formatter)
					: null);
			auditTrailDto.setLastEventReceived(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.CREATED_DATE);
			auditTrailNewOldValueDto.setNewValue(Objects.nonNull(logStoppageDetectedDevices.getDetectedDate())
					? logStoppageDetectedDevices.getDetectedDate().format(AuditTrailLableConstans.formatter)
					: null);
			auditTrailDto.setCreatedDate(auditTrailNewOldValueDto);

			auditTrailNewOldValueDto = new AuditTrailNewOldValueDto();
			auditTrailNewOldValueDto.setLabel(AuditTrailLableConstans.UPDATED_DATE);
			auditTrailNewOldValueDto.setNewValue(Objects.nonNull(logStoppageDetectedDevices.getUpdatedDate())
					? logStoppageDetectedDevices.getUpdatedDate().format(AuditTrailLableConstans.formatter)
					: null);
			auditTrailDto.setUpdatedDate(auditTrailNewOldValueDto);

		}

		detailsJson.setType(action);
		detailsJson.setData(auditTrailDto);
		ObjectMapper objectMapper = new ObjectMapper();

		try {
			return objectMapper.writeValueAsString(detailsJson);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Specification<AuditTrail> getAuditTrailListFilter(AuditTrailListRequest auditTrailListRequest,Set<Long> auditTrailMasterId) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();

			if (!ObjectUtils.isEmpty(auditTrailListRequest.getSearchOrgIds()))
				predicates.add(criteriaBuilder.in(root.get("tenantId")).value(auditTrailListRequest.getSearchOrgIds()));

			if (Objects.nonNull(auditTrailListRequest.getSearchFromDate())) {
				predicates.add(criteriaBuilder.greaterThan(root.get("createdDate"),auditTrailListRequest.getSearchFromDate()));
			}
			if (Objects.nonNull(auditTrailListRequest.getSearchToDate())) {
				predicates.add(criteriaBuilder.lessThan(root.get("createdDate"),auditTrailListRequest.getSearchToDate()));
			}

			predicates.add(criteriaBuilder.in(root.get("auditTrailMasterId")).value(auditTrailMasterId));
			if (auditTrailListRequest.getSearchType() == 1) {

				List<Predicate> freeSearchPredicates = new ArrayList<>();
				Map<Search, String> searchMap = auditTrailListRequest.getSearch();
				String searchTxt = "";
				if (searchMap != null && searchMap.size() > 0) {
					searchTxt = searchMap.get(Search.value);
				}
				if (StringUtils.isNotEmpty(searchTxt)) {
					freeSearchPredicates.add(criteriaBuilder.like(root.get("details"), AppConstants.PERCENTAGE
							+ "\"label\":\"Product Vendor\",\"newValue\":\"" + searchTxt + AppConstants.PERCENTAGE));
					freeSearchPredicates.add(criteriaBuilder.like(root.get("details"), AppConstants.PERCENTAGE
							+ "\"label\":\"Product Name\",\"newValue\":\"" + searchTxt + AppConstants.PERCENTAGE));

					predicates.add(criteriaBuilder.or(freeSearchPredicates.toArray(new Predicate[0])));
				}

			}
			query.orderBy(criteriaBuilder.desc(root.get("createdDate")));
			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}
}
