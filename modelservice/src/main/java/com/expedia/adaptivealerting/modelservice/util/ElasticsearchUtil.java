/*
 * Copyright 2018-2019 Expedia Group, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.expedia.adaptivealerting.modelservice.util;

import com.expedia.adaptivealerting.modelservice.elasticsearch.ElasticSearchClient;
import com.expedia.adaptivealerting.modelservice.elasticsearch.ElasticSearchProperties;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsRequest;
import org.elasticsearch.action.admin.indices.mapping.get.GetMappingsResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ElasticsearchUtil {

    @Autowired
    private ElasticSearchClient elasticSearchClient;

    @Autowired
    private ElasticSearchProperties elasticSearchProperties;

    public IndexResponse getIndexResponse(IndexRequest indexRequest, String json) {
        try {
            indexRequest.source(json, XContentType.JSON);
            return elasticSearchClient.index(indexRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error(String.format("Indexing mapping %s failed", json, e));
            throw new RuntimeException(e);
        }
    }

    public SearchSourceBuilder getSourceBuilder(QueryBuilder queryBuilder) {
        val searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(queryBuilder);
        return searchSourceBuilder;
    }

    public SearchRequest getSearchRequest(SearchSourceBuilder searchSourceBuilder, String indexName, String docType) {
        val searchRequest = new SearchRequest();
        return searchRequest.source(searchSourceBuilder).indices(indexName).types(docType);
    }

    public Set<String> removeFieldsHavingExistingMapping(Set<String> fields, String indexName) {
        GetMappingsRequest request = new GetMappingsRequest();
        request.indices(indexName);

        try {
            GetMappingsResponse mappingsResponse = elasticSearchClient.indices().getMapping(request, RequestOptions.DEFAULT);
            Map<String, String> mapProperties = ((Map<String, String>) mappingsResponse.getMappings()
                    .get(indexName)
                    .get(elasticSearchProperties.getDocType())
                    .sourceAsMap().get("properties"));

            Set<String> mappedFields = mapProperties.entrySet().stream()
                    .map(en -> en.getKey()).collect(Collectors.toSet());
            fields.removeAll(mappedFields);
            return fields;
        } catch (IOException e) {
            log.error("Error finding mappings", e);
            throw new RuntimeException(e);
        }
    }

    public void updateIndexMappings(Set<String> newFieldMappings, String indexName) {
        PutMappingRequest request = new PutMappingRequest(indexName);

        Map<String, Object> type = new HashMap<>();
        type.put("type", "keyword");

        Map<String, Object> properties = new HashMap<>();
        newFieldMappings.forEach(field -> {
            properties.put(field, type);
        });

        Map<String, Object> jsonMap = new HashMap<>();
        jsonMap.put("properties", properties);
        request.source(jsonMap);
        request.type(elasticSearchProperties.getDocType());

        try {
            elasticSearchClient.indices().putMapping(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("Error updating mappings", e);
            throw new RuntimeException(e);
        }
    }
}
