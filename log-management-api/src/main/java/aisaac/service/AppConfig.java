package aisaac.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

	@Value("${app.pageSize}")
	private long pageSize = 100;
	
	@Value("${druid.initialResultSize}")
	private long initialResultSize = 1000;
	
	@Value("${druid.midResultSize}")
	private long midResultSize = 10000;
	
	@Value("${druid.maxUIResultSize}")
	private long maxUIResultSize = 50000;
	
	@Value("${druid.histogram.barsLimit}")
	private long histogramBarsLimit = 40;
	
	@Value("${druid.columnChartLimit}")
	private long columnChartLimit = 20;

	private DruidConfig druidConfig;
	
	private final String[] topFields = new String[] { "src", "dst", "cs1", "cs2", "cs3", "atz" };

	@Autowired
	public AppConfig(DruidConfig druidConfig) {
		this.druidConfig = druidConfig;
	}

	public String[] getTopFields() {
		return this.topFields;
	}

	public long getPageSize() {
		return this.pageSize;
	}
	
	public long getInitialResultSize() {
		return initialResultSize;
	}
	
	public long getMidResultSize() {
		return midResultSize;
	}

	public long getMaxUIResultSize() {
		return maxUIResultSize;
	}
	
	public long getHistogramBarsLimit() {
		return histogramBarsLimit;
	}

	public long getColumnChartLimit() {
		return columnChartLimit;
	}

	public DruidConfig getDruidConfig() {
		return this.druidConfig;
	}

	public void setDruidConfig(DruidConfig druidConfig) {
		this.druidConfig = druidConfig;
	}

}
