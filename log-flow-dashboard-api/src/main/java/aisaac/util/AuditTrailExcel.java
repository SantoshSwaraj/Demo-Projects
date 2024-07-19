package aisaac.util;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import aisaac.dto.AuditTrailNewOldValueDto;
import aisaac.dto.CustomTimeFields;
import aisaac.payload.response.AuditTrailListResponse;

public class AuditTrailExcel implements Excel{

	private List<Object> listRecords;
	private XSSFWorkbook workbook;
	private XSSFSheet sheet;
	int rowCount;
	int totalRecords;

	public AuditTrailExcel() {
		rowCount = 1;
		workbook = new XSSFWorkbook();
	}

	public AuditTrailExcel(List<Object> liObjects) {
		rowCount = 1;
		workbook = new XSSFWorkbook();
		this.listRecords = liObjects;
	}
	@Override
	public void writeHeader() {
		sheet = workbook.createSheet("Sheet1");

		Row row = sheet.createRow(0);

		CellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setBold(true);
		font.setFontHeight(11);
		style.setFont(font);

		AtomicInteger columnCount = new AtomicInteger();
		AppConstants.AUDIT_TRAIL_EXPORT_FIELDS.stream()
				.forEach(e -> createCell(row, columnCount.getAndIncrement(), e, style));

	}

	@Override
	public void createCell(Row row, int columnCount, Object value, CellStyle style) {

		if (ObjectUtils.isEmpty(value)) {
			value = StringUtils.EMPTY;
		}
		Cell cell = row.createCell(columnCount);
		if (value instanceof Integer) {
			cell.setCellValue((Integer) value);
		} else if (value instanceof Long) {
			cell.setCellValue((Long) value);
		} else if (value instanceof Boolean) {
			cell.setCellValue((Boolean) value);
		} else if (value instanceof LocalDateTime) {
			cell.setCellValue((LocalDateTime) value);
		} else if (value instanceof String) {
			cell.setCellValue((String) value);
		} else {
			cell.setBlank();
		}
		cell.setCellStyle(style);
		
		if(rowCount==totalRecords)
			sheet.autoSizeColumn(columnCount);
	}

	@Override
	public void write() {

		CellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setFontHeight(11);
		style.setFont(font);

		CellStyle wrapTextStyle = workbook.createCellStyle();
		wrapTextStyle.setWrapText(true);
		wrapTextStyle.setFont(font);
		
		totalRecords = listRecords.size();

		listRecords.forEach(obj -> {

			AuditTrailListResponse auditTrailResponse = (AuditTrailListResponse) obj;

			Row row = sheet.createRow(rowCount++);
			AtomicInteger columnCount = new AtomicInteger();

			createCell(row, columnCount.getAndIncrement(), auditTrailResponse.getOrganization(), style);

			createCell(row, columnCount.getAndIncrement(),
					auditTrailResponse.getProductVendor() + " " + auditTrailResponse.getProductName(), style);

			createCell(row, columnCount.getAndIncrement(), auditTrailResponse.getActionDesc(), style);
			createCell(row, columnCount.getAndIncrement(), auditTrailResponse.getCreatorDisplayName(), style);

			createCell(row, columnCount.getAndIncrement(),
					getLocalDateTimeFromMilliSec(auditTrailResponse.getCreatedDateTime()), style);

			createCell(row, columnCount.getAndIncrement(),
					getAuditTrailDetailsFormat(auditTrailResponse.getDetails(), auditTrailResponse.getActionDesc()),
					wrapTextStyle);

		});
	}

	@Override
	public Workbook workbook() {
		return workbook;
	}

	private String getLocalDateTimeFromMilliSec(Long milliSec) {
		return LogFlowDashboardUtils.getLocalDateTimeInStringFormat(LogFlowDashboardUtils.getUtcDateTime(milliSec));
	}
	
