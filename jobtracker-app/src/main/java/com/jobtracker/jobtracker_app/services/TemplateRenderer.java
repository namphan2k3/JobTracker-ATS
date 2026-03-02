package com.jobtracker.jobtracker_app.services;

import java.util.Map;

public interface TemplateRenderer {
    String render(String template, Map<String, Object> variables);
}
