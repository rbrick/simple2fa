package io.dreamz.simple2fa.utils;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public final class Config {
    private static Object parseSection(Class<?> clazz, ConfigurationSection section) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, InstantiationException {

        Constructor<?> constructor = clazz.getDeclaredConstructor();
        constructor.setAccessible(true);

        Object instance = constructor.newInstance();

        for (Field f : clazz.getDeclaredFields()) {
            if (!f.isAccessible()) f.setAccessible(true);
            if (f.getType().isMemberClass() && f.getType().getDeclaringClass().equals(clazz)) {
                final String sectionName = findSectionName(f.getType().getSimpleName(), section);
                if (section.isConfigurationSection(sectionName)) {
                    f.set(instance, parseSection(f.getType(), section.getConfigurationSection(sectionName)));
                }
            } else {
                f.set(instance, section.get(findSectionName(f.getName(), section)));
            }
        }

        return instance;
    }

    public static <T> T newConfig(Class<? extends T> clazz, Configuration configuration) throws IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        return (T) parseSection(clazz, configuration);
    }

    private static String findSectionName(String name, ConfigurationSection section) {
        // try numerous variants.
        if (section.isConfigurationSection(name))
            return name;
        // camel case
        final String camelCase = name.substring(0, 1).toLowerCase() + name.substring(1);
        if (section.isConfigurationSection(camelCase)) {
            return camelCase;
        }
        final String snakeCase = testCases(name, '_');
        if (section.isConfigurationSection(snakeCase)) {
            return snakeCase;
        }

        final String skeweredCase = testCases(name, '-');
        if (section.isConfigurationSection(skeweredCase)) {
            return skeweredCase;
        }
        return name.toLowerCase();
    }


    private static String testCases(String name, char separator) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            if (i != 0 && Character.isUpperCase(name.charAt(i))) {
                builder.append(separator);
            }
            builder.append(Character.toLowerCase(name.charAt(i)));
        }
        return builder.toString();
    }


}
