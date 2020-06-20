package com.example.ai_smile.test;

import com.example.ai_smile.http.UseCase;
import java.util.List;
import okhttp3.MultipartBody;

public class RecordRequestValue implements UseCase.RequestValues {

    private int errorResources;
    private String id;
    private String macAddress;

    public void setErrorResources(int errorResources) {
        this.errorResources = errorResources;
    }

    @Override
    public boolean checkInput() {
        return true;
    }

    @Override
    public int getErrorStringRes() {
        return errorResources;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }
}