	private String getAuditTrailDetailsFormat(String details, String actionDesc) {
		ObjectMapper objectMapper = new ObjectMapper();
		StringBuilder response = new StringBuilder();
		try {
			JsonNode deJsonNode = objectMapper.readTree(details);
			if (Objects.nonNull(deJsonNode.get("data"))) {
				JsonNode data = objectMapper.readTree(deJsonNode.get("data").toString());
				if (actionDesc.contains("edited")) {
					for (String key : AppConstants.AUDIT_TRAIL_DETAIL_FILED_EDIT_ACTION) {
						if (data.get(key) != null) {
							AuditTrailNewOldValueDto dto = objectMapper.readValue(data.get(key).toString(),
									AuditTrailNewOldValueDto.class);
							if (Objects.nonNull(dto) && ((Objects.nonNull(dto.getNewValue())
									&& !dto.getNewValue().equalsIgnoreCase(dto.getOldValue())
									|| (Objects.nonNull(dto.getOldValue())
											&& !dto.getOldValue().equalsIgnoreCase(dto.getNewValue()))))) {

								if (!response.isEmpty()) {
									response.append("\n");
								}
								if (key.equalsIgnoreCase("logStoppageThresholdJson")
										|| key.equalsIgnoreCase("emailNotificationFrequencyJson")) {
									CustomTimeFields customTimeFieldsNew = objectMapper.readValue(dto.getNewValue(),
											CustomTimeFields.class);
									CustomTimeFields customTimeFieldsOld = objectMapper.readValue(dto.getOldValue(),
											CustomTimeFields.class);
									if (customTimeFieldsNew.getIscustom() && customTimeFieldsOld.getIscustom()) {
										response.append(
												dto.getLabel() + " changed from " + customTimeFieldsOld.getCustomValue()
														+ " " + nullCheck(customTimeFieldsOld.getCustomUnit()) + " to "
														+ customTimeFieldsNew.getCustomValue() + " "
														+ nullCheck(customTimeFieldsNew.getCustomUnit()));
									} else if (customTimeFieldsNew.getIscustom()) {
										response.append(dto.getLabel() + " changed from "
												+ nullCheck(customTimeFieldsOld.getValue()) + " to "
												+ customTimeFieldsNew.getCustomValue() + " "
												+ nullCheck(customTimeFieldsNew.getCustomUnit()));
									} else if (customTimeFieldsOld.getIscustom()) {
										response.append(
												dto.getLabel() + " changed from " + customTimeFieldsOld.getCustomValue()
														+ " " + nullCheck(customTimeFieldsOld.getCustomUnit()) + " to "
														+ nullCheck(customTimeFieldsNew.getValue()));
									} else {
										response.append(dto.getLabel() + " changed from "
												+ nullCheck(customTimeFieldsOld.getValue()) + " to "
												+ nullCheck(customTimeFieldsNew.getValue()));
									}
								} else
									response.append(dto.getLabel() + " changed from " + nullCheck(dto.getOldValue())
											+ " to " + nullCheck(dto.getNewValue()));
							}
						}
					}
				} else if ((actionDesc.contains("added") || actionDesc.contains("deleted"))) {
					for (String key : AppConstants.AUDIT_TRAIL_DETAIL_FILED) {
						if (data.get(key) != null) {
							AuditTrailNewOldValueDto dto = objectMapper.readValue(data.get(key).toString(),
									AuditTrailNewOldValueDto.class);

							if (Objects.nonNull(dto) && ((actionDesc.contains("Product added")
									&& !"Note".contains(dto.getLabel())
									&& !(actionDesc.equalsIgnoreCase(
											"Product added to Allowlist from Add to Log Flow Monitoring")
									&& "Description".contains(dto.getLabel())))
									|| (actionDesc.toLowerCase().contains("note") && "Note".contains(dto.getLabel()))
									|| (actionDesc.contains("deleted") && !actionDesc.contains("deleted from Allowlist")
											&& !"Note".contains(dto.getLabel()))
									|| actionDesc.contains("deleted from Allowlist"))) {

								if (!response.isEmpty()) {
									response.append("\n");
								}
								response.append(dto.getLabel() + " : " + nullCheck(dto.getNewValue()));

							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response.toString();
	}
	
	private String nullCheck(String value) {
		if(StringUtils.isBlank(value)) {
			return "-";
		}
		return value;
	}
}
