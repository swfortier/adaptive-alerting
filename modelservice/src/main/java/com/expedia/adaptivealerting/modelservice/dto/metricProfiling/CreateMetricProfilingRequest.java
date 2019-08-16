package com.expedia.adaptivealerting.modelservice.dto.metricProfiling;

import com.expedia.adaptivealerting.modelservice.dto.common.Expression;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class CreateMetricProfilingRequest {
    private Expression expression;
    private Boolean isStationary;

    public Set<String> getFields() {
        return this.expression.getOperands().stream()
                .map(operand -> operand.getField().getKey()).collect(Collectors.toSet());
    }
}

