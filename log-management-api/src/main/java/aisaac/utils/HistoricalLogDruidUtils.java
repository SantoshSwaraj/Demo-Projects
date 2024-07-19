package aisaac.utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import aisaac.dao.GlobalSettingsRepository;
import aisaac.service.DruidConfig;
import aisaac.util.SecurityUtils;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class HistoricalLogDruidUtils {
	
	private DruidConfig configs;
	private GlobalSettingsRepository globalSettingsRepo;
	
	public HistoricalLogDruidUtils(
			DruidConfig configs,
			GlobalSettingsRepository globalSettingsRepo) {
		this.configs = configs;
		this.globalSettingsRepo = globalSettingsRepo;
	}
	
	/*Starts : Client code to run druid query*/
	public ResponseEntity<Object> executeDruidQuery(String uri, HttpMethod httpMethod) throws RuntimeException {
		return executeDruidQuery(uri, httpMethod, null);
	}
	
	public ResponseEntity<Object> executeDruidQuery(String uri, HttpMethod httpMethod, String requestBody){
		Object response = null;
		try {
			RestTemplate restTemplate = getRestTemplate();
			restTemplate.getMessageConverters().add(0, new StringHttpMessageConverter(StandardCharsets.UTF_8));
			log.error("Druid JSON request - {}", requestBody);
		
			HttpHeaders requestHeader = new HttpHeaders();
			requestHeader.setContentType(MediaType.APPLICATION_JSON_UTF8);
			
			requestHeader.add(HttpHeaders.AUTHORIZATION, "Basic " + Base64.getUrlEncoder()
				.encodeToString((configs.getUser() + ":" + SecurityUtils.decrypt256(configs.getPwd())).getBytes(Charset.forName("UTF-8"))));
			
			HttpEntity httpEntity = new HttpEntity<>(requestBody, requestHeader);
			ResponseEntity<String> result = restTemplate
					  	.exchange(configs.getUrl().concat("sql/statements").concat(uri), httpMethod, httpEntity, String.class);
			
			return new ResponseEntity<Object>(Optional.ofNullable(result.getBody()).orElse(""), HttpStatus.OK);
			
		}catch(ResourceAccessException rae) {
			rae.printStackTrace();
			response = rae.getMessage();
			return new ResponseEntity<Object>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception ex) {
			ex.printStackTrace();
			response = ex.getMessage();
			return new ResponseEntity<Object>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@SuppressWarnings("deprecation")
	private RestTemplate getRestTemplate() throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {

		SSLContextBuilder sslContextBuilder = new SSLContextBuilder();
		sslContextBuilder.loadTrustMaterial(null, (chain, authType) -> true);
		SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContextBuilder.build(),
				SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		httpClientBuilder.setSSLSocketFactory(sslsf);

		String proxyEnabled = globalSettingsRepo.findByParamTypeAndParamName("proxy_setting", "proxy_enabled").get().getParamValue();
		if ("true".equalsIgnoreCase(proxyEnabled)) {
			String proxyHostname = globalSettingsRepo.findByParamTypeAndParamName("proxy_setting", "proxy_hostname").get().getParamValue();
			String proxyPortnumber = globalSettingsRepo.findByParamTypeAndParamName("proxy_setting", "proxy_portnumber").get().getParamValue();
			Integer proxyPortnumberInteger = (StringUtils.isNumeric(proxyPortnumber))
					? Integer.parseInt(proxyPortnumber)
					: 0;
			HttpHost proxy = new HttpHost(proxyHostname, proxyPortnumberInteger);
			httpClientBuilder.setProxy(proxy);
			String proxyisauthorized = globalSettingsRepo.findByParamTypeAndParamName("proxy_setting",
					"proxy_is_authorized").get().getParamValue();
			if ("true".equalsIgnoreCase(proxyisauthorized)) {
				String proxyUsername = globalSettingsRepo.findByParamTypeAndParamName("proxy_setting", "proxy_username").get().getParamValue();
				String proxyPassword = globalSettingsRepo.findByParamTypeAndParamName("proxy_setting", "proxy_password").get().getParamValue();
				try {
					proxyPassword = SecurityUtils.decrypt256(proxyPassword);
				} catch (Exception e) {
					log.error("Error decrypting proxy password for threatQ client is " + e.getMessage());
					e.printStackTrace();
				}
				Credentials credentials = new UsernamePasswordCredentials(proxyUsername, proxyPassword);
				AuthScope authScope = new AuthScope(proxyHostname, proxyPortnumberInteger);
				CredentialsProvider credsProvider = new BasicCredentialsProvider();
				credsProvider.setCredentials(authScope, credentials);
				httpClientBuilder.setDefaultCredentialsProvider(credsProvider);
			}

		}

		CloseableHttpClient httpClient = httpClientBuilder.build();
		String apiTimeoutVal = this.globalSettingsRepo.findByParamTypeAndParamName(LMConstants.PARAM_APP_SETTING_TYPE,
				LMConstants.PARAM_MVC_APP_SETTING_NAME_API_TIMEOUT).get().getParamValue();

		Integer apiTimeout = 60;

		try {
			apiTimeout = Integer.parseInt(apiTimeoutVal);
		} catch (Exception e) {
			e.printStackTrace();
		}

		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

		requestFactory.setConnectionRequestTimeout(apiTimeout * 1000);
		requestFactory.setConnectTimeout(apiTimeout * 1000);
		requestFactory.setReadTimeout(apiTimeout * 1000);
		requestFactory.setHttpClient(httpClient);

		RestTemplate restTemplate = new RestTemplate(requestFactory);
		return restTemplate;
	}
	/*Ends : Client code to run druid query*/

}
