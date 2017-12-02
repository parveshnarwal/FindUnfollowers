package com.narwal.parvesh.findunfollowers;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.List;

import twitter4j.IDs;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;


/**
 * Created by Parvesh on 24-Dec-16.
 */

public class NonFollowers extends Activity implements SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

    private GridView gridView;
    private TextView loadingText;
    List<String> pic_urls = new ArrayList<>();
    List<String> nfb_screen = new ArrayList<>();
    List<String> nfb_users = new ArrayList<>();
    ProgressBar progress_bar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private NonFollowersAdapter nonFollowersAdapter;
    private FloatingActionButton fab_logout,fab_show_more,fab_refresh;

    private final String PREF_DATE = "pref_cache_date";
    private final String PREF_UNFOLLOW_COUNT = "pref_unfollow_count";

    private int masterUnfollowCount = 0;
    private final int DAILY_LIMIT = 60;

    private InterstitialAd mAdOnRefresh, mAdOnUnfollow, mAdOnLand;


    private SharedPreferences sharedPreferences;

    private int UnfollowedCount = 0;

    private Boolean maxReached = false;

    int start = 0;
    int end = 0;

    public UserTwitterData userTwitterData = new UserTwitterData();

    private Twitter twitter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //full screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.non_followers);

        init_activity();
        sharedPreferences = getSharedPreferences(MainActivity.PREF_NAME, 0);
        config_cache();

        config_ads();

        twitter = GetTwitterInstance();

        GoFetchUsers(twitter);

        if(mAdOnLand.isLoaded()) mAdOnLand.show();

    }

    private void config_ads() {
        AdView adView = (AdView) findViewById(R.id.adView);

        mAdOnLand = new InterstitialAd(this);
        mAdOnLand.setAdUnitId("ca-app-pub-4327820221556313/4377841945");
        mAdOnLand.loadAd(new AdRequest.Builder().build());

        mAdOnLand.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                mAdOnLand.loadAd(new AdRequest.Builder().build());
            }
        });

        mAdOnRefresh = new InterstitialAd(this);
        mAdOnRefresh.setAdUnitId("ca-app-pub-4327820221556313/3152513895");
        mAdOnRefresh.loadAd(new AdRequest.Builder().build());

        mAdOnRefresh.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                mAdOnRefresh.loadAd(new AdRequest.Builder().build());
                startActivity(new Intent(NonFollowers.this, NonFollowers.class));
            }


        });

        mAdOnUnfollow = new InterstitialAd(this);
        mAdOnUnfollow.setAdUnitId("ca-app-pub-4327820221556313/1827043689");
        mAdOnUnfollow.loadAd(new AdRequest.Builder().build());

        mAdOnUnfollow.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                mAdOnUnfollow.loadAd(new AdRequest.Builder().build());
            }
        });


        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("8BE8614F2298107266F01F0E41BEDE27")
                .build();

        adView.loadAd(adRequest);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mAdOnLand.isLoaded()) mAdOnLand.show();
    }

    private void config_cache() {
        SharedPreferences.Editor e = sharedPreferences.edit();
        String today = android.text.format.DateFormat.format("yyyy-MM-dd", new java.util.Date()).toString();


        if (!sharedPreferences.contains(PREF_DATE)) {
            e.putString(PREF_DATE, today);
        } else {
            String saved = sharedPreferences.getString(PREF_DATE, "");

            if (!today.equals(saved)) {
                //delete cache
                DatabaseHandler handler = new DatabaseHandler(this);
                handler.deleteCachedData();
                e.putString(PREF_DATE, today);
            }

        }

        if (sharedPreferences.contains(PREF_UNFOLLOW_COUNT)) {
            masterUnfollowCount = sharedPreferences.getInt(PREF_UNFOLLOW_COUNT, 0);

        } else {
            e.putInt(PREF_UNFOLLOW_COUNT, masterUnfollowCount);
        }

        e.apply();
    }

    private Twitter GetTwitterInstance() {
        String consumerKey = getResources().getString(R.string.twitter_consumer_key);
        String consumerSecret = getResources().getString(R.string.twitter_consumer_secret);
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.setOAuthConsumerSecret(consumerSecret);
        configurationBuilder.setOAuthConsumerKey(consumerKey);

        String access_token = sharedPreferences.getString(MainActivity.PREF_KEY_OAUTH_TOKEN, "");
        String access_token_secret = sharedPreferences.getString(MainActivity.PREF_KEY_OAUTH_SECRET, "");

        AccessToken accessToken = new AccessToken(access_token, access_token_secret);

        twitter = new TwitterFactory(configurationBuilder.build()).getInstance(accessToken);

        return twitter;
    }

    private void GoFetchUsers(Twitter twitter) {
        new FetchUsers().execute(twitter);
    }


    private void GoFetchUnfollowersData() {
        start = end;

        if (start != 0 && start < userTwitterData.getNot_following_back_ids().size() - 1) {
            start++;
        }

        end = start + 9;

        if (userTwitterData.getNot_following_back_ids().size() - 1 <= end) {
            end = userTwitterData.getNot_following_back_ids().size() - 1;
            maxReached = true;
        }

        new FetchProfilePicURLs().execute(start, end);
    }

    private void init_activity() {
        gridView = (GridView) findViewById(R.id.gvNonFollowers);
        progress_bar = (ProgressBar) findViewById(R.id.progressBar);
        loadingText = (TextView) findViewById(R.id.loading_text);
        gridView.setVisibility(View.GONE);
        loadingText.setTypeface(FontCache.get("font/consola.ttf", this));

        fab_logout = (FloatingActionButton) findViewById(R.id.logout_fab);
        fab_show_more = (FloatingActionButton) findViewById(R.id.show_more_fab);
        fab_refresh = (FloatingActionButton) findViewById(R.id.refresh_fab);


        fab_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LogOutTwitter();
            }
        });

        fab_logout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(NonFollowers.this, "Logout", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        fab_show_more.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(NonFollowers.this, "Show More Users", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        fab_show_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!maxReached) GoFetchUnfollowersData();
                else
                    Toast.makeText(NonFollowers.this, "Nothing more to show!", Toast.LENGTH_SHORT).show();
            }
        });

        fab_refresh.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Toast.makeText(NonFollowers.this, "Refresh", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        fab_refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mAdOnRefresh.isLoaded()) mAdOnRefresh.show();

                else startActivity(new Intent(NonFollowers.this, NonFollowers.class));
            }
        });

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        gridView.setOnItemClickListener(this);
    }

    private void LogOutTwitter() {

        SharedPreferences.Editor e = sharedPreferences.edit();
        e.putString(MainActivity.PREF_KEY_OAUTH_TOKEN, "");
        e.putString(MainActivity.PREF_KEY_OAUTH_SECRET, "");
        e.putBoolean(MainActivity.PREF_KEY_TWITTER_LOGIN, false);
        e.putString(MainActivity.PREF_USER_NAME, "");
        e.putLong(MainActivity.PREF_USER_ID, 0L);

        e.apply();

        Toast.makeText(this, "You have logged out successfully!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this, MainActivity.class);

        startActivity(intent);

    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        moveTaskToBack(true);
    }


    @Override
    public void onRefresh() {
        if (!maxReached) GoFetchUnfollowersData();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

        Long userID = userTwitterData.getNot_following_back_ids().get(i);

        if (masterUnfollowCount < DAILY_LIMIT) {
            try {

                SharedPreferences.Editor e = sharedPreferences.edit();

                User u = twitter.destroyFriendship(userID);

                Toast.makeText(this, "Unfollowed @" + u.getScreenName(), Toast.LENGTH_SHORT).show();

                UnfollowedCount++;
                masterUnfollowCount++;

                e.putInt(PREF_UNFOLLOW_COUNT, masterUnfollowCount);

                e.apply();

                //remove user from list
                userTwitterData.getNot_following_back_ids().remove(i);
                userTwitterData.getNfb_screen_names().remove(i);
                userTwitterData.getNfb_usernames().remove(i);
                userTwitterData.getProfile_pic_ids().remove(i);

                //set adapter again
                nonFollowersAdapter.notifyDataSetChanged();

                if (UnfollowedCount == 5 && !maxReached) {
                    if(mAdOnUnfollow.isLoaded()) mAdOnUnfollow.show();
                    GoFetchUnfollowersData();
                    UnfollowedCount = 0;
                }

            } catch (TwitterException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "Sorry! You have used up your limit of " + DAILY_LIMIT + " unfollows/day!", Toast.LENGTH_SHORT).show();
        }

    }


    private class FetchProfilePicURLs extends AsyncTask<Integer, String, UserTwitterData> {

        @Override
        protected UserTwitterData doInBackground(Integer... params) {

            int start = params[0];
            int end = params[1];

            List<Long> non_folllowers_ids = userTwitterData.getNot_following_back_ids();

            DatabaseHandler handler = new DatabaseHandler(NonFollowers.this);


            for (int i = start; i <= end; i++) {
                //some how get the profile pics

                try {

                    NFB_User nfb_user = handler.getNFB_User(non_folllowers_ids.get(i));

                    if (nfb_user == null) {
                        User user = twitter.showUser(non_folllowers_ids.get(i));
                        nfb_user = new NFB_User(user.getId(), user.getName(), user.getScreenName(), user.getProfileImageURL().replace("_normal", ""));
                        handler.addNFB_User(nfb_user);

                        pic_urls.add(user.getProfileImageURL().replace("_normal", ""));
                        nfb_screen.add(user.getScreenName());
                        nfb_users.add(user.getName());
                    } else {
                        pic_urls.add(nfb_user.getProfile_pic_url());
                        nfb_screen.add(nfb_user.getScreen_name());
                        nfb_users.add(nfb_user.getUser_name());
                    }


                } catch (TwitterException e) {
                    e.printStackTrace();
                }

            }

            userTwitterData.setProfile_pic_ids(pic_urls);
            userTwitterData.setNfb_screen_names(nfb_screen);
            userTwitterData.setNfb_usernames(nfb_users);

            return userTwitterData;
        }

        @Override
        protected void onPostExecute(final UserTwitterData userTwitterData) {
            super.onPostExecute(userTwitterData);

            progress_bar.setVisibility(View.GONE);
            loadingText.setVisibility(View.GONE);

            //set adapter here
            if (gridView.getVisibility() == View.GONE) {
                nonFollowersAdapter = new NonFollowersAdapter(NonFollowers.this, userTwitterData);
                gridView.setAdapter(nonFollowersAdapter);
            } else {
                Toast.makeText(NonFollowers.this, "List Updated", Toast.LENGTH_SHORT).show();
                nonFollowersAdapter.notifyDataSetChanged();
            }


            gridView.setVisibility(View.VISIBLE);
            fab_logout.setVisibility(View.VISIBLE);
            fab_show_more.setVisibility(View.VISIBLE);
            fab_refresh.setVisibility(View.VISIBLE);

            if (!(userTwitterData.getNot_following_back_ids().size() > 0))
                Toast.makeText(NonFollowers.this, "Congratulations.. Everyone is following you back!", Toast.LENGTH_SHORT).show();

        }
    }


    private class FetchUsers extends AsyncTask<Twitter, String, UserTwitterData> {

        List<Long> temp_followers = new ArrayList<>();
        List<Long> temp_following = new ArrayList<>();

        Long next_cursor_followers = null;
        Long next_cursor_following = null;


        @Override
        protected UserTwitterData doInBackground(Twitter... twitter) {

            userTwitterData.setName(sharedPreferences.getString(MainActivity.PREF_USER_NAME, ""));
            userTwitterData.setUserid(sharedPreferences.getLong(MainActivity.PREF_USER_ID, 0L));

            next_cursor_followers = -1L;
            next_cursor_following = -1L;

            IDs followersIDs = null;

            IDs followingIDs = null;

            do {
                try {
                    followersIDs = twitter[0].getFollowersIDs(next_cursor_followers);
                    for (Long userID : followersIDs.getIDs()) {
                        temp_followers.add(userID);
                    }
                } catch (TwitterException e) {
                    e.printStackTrace();
                }
            }


            while ((next_cursor_followers = followersIDs.getNextCursor()) != 0 && temp_followers.size() < 100000);

            //following
            do {

                try {
                    followingIDs = twitter[0].getFriendsIDs(next_cursor_following);
                    for (Long userID : followingIDs.getIDs()) {
                        temp_following.add(userID);
                    }
                } catch (TwitterException e) {
                    e.printStackTrace();
                }

            }

            while ((next_cursor_following = followingIDs.getNextCursor()) != 0 && temp_following.size() < 100000);


            userTwitterData.setFollowers_ids(temp_followers);
            userTwitterData.setFollowing_ids(temp_following);
            List<Long> not_following_back = new ArrayList<>(temp_following);
            not_following_back.removeAll(temp_followers);
            userTwitterData.setNot_following_back_ids(not_following_back);

            return userTwitterData;

        }


        @Override
        protected void onPostExecute(UserTwitterData userTwitterData) {
            super.onPostExecute(userTwitterData);

            GoFetchUnfollowersData();

        }
    }


}

