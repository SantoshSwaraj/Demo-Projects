package aisaac.dto;

import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AutoSuggestionRequiredFileds {

	Set<String> productVendor;
	
	Set<String> productName;
	
	Set<String> productType;
	
	Set<String> productIP;
	
	Set<String> productHostName;
	
	Set<String> collectorIP;
	
}
