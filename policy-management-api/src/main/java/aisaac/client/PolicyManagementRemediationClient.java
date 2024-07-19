package aisaac.client;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.entity.ContentType;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@ToString
@Slf4j
public class PolicyManagementRemediationClient {
    
    private String baseUrl;
    
    private String externalTenantId;
    
    private boolean useProxy = false;
    
    private String proxyHostname = "";
    
    private Integer proxyPort = 0;
    
    private boolean proxyAuthorized = false;
    
    private String proxyUsername = "";
    
    private String proxyPassword = "";
    
    private Integer timeoutSeconds = 60;
    
    public static final List<Integer> OK_STATUS_CODES = Collections
            .unmodifiableList(Arrays.asList(200, 201, 202, 204, 207));
    
    private String getCleanBaseUrl() {
        String cleanedBaseUrl = "";
        URL url = null;
        try {
            url = new URL(baseUrl + "?");
            cleanedBaseUrl = String.format("%s://%s%s", url.getProtocol(), url.getHost(),
                    (url.getPort() < 1) ? "" : ":" + url.getPort());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return cleanedBaseUrl;
    }
    
    private JSONObject makeRequest(HttpUriRequest httpUriRequest) throws Exception {
        final JSONObject responseJSONObject = new JSONObject();
        URI requestUri = httpUriRequest.getURI();
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
        if (this.useProxy) {
            HttpHost proxy = new HttpHost(proxyHostname, proxyPort);
            httpClientBuilder.setProxy(proxy);
            if (this.proxyAuthorized) {
                Credentials credentials = new UsernamePasswordCredentials(proxyUsername, proxyPassword);
                AuthScope authScope = new AuthScope(proxyHostname, proxyPort);
                CredentialsProvider credsProvider = new BasicCredentialsProvider();
                credsProvider.setCredentials(authScope, credentials);
                httpClientBuilder.setDefaultCredentialsProvider(credsProvider);
            }
        }
        
        if (requestUri.toString().startsWith("https")) {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            
            sslContext.init(null, new TrustManager[] { new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }
                
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            } }, new SecureRandom());
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
                    SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            httpClientBuilder.setSSLSocketFactory(sslsf);
        }
        RequestConfig config = RequestConfig.custom().setConnectTimeout(timeoutSeconds * 1000)
                .setConnectionRequestTimeout(timeoutSeconds * 1000).setSocketTimeout(timeoutSeconds * 1000).build();
        
        StringBuilder responseBuilder = new StringBuilder();
        try (CloseableHttpClient httpClient = httpClientBuilder.setDefaultRequestConfig(config).build();
                CloseableHttpResponse httpResponse = httpClient.execute(httpUriRequest);) {
            Integer responseStatusCode = httpResponse.getStatusLine().getStatusCode();
            responseJSONObject.put("statusCode", responseStatusCode);
            String reasonPhrase = httpResponse.getStatusLine().getReasonPhrase();
            responseJSONObject.put("reasonPhrase", reasonPhrase);
            JSONArray headers = new JSONArray();
            Arrays.asList(httpResponse.getAllHeaders()).forEach(header -> {
                JSONObject headerJSONObject = new JSONObject();
                try {
                    headerJSONObject.put(header.getName(), header.getValue());
                } catch (JSONException e) {
                    log.error(String.format("Error adding response header %s due to %s", header, e.getMessage()), e);
                }
                headers.put(headerJSONObject);
            });
            responseJSONObject.put("headers", headers);
            
            if (httpResponse.getEntity() != null) {
                if (httpResponse.getEntity().getContent() != null) {
                    InputStreamReader reader = new InputStreamReader(httpResponse.getEntity().getContent());
                    
                    BufferedReader bufferedReader = new BufferedReader(reader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        responseBuilder.append(line);
                    }
                    responseJSONObject.put("content", responseBuilder.toString().trim());
                    if (responseBuilder.toString().trim().startsWith("{")
                            && responseBuilder.toString().trim().endsWith("}")) {
                        JSONObject contentJSONObject = new JSONObject(responseBuilder.toString());
                        responseJSONObject.put("content", contentJSONObject);
                    } else if (responseBuilder.toString().trim().startsWith("[")
                            && responseBuilder.toString().trim().endsWith("]")) {
                        JSONArray contentJSONArray = new JSONArray(responseBuilder.toString());
                        responseJSONObject.put("content", contentJSONArray);
                    } else {
                        responseJSONObject.put("contentString", responseBuilder.toString().trim());
                    }
                }
            }
        } catch (Exception e) {
            StringBuilder errors = new StringBuilder();
            log.error(String.format("Error for [%s] is [%s] due to [%s] with response as [%s] ", this.toString(),
                    e.getMessage(), e.getCause(), responseJSONObject), e);
            errors.append(e.getMessage());
            Throwable[] throwables = e.getSuppressed();
            if (throwables != null) {
                for (Throwable throwable : throwables) {
                    log.error(String.format("Error for [%s] is [%s] due to [%s] with response as [%s] ",
                            this.toString(), throwable.getMessage(), throwable.getCause(), responseJSONObject),
                            throwable);
                    errors.append(" | ").append(throwable.getMessage());
                }
            }
            responseJSONObject.put("errors", errors.toString());
        } catch (Throwable th) {
            log.error(String.format("Error for [%s] is [%s] due to [%s] with response as [%s] ", this.toString(),
                    th.getMessage(), th.getCause(), responseJSONObject), th);
            responseJSONObject.put("errors", th.getMessage());
        }
        
        return responseJSONObject;
    }   
    
    public Optional<JSONObject> launchRunbook(String cloudResourceId, String runbookName)
            throws Exception {
        String uriString = String.format("%s/api/v1/response/launchRunbook", this.getCleanBaseUrl());
        
        JSONObject requestJson = new JSONObject();
        requestJson.put("cloudResourceId", cloudResourceId);
        requestJson.put("aisaacTenantID", this.externalTenantId);
        requestJson.put("runbookName", runbookName);
        
        HttpEntity entity = new StringEntity(requestJson.toString());
        
        HttpPost post = new HttpPost(uriString);
        post.setEntity(entity);
        
        HttpUriRequest httpUriRequest = post;
        httpUriRequest.setHeader("Content-Type", ContentType.APPLICATION_JSON.toString());
        
        JSONObject response = this.makeRequest(httpUriRequest);
        return Optional.ofNullable(response);
    }

}
