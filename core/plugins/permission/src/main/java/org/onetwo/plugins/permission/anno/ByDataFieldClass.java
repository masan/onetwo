package org.onetwo.plugins.permission.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.onetwo.common.web.s2.security.config.annotation.Authentic;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Authentic
public @interface ByDataFieldClass {

	Class<?>[] codeClass();
}