package com.expedia.adaptivealerting.modelservice.dto.metricProfiling;

import com.expedia.adaptivealerting.modelservice.dto.detectormapping.Detector;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class MatchedMetricResponse {
    private Map<Integer, List<Detector>> groupedDetectorsBySearchIndex;
    private long lookupTimeInMillis;
}
