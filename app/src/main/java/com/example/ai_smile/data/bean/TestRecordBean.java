package com.example.ai_smile.data.bean;

import java.io.Serializable;

public class TestRecordBean implements Serializable {

    private String id;
    private String createtime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    @Override
    public String toString() {
        return "TestRecordBean{" +
                "id='" + id + '\'' +
                ", createtime='" + createtime + '\'' +
                '}';
    }
}
