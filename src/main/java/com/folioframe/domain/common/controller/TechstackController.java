package com.folioframe.domain.common.controller;

import com.folioframe.domain.common.dto.request.TechstackReqDTO;
import com.folioframe.domain.common.dto.response.TechstackResDTO;
import com.folioframe.domain.common.service.TechstackService;
import com.folioframe.global.apiPayload.ApiResponse;
import com.folioframe.global.apiPayload.code.GeneralSuccessCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/techstacks")
@RequiredArgsConstructor
public class TechstackController implements TechstackControllerDocs {

    private final TechstackService techstackService;

    @Override
    @GetMapping
    public ResponseEntity<ApiResponse<List<TechstackResDTO>>> search(
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(GeneralSuccessCode.OK, techstackService.search(keyword)));
    }

    @Override
    @PostMapping
    public ResponseEntity<ApiResponse<TechstackResDTO>> findOrCreate(
            @Valid @RequestBody TechstackReqDTO request) {
        return ResponseEntity.ok(
                ApiResponse.onSuccess(GeneralSuccessCode.OK, techstackService.findOrCreate(request.name())));
    }
}
