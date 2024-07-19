package aisaac.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LmSearchDTO {
	DruidRequestDTO request;
	int requestedPage; 
	boolean isDownload;
	List<String> columnsToUseForDownload; 
	List<LmDruidFieldsDTO> lmDruidColumns;
}
