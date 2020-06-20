package com.example.ai_smile.data.bean;

import java.io.Serializable;

/**
 * Created by louis on 2020/5/24.
 */
public class UserInfo implements Serializable {

    private int userid;
    private String username;

    private int status;//0:待审核 1:已通过 2:未通过 3:禁用
    private int deptid;
    private String deptname;
    private String companyname;
    private String jituanname;
    private String job;
    private String birthday;
    private String sex;
    private String headpath;
    private int isedit;//0:可编辑  1:不可编辑
    private int isset;//0:未设置过密码  1:已设置过密码
    private int isinfo;//资料是否已完善  0:未完善  1:已完善
    private String mobile;

    //共用本实体Bean, 后续接口返回字段
    private String name;

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDeptname() {
        return deptname;
    }

    public void setDeptname(String deptname) {
        this.deptname = deptname;
    }

    public String getConpanyname() {
        return companyname;
    }

    public void setConpanyname(String conpanyname) {
        this.companyname = conpanyname;
    }

    public String getJituanname() {
        return jituanname;
    }

    public void setJituanname(String jituanname) {
        this.jituanname = jituanname;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getHeadpath() {
        return headpath;
    }

    public void setHeadpath(String headpath) {
        this.headpath = headpath;
    }

    public int getIsedit() {
        return isedit;
    }

    public void setIsedit(int isedit) {
        this.isedit = isedit;
    }

    public int getIsinfo() {
        return isinfo;
    }

    public void setIsinfo(int isinfo) {
        this.isinfo = isinfo;
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDeptid() {
        return deptid;
    }

    public void setDeptid(int deptid) {
        this.deptid = deptid;
    }

    public int getIsset() {
        return isset;
    }

    public void setIsset(int isset) {
        this.isset = isset;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "userid=" + userid +
                ", username='" + username + '\'' +
                ", status=" + status +
                ", deptid=" + deptid +
                ", deptname='" + deptname + '\'' +
                ", companyname='" + companyname + '\'' +
                ", jituanname='" + jituanname + '\'' +
                ", job='" + job + '\'' +
                ", birthday='" + birthday + '\'' +
                ", sex='" + sex + '\'' +
                ", headpath='" + headpath + '\'' +
                ", isedit=" + isedit +
                ", isset=" + isset +
                ", isinfo=" + isinfo +
                ", mobile='" + mobile + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
