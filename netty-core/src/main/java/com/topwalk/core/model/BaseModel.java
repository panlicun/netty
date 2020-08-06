package com.topwalk.core.model;


import com.topwalk.core.model.enums.DataSourceEnum;

public class BaseModel {

    /**
     * 起源IP ， 这条信息的来源
     */
    private String originIp;
    /**
     * 起源端口
     */
    private int originPort;

    /**
     * 最终IP，这条消息最终要到达的地址
     */
    private String ultimateIp;
    /**
     * 最终地址
     */
    private int ultimatePort;

    private DataSourceEnum dataSource;

    public String getUltimateIp() {
        return ultimateIp;
    }

    public void setUltimateIp(String ultimateIp) {
        this.ultimateIp = ultimateIp;
    }

    public int getUltimatePort() {
        return ultimatePort;
    }

    public void setUltimatePort(int ultimatePort) {
        this.ultimatePort = ultimatePort;
    }

    public DataSourceEnum getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSourceEnum dataSource) {
        this.dataSource = dataSource;
    }

    public String getOriginIp() {
        return originIp;
    }

    public void setOriginIp(String originIp) {
        this.originIp = originIp;
    }

    public int getOriginPort() {
        return originPort;
    }

    public void setOriginPort(int originPort) {
        this.originPort = originPort;
    }
}
