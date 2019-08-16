package com.expedia.adaptivealerting.modelservice.repo;

import com.expedia.adaptivealerting.modelservice.dto.metricProfiling.CreateMetricProfilingRequest;

import java.util.List;
import java.util.Map;

public interface MetricProfilingRepository {

    String createMetricProfile(CreateMetricProfilingRequest metricProfilingRequest);

    void updateMetricProfile(String id, Boolean isStationary);

    Boolean findMatchingMetricProfiles(List<Map<String, String>> tagsList);
}
