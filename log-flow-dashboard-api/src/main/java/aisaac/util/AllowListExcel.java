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

import aisaac.dto.NotesFormatDto;
import aisaac.payload.response.AllowListResponse;

public class AllowListExcel implements Excel{

	private List<Object> listRecords;
	private XSSFWorkbook workbook;
	private XSSFSheet sheet;
	int rowCount;
	
	public AllowListExcel() {
		rowCount = 1;
		workbook = new XSSFWorkbook();
	}
	
	public AllowListExcel(List<Object> liObjects) {
		rowCount = 1;
		workbook = new XSSFWorkbook();
		this.listRecords=liObjects;
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
		AppConstants.LOG_FLOW_ALLOW_LIST_DEVICES_EXPORT_COLUMN.stream()
				.forEach(e -> createCell(row, columnCount.getAndIncrement(), e, style));

	}

	@SuppressWarnings("unchecked")
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
		}else if ((List<NotesFormatDto>)value instanceof List<NotesFormatDto>) {
			cell.setCellValue((String) ((List<NotesFormatDto>)value).get(0).getNote());
		}else {
			cell.setBlank();
		}
		cell.setCellStyle(style);
		sheet.autoSizeColumn(columnCount);
	}

	@Override
	public void write() {

		CellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setFontHeight(14);
		style.setFont(font);
		
		listRecords.forEach(obj -> {
			
			AllowListResponse lfmResponse = (AllowListResponse) obj;
			
			Row row = sheet.createRow(rowCount++);
			AtomicInteger columnCount = new AtomicInteger();

			createCell(row, columnCount.getAndIncrement(), lfmResponse.getTenantName(), style);
			createCell(row, columnCount.getAndIncrement(), lfmResponse.getProductVendor(), style);
			createCell(row, columnCount.getAndIncrement(), lfmResponse.getProductName(), style);
			createCell(row, columnCount.getAndIncrement(), lfmResponse.getDescription(), style);
			createCell(row, columnCount.getAndIncrement(), getLocalDateTimeFromMilliSec(lfmResponse.getAllowListedDate()) , style);
			

		});
	}

	@Override
	public Workbook workbook() {
		// TODO Auto-generated method stub
		return workbook;
	}

	private String getLocalDateTimeFromMilliSec(Long milliSec) {
		return LogFlowDashboardUtils.getLocalDateTimeInStringFormat(LogFlowDashboardUtils.getUtcDateTime(milliSec));
	}
}
