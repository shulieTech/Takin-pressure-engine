package io.shulie.flpt.pressure.engine.plugin.jmeter.util;

import java.util.Map;

import lombok.extern.slf4j.Slf4j;

import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.CryptoException;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.core.text.CharSequenceUtil;

/**
 * 文件加密解密操作
 */
@Slf4j
@SuppressWarnings("unused")
public class FileEncoder {

    // copy from 安全中心；io.shulie.takin.transform.runtime.matcher.DefaultEncoderMatcher#TAKIN
    private static final String TAKIN = "@takin";

    private FileEncoder() {}

    public static String decode(String filePath, Map<Integer, String> privateKeyMap) {
        String fileContent = FileUtil.readUtf8String(filePath);
        //文件不以TAKIN开头，不需要进行解密
        if (!fileContent.startsWith(TAKIN)) {return fileContent;}
        //获取秘钥对应的版本，通过版本获取当前使用的秘钥
        int tagIndex = fileContent.lastIndexOf(TAKIN);
        // 获取到结尾的标记
        String tag = fileContent.substring(tagIndex);
        // 从结尾标记中获取秘钥版本
        int version = Integer.parseInt(tag.substring(TAKIN.length()));
        // 获取秘钥
        String privateKey = privateKeyMap.get(version);
        // 秘钥不存在则报错
        if (CharSequenceUtil.isBlank(privateKey)) {
            log.error("找不到文件中秘钥版本所对应的私钥,秘钥版本为:{},当前秘钥共有:{}", version, privateKeyMap.keySet());
            throw new CryptoException("找不到文件中秘钥版本所对应的私钥,秘钥版本为:" + version);
        }
        //获取加密内容
        String privateContent = fileContent.substring(tag.length(), tagIndex);
        // 进行解密操作
        return decode(privateKey, privateContent);
    }

    private static String decode(String privateKey, String encryptor) {
        return SecureUtil.rsa(privateKey, null).decryptStr(encryptor, KeyType.PrivateKey);
    }
}
