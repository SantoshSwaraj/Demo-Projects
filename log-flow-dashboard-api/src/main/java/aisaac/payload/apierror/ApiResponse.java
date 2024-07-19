package aisaac.payload.apierror;

import aisaac.util.CustomDateTimeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Getter
@Builder
public class ApiResponse {
    private Integer status;
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    private Date timestamp;
    private String message;
    private List<Object> data;

    private ApiResponse() {
        timestamp = new Date();
    }

    public ApiResponse(Integer status) {
        this();
        this.status = status;
    }

    public ApiResponse(Integer status, String message) {
        this();
        this.status = status;
        this.message = message;
    }

    public ApiResponse(Integer status, Date timestamp, String message, List<Object> data) {
        this();
        this.status = status;
        this.timestamp = timestamp;
        this.message = message;
        
        if(data.isEmpty())
        	data = new ArrayList<>();
        	
        this.data = data;
    }
}

