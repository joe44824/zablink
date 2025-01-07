package com.zabbix.zablink.model;

import lombok.Data;

/**
 * Represents the facts of a host.
 */

@Data
public class HostFact {
    private String hostname;
    private String uptimeSeconds;
    private String biosDate;
    private String osVersion;
}
