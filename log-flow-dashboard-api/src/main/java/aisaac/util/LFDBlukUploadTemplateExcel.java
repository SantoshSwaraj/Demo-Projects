package aisaac.util;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFName;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import aisaac.entities.ProductMaster;

public class LFDBlukUploadTemplateExcel implements Excel {

	private List<ProductMaster> products;
	private List<String> assetTypeList;
	private List<String> organizations;
	private List<Object> customFalseSettings;
	private String[] severityArr;
	private String[] monitorStatus;
	private XSSFWorkbook workbook;
	private XSSFSheet sheet;
	int rowCount;
	int exportLimit;

	public LFDBlukUploadTemplateExcel() {
		rowCount = 1;
		exportLimit = AppConstants.DEFAULT_APP_SETTINGS_EXPORT_LIMIT;
		workbook = new XSSFWorkbook();
	}

	public LFDBlukUploadTemplateExcel(List<ProductMaster> productMastersList, List<String> organizations,
			List<String> assetTypeList, String[] severity, String[] monitorStatus, int exportLimit,List<Object> customFalseSettings) {
		rowCount = 1;
		workbook = new XSSFWorkbook();
		this.products = productMastersList;
		this.severityArr = severity;
		this.monitorStatus = monitorStatus;
		this.organizations = organizations;
		this.assetTypeList = assetTypeList;
		this.exportLimit = exportLimit;
		this.customFalseSettings = customFalseSettings;
	}

