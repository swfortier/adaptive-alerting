package com.expedia.adaptivealerting.modelservice.repo.impl;

import com.expedia.adaptivealerting.modelservice.dto.metricProfiling.CreateMetricProfilingRequest;
import com.expedia.adaptivealerting.modelservice.dto.percolator.PercolatorMetricProfiling;
import com.expedia.adaptivealerting.modelservice.elasticsearch.ElasticSearchClient;
import com.expedia.adaptivealerting.modelservice.elasticsearch.ElasticSearchProperties;
import com.expedia.adaptivealerting.modelservice.repo.MetricProfilingRepository;
import com.expedia.adaptivealerting.modelservice.util.ElasticsearchUtil;
import com.expedia.adaptivealerting.modelservice.util.ObjectMapperUtil;
import com.expedia.adaptivealerting.modelservice.util.QueryUtil;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.percolator.PercolateQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class MetricProfilingRepositoryImpl implements MetricProfilingRepository {

    private static final String METRIC_PROFILING_INDEX = "metric_profiling";
    private static final String METRIC_PROFILING_DOC_TYPE = "details";

    @Autowired
    private ElasticSearchProperties elasticSearchProperties;

    @Autowired
    private ElasticSearchClient elasticSearchClient;

    @Autowired
    private ElasticsearchUtil elasticsearchUtil;

    @Autowired
    private ObjectMapperUtil objectMapperUtil;

    @Override
    public String createMetricProfile(CreateMetricProfilingRequest createRequest) {
        val fields = createRequest.getFields();
        val newFieldMappings = elasticsearchUtil.removeFieldsHavingExistingMapping(fields, METRIC_PROFILING_INDEX);
        elasticsearchUtil.updateIndexMappings(newFieldMappings, METRIC_PROFILING_INDEX);

        val percolatorMetricProfiling = new PercolatorMetricProfiling()
                .setProfilingTime(System.currentTimeMillis())
                .setIsStationary(createRequest.getIsStationary())
                .setQuery(QueryUtil.buildQuery(createRequest.getExpression()));

        val indexRequest = new IndexRequest(METRIC_PROFILING_INDEX, elasticSearchProperties.getDocType());
        val json = objectMapperUtil.convertToString(percolatorMetricProfiling);
        return elasticsearchUtil.getIndexResponse(indexRequest, json).getId();
    }

    @Override
    public void updateMetricProfile(String id, Boolean isStationary) {
        val updateRequest = new UpdateRequest(METRIC_PROFILING_INDEX, METRIC_PROFILING_DOC_TYPE, id);
        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("isStationary", isStationary);
        updateRequest.doc(jsonMap);
        try {
            elasticSearchClient.update(updateRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error(String.format("Updating elastic search failed", e));
            throw new RuntimeException(e);
        }
    }

    @Override
    public Boolean findMatchingMetricProfiles(List<Map<String, String>> tagsList) {
        try {
            List<BytesReference> refList = new ArrayList<>();
            for (Map<String, String> tags : tagsList) {
                XContentBuilder xContent = XContentFactory.jsonBuilder();
                xContent.map(tags);
                refList.add(BytesReference.bytes(xContent));
            }
            PercolateQueryBuilder percolateQuery = new PercolateQueryBuilder(PercolatorMetricProfiling.QUERY_KEYWORD,
                    refList, XContentType.JSON);

            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
            boolQueryBuilder.filter(percolateQuery);

            SearchSourceBuilder searchSourceBuilder = elasticsearchUtil.getSourceBuilder(boolQueryBuilder);
            searchSourceBuilder.timeout(new TimeValue(elasticSearchProperties.getConfig().getConnectionTimeout()));
            searchSourceBuilder.size(500);

            final SearchRequest searchRequest =
                    new SearchRequest()
                            .source(searchSourceBuilder)
                            .indices(METRIC_PROFILING_INDEX);

            log.info("searchRequest:{}", searchRequest);
            SearchResponse searchResponse = elasticSearchClient.search(searchRequest, RequestOptions.DEFAULT);
            log.info("searchResponse:{}", searchResponse);
            return searchResponse.getHits().getHits().length > 0;
        } catch (IOException e) {
            log.error("Error ES lookup", e);
            throw new RuntimeException(e);
        }
    }

}
