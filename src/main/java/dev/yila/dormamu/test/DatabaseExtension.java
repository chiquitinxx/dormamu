package dev.yila.dormamu.test;

import dev.yila.dormamu.Db;
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
            throw new RuntimeException("Missing Tables implementation definition using @DatabaseTables(ClassExtendingTables.class)");
        }
        try {
            Tables tables = createNewInstance(clazz);
            return new Db(tables, validationChangesStore);
        } catch (Exception ex) {
            throw new RuntimeException("Error creating implementation class of Tables: " + clazz.getCanonicalName());
        }
    }

    @Override
    public void postProcessTestInstance(Object test, ExtensionContext extensionContext) throws Exception {
        Annotation[] annotations = test.getClass().getAnnotations();
        if (Arrays.stream(annotations).anyMatch(annotation -> annotation.annotationType().equals(DatabaseTables.class))) {
            DatabaseTables tablesAnnotation = (DatabaseTables) Arrays.stream(annotations)
                    .filter(annotation -> annotation.annotationType().equals(DatabaseTables.class))
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

    private ValidationChange addMethodName(ValidationChange change, ExtensionContext extensionContext) {
        if (change.getTestName() == null) {
            return new ValidationChange(
                    null,
                    extensionContext.getTestMethod().map(Method::getName).orElse("undefined"),
                    change.getDescription(),
                    change.getChanges()
            );
        }
        return change;
    }

    private ValidationChange addTestClassName(ValidationChange change, ExtensionContext extensionContext) {
        if (change.getTestClassName() == null) {
            return new ValidationChange(
                    extensionContext.getTestClass().map(Class::getCanonicalName).orElse("unknown"),
                    change.getTestName(),
                    change.getDescription(),
                    change.getChanges()
            );
        }
        return change;
    }

    private void generateReport(List<ValidationChange> list) throws IOException {
        File tmpFile = File.createTempFile("report_", ".txt");
        FileWriter writer = new FileWriter(tmpFile);
        writer.write(list.stream().map(ValidationChange::toString).collect(Collectors.joining("\r\n")));
        writer.close();
        System.out.println("Database validations report generated in: " + tmpFile.getAbsolutePath());
    }
}
