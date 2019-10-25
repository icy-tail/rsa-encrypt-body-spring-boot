package cn.icytail.springboot.rsa.annotation;

import cn.icytail.springboot.rsa.advice.EncryptRequestBodyAdvice;
import cn.icytail.springboot.rsa.advice.EncryptResponseBodyAdvice;
import cn.icytail.springboot.rsa.config.SecretKeyConfig;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Author:Bobby
 * DateTime:2019/4/9 16:44
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import({SecretKeyConfig.class,
        EncryptResponseBodyAdvice.class,
        EncryptRequestBodyAdvice.class})
public @interface EnableSecurity{

}
