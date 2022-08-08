package io.shulie.flpt.pressure.engine.plugin.jmeter.util;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import sun.misc.BASE64Decoder;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Map;
import java.util.Objects;

/**
 * 文件加密解密操作
 */
@Slf4j
public class FileEncoder {

    // copy from 安全中心；io.shulie.takin.transform.runtime.matcher.DefaultEncoderMatcher#TAKIN
    private static final String TAKIN = "@takin";
    private static final String ALGORITHM = "RSA";

    public static String decode(File file, Map<Integer,String> privateKeyMap) {
        String fileContent = FileUtil.readString(file, StandardCharsets.UTF_8);
        //文件不以TAKIN开头，不需要进行解密
        if (!fileContent.startsWith(TAKIN)) {
            return fileContent;
        }
        //获取秘钥对应的版本，通过版本获取当前使用的秘钥
        String substring = fileContent.substring(fileContent.lastIndexOf(TAKIN));
        String version = substring.substring(TAKIN.length());
        int parseInt = Integer.parseInt(version);
        String privateKey = privateKeyMap.get(parseInt);
        if (StringUtils.isBlank(privateKey)) {
            log.error("找不到文件中秘钥版本所对应的私钥,秘钥版本为:{},当前秘钥共有:{}", version, privateKeyMap.keySet());
            throw new RuntimeException("找不到文件中秘钥版本所对应的私钥,秘钥版本为:" + version);
        }
        //获取加密内容
        String pre = substring + version;
        String privateContent = fileContent.substring(pre.length(), fileContent.lastIndexOf(pre));
        return decode(privateKey,privateContent);
    }



    private static String decode(String privateKey, String encryptor) {
        try {
            byte[] bytes = decrypt(loadPrivateKeyByStr(privateKey), encryptor.getBytes(StandardCharsets.UTF_8));
            return Base64.encode(bytes);
        } catch (Throwable t) {
            return encryptor;
        }
    }


    private static byte[] decrypt(RSAPrivateKey privateKey, byte[] plainTextData)
            throws Exception {
        if (privateKey == null) {
            throw new Exception("解密私钥为空, 请设置");
        }
        Cipher cipher;
        try {
            // 使用默认RSA
            cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return cipher.doFinal(plainTextData);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此加密算法");
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
            return null;
        } catch (InvalidKeyException e) {
            throw new Exception("解密私钥非法,请检查");
        } catch (IllegalBlockSizeException e) {
            throw new Exception("密文长度非法");
        } catch (BadPaddingException e) {
            throw new Exception("密文数据已损坏");
        }
    }

    private static final BASE64Decoder base64Decoder = new BASE64Decoder();

    /**
     * 从字符串中加载私钥
     *
     * @param privateKeyStr 私钥数据字符串
     * @throws Exception 加载私钥时产生的异常
     */
    public static RSAPrivateKey loadPrivateKeyByStr(String privateKeyStr)
            throws Exception {
        try {
            byte[] buffer = base64Decoder.decodeBuffer(privateKeyStr);
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(buffer);
            return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
        } catch (NoSuchAlgorithmException e) {
            throw new Exception("无此算法");
        } catch (InvalidKeySpecException e) {
            throw new Exception("私钥非法");
        } catch (NullPointerException e) {
            throw new Exception("私钥数据为空");
        }
    }
}
