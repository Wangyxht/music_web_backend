package com.music.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.music.demo.dto.PlayListUploadDTO;
import com.music.demo.dto.PlaylistSearchDTO;
import com.music.demo.dto.PlaylistUpdateDTO;
import com.music.demo.dto.PlaylistUserSearchDTO;
import com.music.demo.entity.Playlist;
import com.music.demo.service.PlaylistService;
import com.music.demo.utils.ApiResponse;
import com.music.demo.vo.PlaylistBaseVO;
import com.music.demo.vo.PlaylistDetailVO;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/playlist/")
@CrossOrigin("*")
public class PlaylistController {

    @Autowired
    PlaylistService playlistService;

    @PostMapping("create")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class))})
    public ResponseEntity<Object> create(@Validated @ModelAttribute PlayListUploadDTO playListUploadDTO)
    {
        try{
            playlistService.create(playListUploadDTO);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("歌单上传成功"));

        } catch (Exception e){
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("update")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class))})
    public ResponseEntity<Object> update(@Validated @ModelAttribute PlaylistUpdateDTO playlistUpdateDTO)
    {
        try{
            playlistService.update(playlistUpdateDTO);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("歌单更新成功"));

        } catch (Exception e){
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("delete")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class))})
    public ResponseEntity<Object> update(@NotNull @RequestParam Long playlistId)
    {
        try{

            playlistService.delete(playlistId);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("歌单删除成功"));

        } catch (Exception e){
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("search")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Page.class))})
    ResponseEntity<Object> search(@Validated @ModelAttribute PlaylistSearchDTO playlistSearchDTO){
        try {
            Page<PlaylistBaseVO> page = playlistService.search(playlistSearchDTO);
            return ResponseEntity
                    .ok()
                    .body(ApiResponse.success(page, "歌单查询成功"));
        } catch (Exception e){
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }

    }

    @GetMapping("user-search")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Page.class))})
    ResponseEntity<Object> userSearch(@Validated @ModelAttribute PlaylistUserSearchDTO playlistUserSearchDTO){
        try{
            Page<PlaylistBaseVO> page = playlistService.searchByUser(playlistUserSearchDTO);
            return ResponseEntity
                    .ok()
                    .body(ApiResponse.success(page, "歌单查询成功"));
        }catch (Exception e){
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("inf/{playlistId}")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = PlaylistDetailVO.class))})
    ResponseEntity<Object> inf(@PathVariable Long playlistId){
        try{
            PlaylistDetailVO playlistDetailVO = playlistService.getPlaylistDetail(playlistId);
            return ResponseEntity
                    .ok()
                    .body(ApiResponse.success(playlistDetailVO, "歌单查询成功"));
        }catch (Exception e){
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("cover/{playlistId}")
    public void getCover(HttpServletResponse response, @PathVariable String playlistId)
    {
        try{
            playlistService.getCover(response, Long.valueOf(playlistId));
        } catch (Exception e){
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @GetMapping("admin/search")
    public ResponseEntity<Object> adminSearch(@ModelAttribute PlaylistSearchDTO playlistSearchDTO){
        try{
            Page<Playlist> page = playlistService.adminSearch(playlistSearchDTO);
            return ResponseEntity
                    .ok()
                    .body(ApiResponse.success(page, "歌单查询成功"));
        }catch (Exception e){
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("recommend")
    public ResponseEntity<Object> recommend(@NotNull @RequestParam Integer playlistNum){
        try{
            List<PlaylistBaseVO> playlistBaseVOList = playlistService.recommend(playlistNum);
            return ResponseEntity
                    .ok()
                    .body(ApiResponse.success(playlistBaseVOList, "歌单查询成功"));
        }catch (Exception e){
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }


}
