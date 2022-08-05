package io.shulie.flpt.pressure.engine.plugin.jmeter.util;

import cn.hutool.core.codec.Base64;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.RSA;

/**
 * rsa签名加解密接口
 */
public class RsaUtils {

    /**
     * RSA解密
     *
     * @param data 待解密数据
     * @param privateKey 私钥
     * @return
     */
    public static String decrypt(String data, String privateKey) {
       return new RSA(Base64.decode(privateKey), null).decryptStr(data, KeyType.PrivateKey);
    }

    public static String decryptScriptContent(String data, String privateKey){
        //判断是否需要解密
        if (data != null && data.startsWith("12345678")){
            String substring = data.substring("12345678".length());
            return decrypt(substring,privateKey);
        }
        return data;
    }

}
