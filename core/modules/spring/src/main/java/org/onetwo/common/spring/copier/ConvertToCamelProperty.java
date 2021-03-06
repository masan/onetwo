package org.onetwo.common.spring.copier;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConvertToCamelProperty {
	
	boolean value() default true;
	char spliter() default '_';
	
}
