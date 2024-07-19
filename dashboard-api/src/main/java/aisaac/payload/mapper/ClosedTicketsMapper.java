package aisaac.payload.mapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import aisaac.entities.TicketWithOutFormula;
import aisaac.payload.response.CountAndNameResponse;
import aisaac.util.DashboardUtils;
import aisaac.util.ResponseOrMessageConstants;

public class ClosedTicketsMapper {

	public static Object map(List<TicketWithOutFormula> ticketsList) {
		Map<String, Long> defaultData = new HashMap<String, Long>(Map.of("Incident", 0L, "Valuable Finding", 0L,
				"Allowlist", 0L, "Valuable Finding+Allowlist", 0L, "totalClosedTicketCount", 0L, "Informational", 0L));

		List<CountAndNameResponse> data = ticketsList.stream().map(o -> {
			if (o.isWhitelist() && o.isRelevant()) {
				defaultData.put("totalClosedTicketCount",
						defaultData.get("totalClosedTicketCount") + o.getTicketIdCount());
				defaultData.remove("Valuable Finding+Allowlist");
				return new CountAndNameResponse(o.getTicketIdCount(), "Valuable Finding+Allowlist",
						DashboardUtils.getLocalDateTimeInMilliSec(o.getMinClosedDate()),
						DashboardUtils.getLocalDateTimeInMilliSec(o.getMaxClosedDate()));
			} else if (o.isIncident()) {
				defaultData.put("totalClosedTicketCount",
						defaultData.get("totalClosedTicketCount") + o.getTicketIdCount());
				defaultData.remove("Incident");
				return new CountAndNameResponse(o.getTicketIdCount(), "Incident",
						DashboardUtils.getLocalDateTimeInMilliSec(o.getMinClosedDate()),
						DashboardUtils.getLocalDateTimeInMilliSec(o.getMaxClosedDate()));
			} else if (o.isRelevant()) {
				defaultData.put("totalClosedTicketCount",
						defaultData.get("totalClosedTicketCount") + o.getTicketIdCount());
				defaultData.remove("Valuable Finding");
				return new CountAndNameResponse(o.getTicketIdCount(), "Valuable Finding",
						DashboardUtils.getLocalDateTimeInMilliSec(o.getMinClosedDate()),
						DashboardUtils.getLocalDateTimeInMilliSec(o.getMaxClosedDate()));
			} else if (o.isWhitelist()) {
				defaultData.put("totalClosedTicketCount",
						defaultData.get("totalClosedTicketCount") + o.getTicketIdCount());

				defaultData.remove("Allowlist");
				return new CountAndNameResponse(o.getTicketIdCount(), "Allowlist",
						DashboardUtils.getLocalDateTimeInMilliSec(o.getMinClosedDate()),
						DashboardUtils.getLocalDateTimeInMilliSec(o.getMaxClosedDate()));
			} else if (o.isInfo()) {
				defaultData.put("totalClosedTicketCount",
						defaultData.get("totalClosedTicketCount") + o.getTicketIdCount());
				defaultData.remove("Informational");
				return new CountAndNameResponse(o.getTicketIdCount(), "Informational",
						DashboardUtils.getLocalDateTimeInMilliSec(o.getMinClosedDate()),
						DashboardUtils.getLocalDateTimeInMilliSec(o.getMaxClosedDate()));
			}
			return new CountAndNameResponse();
		}).collect(Collectors.toList());

		defaultData.forEach((key, value) -> {
			if (!key.equalsIgnoreCase("totalClosedTicketCount")) {
				data.add(new CountAndNameResponse(value, key, null, null));
				defaultData.put("totalClosedTicketCount", defaultData.get("totalClosedTicketCount") + value);
			}
		});

		return Map.of(ResponseOrMessageConstants.TOTAL_TICKET_COUNT, defaultData.get("totalClosedTicketCount"),
				ResponseOrMessageConstants.DATA, data);
	}
}
