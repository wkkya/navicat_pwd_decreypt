package com.github.wkkya.npd.util;


import com.github.wkkya.npd.enums.VersionEnum;
import com.github.wkkya.npd.factory.NavicatCipherFactory;
import com.github.wkkya.npd.navicat.NavicatChiper;

/**
 * DecodeNcx 解密navicat导出的密码
 *
 * <p>
 * navicat 11采用BF(blowfish)-ECB方式，对应mode为ECB
 * navicat 12以上采用AES-128-CBC方式，对应mode为CBC
 *
 * @author lzy
 * @date 2022/01/10 15:13
 */
public class DecodeNcx {

    public static String mode;

    public DecodeNcx(String mode) {
        DecodeNcx.mode = mode;
    }

    /**
     * 根据mode进行解密
     *
     * @param str 密文
     * @return String
     */
    public String decode(String str) {
        if (StringUtil.isEmpty(str)) {
            return "";
        }
        NavicatChiper chiper = NavicatCipherFactory.get(mode);
        return chiper.decryptString(str);
    }

    public static void main(String[] args) {
        DecodeNcx decodeNcx = new DecodeNcx(VersionEnum.navicat12more.name());
        String decode = decodeNcx.decode("0411DB8F7258F048A60EE7227C1BE76A");
        System.out.println(decode);
    }
}
