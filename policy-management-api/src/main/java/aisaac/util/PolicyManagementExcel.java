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

import aisaac.payload.response.PolicyManagementResponse;

public class PolicyManagementExcel implements  Excel{

	private List<Object> listRecords;
	private XSSFWorkbook workbook;
	private XSSFSheet sheet;
	int rowCount;
	int totalRecords;
	private boolean isMultiTenantUser;


	
	public PolicyManagementExcel() {
		rowCount = 1;
		workbook = new XSSFWorkbook();
	}
	
	public PolicyManagementExcel(List<Object> liObjects, boolean isMultiTenantUser) {
		rowCount = 1;
		workbook = new XSSFWorkbook();
		this.listRecords=liObjects;
		this.isMultiTenantUser = isMultiTenantUser;

	}
	
	@Override
	public void writeHeader() {
		sheet = workbook.createSheet("Sheet1");

		Row row = sheet.createRow(0);

		CellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setBold(true);
		font.setFontHeight(14);
		style.setFont(font);
		
		AtomicInteger columnCount = new AtomicInteger();
		AppConstants.POLICY_MANAGEMENT_EXPORT_COLUMNS.stream().forEach(e -> createCell(row, columnCount.getAndIncrement(), e, style));

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
		}else if (value instanceof LocalDateTime) {
			cell.setCellValue((LocalDateTime) value);
		} else {
			cell.setCellValue((String) value);
		}
		cell.setCellStyle(style);
		
		if(rowCount==totalRecords)
			sheet.autoSizeColumn(columnCount);
	
	}

	@Override
	public void write() {

		CellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setFontHeight(14);
		style.setFont(font);
		
		totalRecords=listRecords.size();
		listRecords.forEach(obj -> {
			
			PolicyManagementResponse policyManagementResponse = (PolicyManagementResponse) obj;
			
			Row row = sheet.createRow(rowCount++);
			AtomicInteger columnCount = new AtomicInteger();

			createCell(row, columnCount.getAndIncrement(), policyManagementResponse.getTenantName(), style);
			createCell(row, columnCount.getAndIncrement(), policyManagementResponse.getFinding(), style);
			createCell(row, columnCount.getAndIncrement(), policyManagementResponse.getCloudResourceId(), style);
			createCell(row, columnCount.getAndIncrement(), policyManagementResponse.getSecurityControlId(), style);
			createCell(row, columnCount.getAndIncrement(), policyManagementResponse.getCloudAccountId(), style);
			createCell(row, columnCount.getAndIncrement(), policyManagementResponse.getSeverity(), style);
			createCell(row, columnCount.getAndIncrement(), DateUtils.getUtcDateTime(policyManagementResponse.getUpdatedAt()), style);
			createCell(row, columnCount.getAndIncrement(), DateUtils.getUtcDateTime(policyManagementResponse.getCreatedAt()), style);
			createCell(row, columnCount.getAndIncrement(), policyManagementResponse.getComplianceStatus(), style);
			createCell(row, columnCount.getAndIncrement(), policyManagementResponse.getFindingStatus(), style);
			createCell(row, columnCount.getAndIncrement(), policyManagementResponse.getStatusReasonsDescription(), style);
			createCell(row, columnCount.getAndIncrement(), policyManagementResponse.getComplianceStandards(), style);
			createCell(row, columnCount.getAndIncrement(), DateUtils.getUtcDateTime(policyManagementResponse.getCreatedDate()), style);
			createCell(row, columnCount.getAndIncrement(), policyManagementResponse.getRemediationStatus(), style);
		});
		
		if (!isMultiTenantUser) {
            sheet.setColumnHidden(0, true);
        }
	}

	@Override
	public Workbook workbook() {
		// TODO Auto-generated method stub
		return workbook;
	}

}
