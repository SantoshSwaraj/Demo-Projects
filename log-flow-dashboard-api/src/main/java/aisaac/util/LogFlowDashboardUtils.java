package aisaac.util;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import aisaac.dto.CustomTimeFields;
import aisaac.dto.MinutesAndLastEventDto;
import aisaac.entities.LogStoppageDetectedDevices;
import aisaac.entities.ProductMaster;
import jakarta.persistence.Column;

public class LogFlowDashboardUtils {

	private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter
			.ofPattern(AppConstants.EXPORT_GENERIC_DATETIME_FORMAT_STR);

	public static <T> String getTableColumnNameUsingEntityVariable(String name, Class<T> className) {
		for (Field field : className.getDeclaredFields()) {
			Column column = field.getAnnotation(Column.class);
			if (column != null && name.equals(field.getName())) {
				return column.name();
			}
		}
		return null;
	}

	public static List<String> getColumnNamesFromExcelRow(Row row) {
		return IntStream.range(0, row.getLastCellNum())
				.mapToObj(i -> row.getCell(i).getStringCellValue().replace("*", "").toLowerCase())
				.collect(Collectors.toList());
	}

	public static MinutesAndLastEventDto getMinutesDifferenceFromDetectedDeviceCreatedDateAndLastEventReceived(
			LogStoppageDetectedDevices log) {
		if (log.getLastEventReceived() != null) {
			MinutesAndLastEventDto dto = new MinutesAndLastEventDto();
			dto.setMinutes(ChronoUnit.MINUTES.between(log.getLastEventReceived(),LocalDateTime.now()));
			dto.setLastEventReceived(log.getLastEventReceived());
			return dto;

		}
		return null;
	}

	public static Integer getMinutesFromCustomText(CustomTimeFields jsonRequest, String applicationCustomTrueSettings,
			String applicationCustomFalseSettings) {

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonApplicationCustomTrueSettings;
		JsonNode jsonApplicationCustomFalseSettings;
		try {
			jsonApplicationCustomTrueSettings = objectMapper.readTree(applicationCustomTrueSettings);
			jsonApplicationCustomFalseSettings = objectMapper.readTree(applicationCustomFalseSettings);
			if (jsonRequest.getIscustom()) {
				int customValue = jsonRequest.getCustomValue();
				String customUnit = jsonRequest.getCustomUnit();
				int minutes = jsonApplicationCustomTrueSettings.get(customUnit).asInt();
				return minutes * customValue;

			} else {
				String value = jsonRequest.getValue();
				int minutes = jsonApplicationCustomFalseSettings.get(value).asInt();
				return minutes * AppConstants.HOUR_TO_MINUTE;
			}
		} catch (JsonProcessingException e) {
			return null;
		}

	}

