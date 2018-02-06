package org.onetwo.boot.plugin.mvc;

import java.lang.reflect.Method;
import java.util.Optional;

import org.onetwo.boot.core.config.BootJFishConfig;
import org.onetwo.boot.core.config.PluginContextPathModes.PathContext;
import org.onetwo.boot.core.config.PluginProperties;
import org.onetwo.boot.core.web.mvc.ExtRequestMappingHandlerMapping.RequestMappingCombiner;
import org.onetwo.boot.plugin.core.PluginManager;
import org.onetwo.boot.plugin.core.WebPlugin;
import org.onetwo.boot.plugin.mvc.annotation.WebPluginContext;
import org.onetwo.common.annotation.AnnotationUtils;
import org.onetwo.common.spring.SpringUtils;
import org.onetwo.common.utils.LangUtils;
import org.onetwo.common.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

/**********
 * 
 * @author wayshall
 *
 */
public class BootPluginRequestMappingCombiner implements RequestMappingCombiner {

	@Autowired
	private PluginManager pluginManager;
	@Autowired
	private ApplicationContext applicationContext;
	@Autowired
	private BootJFishConfig bootJFishConfig;
	
	
	@Override
	public RequestMappingInfo combine(Method method, Class<?> handlerType, RequestMappingInfo info) {
		if(info!=null){
			String contextPath = this.getPluginContextPath(method, handlerType);
			String existPath = LangUtils.getFirst(info.getPatternsCondition().getPatterns());
			/*
			//如果路径不是以插件前缀开始，则自动加插件前缀
			final String contextPathWithSlash = StringUtils.appendEndWithSlash(contextPath);
			if(StringUtils.isNotBlank(contextPath) && !existPath.startsWith(contextPathWithSlash)){
				info = createPluginRequestMappingInfo(contextPath, method, handlerType).combine(info);
			}*/
			PathContext pc = PathContext.builder()
										.pluginContextPath(contextPath)
										.controllerPath(existPath)
										.build();
			contextPath = bootJFishConfig.getPluginContextPathModes().getPluginContextPath(pc);
			if(StringUtils.isNotBlank(contextPath)){
				info = createPluginRequestMappingInfo(contextPath, method, handlerType).combine(info);
			}
		}
		return info;
	}
	
	private String getPluginContextPath(Method method, Class<?> handlerType){
		WebPluginContext pluginContext = AnnotationUtils.findAnnotationWithStopClass(handlerType, method, WebPluginContext.class);
		if(pluginContext!=null){
			return resolvePluginContextPath(pluginContext.contextPath());
		}
		Optional<WebPlugin> plugin = pluginManager.findPluginByElementClass(handlerType);
		if(plugin.isPresent()){
			String pluginName = plugin.get().getPluginMeta().getName();
			PluginProperties pluginProps = bootJFishConfig.getPlugin().get(pluginName);
			/*if(!bootJFishConfig.isAppendPluginContextPath() || (pluginProps!=null && !pluginProps.isAppendPluginContextPath())){
				return null;
			}*/
			if(pluginProps!=null && !pluginProps.isAppendPluginContextPath()){
				return null;
			}
			return resolvePluginContextPath(plugin.get().getContextPath());
		}
		return null;
	}

	private RequestMappingInfo createPluginRequestMappingInfo(String rootPath, Method method, Class<?> handlerType) {
		return RequestMappingCombiner.createRequestMappingInfo(rootPath, method, handlerType);
	}
	
	private String resolvePluginContextPath(final String pluginContextPath){
		String path = SpringUtils.resolvePlaceholders(applicationContext, pluginContextPath);
		if(StringUtils.isNotBlank(path)){
			path = StringUtils.appendStartWithSlash(path);
		}
		return path;
	}

}
