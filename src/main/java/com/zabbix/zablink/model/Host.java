package com.zabbix.zablink.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import com.zabbix.zablink.enums.VMStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Host {
    private String name;
    private String osVersion;
    private VMStatus status;
    private String ip;
    private LocalDateTime createdTime;
}
