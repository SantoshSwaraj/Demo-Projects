package aisaac.model;

import com.fasterxml.jackson.databind.JsonNode;
import java.util.List;

public class DruidResponse {

	private String id;
	private List<String> columns;
	private List<JsonNode> events;
	private Long count;

	public DruidResponse(String id, List<String> columns, List<JsonNode> events, Long count) {
		this.id = id;
		this.columns = columns;
		this.events = events;
		this.count = count;
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getColumns() {
		return this.columns;
	}

	public void setColumns(List<String> columns) {
		this.columns = columns;
	}

	public List<JsonNode> getEvents() {
		return this.events;
	}

	public void setEvents(List<JsonNode> events) {
		this.events = events;
	}

	public Long getCount() {
		return this.count;
	}

	public void setCount(Long count) {
		this.count = count;
	}

	public String toString() {
		return "Response [id=" + this.id + ", columns=" + this.columns + ", events=" + this.events + ", count="
				+ this.count + "]";
	}
}
