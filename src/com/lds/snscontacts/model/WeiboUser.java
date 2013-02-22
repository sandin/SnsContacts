package com.lds.snscontacts.model;

public class WeiboUser {

    private Long id;

    private String idstr;

    private String screen_name;

    private String name;

    private String location;

    private String description;

    private String profile_image_url;

    /**
     * 用户大头像地址
     */
    private String avatar_large;

    /**
     * 性别，m：男、f：女、n：未知
     */
    private String gender;

    /**
     * 用户备注信息，只有在查询用户关系时才返回此字段
     */
    private String remark;

    private Status status;

    private boolean follow_me;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdstr() {
        return idstr;
    }

    public void setIdstr(String idstr) {
        this.idstr = idstr;
    }

    public String getScreen_name() {
        return screen_name;
    }

    public void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfile_image_url() {
        return profile_image_url;
    }

    public void setProfile_image_url(String profile_image_url) {
        this.profile_image_url = profile_image_url;
    }

    public String getAvatar_large() {
        return avatar_large;
    }

    public void setAvatar_large(String avatar_large) {
        this.avatar_large = avatar_large;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public boolean isFollow_me() {
        return follow_me;
    }

    public void setFollow_me(boolean follow_me) {
        this.follow_me = follow_me;
    }

    @Override
    public String toString() {
        return "WeiboUser [id=" + id + ", idstr=" + idstr + ", screen_name="
                + screen_name + ", name=" + name + ", location=" + location
                + ", description=" + description + ", profile_image_url="
                + profile_image_url + ", avatar_large=" + avatar_large
                + ", gender=" + gender + ", remark=" + remark + ", status="
                + status + ", follow_me=" + follow_me + "]";
    }

}
