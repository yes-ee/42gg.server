package com.gg.server.admin.feedback.controller;

import com.gg.server.admin.feedback.dto.FeedbackIsSolvedResponseDto;
import com.gg.server.admin.feedback.dto.FeedbackListAdminResponseDto;
import com.gg.server.admin.feedback.service.FeedbackAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("pingpong/admin/feedback")
@RequiredArgsConstructor
@Validated
public class FeedbackAdminController {
    private final FeedbackAdminService feedbackAdminService;

    @GetMapping
    public FeedbackListAdminResponseDto feedbackAll(
            @RequestParam(defaultValue = "1") @Min(1) int page, @RequestParam(defaultValue = "5") @Min(1) int size) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("isSolved").and(Sort.by("createdAt")));
        return feedbackAdminService.findAllFeedback(pageable);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<FeedbackIsSolvedResponseDto> feedbackIsSolvedToggle(@PathVariable @NotNull Long id){

        return ResponseEntity.ok()
                .body(feedbackAdminService.toggleFeedbackIsSolvedByAdmin(id));
    }

    @GetMapping("/users")
    public FeedbackListAdminResponseDto feedbackFindByIntraId(@RequestParam String intraId,
                                                              @RequestParam(defaultValue = "1") @Min(1) int page,
                                                              @RequestParam(defaultValue = "5") @Min(1) int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("intra_id").and(Sort.by("createdAt")));

        return feedbackAdminService.findByPartsOfIntraId(intraId, pageable);
    }
}
