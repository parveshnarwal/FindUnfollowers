package com.narwal.parvesh.findunfollowers;

/**
 * Created by Parvesh on 01-Dec-17.
 */

public class NFB_User {

    private Long user_id;
    private String user_name;
    private String screen_name;
    private String profile_pic_url;

    public NFB_User() {

    }




    public NFB_User(Long user_id, String user_name, String screen_name, String profile_pic_url) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.screen_name = screen_name;
        this.profile_pic_url = profile_pic_url;
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getScreen_name() {
        return screen_name;
    }

    public void setScreen_name(String screen_name) {
        this.screen_name = screen_name;
    }

    public String getProfile_pic_url() {
        return profile_pic_url;
    }

    public void setProfile_pic_url(String profile_pic_url) {
        this.profile_pic_url = profile_pic_url;
    }
}
