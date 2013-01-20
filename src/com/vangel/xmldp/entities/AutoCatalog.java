package com.vangel.xmldp.entities;

/**
 * @author VAngeL
 * date: 20.01.13
 */
public class AutoCatalog {
    private Long id;

    private String creationDate;

    private String host;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }
}
