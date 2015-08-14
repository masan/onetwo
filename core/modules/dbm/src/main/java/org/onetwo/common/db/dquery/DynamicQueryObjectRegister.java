package org.onetwo.common.db.dquery;

import java.util.stream.Stream;


import org.apache.commons.lang3.StringUtils;
import org.onetwo.common.db.filequery.SqlFileScanner;
import org.onetwo.common.log.JFishLoggerFactory;
import org.onetwo.common.spring.sql.SpringBasedSqlFileScanner;
import org.onetwo.common.utils.ReflectUtils;
import org.onetwo.common.utils.propconf.ResourceAdapter;
import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.util.ClassUtils;


public class DynamicQueryObjectRegister implements /*FileNamedQueryFactoryListener, */ BeanDefinitionRegistryPostProcessor {
	protected final Logger logger = JFishLoggerFactory.getLogger(this.getClass());

	private SqlFileScanner sqlFileScanner = new SpringBasedSqlFileScanner(ClassUtils.getDefaultClassLoader());
	

	@Override
	public void postProcessBeanFactory(
			ConfigurableListableBeanFactory beanFactory) throws BeansException {
	}

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		ResourceAdapter<?>[] sqlfiles = sqlFileScanner.scanMatchSqlFiles();
		Stream.of(sqlfiles).forEach(f->{
			final String fileName = f.getName();
			String className = StringUtils.substring(fileName, 0, fileName.length()-SqlFileScanner.JFISH_SQL_POSTFIX.length());
			final Class<?> interfaceClass = ReflectUtils.loadClass(className);
			BeanDefinition beandef = BeanDefinitionBuilder.rootBeanDefinition(JDKDynamicProxyCreator.class)
								.addConstructorArgValue(interfaceClass)
								.addPropertyValue("sqlFile", f)
								.setScope(BeanDefinition.SCOPE_SINGLETON)
//								.setRole(BeanDefinition.ROLE_APPLICATION)
								.getBeanDefinition();
			registry.registerBeanDefinition(className, beandef);
			logger.info("register dao bean: {} ", className);
		});
		
	}
	
}
