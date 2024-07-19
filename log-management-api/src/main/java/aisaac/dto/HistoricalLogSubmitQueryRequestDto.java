package aisaac.dto;

import java.io.Serializable;

public class HistoricalLogSubmitQueryRequestDto implements Serializable{
	private static final long serialVersionUID = 1L;
	private String query;
	private Context context;

	public HistoricalLogSubmitQueryRequestDto() {}
	
	public HistoricalLogSubmitQueryRequestDto(String query, Context context) {
		this.query = query;
		this.context = context;
	}
	
	public void setQuery(String query) {
		this.query = query;
	}

	public void setContext(Context context) {
		this.context = context;
	}
	
	public String getQuery() {
		return query;
	}

	public Context getContext() {
		return context;
	}

	public class Context{
		private String executionMode;
		private Integer priority;
		private String selectDestination;
		
		public Context() {}
		
		public Context(String executionMode, Integer priority, String selectDestination) {
			this.executionMode = executionMode;
			this.priority = priority;
			this.selectDestination = selectDestination;
		}
		
		public void setExecutionMode(String executionMode) {
			this.executionMode = executionMode;
		}
		
		public void setPriority(Integer priority) {
			this.priority = priority;
		}
		
		public void setSelectDestination(String selectDestination) {
			this.selectDestination = selectDestination;
		}

		public String getExecutionMode() {
			return executionMode;
		}

		public Integer getPriority() {
			return priority;
		}

		public String getSelectDestination() {
			return selectDestination;
		}
	}

}
