package com.example.ai_smile.http;


/**
 * Created by Administrator on 2016/11/11.
 */

public class ServerErrorResponse {

    private String code;
    private String description;
    private String semantic;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSemantic() {
        return semantic;
    }

    public void setSemantic(String semantic) {
        this.semantic = semantic;
    }
}
