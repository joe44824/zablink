package com.zabbix.zablink.model;

import java.util.Date;

public class Host {
    private String name;
    private String osVersion;
    private VMStatus status;
    private String ip;
    private Date createdTime;

    public Host(String name, String osVersion, VMStatus status, String ip, Date createdTime) {
        this.name = name;
        this.osVersion = osVersion;
        this.status = status;
        this.ip = ip;
        this.createdTime = createdTime;
    }

    // Getters and Setters
}
