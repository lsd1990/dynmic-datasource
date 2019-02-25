package com.lsd.test.dynmic.source.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class TenantDbConfig implements Serializable {

    private String tenantId;
    private String dbUrl;
    private String dbUser;
    private String dbPassword;
    private String dbDriver;
    private String publicKey;


}
