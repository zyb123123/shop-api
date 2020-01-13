package com.fh.bean;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

public class Category {

    private Integer id;

    private String name;

    private Integer pid;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date createDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date updateDate;

    private Integer ishot;

    public Integer getIshot() {
        return ishot;
    }

    public void setIshot(Integer ishot) {
        this.ishot = ishot;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}

