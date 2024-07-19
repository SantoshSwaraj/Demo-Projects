package aisaac.util;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
/*import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;*/
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import aisaac.payload.response.ExposuresResponse;


public class ExposuresExcel implements  Excel{

	
	private List<Object> listRecords;
	private boolean isMultiTenantUser;
	private XSSFWorkbook workbook;
	private XSSFSheet sheet;
	int rowCount;
	int totalRecords;

	public ExposuresExcel() {
		rowCount = 1;
		workbook = new XSSFWorkbook();
	}

	public ExposuresExcel(List<Object> liObjects, boolean isMultiTenantUser) {
		rowCount = 1;
		workbook = new XSSFWorkbook();
		this.listRecords = liObjects;
		this.isMultiTenantUser = isMultiTenantUser;
	}

	@Override
	public void writeHeader() {
		sheet = workbook.createSheet("Sheet1");

		Row row = sheet.createRow(0);

		CellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		/*
		 * font.setBold(true); font.setFontHeight(16);
		 */
		style.setFont(font);

		AtomicInteger columnCount = new AtomicInteger();
		AppConstants.EXPOSURES_EXPORT_COLUMNS.stream()
				.forEach(e -> createCell(row, columnCount.getAndIncrement(), e, style));

	}

	@Override
	public void createCell(Row row, int columnCount, Object value, CellStyle style) {
		/* sheet.autoSizeColumn(columnCount); */
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
		} else {
			cell.setCellValue((String) value);
		}
		//cell.setCellStyle(style);
		
		if(rowCount==totalRecords)
			sheet.autoSizeColumn(columnCount);
	}

	@Override
	public void write() {

		CellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		//font.setFontHeight(14);
		style.setFont(font);

		totalRecords=listRecords.size();
		listRecords.forEach(obj -> {

			ExposuresResponse exposuresResponse = (ExposuresResponse) obj;

			Row row = sheet.createRow(rowCount++);
			AtomicInteger columnCount = new AtomicInteger();

			createCell(row, columnCount.getAndIncrement(), exposuresResponse.getTenantName(), style);
			createCell(row, columnCount.getAndIncrement(), exposuresResponse.getIssueTitle(), style);
			createCell(row, columnCount.getAndIncrement(), exposuresResponse.getConfidence(), style);
			createCell(row, columnCount.getAndIncrement(),
					DateUtils.getUtcDateTime(exposuresResponse.getLastDetectedAt()), style);
			createCell(row, columnCount.getAndIncrement(), exposuresResponse.getIssueCategory(), style);
			createCell(row, columnCount.getAndIncrement(), exposuresResponse.getIssueId(), style);
			createCell(row, columnCount.getAndIncrement(), exposuresResponse.getIssueSubcategory(), style);
			createCell(row, columnCount.getAndIncrement(), exposuresResponse.getMitreNextName(), style);
			createCell(row, columnCount.getAndIncrement(), exposuresResponse.getSeverity(), style);
			createCell(row, columnCount.getAndIncrement(), exposuresResponse.getSeverityScore(), style);
			createCell(row, columnCount.getAndIncrement(), exposuresResponse.getResourceIp(), style);
			createCell(row, columnCount.getAndIncrement(), exposuresResponse.getCveId(), style);
			createCell(row, columnCount.getAndIncrement(), exposuresResponse.getPotentialImpact(), style);
			createCell(row, columnCount.getAndIncrement(), exposuresResponse.getSummary(), style);
			createCell(row, columnCount.getAndIncrement(), exposuresResponse.getAssetOwnerDetails(), style);
			createCell(row, columnCount.getAndIncrement(), exposuresResponse.getLocation(), style);
			createCell(row, columnCount.getAndIncrement(),
					DateUtils.getUtcDateTime(exposuresResponse.getFirstDetectedAt()), style);
		});
		
		if (!isMultiTenantUser) {
            sheet.setColumnHidden(0, true);
        }
	}

	@Override
	public Workbook workbook() { // TODO Auto-generated method stub
		return workbook;
	}
	 
}
