package com.folioframe.global.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.folioframe.global.apiPayload.ApiResponse;
import com.folioframe.global.apiPayload.code.BaseErrorCode;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class FilterResponseUtils {

    private final ObjectMapper objectMapper;

    public void sendErrorResponse(HttpServletResponse response, BaseErrorCode errorCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(errorCode.getStatus().value());
        ApiResponse<Object> errorResponse = ApiResponse.onFailure(errorCode, null);
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);

        response.getWriter().write(jsonResponse);
    }
}

