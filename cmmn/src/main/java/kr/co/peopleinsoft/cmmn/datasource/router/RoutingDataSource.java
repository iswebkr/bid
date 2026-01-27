package kr.co.peopleinsoft.cmmn.datasource.router;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target({METHOD, TYPE})
@Documented
public @interface RoutingDataSource {
	DataSourceType value() default DataSourceType.DEFAULT;
}