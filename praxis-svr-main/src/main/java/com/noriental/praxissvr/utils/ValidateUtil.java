package com.noriental.praxissvr.utils;

import com.noriental.praxissvr.exception.PraxisErrorCode;
import com.noriental.validate.exception.BizLayerException;
import com.noriental.validate.util.LazyInitValidatorConfig;
import com.noriental.validate.util.MsgTemplateResolverUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

/**
 * Created by shengxian on 2016/12/22.
 */
public class ValidateUtil {
    static Validator validator = LazyInitValidatorConfig.getValidator();
    public static void validateThrow(Object t){
        Set<ConstraintViolation<Object>> set = validator.validate(t);
        ConstraintViolation constraintViolation = null;
        if (set != null && !set.isEmpty()) {
            constraintViolation = set.iterator().next();
        }
        if (constraintViolation != null) {
            throw new BizLayerException(MsgTemplateResolverUtils.getMsgFromViolation(constraintViolation), PraxisErrorCode.ANSWER_PARAMETER_ILL);
        }
    }
}
