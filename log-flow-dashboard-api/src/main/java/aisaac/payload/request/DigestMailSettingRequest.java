package aisaac.payload.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class DigestMailSettingRequest {

	@NotNull(message = "enableMail can't be null")
	@Min(value = 0, message = "enableMail must be 0 or 1")
    @Max(value = 1, message = "enableMail must be 0 or 1")
	private Integer enableMail;

	@NotNull(message = "tenantId can't be null")
	private Long tenantId;

	@NotEmpty(message = "tenantName can't be Blank")
	private String tenantName;

//	@NotEmpty(message = "toEmail can't be Blank")
	private String toEmail;

	private String ccEmail;

	@NotNull(message = "userId can't be null")
	private Long userId;

	//@NotNull(message = "mailTriggeredHour can't be null")
	private Integer mailTriggeredHour;

}
