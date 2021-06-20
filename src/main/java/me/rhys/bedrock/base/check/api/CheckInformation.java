package me.rhys.bedrock.base.check.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CheckInformation {
    String checkName() default "Check";
    String checkType() default "A";
    String description() default "Does something... with... packets?...";
    boolean enabled() default true;
    int punishmentVL() default 20;
    boolean lagBack() default true;
    boolean canPunish() default true;
}
