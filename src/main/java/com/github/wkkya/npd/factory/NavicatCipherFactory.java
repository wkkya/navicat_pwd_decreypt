package com.github.wkkya.npd.factory;


import com.github.wkkya.npd.enums.VersionEnum;
import com.github.wkkya.npd.navicat.Navicat11Cipher;
import com.github.wkkya.npd.navicat.Navicat12Cipher;
import com.github.wkkya.npd.navicat.NavicatChiper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * NavicatCipherFactory
 *
 * @author lzy
 * @date 2021/01/14 15:58
 **/
public class NavicatCipherFactory {
    /**
     * NavicatCipher缓存池
     */
    private static final Map<String, NavicatChiper> REPORT_POOL = new ConcurrentHashMap<>(0);

    static {
        REPORT_POOL.put(VersionEnum.native11.name(), new Navicat11Cipher());
        REPORT_POOL.put(VersionEnum.navicat12more.name(), new Navicat12Cipher());
    }

    /**
     * 获取对应数据源
     *
     * @param type 类型
     * @return ITokenGranter
     */
    public static NavicatChiper get(String type) {
        NavicatChiper chiper = REPORT_POOL.get(type);
        if (chiper == null) {
            try {
                throw new ClassNotFoundException("no NavicatCipher was found");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        } else {
            return chiper;
        }
    }
}
