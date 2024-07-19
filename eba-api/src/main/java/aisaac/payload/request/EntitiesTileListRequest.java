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
public class EntitiesTileListRequest extends DataTableRequest{
	
	private int searchType = 0;
	
	private Long tenantId;
	
	private String searchEntityName;
	
	private String searchProductVendor;
	
	private String searchProductName;
	
	private float minScore;
	
	private float maxScore;
	
	private Integer searchShow;
	
	private boolean watchlistSelected;
	
	private boolean ipSelected;
	
	private boolean hostNameSelected;
	
	private boolean cloudResourcIdSelected;

	private String dateType;
	
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date searchFrom;

	@JsonSerialize(using = CustomDateTimeSerializer.class)
	private Date searchTo;

}
