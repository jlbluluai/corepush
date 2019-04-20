package com.xyz.core.push.util.ftp;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class FtpConfigFactory {

    @Autowired
    private FtpConfig ftpConfig;

    private static FtpConfigFactory factory;

    @PostConstruct
    public void init() {
        factory = this;
    }

    /**
     * 获取ftp的配置
     *
     * @return
     */
    public static FtpConfig getConfig() {
        return factory.ftpConfig.getFtpConfig();
    }
}
