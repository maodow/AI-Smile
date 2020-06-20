package com.example.ai_smile.data.bean;

import java.io.Serializable;

public class TestResultBean implements Serializable {

    private String key;
    private String value;
    private String image;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "TestResultBean{" +
                "key='" + key + '\'' +
                ", value='" + value + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
