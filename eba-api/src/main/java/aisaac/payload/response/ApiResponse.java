package aisaac.payload.response;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import aisaac.util.CustomDateTimeSerializer;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@Builder
public class ApiResponse {
	private Integer status;
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date timestamp;
	private String message;
	private Object data;

	public ApiResponse() {
		timestamp = new Date();
	}

	public ApiResponse(Integer status) {
		this();
		this.status = status;
	}

	public ApiResponse(Integer status, String message) {
		this();
		this.status = status;
		this.message = message;
	}

	public ApiResponse(Integer status, Date timestamp, String message, Object data) {
		this();
		this.status = status;
		this.message = message;
		this.data = data;
	}
}
