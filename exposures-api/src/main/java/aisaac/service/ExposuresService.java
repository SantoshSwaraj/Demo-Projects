package aisaac.service;

import aisaac.payload.request.ExposuresDetailsRequest;

public interface ExposuresService {

	Object getExposuresList(ExposuresDetailsRequest request, Integer userId, boolean isExport);

	Object exportExposuresList(ExposuresDetailsRequest request, Integer limit, String path);

}