	@Override
	public void writeHeader() {
		sheet = workbook.createSheet("Sheet1");

		Row row = sheet.createRow(0);

		CellStyle style = workbook.createCellStyle();
		CellStyle styleRed = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
//		font.setBold(true);
		font.setFontHeight(16);
		style.setFont(font);

		XSSFFont fontRed = workbook.createFont();
//		fontRed.setBold(true);
		fontRed.setColor(IndexedColors.RED.getIndex());
		fontRed.setFontHeight(16);
		styleRed.setFont(fontRed);

		AtomicInteger columnCount = new AtomicInteger();
		AppConstants.LFD_BULK_UPLOAD_TEMPLATE_HEADERS.stream().forEach(e -> {
			if (e.indexOf("*") > -1)
				createCell(row, columnCount.getAndIncrement(), e, styleRed);
			else
				createCell(row, columnCount.getAndIncrement(), e, style);
		});

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
		} else if (value instanceof Date) {
			cell.setCellValue((Date) value);
		} else if (value instanceof LocalDateTime) {
			cell.setCellValue((LocalDateTime) value);
		} else if (value instanceof String) {
			cell.setCellValue((String) value);
		} else {
			cell.setBlank();
		}
		cell.setCellStyle(style);
		sheet.autoSizeColumn(columnCount);
	}

	@Override
	public void write() {
		String[] assetTypes = assetTypeList.toArray(new String[assetTypeList.size()]);
		XSSFSheet hidden001 = (XSSFSheet) workbook.createSheet("hidden001");
		for (int k = 0, length = assetTypes.length; k < length; k++) {
			String name = assetTypes[k];
			XSSFRow row = hidden001.createRow(k);
			XSSFCell cell = row.createCell(0);
			cell.setCellValue(name);
		}
		XSSFName namedCellAssetType = workbook.createName();
		namedCellAssetType.setNameName("hidden001");
		namedCellAssetType.setRefersToFormula("hidden001!$A$1:$A$" + assetTypes.length);
		XSSFDataValidationHelper dvHelperAssetType = new XSSFDataValidationHelper(hidden001);
		XSSFDataValidationConstraint dvConstraintAssetType = (XSSFDataValidationConstraint) dvHelperAssetType
				.createFormulaListConstraint("hidden001");

		XSSFSheet hidden002 = (XSSFSheet) workbook.createSheet("hidden002");
		String[] sendMailValues = AppConstants.SEND_MAIL_DROP_DOWN_VALUES;
		XSSFName namedCellSendMail = workbook.createName();
		namedCellSendMail.setNameName("hidden002");
		namedCellSendMail.setRefersToFormula("hidden002!$A$1:$A$" + sendMailValues.length);
		XSSFDataValidationHelper dvHelperSendMail = new XSSFDataValidationHelper(hidden002);
        XSSFDataValidationConstraint sendMailDropdownConstraint = (XSSFDataValidationConstraint) dvHelperSendMail.createExplicitListConstraint(sendMailValues);
        
		Set<String> deviceVendorList = products.stream().map(ProductMaster::getProductVendor)
				.sorted((s1, s2) -> s1.compareToIgnoreCase(s2)).collect(Collectors.toSet());

		XSSFSheet hidden003 = (XSSFSheet) workbook.createSheet("hidden003");
		AtomicInteger vendorCounter = new AtomicInteger(0);
		deviceVendorList.forEach(p -> {
			String name = p;
			XSSFRow row = hidden003.createRow(vendorCounter.getAndIncrement());
			XSSFCell cell = row.createCell(0);
			cell.setCellValue(name);
		});
		XSSFName namedCellDeviceVendor = workbook.createName();
		namedCellDeviceVendor.setNameName("hidden003");
		namedCellDeviceVendor.setRefersToFormula("hidden003!$A$1:$A$" + deviceVendorList.size());
		XSSFDataValidationHelper dvHelperDeviceVendor = new XSSFDataValidationHelper(hidden003);
		XSSFDataValidationConstraint dvConstraintDeviceVendor = (XSSFDataValidationConstraint) dvHelperDeviceVendor
				.createFormulaListConstraint("hidden003");

		Set<String> productList = products.stream().map(ProductMaster::getProductName)
				.sorted((s1, s2) -> s1.compareToIgnoreCase(s2)).collect(Collectors.toSet());

		XSSFSheet hidden004 = (XSSFSheet) workbook.createSheet("hidden004");
		AtomicInteger productCounter = new AtomicInteger(0);
		productList.forEach(p -> {
			String name = p;
			XSSFRow row = hidden004.createRow(productCounter.getAndIncrement());
			XSSFCell cell = row.createCell(0);
			cell.setCellValue(name);
		});
		XSSFName namedCellProduct = workbook.createName();
		namedCellProduct.setNameName("hidden004");
		namedCellProduct.setRefersToFormula("hidden004!$A$1:$A$" + productList.size());
		XSSFDataValidationHelper dvHelperProduct = new XSSFDataValidationHelper(hidden004);
		XSSFDataValidationConstraint dvConstraintProduct = (XSSFDataValidationConstraint) dvHelperProduct
				.createFormulaListConstraint("hidden004");

		XSSFSheet hidden005 = (XSSFSheet) workbook.createSheet("hidden005");
		for (int k = 0, length = severityArr.length; k < length; k++) {
			String name = severityArr[k];
			XSSFRow row = hidden005.createRow(k);
			XSSFCell cell = row.createCell(0);
			cell.setCellValue(name);
		}
		XSSFName namedCellSeverityType = workbook.createName();
		namedCellSeverityType.setNameName("hidden005");
		namedCellSeverityType.setRefersToFormula("hidden005!$A$1:$A$" + severityArr.length);
		XSSFDataValidationHelper dvHelperSeverityType = new XSSFDataValidationHelper(hidden005);
		XSSFDataValidationConstraint dvConstraintSeverityType = (XSSFDataValidationConstraint) dvHelperSeverityType
				.createFormulaListConstraint("hidden005");

//		XSSFSheet hidden006 = (XSSFSheet) workbook.createSheet("hidden006");
//		XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(hidden006);
//		XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper.createNumericConstraint(
//				XSSFDataValidationConstraint.ValidationType.INTEGER, XSSFDataValidationConstraint.OperatorType.BETWEEN,
//				String.valueOf(Integer.MIN_VALUE), String.valueOf(Integer.MAX_VALUE));
		
//		XSSFSheet hidden006 = (XSSFSheet) workbook.createSheet("hidden006");
//		XSSFDataValidationHelper logStoppageAndFrequencyHelper = new XSSFDataValidationHelper(hidden006);
//		XSSFDataValidationConstraint logStoppageAndFrequencyConstraint = (XSSFDataValidationConstraint) logStoppageAndFrequencyHelper.createCustomConstraint("OR(ISNUMBER(SEARCH(\" hours\", INDIRECT(ADDRESS(ROW(),COLUMN())))), ISNUMBER(SEARCH(\" minutes\",INDIRECT(ADDRESS(ROW(),COLUMN())))), ISNUMBER(SEARCH(\" days\",INDIRECT(ADDRESS(ROW(),COLUMN())))))");
		
		XSSFSheet hidden006 = (XSSFSheet) workbook.createSheet("hidden006");
		for (int k = 0, length = customFalseSettings.size(); k < length; k++) {
			Object name = customFalseSettings.get(k);
			if(Objects.nonNull(name)&&"Custom".equalsIgnoreCase(name.toString())) {
				continue;
			}
			XSSFRow row = hidden006.createRow(k);
			XSSFCell cell = row.createCell(0);
			
			cell.setCellValue(name.toString());
		}
		XSSFName namedCellCustomFalseSettings = workbook.createName();
		namedCellCustomFalseSettings.setNameName("hidden006");
		namedCellCustomFalseSettings.setRefersToFormula("hidden006!$A$1:$A$" + customFalseSettings.size());
		XSSFDataValidationHelper logStoppageAndFrequencyHelper = new XSSFDataValidationHelper(hidden006);
		XSSFDataValidationConstraint logStoppageAndFrequencyConstraint = (XSSFDataValidationConstraint) logStoppageAndFrequencyHelper
				.createFormulaListConstraint("hidden006");

		organizations = organizations.stream().sorted((o1, o2) -> o1.compareToIgnoreCase(o2))
				.collect(Collectors.toList());

		if (organizations.isEmpty()) {
			organizations.add("");
		}
		String[] organizationTypes = organizations.toArray(new String[organizations.size()]);
		XSSFSheet hidden007 = (XSSFSheet) workbook.createSheet("hidden007");
		for (int k = 0, length = organizationTypes.length; k < length; k++) {
			String name = organizationTypes[k];
			XSSFRow row = hidden007.createRow(k);
			XSSFCell cell = row.createCell(0);
			cell.setCellValue(name);
		}
		XSSFName namedCellOrganizationType = workbook.createName();
		namedCellOrganizationType.setNameName("hidden007");
		namedCellOrganizationType.setRefersToFormula("hidden007!$A$1:$A$" + organizationTypes.length);
		XSSFDataValidationHelper dvHelperOrganizationType = new XSSFDataValidationHelper(hidden007);
		XSSFDataValidationConstraint dvConstraintOrganizationType = (XSSFDataValidationConstraint) dvHelperOrganizationType
				.createFormulaListConstraint("hidden007");

		XSSFSheet hidden008 = (XSSFSheet) workbook.createSheet("hidden008");
		for (int k = 0, length = monitorStatus.length; k < length; k++) {
			String name = monitorStatus[k];
			XSSFRow row = hidden008.createRow(k);
			XSSFCell cell = row.createCell(0);
			cell.setCellValue(name);
		}
		XSSFName namedCellMonitorStatus = workbook.createName();
		namedCellMonitorStatus.setNameName("hidden008");
		namedCellMonitorStatus.setRefersToFormula("hidden008!$A$1:$A$" + severityArr.length);
		XSSFDataValidationHelper dvHelperMonitorStatus = new XSSFDataValidationHelper(hidden008);
		XSSFDataValidationConstraint dvConstraintMonitorStatus = (XSSFDataValidationConstraint) dvHelperMonitorStatus
				.createFormulaListConstraint("hidden008");

		for (int i = 0; i < AppConstants.LFD_BULK_UPLOAD_TEMPLATE_HEADERS.size(); i++) {
			// Organization
			if (AppConstants.LFD_BULK_UPLOAD_TEMPLATE_HEADERS.get(i).equalsIgnoreCase("Organization*")) {
				CellRangeAddressList addressList = new CellRangeAddressList(rowCount, exportLimit, i, i);

				XSSFDataValidation dataValidation = (XSSFDataValidation) dvHelperOrganizationType
						.createValidation(dvConstraintOrganizationType, addressList);

				dataValidation.setSuppressDropDownArrow(true);
				dataValidation.setShowErrorBox(true);
				sheet.addValidationData(dataValidation);
			}
			// Product Vendor*
			else if (AppConstants.LFD_BULK_UPLOAD_TEMPLATE_HEADERS.get(i).equalsIgnoreCase("Product Vendor*")) {
				CellRangeAddressList addressList = new CellRangeAddressList(rowCount, exportLimit, i, i);

				XSSFDataValidation dataValidation = (XSSFDataValidation) dvHelperDeviceVendor
						.createValidation(dvConstraintDeviceVendor, addressList);

				dataValidation.setSuppressDropDownArrow(true);
				dataValidation.setShowErrorBox(true);
				sheet.addValidationData(dataValidation);

			}
			// Product Name*
			else if (AppConstants.LFD_BULK_UPLOAD_TEMPLATE_HEADERS.get(i).equalsIgnoreCase("Product Name*")) {
				CellRangeAddressList addressList = new CellRangeAddressList(rowCount, exportLimit, i, i);

				XSSFDataValidation dataValidation = (XSSFDataValidation) dvHelperProduct
						.createValidation(dvConstraintProduct, addressList);

				dataValidation.setSuppressDropDownArrow(true);
				dataValidation.setShowErrorBox(true);
				sheet.addValidationData(dataValidation);

			}
			// Asset Type*
			else if (AppConstants.LFD_BULK_UPLOAD_TEMPLATE_HEADERS.get(i).equalsIgnoreCase("Asset Type*")) {
				CellRangeAddressList addressList = new CellRangeAddressList(rowCount, exportLimit, i, i);

				XSSFDataValidation dataValidation = (XSSFDataValidation) dvHelperAssetType
						.createValidation(dvConstraintAssetType, addressList);

				dataValidation.setSuppressDropDownArrow(true);
				dataValidation.setShowErrorBox(true);
				sheet.addValidationData(dataValidation);
			}
			// Monitoring Status*
			else if (AppConstants.LFD_BULK_UPLOAD_TEMPLATE_HEADERS.get(i).equalsIgnoreCase("Monitoring Status*")) {
				CellRangeAddressList addressList = new CellRangeAddressList(rowCount, exportLimit, i, i);

				XSSFDataValidation dataValidation = (XSSFDataValidation) dvHelperMonitorStatus
						.createValidation(dvConstraintMonitorStatus, addressList);

				dataValidation.setSuppressDropDownArrow(true);
				dataValidation.setShowErrorBox(true);
				sheet.addValidationData(dataValidation);
			}
			// Severity*
			else if (AppConstants.LFD_BULK_UPLOAD_TEMPLATE_HEADERS.get(i).equalsIgnoreCase("Severity*")) {

				CellRangeAddressList addressList = new CellRangeAddressList(rowCount, exportLimit, i, i);

				XSSFDataValidation dataValidation = (XSSFDataValidation) dvHelperSeverityType
						.createValidation(dvConstraintSeverityType, addressList);

				dataValidation.setSuppressDropDownArrow(true);
				dataValidation.setShowErrorBox(true);
				sheet.addValidationData(dataValidation);
			}
			// Log Stoppage Threshold (in mins)*
			else if (AppConstants.LFD_BULK_UPLOAD_TEMPLATE_HEADERS.get(i).equalsIgnoreCase("Log Stoppage Threshold*")) {
				CellRangeAddressList addressList = new CellRangeAddressList(rowCount, exportLimit, i, i);

				XSSFDataValidation dataValidation = (XSSFDataValidation) logStoppageAndFrequencyHelper.createValidation(logStoppageAndFrequencyConstraint,
						addressList);

				dataValidation.setSuppressDropDownArrow(true);
				dataValidation.setShowErrorBox(true);
//				dataValidation.createErrorBox("Invalid Input", "Please enter a value containing ' hours' or ' minutes' or ' days'.");
				sheet.addValidationData(dataValidation);
			}
			// Email Notification frequency (in mins)*
			else if (AppConstants.LFD_BULK_UPLOAD_TEMPLATE_HEADERS.get(i)
					.equalsIgnoreCase("Email Notification frequency*")) {
				CellRangeAddressList addressList = new CellRangeAddressList(rowCount, exportLimit, i, i);

				XSSFDataValidation dataValidation = (XSSFDataValidation) logStoppageAndFrequencyHelper.createValidation(logStoppageAndFrequencyConstraint,
						addressList);

				dataValidation.setSuppressDropDownArrow(true);
				dataValidation.setShowErrorBox(true);
//				dataValidation.createErrorBox("Invalid Input", "Please enter a value containing ' hours' or ' minutes' or ' days'.");
				sheet.addValidationData(dataValidation);
			}
			//
			else if (AppConstants.LFD_BULK_UPLOAD_TEMPLATE_HEADERS.get(i)
					.equalsIgnoreCase("Send Email Notification for Log Stoppage")) {
				CellRangeAddressList addressList = new CellRangeAddressList(rowCount, exportLimit, i, i);

				XSSFDataValidation dataValidation = (XSSFDataValidation) dvHelperSendMail.createValidation(sendMailDropdownConstraint,
						addressList);

				dataValidation.setSuppressDropDownArrow(true);
				sheet.addValidationData(dataValidation);
			}

		}

		workbook.setSheetHidden(1, true);
		workbook.setSheetHidden(2, true);
		workbook.setSheetHidden(3, true);
		workbook.setSheetHidden(4, true);
		workbook.setSheetHidden(5, true);
		workbook.setSheetHidden(6, true);
		workbook.setSheetHidden(7, true);
		workbook.setSheetHidden(8, true);

	}

	@Override
	public Workbook workbook() {
		// TODO Auto-generated method stub
		return workbook;
	}

}
