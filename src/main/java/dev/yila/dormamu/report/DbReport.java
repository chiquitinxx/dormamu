package dev.yila.dormamu.report;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DbReport {
    Class<? extends ReportGenerator> generator();
    Class<? extends ReportDataProvider> dataProvider();
}
