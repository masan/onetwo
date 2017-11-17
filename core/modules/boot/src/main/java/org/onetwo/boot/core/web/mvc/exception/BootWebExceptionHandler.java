package org.onetwo.boot.core.web.mvc.exception;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.onetwo.boot.core.config.BootSiteConfig;
import org.onetwo.boot.core.web.service.impl.ExceptionMessageAccessor;
import org.onetwo.boot.core.web.utils.BootWebHelper;
import org.onetwo.boot.core.web.utils.BootWebUtils;
import org.onetwo.common.data.DataResult;
import org.onetwo.common.log.JFishLoggerFactory;
import org.onetwo.common.spring.mvc.utils.DataResults;
import org.onetwo.common.utils.LangUtils;
import org.onetwo.common.web.utils.RequestUtils;
import org.onetwo.common.web.utils.WebHolder;
import org.slf4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/***
 * TODO 
 * 
 * @ExceptionHandler 声明的异常， 深度越接近实际异常的方法优先匹配
 * @author way
 *
 */
@ControllerAdvice
public class BootWebExceptionHandler extends ResponseEntityExceptionHandler implements ExceptionMessageFinder, InitializingBean {
	protected final Logger logger = JFishLoggerFactory.getLogger(this.getClass());

	@Autowired
	private BootSiteConfig bootSiteConfig;
	@Autowired(required=false)
	private ExceptionMessageAccessor exceptionMessageAccessor;
	private List<String> notifyThrowables;
	
	
	@Override
	public void afterPropertiesSet() throws Exception {
		this.notifyThrowables = this.bootSiteConfig.getNotifyThrowables();
	}

	@ExceptionHandler({
		Throwable.class
	})
	@ResponseBody
	public ResponseEntity<Object> handleUnhandledException(Exception ex, WebRequest request) {
//		return super.handleException(ex, request);
		ErrorMessage errorMessage = handleException(ex);
		
		DataResult<?> result = DataResults.error(errorMessage.getMesage())
													.code(errorMessage.getCode())
													.build();

//		this.doLog(WebHolder.getRequest().orElse(null), null, ex, errorMessage.isDetail());
		HttpHeaders headers = new HttpHeaders();
		HttpStatus status = errorMessage.getHttpStatus();
		return super.handleExceptionInternal(ex, result, headers, status, request);
	}
	
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatus status, WebRequest request) {
		ErrorMessage errorMessage = handleException(ex);
		DataResult<?> result = DataResults.error(errorMessage.getMesage())
				.code(errorMessage.getCode())
				.build();
		return super.handleExceptionInternal(ex, result, headers, status, request);
	}
	
	protected ErrorMessage handleException(Exception ex){
		ErrorMessage errorMessage = (ErrorMessage)RequestContextHolder.getRequestAttributes().getAttribute(BootWebExceptionResolver.ERROR_MESSAGE_OBJECT_KEY, RequestAttributes.SCOPE_REQUEST);;
		if(errorMessage==null){
			errorMessage = this.getErrorMessage(ex, bootSiteConfig.isProduct());
		}
		Optional<HttpServletRequest> reqOpt = WebHolder.getRequest();
		doLog(reqOpt.orElse(null), errorMessage);
		if(errorMessage.getHttpStatus()==null){
			errorMessage.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		return errorMessage;
	}

	protected void doLog(HttpServletRequest request, ErrorMessage errorMessage){
		String msg = "";
		Object handlerMethod = null;
		if(request!=null){
			BootWebHelper helper = BootWebUtils.webHelper(request);
			msg = RequestUtils.getServletPath(request);
			handlerMethod = helper.getControllerHandler();
		}
		Exception ex = errorMessage.getException();
		errorMessage.logErrorContext(logger);
		if(errorMessage.isDetail()){
			msg += " ["+handlerMethod+"] error: " + ex.getMessage();
			logger.error(msg, ex);
			JFishLoggerFactory.mailLog(notifyThrowables, ex, msg);
		}else{
			logger.error(msg + " code[{}], message[{}]", LangUtils.getBaseExceptonCode(ex), ex.getMessage());
		}
	}

	@Override
	public ExceptionMessageAccessor getExceptionMessageAccessor() {
		return exceptionMessageAccessor;
	}
	
	
}
