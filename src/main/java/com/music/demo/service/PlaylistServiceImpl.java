package com.music.demo.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.music.demo.dto.*;
import com.music.demo.entity.Playlist;
import com.music.demo.entity.Tags;
import com.music.demo.mapper.PlaylistMapper;
import com.music.demo.mapstruct.PlaylistDtoMapper;
import com.music.demo.utils.TextAuditUtil;
import com.music.demo.utils.ThreadLocalUtil;
import com.music.demo.vo.PlaylistBaseVO;
import com.music.demo.vo.PlaylistDetailVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class PlaylistServiceImpl extends ServiceImpl<PlaylistMapper, Playlist> implements PlaylistService {

    @Autowired
    private PlaylistMapper playlistMapper;

    @Autowired
    private TagsService tagsService;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private TextAuditUtil textAuditUtil;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(PlayListUploadDTO playlistUploadDTO) {

        Map<String, Object> map = (Map<String, Object>) ThreadLocalUtil.get();
        Long uploaderId = (Long) map.get("id");
        // 自动审核
        Map<String, String> auditResult = textAuditUtil.auditText(
                playlistUploadDTO.getTitle() + " "
                        + playlistUploadDTO.getIntroduction() + " ",
                "comment_detection"
        );

        if(auditResult.get("riskLevel") != null)
            switch (auditResult.get("riskLevel")) {
                case "high", "medium" -> throw new IllegalArgumentException("文本涉嫌违规");
            }

        String coverPath = fileStorageService.storeCover(playlistUploadDTO.getCover());
        Playlist playlist = PlaylistDtoMapper.INSTANCE.toEntity(playlistUploadDTO);




        playlist.setCoverPath(coverPath);
        playlist.setIsDelete(false);
        playlist.setUserId(uploaderId);
        playlist.setCreateTime(LocalDateTime.now());

        ArrayList<Tags> tagsList = new ArrayList<>();

        if(playlistMapper.insert(playlist) == 0) {
            throw new RuntimeException("歌单创建失败，请重试");
        }

        // 设置标签
        for(Long tagId : playlistUploadDTO.getTags())
        {
            Tags tag = new Tags();
            tag.setType(1);
            tag.setTagId(tagId);
            tag.setEntityId(playlist.getId());
            tagsList.add(tag);
        }

        if (!tagsList.isEmpty() && !tagsService.saveBatch(tagsList)){
            throw new RuntimeException("上传失败，请重试。");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Map<String, Object> map = (Map<String, Object>) ThreadLocalUtil.get();
        Long userId = (Long) map.get("id");
        Integer userType = (Integer) map.get("type");
        Playlist playlist = playlistMapper.selectById(id);

        if(!(playlist.getUserId().equals(userId) || userType.equals(0))){
            throw new IllegalArgumentException("歌单创建失败，请重试");
        }

        if(!removeById(id)) {
           throw new RuntimeException("歌单删除错误");
        }


    }

    @Override
    public Page<PlaylistBaseVO> search(PlaylistSearchDTO playlistSearchDTO) {

        Page<PlaylistBaseVO> page = new Page<>(playlistSearchDTO.getPage(), playlistSearchDTO.getLimit());
        page.setOptimizeCountSql(false);

        QueryWrapper<PlaylistBaseVO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("pl.is_delete", false);
        queryWrapper.like("title", playlistSearchDTO.getKeyword());

        return playlistMapper.selectPlaylistPage(page, queryWrapper);
    }

    @Override
    public Page<PlaylistBaseVO> searchByUser(PlaylistUserSearchDTO playlistUserSearchDTO) {
        Page<PlaylistBaseVO> page = new Page<>(playlistUserSearchDTO.getPage(), playlistUserSearchDTO.getLimit());
        page.setOptimizeCountSql(false);

        QueryWrapper<PlaylistBaseVO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("u.id", playlistUserSearchDTO.getUserId());
        queryWrapper.eq("pl.is_delete", false);
        return playlistMapper.selectPlaylistPage(page, queryWrapper);
    }

    @Override
    public PlaylistDetailVO getPlaylistDetail(Long playlistId){
        Map<String, Object> map = (Map<String, Object>) ThreadLocalUtil.get();
        Long userId = (Long) map.get("id");

        PlaylistDetailVO playlistDetailVO =  playlistMapper.selectPlaylistDetail(playlistId, userId);
        if(playlistDetailVO == null){
            throw new IllegalArgumentException("未找到该歌单");
        }
        playlistDetailVO.setTags(tagsService.getTags(playlistId, 1));

        return playlistDetailVO;
    }

    @Override
    public void getCover(HttpServletResponse response, Long playlistId) {
        Playlist playlist = playlistMapper.selectById(playlistId);
        String songCoverPath = playlist.getCoverPath();

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
    @Transactional(rollbackFor = Exception.class)
    public void update(PlaylistUpdateDTO playlistUpdateDTO) {

        // 自动审核
        Map<String, String> auditResult = textAuditUtil.auditText(
                playlistUpdateDTO.getTitle() + " "
                        + playlistUpdateDTO.getIntroduction() + " ",
                "comment_detection"
        );

        if(auditResult.get("riskLevel") != null)
            switch (auditResult.get("riskLevel")) {
                case "high", "medium" -> throw new IllegalArgumentException("文本涉嫌违规");
            }

        Map<String, Object> map = (Map<String, Object>) ThreadLocalUtil.get();
        Long userId = (Long) map.get("id");
        Integer userType = (Integer) map.get("type");
        Playlist playlist = playlistMapper.selectById(playlistUpdateDTO.getPlaylistId());

        if(!(playlist.getUserId().equals(userId) || userType.equals(0))){
            throw new IllegalArgumentException("权限错误");
        }

        String newPath = null;
        if(playlistUpdateDTO.getCover() != null){
            fileStorageService.deleteCover(playlist.getCoverPath());
            newPath = fileStorageService.storeCover(playlistUpdateDTO.getCover());
        }

        Playlist playlistUpdate = new Playlist();
        playlistUpdate.setId(playlistUpdateDTO.getPlaylistId());
        playlistUpdate.setTitle(playlistUpdateDTO.getTitle());
        playlistUpdate.setCoverPath(newPath);
        playlistUpdate.setIsDelete(false);
        playlistUpdate.setUserId(userId);
        playlistUpdate.setCreateTime(LocalDateTime.now());

        // 设置标签
        tagsService.deleteTags(playlistUpdate.getId(), 1);
        ArrayList<Tags> tagsList = new ArrayList<>();
        for(Long tagId : playlistUpdateDTO.getTags()) {
            Tags tag = new Tags();
            tag.setType(1);
            tag.setTagId(tagId);
            tag.setEntityId(playlist.getId());
            tagsList.add(tag);
        }

        if (!tagsList.isEmpty() && !tagsService.saveBatch(tagsList)){
            throw new RuntimeException("更改异常");
        }

        // 更新歌单信息
        if(playlistMapper.updateById(playlistUpdate) == 0){
            throw new RuntimeException("更改异常");
        }
    }

    @Override
    public Page<Playlist> adminSearch(PlaylistSearchDTO playlistSearchDTO){
        Map<String, Object> map = (Map<String, Object>) ThreadLocalUtil.get();
        Integer userType = (Integer) map.get("type");

        if(!userType.equals(0)){
            throw new IllegalArgumentException("无管理员权限。");
        }

        Page<Playlist> page = new Page<>(playlistSearchDTO.getPage(), playlistSearchDTO.getLimit());
        QueryWrapper<Playlist> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().like(Playlist::getTitle, playlistSearchDTO.getKeyword());

        return playlistMapper.selectPage(page, queryWrapper);
    }

    @Override
    public List<PlaylistBaseVO> recommend(Integer playlistNum){

        // 取得前20首
        List<PlaylistBaseVO> playlistBaseVOList = playlistMapper.selectTopPlaylists(20);

        // 如果需要的数量大于实际获取的数量，直接返回全部
        if (playlistNum >= playlistBaseVOList.size()) {
            return playlistBaseVOList;
        }

        // 使用 Collections.shuffle 随机打乱列表顺序
        Collections.shuffle(playlistBaseVOList);

        // 返回打乱后前 playlistNum 条
        return playlistBaseVOList.subList(0, playlistNum);

    }
}
