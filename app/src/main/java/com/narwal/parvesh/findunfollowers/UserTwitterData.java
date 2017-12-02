package com.narwal.parvesh.findunfollowers;

import java.util.Collection;
import java.util.List;

/**
 * Created by Parvesh on 11-Nov-17.
 */

public class UserTwitterData {

    private Long userid;
    private List<Long> followers_ids;
    private List<Long> following_ids;
    private List<Long> not_following_back_ids;
    private List<String> profile_pic_ids;
    private List<String> nfb_screen_names;
    private List<String> nfb_usernames;
    private String name;



    public Long getUserid() {
        return userid;
    }

    public void setUserid(Long userid) {
        this.userid = userid;
    }

    public List<Long> getFollowers_ids() {
        return followers_ids;
    }

    public void setFollowers_ids(List<Long> followers_ids) {
        this.followers_ids = followers_ids;
    }

    public List<Long> getFollowing_ids() {
        return following_ids;
    }

    public void setFollowing_ids(List<Long> following_ids) {
        this.following_ids = following_ids;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public List<Long> getNot_following_back_ids() {
        return not_following_back_ids;
    }

    public void setNot_following_back_ids(List<Long> not_following_back_ids) {
        this.not_following_back_ids = not_following_back_ids;
    }

    public List<String> getProfile_pic_ids() {
        return profile_pic_ids;
    }

    public void setProfile_pic_ids(List<String> profile_pic_ids) {
        this.profile_pic_ids = profile_pic_ids;
    }

    public List<String> getNfb_screen_names() {
        return nfb_screen_names;
    }

    public void setNfb_screen_names(List<String> nfb_screen_names) {
        this.nfb_screen_names = nfb_screen_names;
    }

    public List<String> getNfb_usernames() {
        return nfb_usernames;
    }

    public void setNfb_usernames(List<String> nfb_usernames) {
        this.nfb_usernames = nfb_usernames;
    }
}
