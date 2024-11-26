package com.music.demo.utils;

import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.xml.sax.ContentHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class AudioUtil {

    /**
     * 获取音频文件时长（秒）
     * @param filePath 音频文件路径
     * @return 时长（秒），如果无法读取时长则返回 -1
     */
    public static Long getAudioDuration(String filePath) {
        File audioFile = new File(filePath);
        String fileExtension = getFileExtension(filePath);

        try {
            return switch (fileExtension) {
                case "mp3" -> getMp3Duration(audioFile);
                case "ogg", "wav" -> getTikaAudioDuration(audioFile);
                default -> {
                    System.err.println("不支持的文件格式：" + fileExtension);
                    yield null;
                }
            };
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取 MP3 文件时长（秒）
     */
    private static long getMp3Duration(File mp3File) throws Exception {
        AudioFile audioFile = AudioFileIO.read(mp3File);
        return audioFile.getAudioHeader().getTrackLength();
    }

    /**
     * 使用 Apache Tika 获取 OGG/WAV 文件时长（秒）
     */
    private static Long getTikaAudioDuration(File audioFile) {
        try (InputStream input = new FileInputStream(audioFile)) {
            Metadata metadata = new Metadata();
            ContentHandler handler = new BodyContentHandler();
            AutoDetectParser parser = new AutoDetectParser();
            ParseContext parseContext = new ParseContext();
            parser.parse(input, new BodyContentHandler(), metadata, new ParseContext());

            String duration = metadata.get("xmpDM:duration");
            if (duration != null) {
                return Math.round(Double.parseDouble(duration) / 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取文件扩展名
     */
    private static String getFileExtension(String filePath) {
        int dotIndex = filePath.lastIndexOf(".");
        if (dotIndex > 0 && dotIndex < filePath.length() - 1) {
            return filePath.substring(dotIndex + 1).toLowerCase();
        }
        return "";
    }
}