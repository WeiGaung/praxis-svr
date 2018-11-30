package com.noriental.praxissvr.common.constraints;

import com.noriental.praxissvr.common.PageBaseRequest;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author chenlihua
 * @date 2016/7/8
 * @time 17:43
 */
public class PageableValidator implements ConstraintValidator<Pageable, PageBaseRequest> {
    @Override
    public void initialize(Pageable constraintAnnotation) {
        
    }

    @Override
    public boolean isValid(PageBaseRequest value, ConstraintValidatorContext context) {
        boolean pageable = value.isPageable();
        if (pageable) {
            if (value.getPageNo() < 1) {
                return false;
            }
            if (value.getPageSize() < 1) {
                return false;
            }
        }
        return true;
    }
}
