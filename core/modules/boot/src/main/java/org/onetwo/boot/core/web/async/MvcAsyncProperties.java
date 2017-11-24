package org.onetwo.boot.core.web.async;

import lombok.Data;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author wayshall
 * <br/>
 */
@Data
@ConfigurationProperties(MvcAsyncProperties.PREFIX)
public class MvcAsyncProperties {
	public static final String PREFIX = "jfish.mvc.async";
	public static final String ENABLE_KEY = PREFIX+".enabled";

	private int corePoolSize = 5;
	private int maxPoolSize = 50;
	private int keepAliveSeconds = 60;
	private int queueCapacity = 100000;
}
