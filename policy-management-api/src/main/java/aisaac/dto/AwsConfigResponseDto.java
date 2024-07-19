package aisaac.dto;

import aisaac.entities.CspmFinding;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AwsConfigResponseDto {
	
	public String findingTitle;
	public String severity;
	
	

}
