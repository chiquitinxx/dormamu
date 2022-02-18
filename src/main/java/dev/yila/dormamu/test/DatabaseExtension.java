package dev.yila.dormamu.test;

import dev.yila.dormamu.Db;
import org.junit.jupiter.api.extension.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

public class DatabaseExtension implements ParameterResolver, TestInstancePostProcessor {

    private static final String TABLES = "Tables";

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(Db.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
        Tables tables = getStore(extensionContext).get(TABLES, Tables.class);
        if (tables == null) {
            throw new RuntimeException("Missing Tables implementation definition using @DatabaseTables(ClassExtendingTables.class)");
        }
        return new Db(tables);
    }

    @Override
    public void postProcessTestInstance(Object test, ExtensionContext extensionContext) throws Exception {
        Annotation[] annotations = test.getClass().getAnnotations();
        if (Arrays.stream(annotations).anyMatch(annotation -> annotation.annotationType().equals(DatabaseTables.class))) {
            DatabaseTables tablesAnnotation = (DatabaseTables) Arrays.stream(annotations)
                    .filter(annotation -> annotation.annotationType().equals(DatabaseTables.class))
                    .findAny().orElseThrow(() -> new RuntimeException("Not found @DatabaseChanges annotation."));
            Tables tables = createNewInstance(tablesAnnotation.value());
            getStore(extensionContext).put(TABLES, tables);
        }
    }

    private Tables createNewInstance(Class<? extends Tables> clazz) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Constructor constructor = clazz.getDeclaredConstructor();
        return (Tables) constructor.newInstance();
    }

    private ExtensionContext.Store getStore(ExtensionContext context) {
        return context.getStore(ExtensionContext.Namespace.create(getClass()));
    }
}
