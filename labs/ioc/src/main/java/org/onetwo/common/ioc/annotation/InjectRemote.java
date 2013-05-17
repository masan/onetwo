package org.onetwo.common.ioc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@SuppressWarnings("rawtypes")
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectRemote {

	String name() default "";

	String mappedName() default "";
	
	Class businessInterface() default Object.class;
	
	boolean ignoreIfLookupError() default false;
}