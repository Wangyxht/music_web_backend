package com.music.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.music.demo.dto.*;
import com.music.demo.entity.Song;
import com.music.demo.service.SongService;
import com.music.demo.utils.ApiResponse;
import com.music.demo.vo.SongBaseVO;
import com.music.demo.vo.SongDetailVO;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@CrossOrigin("*")
@RequestMapping("/song/")
public class SongController {

    @Autowired
    private SongService songService;

    @PostMapping("upload")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = ApiResponse.class))})
    public ResponseEntity<Object> upload(@Validated @ModelAttribute SongUploadDTO songUploadDTO)
    {
        try{
            songService.upload(songUploadDTO);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("歌曲上传成功"));

        } catch (Exception e){
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("search")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Page.class))
            })
    public ResponseEntity<Object> search(@Validated @ModelAttribute SongSearchDTO songSearchDTO)
    {
        try{
            Page<SongBaseVO> songBaseVOPage =  songService.search(songSearchDTO);
            return ResponseEntity.ok()
                    .body(ApiResponse.success(songBaseVOPage, "歌曲查询成功"));
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("user-search")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Page.class))
            })
    public ResponseEntity<Object> userSearch(@Validated @ModelAttribute SongUserSearchDTO songUserSearchDTO)
    {
        try{
            Page<SongBaseVO> songBaseVOPage =  songService.searchByUser(songUserSearchDTO);
            return ResponseEntity.ok()
                    .body(ApiResponse.success(songBaseVOPage, "歌曲查询成功"));
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("play/{songId}")
    public void playSong(HttpServletResponse response,
                         HttpServletRequest request,
                         @PathVariable String songId)
    {
        Song song = songService.getById(songId);

        File f = new File(song.getAudioPath());

        try(FileInputStream fis = new FileInputStream(f)){
            // audio / video 标签拖动时发送的请求, 第一次：bytes=0-， 后续：bytes=startPos-endPos
            String rangeString = request.getHeader("Range");

            if (rangeString == null){
                rangeString = "bytes=0-";
            }

            // range 值
            long rangeStart = Long.parseLong(rangeString.substring(rangeString.indexOf("=") + 1, rangeString.indexOf("-")));
            // range范围是从0个字节开始，所以结束位置下标是文件长度-1
            long rangeEnd = f.length() - 1;
            // 解析请求Range头信息，bytes=xxx-xxx, (存在0-0,-1这种情况，暂不考虑)
            if (rangeString.indexOf("-") < rangeString.length() - 1) {
                rangeEnd = Long.parseLong(rangeString.substring(rangeString.indexOf("-") + 1));
            }
            // chrome浏览器第一次发送请求，range开始为0
            if (rangeStart == 0) {
                // 没有这个header，audio没有总时长，第一次请求返回总长度
                response.setHeader("Content-Length", f.length() + "");
            } else {
                // 配合content-range头使用，后续Content-Length代表分段大小
                response.setStatus(HttpStatus.PARTIAL_CONTENT.value());
                response.setHeader("Content-Length", (rangeEnd - rangeStart + 1) + "");
            }


            response.setHeader("Content-Type", song.getAudioPath().endsWith("wav") ? "audio/wav" : "audio/mp3");

            // range范围  start-end/total, 这个头可以不要，但是如果不要(content-length必须为文件总大小)，每次拖拽，浏览器请求的文件都是全量大小的文件, 浪费带宽
            response.setHeader("Content-Range", "bytes " + rangeStart + "-" + rangeEnd + "/" + f.length());

            // 告知video / audio标签可以接受范围单位，这个头必须传递
            response.setHeader("Accept-Ranges", "bytes");

            // 不重要
            response.setHeader("content-disposition", "attachment;filename=" + URLEncoder.encode(songId, StandardCharsets.UTF_8));
            response.addHeader("Access-Control-Allow-Origin", "*");


            // skip bytes
            if (rangeStart > 0) {
                long skipped = fis.skip(rangeStart);
            }

            byte[] buf = new byte[4096];
            int len;
            while ((len = fis.read(buf)) != -1) {
                response.getOutputStream().write(buf, 0, len);
            }
            response.getOutputStream().flush();

        } catch (IOException e){
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

    }

    @GetMapping("cover/{songId}")
    public void getCover(HttpServletResponse response, @PathVariable String songId)
    {
        try{
            songService.getCover(response, Long.valueOf(songId));
        } catch (Exception e){
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @GetMapping("inf/{songId}")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = SongDetailVO.class))})
    public ResponseEntity<Object> getSongDetail(@PathVariable Long songId){
        try{
            SongDetailVO songDetailVO = songService.getSongDetail(songId);
            return ResponseEntity.ok()
                    .body(ApiResponse.success(songDetailVO, "歌曲查询成功"));

        } catch (Exception e){
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("audit/{songId}")
    public ResponseEntity<Object> audit(@NotNull @PathVariable Long songId,
                                        @NotNull @RequestParam Boolean audit){
        try {
            songService.audit(songId, audit);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("歌曲审核成功"));

        } catch (Exception e){
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("audit/list")
    public ResponseEntity<Object> getAuditList(@Validated @ModelAttribute PageSearchDTO pageSearchDTO){
        try {
            Page<Song> page = songService.getAuditList(pageSearchDTO);
            return ResponseEntity.ok()
                    .body(ApiResponse.success(page, "查询成功"));

        } catch (Exception e){
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("delete")
    public ResponseEntity<Object> delete(@RequestParam Long songId){
        try {
            songService.delete(songId);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("删除成功"));

        } catch (Exception e){
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PutMapping("update")
    public ResponseEntity<Object> update(@Validated @ModelAttribute SongUpdateDTO songUpdateDTO){
        try {
            songService.update(songUpdateDTO);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("更新成功"));

        } catch (Exception e){
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("admin/search")
    public ResponseEntity<Object> adminSearch(@Validated @ModelAttribute SongSearchDTO songSearchDTO) {
        try{
            Page<Song> page =  songService.adminSearch(songSearchDTO);
            return ResponseEntity.ok()
                    .body(ApiResponse.success(page, "更新成功"));
        }catch (Exception e){
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("recommend")
    public ResponseEntity<Object> recommend(@RequestParam Integer songNum){
        try{
            List<SongBaseVO> songBaseVOList = songService.recommend(songNum);
            return ResponseEntity.ok()
                    .body(ApiResponse.success(songBaseVOList, "更新成功"));
        }catch (Exception e){
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

}
