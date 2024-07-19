package aisaac.payload.request;

import java.util.List;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor(staticName = "build")
public class LogStopageDetectedDeviceCompositeKeyRequest {


	@NotEmpty(message = "ProductIPs cannot be Empty")
	private List<String> productIPs;
	
	@NotEmpty(message = "ProductHostNames cannot be Empty")
	private List<String> productHostNames;

	@NotEmpty(message = "CustomerIds cannot be Empty")
	private List<Long> customerIds;

	@NotEmpty(message = "ProductIds cannot be Empty")
	private List<Integer> productIds;
	

}
