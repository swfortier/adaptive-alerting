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
package com.expedia.adaptivealerting.modelservice.entity;

import com.expedia.adaptivealerting.modelservice.dto.detectormapping.Detector;
import com.expedia.adaptivealerting.modelservice.dto.common.Expression;
import com.expedia.adaptivealerting.modelservice.dto.detectormapping.User;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * The type Detector mapping. Based on elastic search.
 * <p>
 * searchIndexes: index of matching metric-tag in request batch of metric-tags
 */
@Data
@Accessors(chain = true)
public class DetectorMapping {
    private String id;
    private Detector detector;
    private Expression expression;
    private User user;
    private long lastModifiedTimeInMillis;
    private long createdTimeInMillis;
    private boolean isEnabled;
    private List<Integer> searchIndexes;
}
