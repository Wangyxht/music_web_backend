package com.music.demo.controller;

import com.music.demo.service.LikeService;
import com.music.demo.utils.ApiResponse;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/like/")
public class LikeController {

    @Autowired
    LikeService likeService;

    @PostMapping("add")
    public ResponseEntity<Object> add(@NotNull @RequestParam Long playlistId)
    {
        try{
            likeService.add(playlistId);
            return ResponseEntity
                    .ok()
                    .body(ApiResponse.success("点赞成功"));
        } catch (Exception e){
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error("信息更新失败"));
        }
    }

    @DeleteMapping("remove")
    public ResponseEntity<Object> remove(@NotNull @RequestParam Long playlistId)
    {
        try{
            likeService.remove(playlistId);
            return ResponseEntity
                    .ok()
                    .body(ApiResponse.success("取消点赞成功"));
        } catch (Exception e){
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error("信息更新失败"));
        }
    }
}
