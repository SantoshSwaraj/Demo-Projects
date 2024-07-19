package aisaac.util;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
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

import aisaac.payload.response.ThreatListResponse;

public class ThreatExportExcel implements Excel{

	private List<Object> listRecords;
	private XSSFWorkbook workbook;
	private XSSFSheet sheet;
	int rowCount;
	int totalRecords;
	
	public ThreatExportExcel() {
		rowCount = 1;
		workbook = new XSSFWorkbook();
	}
	
	public ThreatExportExcel(List<Object> listRecords) {
		this();
		this.listRecords = listRecords;
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
		AppConstants.UBA_THREAT_EXPORT_FILE_HEADER.stream()
				.forEach(e -> createCell(row, columnCount.getAndIncrement(), e, style));

	}

	@Override
	public void createCell(Row row, int columnCount, Object value, CellStyle style) {
		
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
		} else if (value instanceof LocalDateTime) {
			cell.setCellValue((LocalDateTime) value);
		} else if (value instanceof String) {
			cell.setCellValue((String) value);
		}else {
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
		
		totalRecords=listRecords.size();
		
		listRecords.forEach(obj -> {
			
			ThreatListResponse threatListResponse = (ThreatListResponse) obj;
			
			Row row = sheet.createRow(rowCount++);
			AtomicInteger columnCount = new AtomicInteger();

			createCell(row, columnCount.getAndIncrement(), threatListResponse.getThreatName(), style);
			createCell(row, columnCount.getAndIncrement(), threatListResponse.getThreatSource(), style);
			createCell(row, columnCount.getAndIncrement(), threatListResponse.getSourceIp(), style);
			createCell(row, columnCount.getAndIncrement(), threatListResponse.getDestinationIp(), style);
			createCell(row, columnCount.getAndIncrement(), threatListResponse.getSourceHostName(), style);
			createCell(row, columnCount.getAndIncrement(), threatListResponse.getDestinationHostName(), style);
			createCell(row, columnCount.getAndIncrement(), threatListResponse.getDestinationUrl(), style);
			createCell(row, columnCount.getAndIncrement(), getLocalDateTimeFromMilliSec(threatListResponse.getEventTime()), style);
			createCell(row, columnCount.getAndIncrement(), threatListResponse.getSourcePort(), style);
			createCell(row, columnCount.getAndIncrement(), threatListResponse.getDestinationPort(), style);
			createCell(row, columnCount.getAndIncrement(), threatListResponse.getSourceLocation(), style);
			createCell(row, columnCount.getAndIncrement(), threatListResponse.getDestinationLocation(), style);
			createCell(row, columnCount.getAndIncrement(), threatListResponse.getUserScoreText(), style);
			createCell(row, columnCount.getAndIncrement(), threatListResponse.getUserName(), style);
			

		});
	}

	@Override
	public Workbook workbook() {
		// TODO Auto-generated method stub
		return workbook;
	}
	
	private String getLocalDateTimeFromMilliSec(Long milliSec) {
		return UbaUtil.getLocalDateTimeInStringFormat(UbaUtil.getUtcDateTime(milliSec));
	}

}
