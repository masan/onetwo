package org.onetwo.common.web.filter;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.onetwo.common.exception.ServiceException;
import org.onetwo.common.profiling.UtilTimerStack;
import org.onetwo.common.spring.SpringApplication;
import org.onetwo.common.web.config.BaseSiteConfig;
import org.onetwo.common.web.config.WebAppConfigurator;
import org.onetwo.common.web.utils.RequestUtils;
import org.onetwo.common.web.utils.WebLocaleUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

/****
 * 自定义的过滤器
 * @author weishao
 *
 */
@SuppressWarnings("unused")
public class BaseInitFilter extends IgnoreFiler {

	public static final String LOCALE_SESSION_ATTRIBUTE = WebLocaleUtils.ATTRIBUTE_KEY;//I18nInterceptor.DEFAULT_SESSION_ATTRIBUTE;

	public static final String RELOAD = "reload";
	
//	public static final String REQUEST_ERROR_COUNT = "REQUEST_ERROR_COUNT";
	public static final String LANGUAGE = "cookie.language";
	public static final String REQUEST_URI = "org.onetwo.web.requestUri";


	protected void initApplication(FilterConfig config) {
		super.initApplication(config);
		BaseSiteConfig siteConfig = getBaseSiteConfig().initWeb(config);
		Object webconfig = getWebConfig(siteConfig);
		
		if(WebAppConfigurator.class.isInstance(webconfig)){
			siteConfig.setWebAppConfigurator((WebAppConfigurator)webconfig);
		}
		siteConfig.getFreezer().freezing();
		
		ServletContext context = config.getServletContext();
		context.setAttribute(BaseSiteConfig.CONFIG_NAME, siteConfig);
		context.setAttribute("webConfig", webconfig);
		
		WebApplicationContext app = WebApplicationContextUtils.getRequiredWebApplicationContext(context);
		SpringApplication.initApplication(app);
		
	}
	
	protected BaseSiteConfig getBaseSiteConfig(){
		return BaseSiteConfig.getInstance();
	}
	
	protected Object getWebConfig(BaseSiteConfig siteConfig){
		return siteConfig;
	}

	public String[] getFilterInitializers(FilterConfig config){
		return getBaseSiteConfig().getFilterInitializers();
	}
	
	
	protected void printRequestTime(boolean push, HttpServletRequest request){
		boolean active = "true".equals(request.getParameter("printRequestTime"));
		if(!active)
			return ;
		String url = this.getClass().getSimpleName()+" "+request.getMethod() + ":" + request.getRequestURI();
		UtilTimerStack.setActive(active);
		if(push){
			UtilTimerStack.push(url);
		}else{
			UtilTimerStack.pop(url);
		}
		UtilTimerStack.setActive(!active);
	}
	
	protected void reloadConfigIfNecessary(HttpServletRequest request){
		boolean reloadSiteConfig = RELOAD.equals(request.getParameter(BaseSiteConfig.CONFIG_NAME));
		if(reloadSiteConfig){
			BaseSiteConfig.getInstance().reload();
		}
	}


	public void doFilterInternal(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		HttpSession session = request.getSession();
		
		this.printRequestTime(true, request);
		request.setAttribute(REQUEST_URI, RequestUtils.getServletPath(request));
		try {
			
			this.reloadConfigIfNecessary(request);

//			addP3P(response);
			
			processLocale(request, response);

			filterChain.doFilter(request, response);
			
//			setErrorCount(session, 0);
		}catch (ServletException e) {
			this.logger.error("request["+getRequestURI(request)+"] error: " + e.getMessage(), e);
			throw e;
//			handleException(request, response, e);
		}catch (IOException e) {
			this.logger.error("request["+getRequestURI(request)+"] error: " + e.getMessage(), e);
			throw e;
//			handleException(request, response, e);
		} finally{
			this.printRequestTime(false, request);
		}
		
	}

	/*protected void doProcess(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
		filterChain.doFilter(request, response);
	}*/
	
	protected void addP3P(HttpServletResponse response){
		response.addHeader("P3P", "CP=\"NON DSP COR CURa ADMa DEVa TAIa PSAa PSDa IVAa IVDa CONa HISa TELa OTPa OUR UNRa IND UNI COM NAV INT DEM CNT PRE LOC\"");
	}

	public static String getRequestURI(HttpServletRequest request) {
		return request.getRequestURI();
	}

	/***
	 * 处理request的locale，用于国际化
	 * @param request
	 * @param response
	 */
	protected void processLocale(HttpServletRequest request, HttpServletResponse response) {
//		StrutsUtils.setCurrentSessioniLocale(request.getSession(), BusinessLocale.getDefault());
		/*
		 * Locale locale = StrutsUtils.getCurrentSessionLocale(request, null);
		 * if(locale==null){
		 * StrutsUtils.setCurrentSessioniLocale(request.getSession(),BusinessLocale.getDefault()); }
		 * processLocaleByPath(request, response);
		 */
		// processDataLocale(request);
	}

	protected void processDataLocale(HttpServletRequest request) {
		String localeStr = request.getParameter(WebLocaleUtils.DATA_LOCALE_ATTRIBUTE_KEY);
		Locale locale = WebLocaleUtils.getClosestLocale(localeStr);
		request.getSession().setAttribute(WebLocaleUtils.DATA_LOCALE_ATTRIBUTE_KEY, locale);
	}

	/***
	 * 根据访问路径中的语言路径，设置请求的locale，用于国际化
	 * @param request
	 * @param response
	 */
	protected void processLocaleByPath(HttpServletRequest request, HttpServletResponse response) {
		String uri = RequestUtils.getServletPath(request);

		String localeStr = "";
		int index = uri.indexOf('/', 1);
		if (index != -1)
			localeStr = uri.substring(1, index);

		Locale currentLocale = (Locale) request.getSession().getAttribute(WebLocaleUtils.ATTRIBUTE_KEY);
		Locale closestLocale = null;
		if (WebLocaleUtils.isSupport(localeStr)) {
			closestLocale = WebLocaleUtils.getClosestLocale(localeStr);
		} else
			closestLocale = WebLocaleUtils.getClosestLocale(currentLocale.toString());

		if (closestLocale != null && !WebLocaleUtils.isSameLocale(currentLocale, closestLocale)) {
			request.getSession().setAttribute(WebLocaleUtils.ATTRIBUTE_KEY, closestLocale);
		}

		/*String cookie = request.getParameter("remember_language");
		if ("true".equals(cookie)) {
			CookieUti.setCookieLanguage(response, closestLocale.toString());
		}*/
	}

	/****
	 * 重定向
	 * @param response
	 * @param path
	 */
	public static void redirect(HttpServletResponse response, String path) {
		try {
			response.sendRedirect(path);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	/***
	 * 转发
	 * @param request
	 * @param response
	 * @param path
	 */
	public static void forward(HttpServletRequest request, HttpServletResponse response, String path) {
		RequestDispatcher rd = request.getRequestDispatcher(path);
		try {
			rd.forward(request, response);
		} catch (Exception e) {
			throw new ServiceException(e);
		}
	}

	public void destroy() {
		super.destroy();
	}
}
