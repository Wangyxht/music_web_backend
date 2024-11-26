package com.music.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.music.demo.dto.*;
import com.music.demo.entity.Song;
import com.music.demo.entity.Tags;
import com.music.demo.mapper.SongMapper;
import com.music.demo.mapstruct.SongDtoMapper;
import com.music.demo.utils.AudioUtil;
import com.music.demo.utils.TextAuditUtil;
import com.music.demo.utils.ThreadLocalUtil;
import com.music.demo.vo.SongBaseVO;
import com.music.demo.vo.SongDetailVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class SongServiceImpl extends ServiceImpl<SongMapper, Song> implements SongService {

    @Autowired
    FileStorageService fileStorageService;

    @Autowired
    private SongMapper songMapper;

    @Autowired
    private TagsService tagsService;

    @Autowired
    private TextAuditUtil textAuditUtil;

    @Override
    public Page<SongBaseVO> search(SongSearchDTO songSearchDTO) {

        Map<String, Object> map = (Map<String, Object>) ThreadLocalUtil.get();
        Long userId = (Long) map.get("id");

        Page<SongBaseVO> page = new Page<>(songSearchDTO.getPage(), songSearchDTO.getLimit());
        page.setOptimizeCountSql(false);

        QueryWrapper<SongBaseVO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("audit", true);
        queryWrapper.eq("is_delete", false);
        // 添加搜索条件 (title 或 singer 包含关键字)
        if (songSearchDTO.getKeyword() != null) {
            queryWrapper.and(wrapper ->
                    wrapper.like("title", songSearchDTO.getKeyword())
                            .or()
                            .like("singer", songSearchDTO.getKeyword())
            );
        }

        return songMapper.selectSongPage(page, queryWrapper, userId);
    }

    @Override
    public Page<SongBaseVO> searchByUser(SongUserSearchDTO songUserSearchDTO){

        Map<String, Object> map = (Map<String, Object>) ThreadLocalUtil.get();
        Long userId = (Long) map.get("id");

        Page<SongBaseVO> page = new Page<>(songUserSearchDTO.getPage(), songUserSearchDTO.getLimit());
        page.setOptimizeCountSql(false);

        QueryWrapper<SongBaseVO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("uploader_id", songUserSearchDTO.getUserId());
        queryWrapper.eq("audit", true);
        queryWrapper.eq("is_delete", false);

        return songMapper.selectSongPage(page, queryWrapper, userId);
    }

    @Override
    public SongDetailVO getSongDetail(Long id) throws FileNotFoundException {
        Map<String, Object> map = (Map<String, Object>) ThreadLocalUtil.get();
        Long userId = (Long) map.get("id");

        QueryWrapper<SongDetailVO> wrapper = new QueryWrapper<>();
        wrapper.eq("audit", true);
        wrapper.eq("s.id", id);
        wrapper.eq("is_delete", false);
        SongDetailVO songDetailVO =  songMapper.selectSongDetail(wrapper, userId);
        if (songDetailVO == null) {
            throw new FileNotFoundException("未找到该歌曲");
        }

        songDetailVO.setTags(tagsService.getTags(id, 0));
        return songDetailVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upload(SongUploadDTO songUploadDTO){

        // 自动审核
        Map<String, String> auditResult = textAuditUtil.auditText(
                songUploadDTO.getTitle() + " "
                        + songUploadDTO.getIntroduction() + " "
                        + songUploadDTO.getSinger() + " ",
                "comment_detection"
        );

        if(auditResult.get("riskLevel") != null)
            switch (auditResult.get("riskLevel")) {
                case "high", "medium" -> throw new IllegalArgumentException("文本涉嫌违规");
            }

        Map<String, Object> map = (Map<String, Object>) ThreadLocalUtil.get();
        Long uploaderId = (Long) map.get("id");

        String coverPath;
        String audioPath;

        try{
            // 存储封面和音频文件
            coverPath = fileStorageService.storeCover(songUploadDTO.getCover());
            audioPath = fileStorageService.storeAudio(songUploadDTO.getAudio());
        } catch (Exception e){
            throw new RuntimeException("文件存储失败");
        }

        Long duration = AudioUtil.getAudioDuration(audioPath);

        // 创建 Song 实体并保存
        Song song = SongDtoMapper.INSTANCE.toEntity(songUploadDTO);
        song.setUploaderId(uploaderId);
        song.setAudioPath(audioPath);
        song.setCoverPath(coverPath);
        song.setUploadTime(LocalDateTime.now());
        song.setDuration(duration);
        song.setAudit(false);
        song.setIsDelete(false);

        if (songMapper.insert(song) == 0){
            throw new RuntimeException("上传失败，请重试。");
        }

        ArrayList<Tags> tagsList = new ArrayList<>();
        // 设置标签
        for(Long tagId : songUploadDTO.getTags())
        {
            Tags tag = new Tags();
            tag.setType(0);
            tag.setTagId(tagId);
            tag.setEntityId(song.getId());
            tagsList.add(tag);
        }

        if (!tagsList.isEmpty() && !tagsService.saveBatch(tagsList)){
           throw new RuntimeException("上传失败，请重试。");
        }
    }

    @Override
    public void getCover(HttpServletResponse response, Long songId)
    {
        Song song = songMapper.selectById(songId);
        String songCoverPath = song.getCoverPath();

        File f = new File(songCoverPath);
        try (FileInputStream fis = new FileInputStream(f)){
            response.setHeader("Content-Type", songCoverPath.endsWith("jpg") ? "image/jpeg" : "image/png");
            response.setHeader("Content-Length", f.length() + "");
            byte[] buf = new byte[4096];
            int len;
            while ((len = fis.read(buf)) != -1) {
                response.getOutputStream().write(buf, 0, len);
            }
            response.getOutputStream().flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void audit(Long id, Boolean audit) {
        // 鉴权
        Map<String, Object> map = (Map<String, Object>) ThreadLocalUtil.get();
        Integer type = (Integer) map.get("type");

        if(!type.equals(0)){
            throw new IllegalArgumentException("无管理员权限！");
        }

        // 成功则放行，否则删除
        if(audit){
            UpdateWrapper<Song> updateWrapper = new UpdateWrapper<>();
            updateWrapper.lambda()
                    .eq(Song::getId, id)
                    .eq(Song::getAudit, false)
                    .set(Song::getAudit, true);
            if(songMapper.update(updateWrapper) == 0){
                throw new RuntimeException("数据更新失败");
            }

        } else {
            if(!this.removeById(id)){
                throw new RuntimeException("数据更新失败");
            }
        }
    }

    @Override
    public Page<Song> getAuditList(PageSearchDTO pageSearchDTO) {

        // 鉴权
        Map<String, Object> map = (Map<String, Object>) ThreadLocalUtil.get();
        Integer type = (Integer) map.get("type");
        if(!type.equals(0)){
            throw new IllegalArgumentException("无管理员权限！");
        }

        Page<Song> page = new Page<>(pageSearchDTO.getPage(), pageSearchDTO.getLimit());
        QueryWrapper<Song> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("audit", false);

        return songMapper.selectPage(page, queryWrapper);
    }

    @Override
    public void delete(Long songId){
        Map<String, Object> map = (Map<String, Object>) ThreadLocalUtil.get();
        Long userId = (Long) map.get("id");
        Integer userType = (Integer) map.get("type");

        Song song = songMapper.selectById(songId);
        if(!(song.getUploaderId().equals(userId) || userType.equals(0))){
            throw new IllegalArgumentException("歌单删除权限错误");
        }

        if(songMapper.deleteById(songId) == 0){
            throw new RuntimeException("歌单删除异常。");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(SongUpdateDTO songUpdateDTO){
        Map<String, Object> map = (Map<String, Object>) ThreadLocalUtil.get();
        Long userId = (Long) map.get("id");
        Integer userType = (Integer) map.get("type");

        // 自动审核
        Map<String, String> auditResult = textAuditUtil.auditText(
                songUpdateDTO.getTitle() + " "
                        + songUpdateDTO.getIntroduction() + " "
                        + songUpdateDTO.getSinger() + " ",
                "comment_detection"
        );

        if(auditResult.get("riskLevel") != null)
            switch (auditResult.get("riskLevel")) {
                case "high", "medium" -> throw new IllegalArgumentException("文本涉嫌违规");
            }

        Song song = songMapper.selectById(songUpdateDTO.getSongId());
        if(!(song.getUploaderId().equals(userId) || userType.equals(0))){
            throw new IllegalArgumentException("歌单删除权限错误");
        }

        String coverPath = null;
        String audioPath = null;
        Long duration = null;

        try{
            // 存储封面和音频文件
            if(songUpdateDTO.getCover() != null) {
                fileStorageService.deleteCover(song.getCoverPath());
                coverPath = fileStorageService.storeCover(songUpdateDTO.getCover());
            }
            if(songUpdateDTO.getAudio() != null) {
                fileStorageService.deleteAudio(song.getAudioPath());
                audioPath = fileStorageService.storeAudio(songUpdateDTO.getAudio());
                duration = AudioUtil.getAudioDuration(audioPath);
            }
        } catch (Exception e){
            throw new RuntimeException("文件存储失败");
        }

        song.setId(songUpdateDTO.getSongId());
        song.setUploaderId(userId);
        song.setTitle(songUpdateDTO.getTitle());
        song.setIntroduction(songUpdateDTO.getIntroduction());
        song.setDuration(duration);
        song.setAudioPath(audioPath);
        song.setCoverPath(coverPath);
        song.setUploaderId(userId);
        song.setAudit(false);
        song.setSinger(songUpdateDTO.getSinger());
        song.setLyrics(songUpdateDTO.getLyrics());
        song.setUploadTime(LocalDateTime.now());

        // 设置标签
        tagsService.deleteTags(songUpdateDTO.getSongId(), 0);
        ArrayList<Tags> tagsList = new ArrayList<>();
        for(Long tagId : songUpdateDTO.getTags()) {
            Tags tag = new Tags();
            tag.setType(0);
            tag.setTagId(tagId);
            tag.setEntityId(song.getId());
            tagsList.add(tag);
        }

        if (!tagsList.isEmpty() && !tagsService.saveBatch(tagsList)){
            throw new RuntimeException("更改异常");
        }

        // 更新歌单信息
        if(songMapper.updateById(song) == 0){
            throw new RuntimeException("更改异常");
        }
    }

    @Override
    public Page<Song> adminSearch(SongSearchDTO songSearchDTO){
        Map<String, Object> map = (Map<String, Object>) ThreadLocalUtil.get();
        Integer userType = (Integer) map.get("type");

        Page<Song> page = new Page<>(songSearchDTO.getPage(), songSearchDTO.getLimit());

        if(!userType.equals(0)){
            throw new IllegalArgumentException("无管理员权限");
        }

        QueryWrapper<Song> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().like(Song::getTitle, songSearchDTO.getKeyword());

        return songMapper.selectPage(page, queryWrapper);
    }

    @Override
    public List<SongBaseVO> recommend(Integer songNum){
        Map<String, Object> map = (Map<String, Object>) ThreadLocalUtil.get();
        Long userId = (Long) map.get("id");

        // 获取按收藏数排序的前 50 首歌曲
        List<SongBaseVO> songBaseVOList = songMapper.selectTopSongs(50, userId);

        // 如果需要的歌曲数量大于实际获取数量，直接返回全部
        if (songNum >= songBaseVOList.size()) {
            return songBaseVOList;
        }

        // 随机打乱列表顺序
        Collections.shuffle(songBaseVOList);

        // 返回随机选择的前 playlistNum 首歌曲
        return songBaseVOList.subList(0, songNum);
    }
}

