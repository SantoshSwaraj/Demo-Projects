package aisaac.model;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@JsonIgnoreProperties({"dataSource", "queryType","virtualColumns","filter","intervals","columns","limit","order"})
public class LogMgmtSearchByFieldsDTO extends LogMgmtSearchDTO{
	
	private String fieldType;
	private String fieldColumn;
	private Integer downloadOffset;
	private String fieldCount;
	private String specificDownloadColumns;
	private DruidRequestDTO druidRequest;
	private Boolean utcTimezone;
	private Integer timeZoneOffsetMins;
	private List<LmDruidFieldsDTO> lmDruidColumns;
	
	public List<String> columnsToUseForDownload() {
		return Arrays.asList(this.specificDownloadColumns.split(","));
	}
	public LogMgmtSearchByFieldsDTO() {
	}

	
}
