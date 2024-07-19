package aisaac.payload.request;

import java.util.Date;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import aisaac.domain.datatable.DataTableRequest;
import aisaac.util.CustomDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class UserTileListRequest extends DataTableRequest{
	
	private int searchType = 0;
	
	private Long tenantId;
	
	private String searchUserName;
	
	private String searchSource;
	
	private float minScore;
	
	private float maxScore;
	
	private String searchDepartment;
	
	private Integer searchShow;
	
	private boolean watchlistSelected;
	
	private String dateType;
	
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date searchFrom;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date searchTo;

}
