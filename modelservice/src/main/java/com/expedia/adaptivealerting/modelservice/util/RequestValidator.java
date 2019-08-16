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

import com.expedia.adaptivealerting.anomdetect.source.DetectorDocument;
import com.expedia.adaptivealerting.anomdetect.source.DetectorRegistry;
import com.expedia.adaptivealerting.modelservice.dto.common.Expression;
import com.expedia.adaptivealerting.modelservice.dto.common.Operand;
import com.expedia.adaptivealerting.modelservice.dto.common.Operator;
import com.expedia.adaptivealerting.modelservice.dto.detectormapping.User;
import com.expedia.adaptivealerting.modelservice.dto.percolator.PercolatorDetectorMapping;
import com.expedia.adaptivealerting.modelservice.entity.Detector;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.UUID;

@UtilityClass
public class RequestValidator {

    public static void validateUser(User user) {
        Assert.notNull(user, "subscription user can't null");
        Assert.isTrue(!StringUtils.isEmpty(user.getId()), "subscription userId can't empty");
        Assert.isTrue(!StringUtils.containsWhitespace(user.getId()),
                "subscription userId can't contain whitespaces");
    }

    public static void validateExpression(Expression expression) {
        Assert.notNull(expression, "subscription expression can't null");
        //Only AND operator is supported now
        Assert.isTrue(Operator.AND.equals(expression.getOperator()), "Only AND operator is supported now");
        Assert.notEmpty(expression.getOperands(), "Operands can't be empty");
        expression.getOperands().forEach(operand -> validateOperand(operand));
    }

    private static void validateOperand(Operand operand) {
        //Nested conditions are not supported now
        Assert.isNull(operand.getExpression(), "Nested expressions are not supported");
        Assert.notNull(operand.getField(), "Operands can't be empty");
        Assert.isTrue(!StringUtils.isEmpty(operand.getField().getKey()), "Invalid operand field key");
        Assert.isTrue(!StringUtils.isEmpty(operand.getField().getValue()), "Invalid operand field value");
        Assert.isTrue(!operand.getField().getKey().startsWith(PercolatorDetectorMapping.AA_PREFIX),
                String.format("Invalid operand field key '%s'. %s is a reserved prefix",
                        operand.getField().getKey(), PercolatorDetectorMapping.AA_PREFIX));
    }

    public static void validateMappingDetector(com.expedia.adaptivealerting.modelservice.dto.detectormapping.Detector detector) {
        Assert.notNull(detector, "Detector can't be null");
        Assert.notNull(detector.getUuid(), "Detector uuid can't be null");
    }

    public static void validateDetector(Detector detector) {
        val type = detector.getType();
        val config = detector.getDetectorConfig();
        val document = new DetectorDocument()
                .setType(type)
                .setUuid(UUID.randomUUID())
                .setCreatedBy("adaptive-alerting")
                .setDateUpdated(new Date())
                .setConfig(config);
        new DetectorRegistry()
                .getDetectorFactory(document)
                .buildDetector();
    }
}
