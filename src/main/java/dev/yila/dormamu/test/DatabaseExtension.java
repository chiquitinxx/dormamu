package dev.yila.dormamu.test;

import dev.yila.dormamu.Db;
import dev.yila.dormamu.Tables;
import dev.yila.dormamu.report.DbReport;
import dev.yila.dormamu.report.DefaultReportGenerator;
import dev.yila.dormamu.report.ReportDataProvider;
import dev.yila.dormamu.report.ReportGenerator;
import org.junit.jupiter.api.extension.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class DatabaseExtension implements ParameterResolver, TestInstancePostProcessor, AfterEachCallback, AfterAllCallback {

    private static final String TABLES_CLASS = "Tables";
    private static final String REPORT_GENERATOR = "ReportGenerator";
    private static final String REPORT_DATA_PROVIDER = "ReportDataProvider";
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
            Db finalDb = db;
            db = createNewInstance(tablesClass, Tables.class)
                    .map(db::withTables)
                    .orElseGet(() -> finalDb);
        }
        Class<? extends ReportDataProvider> reportDataProvider = getStore(extensionContext).get(REPORT_DATA_PROVIDER, Class.class);
        if (reportDataProvider != null) {
            Db finalDb = db;
            db = createNewInstance(reportDataProvider, ReportDataProvider.class)
                    .map(db::withReportDataProvider)
                    .orElseGet(() -> finalDb);
        }
        return db;
    }

    @Override
    public void postProcessTestInstance(Object test, ExtensionContext extensionContext) throws Exception {
        Annotation[] annotations = test.getClass().getAnnotations();
        Arrays.stream(annotations)
                .filter(annotation -> annotation.annotationType().equals(DbTables.class))
                .map(DbTables.class::cast)
                .findAny()
                .ifPresent(dbTables -> getStore(extensionContext).put(TABLES_CLASS, dbTables.value()));
        readDbReportAnnotation(extensionContext, annotations);
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
        if (reportGenerator == null) {
            reportGenerator = new DefaultReportGenerator();
        }
        reportGenerator.generate(validationChangesStore.getChanges().stream()
            .map(change -> addTestClassName(change, extensionContext))
            .collect(Collectors.toList()));
    }

    public static void showMessageInConsole(String message) {
        System.out.println(message);
    }

    private void readDbReportAnnotation(ExtensionContext extensionContext, Annotation[] annotations) {
        Arrays.stream(annotations)
                .filter(annotation -> annotation.annotationType().equals(DbReport.class))
                .map(DbReport.class::cast)
                .findAny()
                .ifPresent((DbReport annotation) -> {
                    if (annotation.generator() != null) {
                        createNewInstance(annotation.generator(), ReportGenerator.class)
                                .ifPresent(reportGenerator -> getStore(extensionContext).put(REPORT_GENERATOR, reportGenerator));
                    }
                    if (annotation.dataProvider() != null) {
                        getStore(extensionContext).put(REPORT_DATA_PROVIDER, annotation.dataProvider());
                    }
                });
    }

    private <T> Optional<T> createNewInstance(Class clazz, Class<T> type) {
        try {
            Constructor constructor = clazz.getDeclaredConstructor();
            return Optional.of(type.cast(constructor.newInstance()));
        } catch (Exception e) {
            showMessageInConsole("Error creating instance of type: " + type.getCanonicalName());
            return Optional.empty();
        }
    }

    private ExtensionContext.Store getStore(ExtensionContext context) {
        return context.getStore(ExtensionContext.Namespace.create(getClass()));
    }

    private DatabaseTestChange addMethodNameAndTestResult(DatabaseTestChange change, ExtensionContext extensionContext) {
        if (change.getTestMethodName() == null) {
            return new DatabaseTestChange(
                    null,
                    extensionContext.getTestMethod().map(Method::getName).orElse("undefined"),
                    change.getChanges(),
                    change.getBefore(),
                    change.getAfter(),
                    extensionContext.getExecutionException().isEmpty()
            );
        }
        return change;
    }

    private DatabaseTestChange addTestClassName(DatabaseTestChange change, ExtensionContext extensionContext) {
        return new DatabaseTestChange(
                extensionContext.getTestClass().map(Class::getCanonicalName).orElse("unknown"),
                change.getTestMethodName(),
                change.getChanges(),
                change.getBefore(),
                change.getAfter(),
                change.isTestSuccess()
        );
    }
}
