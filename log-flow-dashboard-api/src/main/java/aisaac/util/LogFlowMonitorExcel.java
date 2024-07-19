package aisaac.util;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import aisaac.dto.NotesFormatDto;
import aisaac.payload.response.LFMResponse;

public class LogFlowMonitorExcel implements Excel {

	private List<Object> listRecords;
	private Map<Long, String> assetIdBuMap;
	private boolean buRequired;
	private XSSFWorkbook workbook;
	private XSSFSheet sheet;
	int rowCount;
	int totalRecords;

	public LogFlowMonitorExcel() {
		rowCount = 1;
		workbook = new XSSFWorkbook();
	}

	public LogFlowMonitorExcel(List<Object> liObjects, Map<Long, String> assetIdBuMap,boolean buRequired) {
		rowCount = 1;
		workbook = new XSSFWorkbook();
		this.listRecords = liObjects;
		this.assetIdBuMap = assetIdBuMap;
		this.buRequired = buRequired;
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
		if (buRequired)
			AppConstants.LOG_FLOW_MONITOR_EXPORT_COLUMNS_WITH_BU.stream()
					.forEach(e -> createCell(row, columnCount.getAndIncrement(), e, style));
		else
			AppConstants.LOG_FLOW_MONITOR_EXPORT_COLUMNS.stream()
					.forEach(e -> createCell(row, columnCount.getAndIncrement(), e, style));

	}

	@SuppressWarnings("unchecked")
	@Override
	public void createCell(Row row, int columnCount, Object value, CellStyle style) {

		if (ObjectUtils.isEmpty(value)) {
			value = AppConstants.EXCEL_NULL_VALUE;
		}
		Cell cell = row.createCell(columnCount);
		if (value instanceof Integer) {
			cell.setCellValue((Integer) value);
		} else if (value instanceof Long) {
			cell.setCellValue((Long) value);
		} else if (value instanceof Boolean) {
			cell.setCellValue((Boolean) value);
		} else if (value instanceof Date) {
			cell.setCellValue((Date) value);
		} else if (value instanceof LocalDateTime) {
			cell.setCellValue((LocalDateTime) value);
		} else if (value instanceof String) {
			cell.setCellValue((String) value);
		} else if ((List<NotesFormatDto>) value instanceof List<NotesFormatDto>) {
			cell.setCellValue((String) ((List<NotesFormatDto>) value).get(0).getNote());
		} else {
			cell.setBlank();
		}
		cell.setCellStyle(style);
		
		if(rowCount==totalRecords)
			sheet.autoSizeColumn(columnCount);
	}

	public void createEmptyCell(Row row, int columnCount, Object value, CellStyle style) {

		Cell cell = row.createCell(columnCount);
			cell.setBlank();
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
		
		totalRecords=listRecords.size();
		
		listRecords.forEach(obj -> {

			LFMResponse lfmResponse = (LFMResponse) obj;

			Row row = sheet.createRow(rowCount++);
			AtomicInteger columnCount = new AtomicInteger();

			createCell(row, columnCount.getAndIncrement(), lfmResponse.getTenantName(), style);
			createCell(row, columnCount.getAndIncrement(), lfmResponse.getProductType(), style);
			createCell(row, columnCount.getAndIncrement(), lfmResponse.getProductVendor(), style);
			createCell(row, columnCount.getAndIncrement(), lfmResponse.getProductName(), style);
			createCell(row, columnCount.getAndIncrement(), lfmResponse.getProductIP(), style);
			createCell(row, columnCount.getAndIncrement(), lfmResponse.getProductHostName(), style);
			createCell(row, columnCount.getAndIncrement(), lfmResponse.getAssetType(), style);
			createCell(row, columnCount.getAndIncrement(), lfmResponse.getCloudResourceID(), style);
			createCell(row, columnCount.getAndIncrement(), lfmResponse.getSeverity(), style);
			createCell(row, columnCount.getAndIncrement(), lfmResponse.getCollectorAddress(), style);
			createCell(row, columnCount.getAndIncrement(), lfmResponse.getCollectorHostName(), style);
			createCell(row, columnCount.getAndIncrement(), lfmResponse.getMdrScannerCode(), style);
			createCell(row, columnCount.getAndIncrement(),
					LogFlowDashboardUtils.getStringFromCustomTimeFields(lfmResponse.getLogStoppageThresholdJson()),
					style);
			createCell(row, columnCount.getAndIncrement(), LogFlowDashboardUtils
					.getStringFromCustomTimeFields(lfmResponse.getEmailNotificationFrequencyJson()), style);
			createCell(row, columnCount.getAndIncrement(), getLocalDateTimeFromMilliSec(lfmResponse.getLoggerDate()),
					style);
			createCell(row, columnCount.getAndIncrement(),
					getLocalDateTimeFromMilliSec(lfmResponse.getLastEventReceived()), style);
			createCell(row, columnCount.getAndIncrement(), lfmResponse.getToEmailAddress(), style);
			createCell(row, columnCount.getAndIncrement(), lfmResponse.getCcEmailAddress(), style);
			createCell(row, columnCount.getAndIncrement(), getLocalDateTimeFromMilliSec(lfmResponse.getCreatedDate()),
					style);
			createCell(row, columnCount.getAndIncrement(), getLocalDateTimeFromMilliSec(lfmResponse.getUpdatedDate()),
					style);
			createCell(row, columnCount.getAndIncrement(),
					LogFlowDashboardUtils.getDeviceStatusByNumber(lfmResponse.getDeviceStatus()), style);
			createCell(row, columnCount.getAndIncrement(), lfmResponse.getMonitorStatus(), style);
			if (Objects.nonNull(lfmResponse.getNote()))
				createCell(row, columnCount.getAndIncrement(), lfmResponse.getNote(), style);
			else
				createEmptyCell(row, columnCount.getAndIncrement(), lfmResponse.getNote(), style);
			
			if (buRequired)
					createCell(row, columnCount.getAndIncrement(), assetIdBuMap.get(lfmResponse.getAssetId()), style);
		
		});
	}

	@Override
	public Workbook workbook() {
		// TODO Auto-generated method stub
		return workbook;
	}

	private String getLocalDateTimeFromMilliSec(Long milliSec) {
		if(Objects.isNull(milliSec))
			return null;
		return LogFlowDashboardUtils.getLocalDateTimeInStringFormat(LogFlowDashboardUtils.getUtcDateTime(milliSec));
	}
}
