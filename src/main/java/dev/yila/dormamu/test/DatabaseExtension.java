package dev.yila.dormamu.test;

import dev.yila.dormamu.Db;
import dev.yila.dormamu.Tables;
import dev.yila.dormamu.report.DefaultReportGenerator;
import org.junit.jupiter.api.extension.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DatabaseExtension implements ParameterResolver, TestInstancePostProcessor, AfterEachCallback, AfterAllCallback {

    private static final String TABLES_CLASS = "Tables";
    private ValidationChangesStore validationChangesStore = new ValidationChangesStore();

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(Db.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Class<? extends Tables> clazz = getStore(extensionContext).get(TABLES_CLASS, Class.class);
        if (clazz == null) {
            return new Db(validationChangesStore);
        }
        Tables tables;
        try {
            tables = createNewInstance(clazz);
            return new Db(validationChangesStore)
                    .withTables(tables)
                    .withReportGenerator(new DefaultReportGenerator());
        } catch (Exception ex) {
            showMessageInConsole("Error creating implementation class of Tables: " + clazz.getCanonicalName());
        }
        return new Db(validationChangesStore).withReportGenerator(new DefaultReportGenerator());
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
                .map(change -> addMethodName(change, extensionContext))
                .collect(Collectors.toList()));
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) throws Exception {
        generateReport(validationChangesStore.getChanges().stream()
                .map(change -> addTestClassName(change, extensionContext))
                .collect(Collectors.toList()));
    }

    public static void showMessageInConsole(String message) {
        System.out.println(message);
    }

    private ValidationChange addMethodName(ValidationChange change, ExtensionContext extensionContext) {
        if (change.getTestName() == null) {
            return new ValidationChange(
                    null,
                    extensionContext.getTestMethod().map(Method::getName).orElse("undefined"),
                    change.getDescription(),
                    change.getChanges(),
                    change.getBefore(),
                    change.getAfter()
            );
        }
        return change;
    }

    private ValidationChange addTestClassName(ValidationChange change, ExtensionContext extensionContext) {
        return new ValidationChange(
                extensionContext.getTestClass().map(Class::getCanonicalName).orElse("unknown"),
                change.getTestName(),
                change.getDescription(),
                change.getChanges(),
                change.getBefore(),
                change.getAfter()
        );
    }

    private void generateReport(List<ValidationChange> list) throws IOException {
        File tmpFile = File.createTempFile("report_", ".txt");
        FileWriter writer = new FileWriter(tmpFile);
        writer.write(list.stream().map(ValidationChange::toString).collect(Collectors.joining("\r\n")));
        writer.close();
        showMessageInConsole("Database validations report generated in: " + tmpFile.getAbsolutePath());
    }
}
