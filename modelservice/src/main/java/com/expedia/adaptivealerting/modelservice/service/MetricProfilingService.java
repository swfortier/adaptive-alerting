package com.expedia.adaptivealerting.modelservice.service;

import com.expedia.adaptivealerting.modelservice.dto.metricProfiling.CreateMetricProfilingRequest;

import java.util.List;
import java.util.Map;

public interface MetricProfilingService {

    String createMetricProfile(CreateMetricProfilingRequest metricProfilingRequest);

    void updateMetricProfile(String id, Boolean isStationary);

    Boolean findMatchingMetricProfiles(List<Map<String, String>> tagsList);

}
