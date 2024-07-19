package aisaac.payload.mapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import aisaac.entities.LogStoppageWhitelistDevices;
import aisaac.entities.Notes;
import aisaac.util.AppConstants;

public class AllowListToNotesMapper {

	public static List<Notes> addMap(List<LogStoppageWhitelistDevices> devices,Map<Long, String> notesMap){
		return devices.stream()
				.filter(o->StringUtils.isNotBlank(notesMap.get(o.getProductId() + o.getTenantId())))
				.map(o->new Notes()
				.setTenantId(o.getTenantId())
				.setTypeRecId(o.getRecId())
				.setCreatedDate(LocalDateTime.now())
				.setNoteType(AppConstants.ALLOWLIST)
				.setCreatedBy(o.getCreatedById().intValue())
				.setNote(notesMap.get(o.getProductId() + o.getTenantId()))
				).collect(Collectors.toList());
	}
	
	public static List<Notes> editMap(List<LogStoppageWhitelistDevices> devices,Map<Long, String> notesMap){
		return devices.stream()
				.filter(o->StringUtils.isNotBlank(notesMap.get(o.getRecId())))
				.map(o->new Notes()
				.setTenantId(o.getTenantId())
				.setTypeRecId(o.getRecId())
				.setCreatedDate(LocalDateTime.now())
				.setNoteType(AppConstants.ALLOWLIST)
				.setCreatedBy(o.getUpdatedById().intValue())
				.setNote(notesMap.get(o.getRecId()))
				).collect(Collectors.toList());
	}
	
	public static List<Notes> deleteMap(List<LogStoppageWhitelistDevices> devices,String note){
		return devices.stream()
				.map(o->new Notes()
						.setTenantId(o.getTenantId())
						.setTypeRecId(o.getRecId())
						.setCreatedDate(LocalDateTime.now())
						.setNoteType(AppConstants.ALLOWLIST)
						.setCreatedBy(o.getUpdatedById().intValue())
						.setNote(note)
						).collect(Collectors.toList());
	}
}
