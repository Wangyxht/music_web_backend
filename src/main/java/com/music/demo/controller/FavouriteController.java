package com.music.demo.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.music.demo.dto.FavouriteSearchDTO;
import com.music.demo.service.FavouriteService;
import com.music.demo.utils.ApiResponse;
import com.music.demo.vo.PlaylistBaseVO;
import com.music.demo.vo.SongBaseVO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/favourite/")
public class FavouriteController {

    @Autowired
    FavouriteService favouriteService;

    @PostMapping("add")
    ResponseEntity<Object> add(@NotNull @RequestParam Long id,
                               @NotNull @RequestParam Integer type)
    {
        try {
            favouriteService.add(id, type);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("收藏成功"));

        } catch (Exception e){
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("remove")
    ResponseEntity<Object> remove(@NotNull @RequestParam Long id,
                                  @NotNull @RequestParam Integer type)
    {
        try {
            favouriteService.delete(id, type);
            return ResponseEntity.ok()
                    .body(ApiResponse.success("取消收藏成功"));

        } catch (Exception e){
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("list")
    ResponseEntity<Object> list(@Valid @ModelAttribute FavouriteSearchDTO favouriteSearchDTO)
    {
        try{
            if(favouriteSearchDTO.getType().equals(0)){
                Page<SongBaseVO> page = favouriteService.getFavouriteSong(favouriteSearchDTO);
                return ResponseEntity.ok()
                        .body(ApiResponse.success(page, "查询收藏成功"));
            } else {
                Page<PlaylistBaseVO> page = favouriteService.getFavouritePlayList(favouriteSearchDTO);
                return ResponseEntity.ok()
                        .body(ApiResponse.success(page, "查询收藏成功"));
            }
        } catch (Exception e){
            return ResponseEntity
                    .internalServerError()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

}