	public static String CustomTimeFieldsEntityToString(CustomTimeFields entity) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			return objectMapper.writeValueAsString(entity);
		} catch (JsonProcessingException e) {
			return null;
		}
	}

	public static String decideMonitorStatus(boolean suppressed, boolean disabled) {

		if(!suppressed && !disabled)
			return AppConstants.MONITOR_STATUS_ACTIVE;
		if(disabled)
			return AppConstants.MONITOR_STATUS_DISABLED;
		if(suppressed)
			return AppConstants.MONITOR_STATUS_SUPPRESSED;
		
		return "";
	}

	public static CustomTimeFields StringToCustomTimeFieldsEntity(String data) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			if (StringUtils.isBlank(data)) {
				return new CustomTimeFields();
			}
			return objectMapper.readValue(data, CustomTimeFields.class);
		} catch (JsonProcessingException e) {
			return null;
		}
	}

	public static List<Object> getListFromJsonString(String jsonString) {
		ObjectMapper objectMapper = new ObjectMapper();
		List<Object> keysList = new ArrayList<>();
		try {
			JsonNode jsonNode = objectMapper.readTree(jsonString);
			Iterator<String> fieldNames = jsonNode.fieldNames();
			while (fieldNames.hasNext()) {
				String key = fieldNames.next();
				keysList.add(key);
			}
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return keysList;

	}

	public static String[] removeSlashFromString(String data){
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			if (StringUtils.isBlank(data)) {
				return null;
			}
			return objectMapper.readValue(data, String[].class);
		} catch (JsonProcessingException e) {
			return null;
		}
	}
	
	public static Long getLocalDateTimeInMilliSec(LocalDateTime date) {
		if (Objects.isNull(date)) {
			return null;
		}
		return date.toInstant(ZoneOffset.UTC).toEpochMilli();
	}
	
	public static LocalDateTime getLocalDateTimeByEpochMilli(Long value) {
		if (Objects.isNull(value)) {
			return null;
		}
		return Instant.ofEpochMilli(value).atZone(ZoneOffset.UTC).toLocalDateTime();
	}

	public static String getDisplayUsername(String displayName, String displayrole, Boolean isDeleted,
			Integer isDisabled) {

		String displayNameStr = displayName;
		if (displayName==null)
			return null;

		if (isDeleted == null)
			isDeleted = false;

		if (isDisabled == null)
			isDisabled = 0;

		if (isDeleted) {
			displayNameStr = "Deleted User";
		} else if ((AppConstants.USERSTATUS).equals(isDisabled)) {
			displayNameStr += " (Disabled User)";
		} else if (StringUtils.isNotEmpty(displayrole))
			displayNameStr += "(" + displayrole + ")";

		return displayNameStr;
	}

	public static String getProductNameById(Integer id, Map<Long, ProductMaster> productMasterMap) {
		if (Objects.isNull(id)) {
			id = 0;
		}
		return productMasterMap.getOrDefault(id.longValue(), new ProductMaster()).getProductName();
	}

	public static String getProductVendorById(Integer id, Map<Long, ProductMaster> productMasterMap) {
		if (Objects.isNull(id)) {
			id = 0;
		}
		return productMasterMap.getOrDefault(id.longValue(), new ProductMaster()).getProductVendor();
	}
	
	public static String getProductTypeById(Integer id, Map<Long, ProductMaster> productMasterMap) {
		if (Objects.isNull(id)) {
			id = 0;
		}
		return productMasterMap.getOrDefault(id.longValue(), new ProductMaster()).getProductType();
	}

	public static LocalDateTime getUtcDateTime(Long epochMilliSeconds) {
		if (Objects.isNull(epochMilliSeconds) || epochMilliSeconds.intValue() == 0) {
			return null;
		}
		return Instant.ofEpochMilli(epochMilliSeconds).atZone(ZoneId.of("UTC")).toLocalDateTime();
	}

	public static String getLocalDateTimeInStringFormat(LocalDateTime localDateTime) {
		if (Objects.isNull(localDateTime))
			return null;

		return localDateTime.format(dateTimeFormatter);
	}

	public static String getStringFromCustomTimeFields(CustomTimeFields customTimeFields) {
		if (Objects.isNull(customTimeFields)) {
			return null;
		}
		if (Objects.nonNull(customTimeFields.getIscustom())	&& customTimeFields.getIscustom()) {
			return customTimeFields.getCustomValue() + AppConstants.SPACE + customTimeFields.getCustomUnit();
		} else {
			return customTimeFields.getValue();
		}
	}
	
	public static Optional<List<Long>> getProductIdsByProductVendorOrNameOrType(List<ProductMaster> productMaster,
			String vendor, String name, String type, Integer searchType) {

		/**
		 * return NULL If vendor is NULL or EMPTY If name is NULL or EMPTY If type is
		 * NULL or EMPTY
		 */
		if (StringUtils.isBlank(vendor) && StringUtils.isBlank(name) && StringUtils.isBlank(type)) {
			return Optional.ofNullable(null);
		}

		/**
		 * return NULL productMaster is NULL or EMPTY
		 */
		if (CollectionUtils.isEmpty(productMaster)) {
			return Optional.ofNullable(null);
		}

		//Free search filter condition
		if (Objects.nonNull(searchType) && searchType == 1) {

			/**
			 * return productIds If vendor is not NULL If name is not NULL If type is not
			 * NULL
			 */
			if (StringUtils.isNotBlank(vendor) && StringUtils.isNotBlank(name) && StringUtils.isNotBlank(type)) {
				return Optional.ofNullable(productMaster.stream().filter(Objects::nonNull)
						.filter(e -> ((Objects.nonNull(e.getProductVendor())
								&& e.getProductVendor().toLowerCase().contains(vendor.trim().toLowerCase()))
								|| (Objects.nonNull(e.getProductName())
										&& e.getProductName().toLowerCase().contains(name.trim().toLowerCase()))
								|| (Objects.nonNull(e.getProductType())
										&& e.getProductType().toLowerCase().contains(type.trim().toLowerCase()))))
						.map(ProductMaster::getProductId).collect(Collectors.toList()));
			}

			/**
			 * return productIds If vendor is not NULL If name is not NULL
			 */
			if (StringUtils.isNotBlank(vendor) && StringUtils.isNotBlank(name)) {
				return Optional.ofNullable(productMaster.stream().filter(Objects::nonNull)
						.filter(e -> ((Objects.nonNull(e.getProductVendor())
								&& e.getProductVendor().toLowerCase().contains(vendor.trim().toLowerCase()))
								|| (Objects.nonNull(e.getProductName())
										&& e.getProductName().toLowerCase().contains(name.trim().toLowerCase()))))
						.map(ProductMaster::getProductId).collect(Collectors.toList()));
			}

			/**
			 * return productIds If name is not NULL If type is not NULL
			 */
			if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(type)) {
				return Optional.ofNullable(productMaster.stream().filter(Objects::nonNull)
						.filter(e -> ((Objects.nonNull(e.getProductName())
								&& e.getProductName().toLowerCase().contains(name.trim().toLowerCase()))
								|| (Objects.nonNull(e.getProductType())
										&& e.getProductType().toLowerCase().contains(type.trim().toLowerCase()))))
						.map(ProductMaster::getProductId).collect(Collectors.toList()));
			}

			/**
			 * return productIds If vendor is not NULL If type is not NULL
			 */
			if (StringUtils.isNotBlank(vendor) && StringUtils.isNotBlank(type)) {
				return Optional.ofNullable(productMaster.stream().filter(Objects::nonNull)
						.filter(e -> ((Objects.nonNull(e.getProductVendor())
								&& e.getProductVendor().toLowerCase().contains(vendor.trim().toLowerCase()))
								|| (Objects.nonNull(e.getProductType())
										&& e.getProductType().toLowerCase().contains(type.trim().toLowerCase()))))
						.map(ProductMaster::getProductId).collect(Collectors.toList()));
			}
		} else {

			/**
			 * return productIds If vendor is not NULL If name is not NULL If type is not
			 * NULL
			 */
			if (StringUtils.isNotBlank(vendor) && StringUtils.isNotBlank(name) && StringUtils.isNotBlank(type)) {
				return Optional.ofNullable(productMaster.stream().filter(Objects::nonNull)
						.filter(e -> ((Objects.nonNull(e.getProductVendor())
								&& e.getProductVendor().toLowerCase().contains(vendor.trim().toLowerCase()))
								&& (Objects.nonNull(e.getProductName())
										&& e.getProductName().toLowerCase().contains(name.trim().toLowerCase()))
								&& (Objects.nonNull(e.getProductType())
										&& e.getProductType().toLowerCase().contains(type.trim().toLowerCase()))))
						.map(ProductMaster::getProductId).collect(Collectors.toList()));
			}

			/**
			 * return productIds If vendor is not NULL If name is not NULL
			 */
			if (StringUtils.isNotBlank(vendor) && StringUtils.isNotBlank(name)) {
				return Optional.ofNullable(productMaster.stream().filter(Objects::nonNull)
						.filter(e -> ((Objects.nonNull(e.getProductVendor())
								&& e.getProductVendor().toLowerCase().contains(vendor.trim().toLowerCase()))
								&& (Objects.nonNull(e.getProductName())
										&& e.getProductName().toLowerCase().contains(name.trim().toLowerCase()))))
						.map(ProductMaster::getProductId).collect(Collectors.toList()));
			}

			/**
			 * return productIds If name is not NULL If type is not NULL
			 */
			if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(type)) {
				return Optional.ofNullable(productMaster.stream().filter(Objects::nonNull)
						.filter(e -> ((Objects.nonNull(e.getProductName())
								&& e.getProductName().toLowerCase().contains(name.trim().toLowerCase()))
								&& (Objects.nonNull(e.getProductType())
										&& e.getProductType().toLowerCase().contains(type.trim().toLowerCase()))))
						.map(ProductMaster::getProductId).collect(Collectors.toList()));
			}

			/**
			 * return productIds If vendor is not NULL If type is not NULL
			 */
			if (StringUtils.isNotBlank(vendor) && StringUtils.isNotBlank(type)) {
				return Optional.ofNullable(productMaster.stream().filter(Objects::nonNull)
						.filter(e -> ((Objects.nonNull(e.getProductVendor())
								&& e.getProductVendor().toLowerCase().contains(vendor.trim().toLowerCase()))
								&& (Objects.nonNull(e.getProductType())
										&& e.getProductType().toLowerCase().contains(type.trim().toLowerCase()))))
						.map(ProductMaster::getProductId).collect(Collectors.toList()));
			}
		}
		/**
		 * return productIds If vendor is not NULL
		 */
		if (StringUtils.isNotBlank(vendor)) {
			return Optional.ofNullable(productMaster.stream().filter(Objects::nonNull)
					.filter(e -> (Objects.nonNull(e.getProductVendor())
							&& e.getProductVendor().toLowerCase().contains(vendor.trim().toLowerCase())))
					.map(ProductMaster::getProductId).collect(Collectors.toList()));
		}
		/**
		 * return productIds If name is not NULL
		 */
		if (StringUtils.isNotBlank(name)) {
			return Optional.ofNullable(productMaster.stream().filter(Objects::nonNull)
					.filter(e -> (Objects.nonNull(e.getProductName())
							&& e.getProductName().toLowerCase().contains(name.trim().toLowerCase())))
					.map(ProductMaster::getProductId).collect(Collectors.toList()));
		}

		/**
		 * return productIds If type is not NULL
		 */
		if (StringUtils.isNotBlank(type)) {
			return Optional.ofNullable(productMaster.stream().filter(Objects::nonNull)
					.filter(e -> (Objects.nonNull(e.getProductType())
							&& e.getProductType().toLowerCase().contains(type.trim().toLowerCase())))
					.map(ProductMaster::getProductId).collect(Collectors.toList()));
		}

		return Optional.ofNullable(null);
	}

	public static String getDeviceStatusByNumber(Integer deviceStatus) {
		if (ObjectUtils.isNotEmpty(deviceStatus)) {
			return deviceStatus == 0 ? AppConstants.LFD_DEVICE_STATUS_STOPPED : AppConstants.LFD_DEVICE_STATUS_RUNNING;
		} else {
			return null;
		}
	}
	
	public static Optional<List<Long>> getProductTypeIdsByProductVendorOrNameOrType(List<ProductMaster> productMaster,
			String vendor, String name, String type) {


		/**
		 * return NULL If vendor is NULL or EMPTY If name is NULL or EMPTY If type is
		 * NULL or EMPTY
		 */
		if (StringUtils.isBlank(vendor) && StringUtils.isBlank(name) && StringUtils.isBlank(type)) {
			return Optional.ofNullable(null);
		}

		/**
		 * return NULL productMaster is NULL or EMPTY
		 */
		if (CollectionUtils.isEmpty(productMaster)) {
			return Optional.ofNullable(null);
		}

		/**
		 * return productIds If vendor is not NULL If name is not NULL If type is not
		 * NULL
		 */
		if (StringUtils.isNotBlank(vendor) && StringUtils.isNotBlank(name) && StringUtils.isNotBlank(type)) {
			return Optional.ofNullable(productMaster.stream().filter(Objects::nonNull)
					.filter(e -> (Objects.nonNull(e.getProductVendor())
							&& e.getProductVendor().toLowerCase().contains(vendor.trim().toLowerCase())
							&& Objects.nonNull(e.getProductName())
							&& e.getProductName().toLowerCase().contains(name.trim().toLowerCase())
							&& Objects.nonNull(e.getProductType())
							&& e.getProductType().toLowerCase().contains(type.trim().toLowerCase())))
					.map(ProductMaster::getProductTypeId).collect(Collectors.toList()));
		}

		/**
		 * return productIds If name is not NULL If type is not NULL
		 */
		if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(type)) {
			return Optional.ofNullable(productMaster.stream().filter(Objects::nonNull)
					.filter(e -> (Objects.nonNull(e.getProductName())
							&& e.getProductName().toLowerCase().contains(name.trim().toLowerCase())
							&& Objects.nonNull(e.getProductType())
							&& e.getProductType().toLowerCase().contains(type.trim().toLowerCase())))
					.map(ProductMaster::getProductTypeId).collect(Collectors.toList()));
		}

		/**
		 * return productIds If vendor is not NULL If type is not NULL
		 */
		if (StringUtils.isNotBlank(vendor) && StringUtils.isNotBlank(type)) {
			return Optional.ofNullable(productMaster.stream().filter(Objects::nonNull)
					.filter(e -> (Objects.nonNull(e.getProductVendor())
							&& e.getProductVendor().toLowerCase().contains(vendor.trim().toLowerCase())
							&& Objects.nonNull(e.getProductType())
							&& e.getProductType().toLowerCase().contains(type.trim().toLowerCase())))
					.map(ProductMaster::getProductTypeId).collect(Collectors.toList()));
		}


		/**
		 * return productIds If type is not NULL
		 */
		if (StringUtils.isNotBlank(type)) {
			return Optional.ofNullable(productMaster.stream().filter(Objects::nonNull)
					.filter(e -> (Objects.nonNull(e.getProductType())
							&& e.getProductType().toLowerCase().contains(type.trim().toLowerCase())))
					.map(ProductMaster::getProductTypeId).collect(Collectors.toList()));
		}

		return Optional.ofNullable(null);
	
	}
	
	public static Optional<List<Long>> getProductIdsByProductVendorOrNameOrTypeForAdvanceSearch(List<ProductMaster> productMaster,
			String vendor, String name, String type) {

		/**
		 * return NULL If vendor is NULL or EMPTY If name is NULL or EMPTY If type is
		 * NULL or EMPTY
		 */
		if (StringUtils.isBlank(vendor) && StringUtils.isBlank(name) && StringUtils.isBlank(type)) {
			return Optional.ofNullable(null);
		}

		/**
		 * return NULL productMaster is NULL or EMPTY
		 */
		if (CollectionUtils.isEmpty(productMaster)) {
			return Optional.ofNullable(null);
		}

		/**
		 * return productIds If vendor is not NULL If name is not NULL If type is not
		 * NULL
		 */
		if (StringUtils.isNotBlank(vendor) && StringUtils.isNotBlank(name) && StringUtils.isNotBlank(type)) {
			return Optional.ofNullable(productMaster.stream().filter(Objects::nonNull)
					.filter(e -> (Objects.nonNull(e.getProductVendor())
							&& e.getProductVendor().toLowerCase().equals(vendor.trim().toLowerCase())
							&& Objects.nonNull(e.getProductName())
							&& e.getProductName().toLowerCase().equals(name.trim().toLowerCase())
							&& Objects.nonNull(e.getProductType())
							&& e.getProductType().toLowerCase().equals(type.trim().toLowerCase())))
					.map(ProductMaster::getProductId).collect(Collectors.toList()));
		}

		/**
		 * return productIds If vendor is not NULL If name is not NULL
		 */
		if (StringUtils.isNotBlank(vendor) && StringUtils.isNotBlank(name)) {
			return Optional.ofNullable(productMaster.stream().filter(Objects::nonNull)
					.filter(e -> (Objects.nonNull(e.getProductVendor())
							&& e.getProductVendor().toLowerCase().equals(vendor.trim().toLowerCase())
							&& Objects.nonNull(e.getProductName())
							&& e.getProductName().toLowerCase().equals(name.trim().toLowerCase())))
					.map(ProductMaster::getProductId).collect(Collectors.toList()));
		}

		/**
		 * return productIds If name is not NULL If type is not NULL
		 */
		if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(type)) {
			return Optional.ofNullable(productMaster.stream().filter(Objects::nonNull)
					.filter(e -> (Objects.nonNull(e.getProductName())
							&& e.getProductName().toLowerCase().equals(name.trim().toLowerCase())
							&& Objects.nonNull(e.getProductType())
							&& e.getProductType().toLowerCase().equals(type.trim().toLowerCase())))
					.map(ProductMaster::getProductId).collect(Collectors.toList()));
		}

		/**
		 * return productIds If vendor is not NULL If type is not NULL
		 */
		if (StringUtils.isNotBlank(vendor) && StringUtils.isNotBlank(type)) {
			return Optional.ofNullable(productMaster.stream().filter(Objects::nonNull)
					.filter(e -> (Objects.nonNull(e.getProductVendor())
							&& e.getProductVendor().toLowerCase().equals(vendor.trim().toLowerCase())
							&& Objects.nonNull(e.getProductType())
							&& e.getProductType().toLowerCase().equals(type.trim().toLowerCase())))
					.map(ProductMaster::getProductId).collect(Collectors.toList()));
		}

		/**
		 * return productIds If vendor is not NULL
		 */
		if (StringUtils.isNotBlank(vendor)) {
			return Optional.ofNullable(productMaster.stream().filter(Objects::nonNull)
					.filter(e -> (Objects.nonNull(e.getProductVendor())
							&& e.getProductVendor().toLowerCase().equals(vendor.trim().toLowerCase())))
					.map(ProductMaster::getProductId).collect(Collectors.toList()));
		}
		/**
		 * return productIds If name is not NULL
		 */
		if (StringUtils.isNotBlank(name)) {
			return Optional.ofNullable(productMaster.stream().filter(Objects::nonNull)
					.filter(e -> (Objects.nonNull(e.getProductName())
							&& e.getProductName().toLowerCase().equals(name.trim().toLowerCase())))
					.map(ProductMaster::getProductId).collect(Collectors.toList()));
		}

		/**
		 * return productIds If type is not NULL
		 */
		if (StringUtils.isNotBlank(type)) {
			return Optional.ofNullable(productMaster.stream().filter(Objects::nonNull)
					.filter(e -> (Objects.nonNull(e.getProductType())
							&& e.getProductType().toLowerCase().equals(type.trim().toLowerCase())))
					.map(ProductMaster::getProductId).collect(Collectors.toList()));
		}

		return Optional.ofNullable(null);
	}
	public static Optional<List<Long>> getProductTypeIdsByProductVendorOrNameOrTypeForAdvanceSearch(List<ProductMaster> productMaster,
			String vendor, String name, String type) {


		/**
		 * return NULL If vendor is NULL or EMPTY If name is NULL or EMPTY If type is
		 * NULL or EMPTY
		 */
		if (StringUtils.isBlank(vendor) && StringUtils.isBlank(name) && StringUtils.isBlank(type)) {
			return Optional.ofNullable(null);
		}

		/**
		 * return NULL productMaster is NULL or EMPTY
		 */
		if (CollectionUtils.isEmpty(productMaster)) {
			return Optional.ofNullable(null);
		}

		/**
		 * return productIds If vendor is not NULL If name is not NULL If type is not
		 * NULL
		 */
		if (StringUtils.isNotBlank(vendor) && StringUtils.isNotBlank(name) && StringUtils.isNotBlank(type)) {
			return Optional.ofNullable(productMaster.stream().filter(Objects::nonNull)
					.filter(e -> (Objects.nonNull(e.getProductVendor())
							&& e.getProductVendor().toLowerCase().equals(vendor.trim().toLowerCase())
							&& Objects.nonNull(e.getProductName())
							&& e.getProductName().toLowerCase().equals(name.trim().toLowerCase())
							&& Objects.nonNull(e.getProductType())
							&& e.getProductType().toLowerCase().equals(type.trim().toLowerCase())))
					.map(ProductMaster::getProductTypeId).collect(Collectors.toList()));
		}

		/**
		 * return productIds If name is not NULL If type is not NULL
		 */
		if (StringUtils.isNotBlank(name) && StringUtils.isNotBlank(type)) {
			return Optional.ofNullable(productMaster.stream().filter(Objects::nonNull)
					.filter(e -> (Objects.nonNull(e.getProductName())
							&& e.getProductName().toLowerCase().equals(name.trim().toLowerCase())
							&& Objects.nonNull(e.getProductType())
							&& e.getProductType().toLowerCase().equals(type.trim().toLowerCase())))
					.map(ProductMaster::getProductTypeId).collect(Collectors.toList()));
		}

		/**
		 * return productIds If vendor is not NULL If type is not NULL
		 */
		if (StringUtils.isNotBlank(vendor) && StringUtils.isNotBlank(type)) {
			return Optional.ofNullable(productMaster.stream().filter(Objects::nonNull)
					.filter(e -> (Objects.nonNull(e.getProductVendor())
							&& e.getProductVendor().toLowerCase().equals(vendor.trim().toLowerCase())
							&& Objects.nonNull(e.getProductType())
							&& e.getProductType().toLowerCase().equals(type.trim().toLowerCase())))
					.map(ProductMaster::getProductTypeId).collect(Collectors.toList()));
		}


		/**
		 * return productIds If type is not NULL
		 */
		if (StringUtils.isNotBlank(type)) {
			return Optional.ofNullable(productMaster.stream().filter(Objects::nonNull)
					.filter(e -> (Objects.nonNull(e.getProductType())
							&& e.getProductType().toLowerCase().equals(type.trim().toLowerCase())))
					.map(ProductMaster::getProductTypeId).collect(Collectors.toList()));
		}

		return Optional.ofNullable(null);
	
	}
}
