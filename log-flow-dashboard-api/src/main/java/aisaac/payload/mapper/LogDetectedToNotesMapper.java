package aisaac.payload.mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import aisaac.entities.LogStoppageDetectedDevices;
import aisaac.entities.Notes;
import aisaac.util.AppConstants;

public class LogDetectedToNotesMapper {

	public static List<Notes> addNoteMap(List<LogStoppageDetectedDevices> logStoppageDetectedDevices, String note, Integer userId){
		return logStoppageDetectedDevices.stream()
				.map(o->  new Notes()
				.setCreatedDate(LocalDateTime.now())
				.setTypeRecId(o.getRecId())
				.setTenantId(o.getTenantId())
				.setNoteType(AppConstants.DETECTED_DEVICES)
				.setCreatedBy(userId)
				.setNote(note)
				).collect(Collectors.toList());
	}
}
