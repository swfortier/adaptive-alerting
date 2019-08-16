package com.expedia.adaptivealerting.modelservice.service;

import com.expedia.adaptivealerting.modelservice.dto.metricProfiling.CreateMetricProfilingRequest;
import com.expedia.adaptivealerting.modelservice.repo.MetricProfilingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class MetricProfilingServiceImpl implements MetricProfilingService {

    @Autowired
    private MetricProfilingRepository metricProfilingRepository;

    @Override
    public String createMetricProfile(CreateMetricProfilingRequest request) {
        return metricProfilingRepository.createMetricProfile(request);
    }

    @Override
    public void updateMetricProfile(String id, Boolean isStationary) {
        metricProfilingRepository.updateMetricProfile(id, isStationary);
    }

    @Override
    public Boolean findMatchingMetricProfiles(List<Map<String, String>> tagsList) {
        return metricProfilingRepository.findMatchingMetricProfiles(tagsList);
    }

}
