package aisaac.util;

import java.time.LocalDateTime;
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

import aisaac.payload.response.AssetListResponse;

public class MappedAssetExportExcel implements Excel{

	private List<Object> listRecords;
	private XSSFWorkbook workbook;
	private XSSFSheet sheet;
	int rowCount;
	int totalRecords;
	
	public MappedAssetExportExcel() {
		rowCount = 1;
		workbook = new XSSFWorkbook();
	}
	
	public MappedAssetExportExcel(List<Object> listRecords) {
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
		AppConstants.EBA_MAPPED_ASSET_EXPORT_FILE_HEADER.stream()
				.forEach(e -> createCell(row, columnCount.getAndIncrement(), e, style));

	}

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
		} else if (value instanceof LocalDateTime) {
			cell.setCellValue((LocalDateTime) value);
		} else if (value instanceof String) {
			cell.setCellValue((String) value);
		} else if ((List<String>) value instanceof List<String>) {
			cell.setCellValue(value.toString().replace("[", StringUtils.EMPTY).replace("]", StringUtils.EMPTY));
		} else {
			cell.setBlank();
		}
		cell.setCellStyle(style);

		if (rowCount > 1 && rowCount - 1 == totalRecords)
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
			
			AssetListResponse assetListResponse = (AssetListResponse) obj;
			
			Row row = sheet.createRow(rowCount++);
			AtomicInteger columnCount = new AtomicInteger();

			createCell(row, columnCount.getAndIncrement(), assetListResponse.getAssetName(), style);
			createCell(row, columnCount.getAndIncrement(), assetListResponse.getIpAddress(), style);
			createCell(row, columnCount.getAndIncrement(), assetListResponse.getHostName(), style);
			createCell(row, columnCount.getAndIncrement(), assetListResponse.getCloudResourceId(), style);
			createCell(row, columnCount.getAndIncrement(), assetListResponse.getProductVendor(), style);
			createCell(row, columnCount.getAndIncrement(), assetListResponse.getProductName(), style);
			createCell(row, columnCount.getAndIncrement(), assetListResponse.getAssetTags(), style);
		});
	}

	@Override
	public Workbook workbook() {
		return workbook;
	}

}
