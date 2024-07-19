package aisaac.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(Include.NON_NULL)
public class LMDeepBiQuery {
	private String filters = "";
	private String[] timestampFields = {};
	private String fields = "";
	private String groupBy = "";
	private String orderBy = "";
	private String limit = "";
	private Long offset = 0L;
	private String having = "";
	private String aggregators = "";
	private String order = "";
	private String threshold = null;
	private String topNMetric = null;
	private LMDeepBiQueryContext context;
}
