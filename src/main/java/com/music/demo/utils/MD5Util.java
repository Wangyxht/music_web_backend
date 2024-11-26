package com.music.demo.utils;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {

    /**
     * 生成字符串的 MD5 哈希值
     *
     * @param input 需要生成 MD5 的字符串
     * @return 返回 MD5 哈希后的字符串
     */
    public static String generateMD5(String input) {
        // 使用 Apache Commons Codec 的 DigestUtils 直接生成 MD5 哈希
        return DigestUtils.md5Hex(input);
    }

}