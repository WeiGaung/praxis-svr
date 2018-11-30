package com.noriental.praxissvr.common.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author chenlihua
 * @date 2016/7/8
 * @time 17:35
 */
@Target({TYPE})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {PageableValidator.class})
public @interface Pageable {
    String message() default " If pageable, pageNo and pageSize must be great than or equal to 1. ";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
