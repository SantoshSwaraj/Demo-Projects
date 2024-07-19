package aisaac.util;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import aisaac.payload.response.LFMResponse;

public class LogFlowDashboardExcel implements Excel {

	private List<Object> listRecords;
	private Class<T> entityName;
	private List<String> excelColumnName;
	private List<String> entityColumnName;
	private XSSFWorkbook workbook;
	private XSSFSheet sheet;
	int rowCount;

	public LogFlowDashboardExcel() {
		rowCount = 1;
		workbook = new XSSFWorkbook();
	}

	public LogFlowDashboardExcel(List<Object> liObjects, Class<T> className, List<String> excelColumnName,
			List<String> entityColumnName) {
		rowCount = 1;
		workbook = new XSSFWorkbook();
		this.listRecords = liObjects;
		this.entityName = className;
		this.excelColumnName=excelColumnName;
		this.entityColumnName=entityColumnName;
	}

	@Override
	public void writeHeader() {
		sheet = workbook.createSheet("Sheet1");

		Row row = sheet.createRow(0);

		CellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setBold(true);
		font.setFontHeight(16);
		style.setFont(font);
		
		AtomicInteger columnCount = new AtomicInteger();
		excelColumnName.stream().forEach(e -> createCell(row, columnCount.getAndIncrement(), e, style));

	}

	@Override
	public void createCell(Row row, int columnCount, Object value, CellStyle style) {
		sheet.autoSizeColumn(columnCount);
		if(ObjectUtils.isEmpty(value)) {
			value=StringUtils.EMPTY;
		}
		Cell cell = row.createCell(columnCount);
		if (value instanceof Integer) {
			cell.setCellValue((Integer) value);
		} 
        else if (value instanceof Long) {
			cell.setCellValue((Long) value);
		} else if (value instanceof Boolean) {
			cell.setCellValue((Boolean) value);
		}else if (value instanceof Date) {
			cell.setCellValue((Date) value);
		} else {
			cell.setCellValue((String) value);
		}
		cell.setCellStyle(style);
	}

	@Override
	public void write() {

		CellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setFontHeight(14);
		style.setFont(font);
		
		listRecords.forEach(obj -> {
			
			 entityName =  (Class<T>) obj;
			
			Row row = sheet.createRow(rowCount++);
			AtomicInteger columnCount = new AtomicInteger();

//			createCell(row, columnCount.getAndIncrement(), lfmResponse.getOrganization(), style);
//			createCell(row, columnCount.getAndIncrement(), lfmResponse.getProductType(), style);
//			createCell(row, columnCount.getAndIncrement(), lfmResponse.getProductVendor(), style);
//			createCell(row, columnCount.getAndIncrement(), lfmResponse.getProductName(), style);
//			createCell(row, columnCount.getAndIncrement(), lfmResponse.getProductIP(), style);
//			createCell(row, columnCount.getAndIncrement(), lfmResponse.getProductHostName(), style);
//			createCell(row, columnCount.getAndIncrement(), lfmResponse.getAssetType(), style);
//			createCell(row, columnCount.getAndIncrement(), lfmResponse.getCloudResourceID(), style);
//			createCell(row, columnCount.getAndIncrement(), lfmResponse.getSeverity(), style);
//			createCell(row, columnCount.getAndIncrement(), lfmResponse.getCollectorIP(), style);
//			createCell(row, columnCount.getAndIncrement(), lfmResponse.getCollectorHostname(), style);
//			createCell(row, columnCount.getAndIncrement(), lfmResponse.getLogStoppageThreshold().toString(), style);
//			createCell(row, columnCount.getAndIncrement(), lfmResponse.getEmailNotificationFrequency(), style);
//			createCell(row, columnCount.getAndIncrement(), lfmResponse.getAisaacCurrentFlowStateSince(), style);
//			createCell(row, columnCount.getAndIncrement(), lfmResponse.getLastEventReceived().toString(), style);
//			createCell(row, columnCount.getAndIncrement(), lfmResponse.getToEmailAddress(), style);
//			createCell(row, columnCount.getAndIncrement(), lfmResponse.getCcEmailAddress(), style);
//			createCell(row, columnCount.getAndIncrement(), lfmResponse.getCreatedOn()!=null ? lfmResponse.getCreatedOn().toString(): StringUtils.EMPTY, style);
//			createCell(row, columnCount.getAndIncrement(), lfmResponse.getUpdatedOn()!=null ? lfmResponse.getUpdatedOn().toString(): StringUtils.EMPTY, style);
//			createCell(row, columnCount.getAndIncrement(), lfmResponse.getAisaacFlowStatus(), style);
//			createCell(row, columnCount.getAndIncrement(), lfmResponse.getMonitorStatus(), style);
//			createCell(row, columnCount.getAndIncrement(), lfmResponse.getNote(), style);
			

		});
	}

	@Override
	public Workbook workbook() {
		// TODO Auto-generated method stub
		return null;
	}

}
