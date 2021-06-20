package me.rhys.anticheat.base.processor.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ProcessorInformation {
    String name() default "Processor";
}
