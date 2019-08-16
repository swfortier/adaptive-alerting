package com.expedia.adaptivealerting.modelservice.web;

import com.expedia.adaptivealerting.modelservice.dto.metricProfiling.CreateMetricProfilingRequest;
import com.expedia.adaptivealerting.modelservice.entity.Detector;
import com.expedia.adaptivealerting.modelservice.service.MetricProfilingService;
import com.expedia.adaptivealerting.modelservice.util.RequestValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/metricProfiling")
public class MetricProfilingController {

    @Autowired
    private MetricProfilingService metricProfilingService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public String createMetricProfile(@RequestBody CreateMetricProfilingRequest request) {
        return metricProfilingService.createMetricProfile(request);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public void updateDetector(@RequestParam String id, @RequestParam Boolean isStationary) {
        metricProfilingService.updateMetricProfile(id, isStationary);
    }

    @PostMapping
    @RequestMapping(value = "/findMatchingByTags")
    public Boolean searchMetricProfiles(@RequestBody List<Map<String, String>> tagsList) {
        return metricProfilingService.findMatchingMetricProfiles(tagsList);
    }

}
