package dev.yila.dormamu.test;

import dev.yila.dormamu.Db;
import dev.yila.dormamu.Tables;
import dev.yila.dormamu.report.DefaultReportGenerator;
import dev.yila.dormamu.report.ReportGenerator;
import org.junit.jupiter.api.extension.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

public class DatabaseExtension implements ParameterResolver, TestInstancePostProcessor, AfterEachCallback, AfterAllCallback {

    private static final String TABLES_CLASS = "Tables";
    private static final String REPORT_GENERATOR = "ReportGenerator";
    private ValidationChangesStore validationChangesStore = new ValidationChangesStore();

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(Db.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Db db = new Db(validationChangesStore);
        Class<? extends Tables> tablesClass = getStore(extensionContext).get(TABLES_CLASS, Class.class);
        if (tablesClass != null) {
            Tables tables;
            try {
                tables = createNewInstance(tablesClass);
                db = db.withTables(tables);
            } catch (Exception ex) {
                showMessageInConsole("Error creating implementation class of Tables: " + tablesClass.getCanonicalName());
            }
        }
        return db;
    }

    @Override
    public void postProcessTestInstance(Object test, ExtensionContext extensionContext) throws Exception {
        Annotation[] annotations = test.getClass().getAnnotations();
        if (Arrays.stream(annotations).anyMatch(annotation -> annotation.annotationType().equals(DbTables.class))) {
            DbTables tablesAnnotation = (DbTables) Arrays.stream(annotations)
                    .filter(annotation -> annotation.annotationType().equals(DbTables.class))
                    .findAny().orElseThrow(() -> new RuntimeException("Not found @DatabaseChanges annotation."));
            getStore(extensionContext).put(TABLES_CLASS, tablesAnnotation.value());
        }
        getStore(extensionContext).put(REPORT_GENERATOR, new DefaultReportGenerator());
    }

    private Tables createNewInstance(Class<? extends Tables> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor constructor = clazz.getDeclaredConstructor();
        return (Tables) constructor.newInstance();
    }

    private ExtensionContext.Store getStore(ExtensionContext context) {
        return context.getStore(ExtensionContext.Namespace.create(getClass()));
    }

    @Override
    public void afterEach(ExtensionContext extensionContext) throws Exception {
        validationChangesStore.setChanges(validationChangesStore.getChanges().stream()
                .map(change -> addMethodNameAndTestResult(change, extensionContext))
                .collect(Collectors.toList()));
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        ReportGenerator reportGenerator = getStore(extensionContext).get(REPORT_GENERATOR, ReportGenerator.class);
        reportGenerator.generate(validationChangesStore.getChanges().stream()
            .map(change -> addTestClassName(change, extensionContext))
            .collect(Collectors.toList()));
    }

    public static void showMessageInConsole(String message) {
        System.out.println(message);
    }

    private ValidationChange addMethodNameAndTestResult(ValidationChange change, ExtensionContext extensionContext) {
        if (change.getTestMethodName() == null) {
            return new ValidationChange(
                    null,
                    extensionContext.getTestMethod().map(Method::getName).orElse("undefined"),
                    change.getDescription(),
                    change.getChanges(),
                    change.getBefore(),
                    change.getAfter(),
                    extensionContext.getExecutionException().isEmpty()
            );
        }
        return change;
    }

    private ValidationChange addTestClassName(ValidationChange change, ExtensionContext extensionContext) {
        return new ValidationChange(
                extensionContext.getTestClass().map(Class::getCanonicalName).orElse("unknown"),
                change.getTestMethodName(),
                change.getDescription(),
                change.getChanges(),
                change.getBefore(),
                change.getAfter(),
                change.isTestSuccess()
        );
    }
}
