package com.ubirouting.bytelib;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target (ElementType.FIELD)
@Retention (RetentionPolicy.RUNTIME)
public @interface ToByte {
    int order() default -1;

    String description() default "null";
}
