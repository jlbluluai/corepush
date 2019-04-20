package com.xyz.core.push.util.mail;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MailConfigFactory {

    @Autowired
    private MailConfig mailConfig;

    private static MailConfigFactory factory;

    @PostConstruct
    public void init() {
        factory = this;
    }

    public static MailConfig getConfig() {
        return factory.mailConfig.getMailConfig();
    }
}
