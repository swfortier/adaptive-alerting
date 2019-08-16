package com.expedia.adaptivealerting.modelservice.dto;

import com.expedia.adaptivealerting.modelservice.dto.detectormapping.CreateDetectorMappingRequest;
import com.expedia.adaptivealerting.modelservice.dto.detectormapping.Detector;
import com.expedia.adaptivealerting.modelservice.dto.common.Expression;
import com.expedia.adaptivealerting.modelservice.dto.common.Field;
import com.expedia.adaptivealerting.modelservice.dto.common.Operand;
import com.expedia.adaptivealerting.modelservice.dto.common.Operator;
import com.expedia.adaptivealerting.modelservice.dto.detectormapping.User;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertNotEquals;

public class CreateDetectorMappingRequestTest {

    @Test
    public void testEquals() {
        CreateDetectorMappingRequest createDetectorMappingRequest = new CreateDetectorMappingRequest();
        createDetectorMappingRequest.setExpression(getExpression());
        createDetectorMappingRequest.setUser(new User("1"));
        createDetectorMappingRequest.setDetector(new Detector(UUID.randomUUID()));
        assertNotEquals(createDetectorMappingRequest, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNull_user_request() {
        CreateDetectorMappingRequest createDetectorMappingRequest = new CreateDetectorMappingRequest();
        createDetectorMappingRequest.setExpression(getExpression());
        createDetectorMappingRequest.setDetector(new Detector(UUID.randomUUID()));
        createDetectorMappingRequest.validate();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNull_detector_request() {
        CreateDetectorMappingRequest createDetectorMappingRequest = new CreateDetectorMappingRequest();
        createDetectorMappingRequest.setExpression(getExpression());
        createDetectorMappingRequest.validate();
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNull_expression_request() {
        CreateDetectorMappingRequest createDetectorMappingRequest = new CreateDetectorMappingRequest();
        createDetectorMappingRequest.setUser(new User("1"));
        createDetectorMappingRequest.setDetector(new Detector(UUID.randomUUID()));
        createDetectorMappingRequest.validate();
    }

    private Expression getExpression() {
        Expression expression = new Expression();
        expression.setOperator(Operator.AND);
        List<Operand> operandsList = new ArrayList<>();
        Operand testOperand = new Operand();
        testOperand.setField(new Field("name", "sample-app"));
        operandsList.add(testOperand);
        expression.setOperands(operandsList);
        return expression;
    }
}
