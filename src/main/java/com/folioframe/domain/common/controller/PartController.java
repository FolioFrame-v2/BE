package com.folioframe.domain.common.controller;

import com.folioframe.domain.common.dto.response.PartResDTO;
import com.folioframe.domain.common.service.PartService;
import com.folioframe.global.apiPayload.ApiResponse;
import com.folioframe.global.apiPayload.code.GeneralSuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/parts")
@RequiredArgsConstructor
public class PartController {

    private final PartService partService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PartResDTO>>> search(
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(GeneralSuccessCode.OK, partService.search(keyword)));
    }
}
