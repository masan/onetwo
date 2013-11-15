package org.onetwo.common.spring.ftl;

import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.onetwo.common.exception.BaseException;
import org.onetwo.common.log.MyLoggerFactory;
import org.onetwo.common.utils.LangUtils;
import org.slf4j.Logger;

import freemarker.core.Environment;
import freemarker.ext.beans.ArrayModel;
import freemarker.ext.beans.BeanModel;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BooleanModel;
import freemarker.ext.beans.CollectionModel;
import freemarker.ext.beans.SimpleMapModel;
import freemarker.ext.beans.StringModel;
import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;

final public class FtlUtils {

	private static final Logger logger = MyLoggerFactory.getLogger(FtlUtils.class);
	public static final BeansWrapper BEAN_WRAPPER = new BeansWrapper();

	static {
		BEAN_WRAPPER.setSimpleMapWrapper(true);
	}
	

	public static void render(String tag, Environment env, TemplateDirectiveBody body){
		if(body==null || env==null)
			return ;
		try {
			body.render(env.getOut());
		} catch (Exception e) {
			LangUtils.throwBaseException("render tempalte["+tag+"] error : "+e.getMessage(), e);
		} 
	}
	
	public static TemplateModel getRequiredVariable(Environment env, String name, String msg){
		TemplateModel model = getVariable(env, name, true);
		if(model==null){
			if(StringUtils.isBlank(msg)){
				throw new BaseException("the variable["+name+"] can not be null!");
			}
		}
		return model;
	}
	public static TemplateModel getVariable(Environment env, String name, boolean throwIfError){
		TemplateModel model = null;
		try {
			model = env.getVariable(name);
		} catch (Exception e) {
			if(throwIfError)
				throw new BaseException("get variable["+name+"] error: " + e.getMessage(), e);
		}
		return model;
	}


	public static void setVariable(Environment env, String name, Object val){
		if(val==null)
			return ;
		TemplateModel model = wrapAsModel(val);
		env.setVariable(name, model);
	}

	public static TemplateModel wrapAsModel(Object val){
		if(val==null)
			return null;
		TemplateModel model = null;
		if(TemplateModel.class.isInstance(val)){
			model = (TemplateModel) val;
		}else if(Map.class.isInstance(val)){
			model = new SimpleMapModel((Map)val, BEAN_WRAPPER);
		}else if(Collection.class.isInstance(val)){
			model = new CollectionModel((Collection)val, BEAN_WRAPPER);
		}else if(LangUtils.isArray(val)){
			model = new ArrayModel(val, BEAN_WRAPPER);
		}else if(String.class.isInstance(val)){
			model = new StringModel(val, BEAN_WRAPPER);
		}else if(Number.class.isInstance(val)){
			model = new SimpleNumber((Number)val);
		}else{
			model = wrapAsBeanModel(val);
		}
		return model;
	}
	
	public static BeanModel wrapAsBeanModel(Object obj){
		if(obj==null)
			return null;
		BeanModel m = null;
		try {
//			BeansWrapper bw = (BeansWrapper)ObjectWrapper.BEANS_WRAPPER;
			m = new BeanModel(obj, BEAN_WRAPPER);
		} catch (Exception e) {
			LangUtils.throwBaseException("wrap bean error : " + obj.getClass(), e);
		}
		
		return m;
	}


	public static void setVariable(Environment env, String name, TemplateModel val){
		env.setVariable(name, val);
	}
	
	public static void setObjectVariable(Environment env, String name, Object val){
		if(TemplateModel.class.isInstance(val)){
			env.setVariable(name, (TemplateModel)val);
		}else if(String.class.isInstance(val)){
			env.setVariable(name, new SimpleScalar(val.toString()));
		}else{
			env.setVariable(name, wrapAsBeanModel(val));
		}
	}
	
	public static Object getObjectVariable(Environment env, String name){
		Object val = null;
		try {
			BeanModel model = (BeanModel)env.getVariable(name);
			val = model.getWrappedObject();
		} catch (TemplateModelException e) {
			return null;
		} catch(Exception e){
			logger.error("getObjectVariable error : " + e.getMessage(), e);
		}
		return val;
	}

	public static String getRequiredParameterByString(Map params, String name){
		TemplateModel val = getParameter(params, name, true);
		return val.toString();
	}
	public static String getParameterByString(Map params, String name, String def){
		TemplateModel attr = getParameter(params, name, false);
		if(attr!=null)
			return attr.toString();
		return def;
	}
	
	public static boolean getParameterByBoolean(Map params, String name, boolean def){
		TemplateModel attr = getParameter(params, name, false);
		if(attr!=null){
			try {
				if(BooleanModel.class.isInstance(attr)){
					return ((BooleanModel)attr).getAsBoolean();
				}else{
					return Boolean.valueOf(attr.toString());
				}
			} catch (Exception e) {
				return def;
			}
		}
		return def;
	}
	public static int getParameterByInt(Map params, String name, int def){
		TemplateModel attr = getParameter(params, name, false);
		if(attr!=null){
			try {
				if(TemplateNumberModel.class.isInstance(attr)){
					return ((TemplateNumberModel)attr).getAsNumber().intValue();
				}else{
					return Integer.parseInt(attr.toString());
				}
			} catch (Exception e) {
				return def;
			}
		}
		return def;
	}

	public static TemplateModel getRequiredParameter(Map params, String name){
		return getParameter(params, name, true);
	}
	
	public static String getParameter(Map params, String name, String defVal){
		TemplateModel val = getParameter(params, name, false);
		if(val==null)
			return defVal;
		return val.toString();
	}
	
	public static <T> T getParameter(Map params, String name, boolean throwIfNotExist){
		if(!params.containsKey(name)){
			if(throwIfNotExist)
				throw LangUtils.asBaseException("freemarker template error : the param["+name+"] has not be given.");
			else
				return null;
		}
		
		T val = (T)params.get(name);
		return val;
		/*if(val!=null)
			return val;
		else
			throw LangUtils.asBaseException("the param["+name+"] can not be null.");*/
	}
	
	private FtlUtils(){
	}

}