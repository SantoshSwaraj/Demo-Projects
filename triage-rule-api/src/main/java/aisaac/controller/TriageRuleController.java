package aisaac.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import aisaac.dto.TriageRuleListingRequestDto;
import aisaac.payload.response.ApiResponse;
import aisaac.service.TriageRuleService;
import aisaac.util.CommonUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/v1/tm-triage")
@RequiredArgsConstructor
public class TriageRuleController {

	@Autowired
	TriageRuleService TriageRuleService;

	// URL :: http://localhost:9008/v1/tm-triage/testApi
	@GetMapping("/testApi")
	public ResponseEntity<Object> testApi() {
		log.info("ErrorLog is working");
		return new ResponseEntity<Object>("api is working", HttpStatus.OK);
	}

	// URL :: http://localhost:9008/v1/tm-triage/generateEpochTime?date=03-Nov-2023 00:00:00
	@GetMapping("/generateEpochTime")
	public ResponseEntity<Object> getEpochTime(@RequestParam Date date) {
		log.info("Called /getEpochTime>>"+date);
		Long epochTime=CommonUtils.parseDateToEpoch(date);
		return new ResponseEntity<Object>("Generated epoch time : "+epochTime + " for the timeStamp:"+date, HttpStatus.OK);
	}
	// URL :: http://localhost:9008/v1/tm-triage/generateDateTimeStamp?epochTime=1708021799000
	@GetMapping("/generateDateTimeStamp")
	public ResponseEntity<Object> generateDateTimeStamp(@RequestParam Long epochTime) {
		Date date=CommonUtils.parseEpochToDate(epochTime);
		return new ResponseEntity<Object>("Generated Date time : "+date + " for the epoch time:"+epochTime, HttpStatus.OK);
	}

	//	URL::localhost:9008/v1/tm-triage/listing
	@PostMapping("/listing")
	public ResponseEntity<Object> getTriageRuleListing(@RequestBody TriageRuleListingRequestDto request) {
		Object data = TriageRuleService.getTriageRuleListing(request);
		ApiResponse response = new ApiResponse();
		response.setData(data);
		response.setStatus(200);
		response.setMessage(null);
		return new ResponseEntity<Object>(response, HttpStatus.OK);
	}

}