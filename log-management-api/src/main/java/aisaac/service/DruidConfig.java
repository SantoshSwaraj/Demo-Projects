package aisaac.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DruidConfig {
	@Value("${druid.address}")
	private String address;

	@Value("${druid.port}")
	private int port;

	@Value("${druid.dataSource}")
	private String dataSource;

	@Value("${druid.resultFormat}")
	private String resultFormat;

	//@Value("${druid.columns}")
	//private String columns;
	
	// Start : changes for enabling authentication
	@Value("${druid.user}")
	private String user;
	
	@Value("${druid.pwd}")
	private String pwd;
	// End : Changes for enabling authentication */
	
	//@Value("${druid.usedcolumns}")
	private String usedColumns="dstHostName,dstIPAddress,dstPortNumber,productEventClassId,productName,productSeverity,productVendor,srcHostName,srcIPAddress,srcPortNumber";
	
	@Value("${query.api.url}")
	private String queryApiURL;
	
	public String getQueryApiURL() {
		return queryApiURL;
	}
	
//	public List<String> columnsToUse() {
//		return Arrays.asList(this.columns.split(","));
//	}
	public List<String> columnsMostUsed() {
		return Arrays.asList(this.usedColumns.split(","));
	}
	public String getAddress() {
		return this.address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDataSource() {
		return this.dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

	public String getResultFormat() {
		return this.resultFormat;
	}

	public void setResultFormat(String resultFormat) {
		this.resultFormat = resultFormat;
	}

	public int getPort() {
		return this.port;
	}

	public void setPort(int port) {
		this.port = port;
	}

//	public String getColumns() {
//		return this.columns;
//	}
//
//	public void setColumns(String columns) {
//		this.columns = columns;
//	}
	
	// Start : changes for enabling authentication
	public String getUser() {
		return user;
	}

	public String getPwd() {
		return pwd;
	}
	//End : Changes for enabling authentication

	public String getUrl() {
		String endpoint = "druid/v2/";
		return String.format("%s://%s:%d/%s", new Object[] { "http",
				getAddress(), Integer.valueOf(getPort()), endpoint });
	}
}
