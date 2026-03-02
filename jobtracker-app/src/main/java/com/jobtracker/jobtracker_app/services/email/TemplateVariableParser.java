package com.jobtracker.jobtracker_app.services.email;

import com.jobtracker.jobtracker_app.enums.ManualVariable;
import com.jobtracker.jobtracker_app.enums.SystemVariable;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class TemplateVariableParser {

    private static final Pattern VARIABLE_PATTERN = Pattern.compile("\\{\\{\\s*([a-zA-Z0-9_]+)\\s*\\}\\}");

    private TemplateVariableParser() {}

    public static Set<String> extractVariables(String content) {
        Set<String> variables = new HashSet<>();
        if (content == null || content.isBlank()) {
            return variables;
        }
        Matcher matcher = VARIABLE_PATTERN.matcher(content);
        while (matcher.find()) {
            variables.add(matcher.group(1).trim());
        }
        return variables;
    }

    public static boolean isSystemVariable(String key) {
        return SystemVariable.isSystem(key);
    }

    public static boolean isManualVariable(String key) {
        return ManualVariable.isManual(key);
    }

    public static boolean isValidVariable(String key) {
        return isSystemVariable(key) || isManualVariable(key);
    }
}
