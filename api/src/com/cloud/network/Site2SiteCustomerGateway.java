package com.cloud.network;

import java.util.Date;

import com.cloud.acl.ControlledEntity;

public interface Site2SiteCustomerGateway extends ControlledEntity {
    public long getId();
    public String getGatewayIp();
    public String getGuestCidrList();
    public String getIpsecPsk();
    public String getIkePolicy();
    public String getEspPolicy();
    public Long getLifetime();
    public Date getRemoved();
    String getName();
}
