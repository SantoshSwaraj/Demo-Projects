package aisaac.payload.mapper;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import aisaac.entities.LogStopageMaster;
import aisaac.entities.Notes;
import aisaac.util.AppConstants;

public class LogStopageMasterToNotesMapper {
	
	private List<LogStopageMaster> logStopageMasters = new ArrayList<>();
	private Map<String, String> noteMap = new HashMap<>();

	public LogStopageMasterToNotesMapper(List<LogStopageMaster> logStopageMasters, Map<String, String> notesMap) {
		this.logStopageMasters = logStopageMasters;
		this.noteMap = notesMap;
	}
	public List<Notes> map(){
		return logStopageMasters.stream()
				.filter(o->StringUtils.isNotBlank(noteMap.get(o.getProductId() + o.getTenantId() + o.getProductIP() + o.getProductHostName())))
				.map(o->  new Notes()
				.setCreatedDate(LocalDateTime.now())
				.setTypeRecId(o.getRecId())
				.setTenantId(o.getTenantId())
				.setNoteType(AppConstants.LOG_FLOW_MONITORING)
				.setCreatedBy(o.getCreatedBy())
				.setNote(noteMap.get(o.getProductId() + o.getTenantId() + o.getProductIP() + o.getProductHostName()))
				).collect(Collectors.toList());
	}

	public List<Notes> editMap(){
		return logStopageMasters.stream()
				.filter(o->StringUtils.isNotBlank(noteMap.get(String.valueOf(o.getRecId()))))
				.map(o->  new Notes()
				.setCreatedDate(LocalDateTime.now())
				.setTypeRecId(o.getRecId())
				.setTenantId(o.getTenantId())
				.setNoteType(AppConstants.LOG_FLOW_MONITORING)
				.setCreatedBy(o.getUpdatedBy().intValue())
				.setNote(noteMap.get(String.valueOf(o.getRecId())))
				).collect(Collectors.toList());
	}
	
	public static List<Notes> addNoteMap(List<LogStopageMaster> logStopageMasters, String note){
		return logStopageMasters.stream()
				.map(o->  new Notes()
				.setCreatedDate(LocalDateTime.now())
				.setTypeRecId(o.getRecId())
				.setTenantId(o.getTenantId())
				.setNoteType(AppConstants.LOG_FLOW_MONITORING)
				.setCreatedBy(o.getUpdatedBy().intValue())
				.setNote(note)
				).collect(Collectors.toList());
	}
}
