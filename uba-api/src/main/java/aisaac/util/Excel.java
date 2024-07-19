package aisaac.util;

import java.io.IOException;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

public interface Excel {
	
	void writeHeader();
	void createCell(Row row, int columnCount, Object value, CellStyle style);
	void write();
	default Excel generate() throws IOException {
		writeHeader();
		write();
		return this;
	}
	Workbook workbook();

}
