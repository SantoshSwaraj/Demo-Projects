package aisaac.dto;

import java.math.BigInteger;

import aisaac.entities.CspmFinding;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class PolicyManagementDTO {
	
	private CspmFinding cspmFinding;
	private Long count;
	private String playbookName;

}
