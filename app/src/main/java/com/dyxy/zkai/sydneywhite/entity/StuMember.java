package com.dyxy.zkai.sydneywhite.entity;

public class StuMember {
    private String id;      //唯一标识符
    private String openid;      //微信唯一标识符
    private String stuName;     //学生姓名
    private String stuSex;      //学生性别
    private String stuNation;       //民族
    private String stuPolitics;     //政治面貌
    private String stuBirth;        //出生年月
    private String stuIdcard;       //身份证号
    private String stuHeadImg;      //头像
    private String stuPhone;        //手机号
    private String stuAddress;      //家庭住址
    private String faculty;        //学院
    private String sclass;      //班级
    private String classTeacher;        //班主任
    private String teacherPhone;        //班主任电话
    private String stuDorm;     //宿舍信息
    private String father;      //父亲姓名
    private String fatherPhone;    //父亲电话
    private String mother;      //母亲姓名
    private String motherPhone;     //母亲电话

    private int classSize;        //班级人数
    private String sBedNum;        //床铺号

    public StuMember() {
    }

    public StuMember(String stuName,  String sBedNum, String stuPhone,String stuAddress,String sclass) {
        this.stuName = stuName;
        this.stuAddress = stuAddress;
        this.sBedNum = sBedNum;
        this.stuPhone = stuPhone;
        this.sclass = sclass;
    }

    public StuMember(String stuName, String stuSex, String stuAddress) {
        this.stuName = stuName;
        this.stuSex = stuSex;
        this.stuAddress = stuAddress;
    }

    public String getStuName() {
        return stuName;
    }

    public void setStuName(String stuName) {
        this.stuName = stuName;
    }

    public String getStuSex() {
        return stuSex;
    }

    public void setStuSex(String stuSex) {
        this.stuSex = stuSex;
    }

    public String getStuNation() {
        return stuNation;
    }

    public void setStuNation(String stuNation) {
        this.stuNation = stuNation;
    }

    public String getStuPolitics() {
        return stuPolitics;
    }

    public void setStuPolitics(String stuPolitics) {
        this.stuPolitics = stuPolitics;
    }

    public String getStuBirth() {
        return stuBirth;
    }

    public void setStuBirth(String stuBirth) {
        this.stuBirth = stuBirth;
    }

    public String getStuIdcard() {
        return stuIdcard;
    }

    public void setStuIdcard(String stuIdcard) {
        this.stuIdcard = stuIdcard;
    }

    public String getStuPhone() {
        return stuPhone;
    }

    public void setStuPhone(String stuPhone) {
        this.stuPhone = stuPhone;
    }

    public String getStuAddress() {
        return stuAddress;
    }

    public void setStuAddress(String stuAddress) {
        this.stuAddress = stuAddress;
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

    public String getClassTeacher() {
        return classTeacher;
    }

    public void setClassTeacher(String classTeacher) {
        this.classTeacher = classTeacher;
    }

    public String getTeacherPhone() {
        return teacherPhone;
    }

    public void setTeacherPhone(String teacherPhone) {
        this.teacherPhone = teacherPhone;
    }

    public String getStuDorm() {
        return stuDorm;
    }

    public void setStuDorm(String stuDorm) {
        this.stuDorm = stuDorm;
    }

    public String getFather() {
        return father;
    }

    public void setFather(String father) {
        this.father = father;
    }

    public String getFatherPhone() {
        return fatherPhone;
    }

    public void setFatherPhone(String fatherPhone) {
        this.fatherPhone = fatherPhone;
    }

    public String getMother() {
        return mother;
    }

    public void setMother(String mother) {
        this.mother = mother;
    }

    public String getMotherPhone() {
        return motherPhone;
    }

    public void setMotherPhone(String motherPhone) {
        this.motherPhone = motherPhone;
    }

    public int getClassSize() {
        return classSize;
    }

    public void setClassSize(int classSize) {
        this.classSize = classSize;
    }

    public String getsBedNum() {
        return sBedNum;
    }

    public void setsBedNum(String sBedNum) {
        this.sBedNum = sBedNum;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getStuHeadImg() {
        return stuHeadImg;
    }

    public void setStuHeadImg(String stuHeadImg) {
        this.stuHeadImg = stuHeadImg;
    }

    @Override
    public String toString() {
        return "StuMember{" +
                "id='" + id + '\'' +
                ", openid='" + openid + '\'' +
                ", stuName='" + stuName + '\'' +
                ", stuSex='" + stuSex + '\'' +
                ", stuNation='" + stuNation + '\'' +
                ", stuPolitics='" + stuPolitics + '\'' +
                ", stuBirth='" + stuBirth + '\'' +
                ", stuIdcard='" + stuIdcard + '\'' +
                ", stuPhone='" + stuPhone + '\'' +
                ", stuAddress='" + stuAddress + '\'' +
                ", faculty='" + faculty + '\'' +
                ", sclass='" + sclass + '\'' +
                ", classTeacher='" + classTeacher + '\'' +
                ", teacherPhone='" + teacherPhone + '\'' +
                ", stuDorm='" + stuDorm + '\'' +
                ", father='" + father + '\'' +
                ", fatherPhone='" + fatherPhone + '\'' +
                ", mother='" + mother + '\'' +
                ", motherPhone='" + motherPhone + '\'' +
                ", classSize=" + classSize +
                ", sBedNum=" + sBedNum +
                '}';
    }
}
