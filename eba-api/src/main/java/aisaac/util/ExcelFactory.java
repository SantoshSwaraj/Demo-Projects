package aisaac.util;

import java.util.List;

public class ExcelFactory {

	public Excel createExcel(List<Object> data) {
		return new ThreatExportExcel(data);
	}
}
