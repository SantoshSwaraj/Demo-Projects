package aisaac.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;

import com.opencsv.CSVWriter;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

public class ExcelUtils {

	public static String getFileName(String start, String subFileName, boolean moreThanOnePartsAvailable,
			Integer number, boolean itIsLastPart, String extension) {

		String fileName = AppConstants.EXPOSURES_FILENAME_PREFIX + start + AppConstants.EXPOSURES_SEPARATOR_SPACE
				+ subFileName;

		if (moreThanOnePartsAvailable) {
			fileName += AppConstants.EXPOSURES_FILENAME_SEPARATOR_PART + number
					+ (itIsLastPart ? AppConstants.EXPOSURES_FILENAME_SEPARATOR_LAST : StringUtils.EMPTY) + extension;
		}

		if (Objects.isNull(number) || number <= 1) {
			fileName += extension;
		}

		fileName += AppConstants.EXPOSURES_FILENAME_SUFFIX;

		return fileName;

	}

	public static void download(Excel excel, HttpServletResponse response) throws IOException {

		if (Objects.isNull(response)) {
			throw new IOException("HttpServletResponse is null");
		}

		if (Objects.isNull(excel)) {
			throw new IOException("Excel is null");
		}

		Workbook workbook = excel.generate().workbook();

		if (Objects.isNull(workbook)) {
			throw new IOException("Workbook is null");
		}

		ServletOutputStream outputStream = response.getOutputStream();
		workbook.write(outputStream);
		workbook.close();

		outputStream.close();
	}

	public static String getFileNameWithoutPrefixAndSuffix(String start, String subFileName,
			boolean moreThanOnePartsAvailable, Integer number, boolean itIsLastPart, String extension) {

		String fileName = start + AppConstants.EXPOSURES_SEPARATOR_SPACE + subFileName;

		if (moreThanOnePartsAvailable) {
			fileName += AppConstants.EXPOSURES_FILENAME_SEPARATOR_PART + number
					+ (itIsLastPart ? AppConstants.EXPOSURES_FILENAME_SEPARATOR_LAST : StringUtils.EMPTY) + extension;
		}

		if (Objects.isNull(number) || number <= 1) {
			fileName += extension;
		}

		return fileName;

	}

	public static String downloadToPath(Excel excel, String path, String fileName) throws IOException {

		if (Objects.isNull(excel)) {
			throw new IOException("Excel is null");
		}

		Workbook workbook = excel.generate().workbook();

		if (Objects.isNull(workbook)) {
			throw new IOException("Workbook is null");
		}
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		path = path + AppConstants.EXPOSURES_FILENAME_FORWARD_SLASH + fileName;
		FileOutputStream fos = new FileOutputStream(path);
		workbook.write(fos);

		fos.close();
		workbook.close();

		return path;
	}
	
	public static String downloadCSVToPath(List<String[]> data, String path, String fileName) throws IOException {
		File file = new File(path);
		if (!file.exists()) {
			file.mkdirs();
		}
		path = path + AppConstants.EXPOSURES_FILENAME_FORWARD_SLASH + fileName;
        // create FileWriter object with file as parameter
        FileWriter outputfile = new FileWriter(path);
  
        // create CSVWriter object filewriter object as parameter
        CSVWriter writer = new CSVWriter(outputfile, CSVWriter.DEFAULT_SEPARATOR, CSVWriter.NO_QUOTE_CHARACTER, CSVWriter.NO_ESCAPE_CHARACTER, CSVWriter.RFC4180_LINE_END);
  
        writer.writeAll(data, false);
        //writer.writeAll(builder.toString());
        
        // closing writer connection
        writer.close();
        return path;
	}


}
