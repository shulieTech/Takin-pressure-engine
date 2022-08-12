package io.shulie.flpt.pressure.engine.plugin.jmeter.util;

import java.util.Map;
import java.security.*;
import java.nio.charset.StandardCharsets;
import java.security.spec.X509EncodedKeySpec;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.Cipher;
import javax.crypto.BadPaddingException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.IllegalBlockSizeException;

import lombok.extern.slf4j.Slf4j;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.CryptoException;
import cn.hutool.core.text.CharSequenceUtil;

/**
 * 文件加密解密操作
 */
@Slf4j
@SuppressWarnings("unused")
public class FileEncoder {

    // copy from 安全中心；io.shulie.takin.transform.runtime.matcher.DefaultEncoderMatcher#TAKIN
    private static final String TAKIN = "@takin";
    private static final String ALGORITHM = "RSA/None/OAEPWITHSHA-256ANDMGF1PADDING";

    private FileEncoder() {}

    /**
     * 解密并覆盖文件内容
     *
     * @param filePath 文件路径
     */
    public static void decodeOverride(String filePath, Map<Integer, String> privateKeyMap) {
        decodeFile(filePath, filePath, privateKeyMap);
    }

    public static void decodeFile(String filePath, String resultPath, Map<Integer, String> privateKeyMap) {
        // 解密文件内容
        String fileContent = decode(filePath, privateKeyMap);
        // 写入新文件
        FileUtil.writeUtf8String(fileContent, resultPath);
    }

    public static String decode(String filePath, Map<Integer, String> privateKeyMap) {
        String fileContent = FileUtil.readString(filePath, StandardCharsets.UTF_8);
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
        try {
            byte[] bytes = decrypt(loadPrivateKeyByStr(privateKey), encryptor.getBytes(StandardCharsets.UTF_8));
            return Base64.encode(bytes);
        } catch (RuntimeException t) {
            return encryptor;
        }
    }

    private static byte[] decrypt(RSAPrivateKey privateKey, byte[] plainTextData)
        throws CryptoException {
        if (privateKey == null) {
            throw new CryptoException("解密私钥为空, 请设置");
        }
        Cipher cipher;
        try {
            // 使用默认RSA
            cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(plainTextData);
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException("无此加密算法");
        } catch (NoSuchPaddingException e) {
            log.error("FileEncoder#decrypt", e);
            return new byte[0];
        } catch (InvalidKeyException e) {
            throw new CryptoException("解密私钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            throw new CryptoException("密文长度非法");
        } catch (BadPaddingException e) {
            throw new CryptoException("密文数据已损坏");
        }
    }

    /**
     * 从字符串中加载私钥
     *
     * @param privateKeyStr 私钥数据字符串
     * @throws CryptoException 加载私钥时产生的异常
     */
    public static RSAPrivateKey loadPrivateKeyByStr(String privateKeyStr)
        throws CryptoException {
        try {
            byte[] buffer = Base64.decode(privateKeyStr);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return (RSAPrivateKey)keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new CryptoException("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new CryptoException("私钥非法");
        } catch (NullPointerException e) {
            throw new CryptoException("私钥数据为空");
        }
    }

}
