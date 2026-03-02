package com.jobtracker.jobtracker_app.services.impl;

import com.jobtracker.jobtracker_app.services.TemplateRenderer;
import com.samskivert.mustache.Mustache;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class MustacheTemplateRenderer implements TemplateRenderer {

    @Override
    public String render(String template, Map<String, Object> variables) {
        Mustache.Compiler compiler = Mustache.compiler();
        return compiler.compile(template).execute(variables);
    }
}
