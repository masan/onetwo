package org.onetwo.common.file;

import java.io.File;

public class SimpleFileStoredMeta implements FileStoredMeta{

	private String accessablePath;
	private String fullAccessablePath;
	private String storedServerLocalPath;
	private String sotredFileName;
	private String originalFilename;
	private String bizModule;
	
	public SimpleFileStoredMeta(String originalFilename, String storedServerLocalPath) {
		super();
		this.originalFilename = originalFilename;
		this.storedServerLocalPath = storedServerLocalPath;
	}

	public String getBizModule() {
		return bizModule;
	}

	public void setBizModule(String bizModule) {
		this.bizModule = bizModule;
	}

	@Override
	public String getAccessablePath() {
		return accessablePath;
	}

	@Override
	public String getOriginalFilename() {
		return originalFilename;
	}

	public File toFile() {
		return new File(accessablePath);
	}

	public String getFullAccessablePath() {
		return fullAccessablePath;
	}

	public void setFullAccessablePath(String fullAccessablePath) {
		this.fullAccessablePath = fullAccessablePath;
	}

	public String getStoredServerLocalPath() {
		return storedServerLocalPath;
	}

	public void setStoredServerLocalPath(String storedServerLocalPath) {
		this.storedServerLocalPath = storedServerLocalPath;
	}

	public String getSotredFileName() {
		return sotredFileName;
	}

	public void setSotredFileName(String sotredFileName) {
		this.sotredFileName = sotredFileName;
	}

	public void setAccessablePath(String accessablePath) {
		this.accessablePath = accessablePath;
	}

}
