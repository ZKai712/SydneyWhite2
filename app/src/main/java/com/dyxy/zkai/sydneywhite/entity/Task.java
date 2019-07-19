package com.dyxy.zkai.sydneywhite.entity;

public class Task {
    private String id;
    private String stuName;
    private String stuIdcard;
    private String faculty;
    private String sclass;
    private String register;
    private String bindClass;
    private String payQuilt;
    private String mealCard;
    private String getQuilt;
    private String bindDorm;
    private String payTuition;
    private String photo;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStuName() {
        return stuName;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
    }

    public String getStuIdcard() {
        return stuIdcard;
    }

    public void setStuIdcard(String stuIdcard) {
        this.stuIdcard = stuIdcard;
    }

    public String getFaculty() {
        return faculty;
    }

    public void setFaculty(String faculty) {
        this.faculty = faculty;
    }

    public String getSclass() {
        return sclass;
    }

    public void setSclass(String sclass) {
        this.sclass = sclass;
    }

    public String getRegister() {
        return register;
    }

    public void setRegister(String register) {
        this.register = register;
    }

    public String getBindClass() {
        return bindClass;
    }

    public void setBindClass(String bindClass) {
        this.bindClass = bindClass;
    }

    public String getPayQuilt() {
        return payQuilt;
    }

    public void setPayQuilt(String payQuilt) {
        this.payQuilt = payQuilt;
    }

    public String getMealCard() {
        return mealCard;
    }

    public void setMealCard(String mealCard) {
        this.mealCard = mealCard;
    }

    public String getGetQuilt() {
        return getQuilt;
    }

    public void setGetQuilt(String getQuilt) {
        this.getQuilt = getQuilt;
    }

    public String getBindDorm() {
        return bindDorm;
    }

    public void setBindDorm(String bindDorm) {
        this.bindDorm = bindDorm;
    }

    public String getPayTuition() {
        return payTuition;
    }

    public void setPayTuition(String payTuition) {
        this.payTuition = payTuition;
    }


    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id='" + id + '\'' +
                ", stuName='" + stuName + '\'' +
                ", stuIdcard='" + stuIdcard + '\'' +
                ", faculty='" + faculty + '\'' +
                ", sclass='" + sclass + '\'' +
                ", register='" + register + '\'' +
                ", bindClass='" + bindClass + '\'' +
                ", payQuilt='" + payQuilt + '\'' +
                ", mealCard='" + mealCard + '\'' +
                ", getQuilt='" + getQuilt + '\'' +
                ", bindDorm='" + bindDorm + '\'' +
                ", payTuition='" + payTuition + '\'' +
                ", photo='" + photo + '\'' +
                '}';
    }
}
