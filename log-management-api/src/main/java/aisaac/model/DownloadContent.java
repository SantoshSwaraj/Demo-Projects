package aisaac.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DownloadContent {
	
	private String csv;
	
	public DownloadContent() {
		
	}
	
	public DownloadContent(String csv) {
		this.csv = csv;
	}

}
