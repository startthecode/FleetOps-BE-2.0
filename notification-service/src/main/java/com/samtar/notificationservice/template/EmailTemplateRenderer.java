package com.samtar.notificationservice.template;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Renders the saved HTML/text templates by substituting {{token}} placeholders.
 * No template engine is on the classpath, so this is a deliberate plain-text replace.
 */
public final class EmailTemplateRenderer {

    private EmailTemplateRenderer() {
    }

    public static String render(String classpathLocation, Map<String, String> variables) {
        String template = read(classpathLocation);
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String value = entry.getValue() == null ? "" : entry.getValue();
            template = template.replace("{{" + entry.getKey() + "}}", value);
        }
        return template;
    }

    private static String read(String classpathLocation) {
        try (InputStream in = new ClassPathResource(classpathLocation).getInputStream()) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load email template: " + classpathLocation, e);
        }
    }
}
