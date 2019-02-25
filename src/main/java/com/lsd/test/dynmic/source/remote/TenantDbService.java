package com.lsd.test.dynmic.source.remote;

import com.lsd.test.dynmic.source.dto.TenantDbConfig;

import java.util.List;

public interface TenantDbService {

    List<TenantDbConfig> listAll();

    Boolean exist(String publicKey);
}
