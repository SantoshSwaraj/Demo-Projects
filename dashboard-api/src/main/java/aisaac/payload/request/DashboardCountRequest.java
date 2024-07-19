package aisaac.payload.request;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import aisaac.util.DashboardUtils;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DashboardCountRequest {

	@NotNull(message = "TenantId Can't be null")
	private Long tenantId;

	@NotBlank(message = "Date Type Can't be Blank")
	private String dateType;

//	@JsonSerialize(using = CustomDateSerializer.class)
//	private Date fromDate;
//	
//	@JsonSerialize(using = CustomDateSerializer.class)
//	private Date toDate;

	private LocalDateTime fromDate;

	private LocalDateTime toDate;

	public void setFromDate(@JsonProperty("fromDate") Long searchFromDateEpochMilli) {
		this.fromDate = DashboardUtils.getLocalDateTimeByEpochMilli(searchFromDateEpochMilli);
		this.fromDate = LocalDateTime.of(this.fromDate.getYear(), this.fromDate.getMonth(),
				this.fromDate.getDayOfMonth(), this.fromDate.getHour(), 0, 0);
	}

	public void setToDate(@JsonProperty("toDate") Long searchToDateEpochMilli) {
		this.toDate = DashboardUtils.getLocalDateTimeByEpochMilli(searchToDateEpochMilli);
		this.toDate = LocalDateTime.of(this.toDate.getYear(), this.toDate.getMonth(), this.toDate.getDayOfMonth(),
				this.toDate.getHour(), 0, 0);
	}
}
