package com.example.ai_smile.test;

import com.example.ai_smile.http.UseCase;

public class FaceTestRequestValue implements UseCase.RequestValues {

    private int errorMessageRes;
    private String macAddress;
    private String filePath;


    public String getMacAddress() {
        return macAddress;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public int getErrorStringRes() {
        return errorMessageRes;
    }

    public void setErrorMessageRes(int errorMessageRes) {
        this.errorMessageRes = errorMessageRes;
    }

    @Override
    public boolean checkInput() {
        return true;
    }

}