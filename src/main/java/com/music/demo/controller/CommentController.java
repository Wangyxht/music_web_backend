package com.music.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.music.demo.dto.CommentCreateDTO;
import com.music.demo.dto.CommentSearchDTO;
import com.music.demo.dto.PageSearchDTO;
import com.music.demo.service.CommentService;
import com.music.demo.utils.ApiResponse;
import com.music.demo.vo.CommentVO;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment/")
@CrossOrigin("*")
public class CommentController {

    @Autowired
    CommentService commentService;

    /**
     * 创建评论
     */
    @PostMapping("create")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class))})
    public ResponseEntity<Object> create(@Validated @RequestBody CommentCreateDTO commentCreateDTO) {
        try {
            commentService.create(commentCreateDTO);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("评论创建成功"));
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 删除评论
     */
    @DeleteMapping("delete")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class))})
    public ResponseEntity<Object> delete(@NotNull @RequestParam Long commentId) {
        try {
            commentService.delete(commentId);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("评论删除成功"));
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("audit/{commentId}")
    public ResponseEntity<Object> audit(@NotNull @PathVariable Long commentId,
                                        @NotNull @RequestParam Boolean audit) {
        try {
            commentService.audit(commentId, audit);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("评论审核成功"));
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("song")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Page.class))})
    public ResponseEntity<Object> songComment(@Validated @ModelAttribute CommentSearchDTO commentSearchDTO) {
        try {
            Page<CommentVO> page = commentService.searchComment(commentSearchDTO, 0);
            return ResponseEntity.ok()
                    .body(ApiResponse.success(page, "查询成功"));
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("playlist")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Page.class))})
    public ResponseEntity<Object> playlistComment(@Validated @ModelAttribute CommentSearchDTO commentSearchDTO) {
        try {
            Page<CommentVO> page = commentService.searchComment(commentSearchDTO, 1);
            return ResponseEntity.ok()
                    .body(ApiResponse.success(page, "查询成功"));
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("user-search")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Page.class))})
    public ResponseEntity<Object> userComment(@Validated @ModelAttribute CommentSearchDTO commentSearchDTO) {
        try {
            Page<CommentVO> page = commentService.searchCommentByUser(commentSearchDTO);
            return ResponseEntity.ok()
                    .body(ApiResponse.success(page, "查询成功"));
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("audit/list")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Page.class))})
    public ResponseEntity<Object> getAuditList(@Validated @ModelAttribute PageSearchDTO pageSearchDTO){
        try {
            Page<CommentVO> page = commentService.getAuditList(pageSearchDTO);
            return ResponseEntity.ok()
                    .body(ApiResponse.success(page, "查询成功"));

        } catch (Exception e){
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

}
