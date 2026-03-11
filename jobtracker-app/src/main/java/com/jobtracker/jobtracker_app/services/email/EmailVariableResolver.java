package com.jobtracker.jobtracker_app.services.email;

import com.jobtracker.jobtracker_app.dto.requests.email.EmailContext;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailVariableResolver {
    List<VariableResolver> resolvers;

    public Map<String, Object> buildAllVariables(EmailContext context) {
        Map<String, Object> map = new HashMap<>();
        for (VariableResolver resolver : resolvers) {
            Object value = resolver.resolve(context);
            map.put(resolver.getKey(), value == null ? "" : value);
        }
        return map;
    }
}
