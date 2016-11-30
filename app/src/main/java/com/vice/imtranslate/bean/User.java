package com.vice.imtranslate.bean;

/**
 * Created by vice on 2016/9/9.
 */
public class User {
    private String userId;
    private String nickname;
    private String avatar;

    public User(String userId, String nickname, String avatar) {
        this.userId = userId;
        this.nickname = nickname;
        this.avatar = avatar;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
