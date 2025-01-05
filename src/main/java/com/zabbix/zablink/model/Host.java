package com.zabbix.zablink.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Host {
    private String name;
    private String osVersion;
    private VMStatus status;
    private String ip;
    private Date createdTime;
}
