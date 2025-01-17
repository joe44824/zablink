package com.zabbix.zablink.model;

import lombok.Data;
import java.util.List;

@Data
public class ZabbixProblemResponse {
    private String jsonrpc;
    private List<EventResult> result;
    private int id;

    @Data
    public static class EventResult {
        private String eventid;
        private String source;
        private String object;
        private String objectid;
        private String clock;
        private String ns;
        private String r_eventid;
        private String r_clock;
        private String r_ns;
        private String correlationid;
        private String userid;
        private String name;
        private String acknowledged;
        private String severity;
        private String opdata;
        private String suppressed;
        private List<Object> urls;
    }
}
