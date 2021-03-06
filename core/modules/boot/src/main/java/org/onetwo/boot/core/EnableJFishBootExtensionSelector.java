package org.onetwo.boot.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import org.onetwo.boot.core.EnableJFishBootExtension.AppcationType;
import org.onetwo.boot.core.jwt.JwtContextConfig;
import org.onetwo.boot.core.ms.BootMSContextAutoConfig;
import org.onetwo.boot.core.shutdown.GraceKillConfiguration;
import org.onetwo.boot.core.web.BootWebUIContextAutoConfig;
import org.onetwo.boot.core.web.async.AsyncMvcConfiguration;
import org.onetwo.boot.core.web.async.AsyncTaskConfiguration;
import org.onetwo.boot.core.web.mvc.EnhanceRequestMappingConfiguration;
import org.onetwo.boot.core.web.mvc.ErrorHandleConfiguration;
import org.onetwo.boot.core.web.mvc.log.AccessLogConfiguration;
import org.onetwo.boot.core.web.service.BootCommonServiceConfig;
import org.onetwo.boot.module.alioss.OssConfiguration;
import org.onetwo.boot.module.cache.SpringCacheConfiguration;
import org.onetwo.boot.module.cos.CosConfiguration;
import org.onetwo.boot.module.redis.RedisConfiguration;
import org.onetwo.boot.module.redission.RedissonConfiguration;
import org.onetwo.boot.module.security.oauth2.OAuth2SsoClientAutoContextConfig;
import org.onetwo.boot.plugin.core.JFishWebPlugin;
import org.onetwo.common.exception.BaseException;
import org.onetwo.common.spring.context.AbstractImportSelector;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.support.SpringFactoriesLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

/**
 * @author wayshall
 * <br/>
 */
public class EnableJFishBootExtensionSelector extends AbstractImportSelector<EnableJFishBootExtension> implements BeanClassLoaderAware {

	private ClassLoader beanClassLoader;
	
	@Override
	protected List<String> doSelect(AnnotationMetadata metadata, AnnotationAttributes attributes) {
		List<String> classNames = new ArrayList<String>();
		
		if(attributes.getBoolean("enableCommonService")){
			classNames.add(BootCommonServiceConfig.class.getName());
		}
		
		AppcationType appcationType = (AppcationType)attributes.get("appcationType");
		if(appcationType==AppcationType.WEB_SERVICE){
			classNames.add(BootMSContextAutoConfig.class.getName());
		}else if(appcationType==AppcationType.WEB_UI){
			classNames.add(BootWebUIContextAutoConfig.class.getName());
		}
		classNames.add(ErrorHandleConfiguration.class.getName());
		classNames.add(EnhanceRequestMappingConfiguration.class.getName());

		classNames.add(JwtContextConfig.class.getName());
		
		classNames.add(OAuth2SsoClientAutoContextConfig.class.getName());
		classNames.add(RedisConfiguration.class.getName());
		classNames.add(AsyncMvcConfiguration.class.getName());
		classNames.add(AsyncTaskConfiguration.class.getName());
		classNames.add(AccessLogConfiguration.class.getName());
		classNames.add(GraceKillConfiguration.class.getName());

		//cache
		classNames.add(SpringCacheConfiguration.class.getName());
		classNames.add(RedissonConfiguration.class.getName());
		
		//store
		classNames.add(OssConfiguration.class.getName());
		classNames.add(CosConfiguration.class.getName());
		
		//activemq
		classNames.add("org.onetwo.boot.module.activemq.ActivemqConfiguration");
		
		Collection<String> exts = new LinkedHashSet<>(SpringFactoriesLoader.loadFactoryNames(this.annotationClass, this.beanClassLoader));
		for(String extClassName : exts){
			Class<?> extClass;
			try {
				extClass = ClassUtils.forName(extClassName, beanClassLoader);
			} catch (ClassNotFoundException | LinkageError e) {
				throw new BaseException("load "+this.annotationClass.getSimpleName()+" error. extension class: " + extClassName, e);
			}
			if(extClass.getAnnotation(JFishWebPlugin.class)==null){
				throw new BaseException("extension class["+extClassName+"] must be annotated by "+JFishWebPlugin.class.getName());
			}
			classNames.add(extClassName);
		}
		
		
		return classNames;
	}

	@Override
	public void setBeanClassLoader(ClassLoader classLoader) {
		this.beanClassLoader = classLoader;
	}
	
	

}
