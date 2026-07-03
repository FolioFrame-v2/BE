package com.folioframe.domain.common.controller;

import com.folioframe.domain.common.dto.response.RegionResDTO;
import com.folioframe.domain.common.service.RegionService;
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
@RequestMapping("/api/v1/regions")
@RequiredArgsConstructor
public class RegionController implements RegionControllerDocs {

    private final RegionService regionService;

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<List<RegionResDTO>>> search(
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(GeneralSuccessCode.OK, regionService.search(keyword)));
    }
}
