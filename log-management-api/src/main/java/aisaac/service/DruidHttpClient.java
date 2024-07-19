package aisaac.service;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.apache.http.HttpException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import in.zapr.druid.druidry.client.DruidClient;
import in.zapr.druid.druidry.client.exception.ConnectionException;
import in.zapr.druid.druidry.client.exception.QueryException;
import in.zapr.druid.druidry.query.DruidQuery;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DruidHttpClient implements DruidClient {
  private final RestTemplate restTemplate;
  
  private final DruidConfig druidConfig;
  
  private final ObjectMapper mapper = new ObjectMapper();
  
  private final HttpHeaders header = new HttpHeaders();
  
  public DruidHttpClient(DruidConfig config) {
    this.druidConfig = config;
    this.restTemplate = new RestTemplate();
    this.header.setContentType(MediaType.APPLICATION_JSON);
    // Start : changes for enabling authentication
    this.header.add("Authorization", "Basic " + 
    		Base64.getUrlEncoder().encodeToString((druidConfig.getUser()+":"+druidConfig.getPwd()).getBytes(Charset.forName("UTF-8"))));
    //	End : Changes for enabling authentication
  }
  
  public void connect() throws ConnectionException {}
  
  public void close() throws ConnectionException {}
  
  public String query(DruidQuery druidQuery) throws QueryException {
	  try {
          String body = mapper.writeValueAsString(druidQuery);
          log.info("Druid JSON Query - {}", body);

          HttpEntity<String> request = new HttpEntity<>(body, header);
          ResponseEntity<String> response = restTemplate.postForEntity(druidConfig.getUrl(), request, String.class);

          if (response.getStatusCode() != HttpStatus.OK){
              if (response.hasBody()) throw new HttpException(Optional.ofNullable(response.getBody()).orElse(""));
              else throw new HttpException("Status code: "+ response.getStatusCodeValue());
          }

          return Optional.ofNullable(response.getBody()).orElse("{}");
      } catch (Exception ex) {
          throw new QueryException(ex);
      }
  }
  
  public <T> List<T> query(DruidQuery druidQuery, Class<T> className) throws QueryException {
    return null;
  }
}
