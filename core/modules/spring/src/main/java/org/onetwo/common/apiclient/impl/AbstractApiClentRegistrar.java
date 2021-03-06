package org.onetwo.common.apiclient.impl;

import java.net.MalformedURLException;
import java.net.URL;

import org.onetwo.common.apiclient.RestExecutorFactory;
import org.onetwo.common.spring.context.AbstractImportRegistrar;
import org.onetwo.common.spring.context.AnnotationMetadataHelper;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.StringUtils;

/**
 * @author wayshall
 * <br/>
 */
abstract public class AbstractApiClentRegistrar<IMPORT, COMPONENT> extends AbstractImportRegistrar<IMPORT, COMPONENT> implements ImportBeanDefinitionRegistrar, BeanClassLoaderAware, ResourceLoaderAware {

	public static final String ATTRS_URL = "url";
	public static final String ATTRS_BASE_URL = "baseUrl";
	public static final String ATTRS_NAME = "name";
	public static final String ATTRS_PATH = "path";
	public static final String ATTRS_REST_EXECUTOR_FACTORY = "restExecutorFactory";
	
//	private RestExecutor restExecutor;
	

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		this.regiseterRestExecutor(getAnnotationMetadataHelper(importingClassMetadata), registry);
		super.registerBeanDefinitions(importingClassMetadata, registry);
	}
	
	protected void regiseterRestExecutor(AnnotationMetadataHelper annotationMetadataHelper, BeanDefinitionRegistry registry){
		if(registry.containsBeanDefinition(RestExecutorFactory.REST_EXECUTOR_FACTORY_BEAN_NAME)){
			return ;
		}
		Class<?> restExecutorFacotryClass = (Class<?>)annotationMetadataHelper.getAttributes().get(ATTRS_REST_EXECUTOR_FACTORY);
//		RestExecutorFactory factory = null;
		if(restExecutorFacotryClass==null || restExecutorFacotryClass==RestExecutorFactory.class){
			restExecutorFacotryClass = DefaultRestExecutorFactory.class;
		}
		BeanDefinitionBuilder definition = BeanDefinitionBuilder.genericBeanDefinition(restExecutorFacotryClass);
		definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
		registry.registerBeanDefinition(RestExecutorFactory.REST_EXECUTOR_FACTORY_BEAN_NAME, definition.getBeanDefinition());
		
	}

	/***
	 * 
	 * @author wayshall
	 * @return
	 */
	abstract protected BeanDefinitionBuilder createApiClientFactoryBeanBuilder(AnnotationMetadata annotationMetadata, AnnotationAttributes attributes);


	@Override
	protected BeanDefinitionBuilder createComponentFactoryBeanBuilder(
			AnnotationMetadata annotationMetadata,
			AnnotationAttributes attributes) {
		return createApiClientFactoryBeanBuilder(annotationMetadata, attributes);
	}


	final protected String resolveUrl(AnnotationAttributes tagAttributes) {
		String url = resolve(tagAttributes.getString(ATTRS_URL));
		if(!StringUtils.hasText(url)){
			url = resolve(annotationMetadataHelper.getAttributes().getString(ATTRS_BASE_URL));
		}
		if (StringUtils.hasText(url)) {
			if (!url.contains("://")) {
				url = "http://" + url;
			}
			try {
				new URL(url);
			}
			catch (MalformedURLException e) {
				throw new IllegalArgumentException(url + " is malformed", e);
			}
		}
		return url;
	}

	final protected String resolvePath(AnnotationAttributes attributes) {
		String path = resolve(attributes.getString(ATTRS_PATH));
		if (StringUtils.hasText(path)) {
			path = path.trim();
			if (!path.startsWith("/")) {
				path = "/" + path;
			}
			if (path.endsWith("/")) {
				path = path.substring(0, path.length() - 1);
			}
		}
		return path;
	}

}
