package aisaac.payload.request;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import aisaac.domain.datatable.DataTableRequest;
import aisaac.util.CustomDateTimeSerializer;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class ThreatListRequest extends DataTableRequest {

	@NotNull(message = "Tenant Id Can't be null")
	private Long tenantId;
	@NotNull(message = "Adr Entity Rec Id Can't be null")
	private Long adrEntityRecId;
	@NotBlank(message = "Date Type Can't be Empty")
	private String dateType;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date searchFrom;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date searchTo;
}
