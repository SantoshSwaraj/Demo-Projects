package aisaac.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomTimeFields {

	Boolean iscustom;

	int customValue;

	String customUnit;

	String value;
}
