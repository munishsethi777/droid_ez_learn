package in.learntech.ezae;

import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.HashMap;

import in.learntech.ezae.utils.ImageViewCircleTransform;
import in.learntech.ezae.utils.StringConstants;
import in.learntech.ezae.Leaderboard.LeaderBoardFragment;
import in.learntech.ezae.Leaderboard.LeaderboardModel;
import in.learntech.ezae.Managers.UserMgr;
import in.learntech.ezae.services.Interface.IServiceHandler;
import in.learntech.ezae.services.ServiceHandler;

public class MyAchievements extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener, IServiceHandler,LeaderBoardFragment.OnListFragmentInteractionListener {

    private static final String SUCCESS = "success";
    private static final String MESSAGE = "message";

    private static final String DASHBOARD_DATA = "dashboardData";
    private static final String BADGES = "badges";

    public static final String GET_MY_ACHIEVEMENTS = "myAchievements";
    public static final String GET_MY_ACHIEVEMENT_BADGES = "myAchievementMyBadges";
    public static final String GET_PROFILES_AND_MODULES = "getProfilesAndModules";
    public static final String GET_LEARNING_PLANS = "getLearningPlans";

    private UserMgr mUserMgr ;
    private ServiceHandler mAuthTask = null;
    private String mCallName;

    private TextView mScores;
    private TextView mProfileRank;
    private TextView mPoints;
    private SwipeRefreshLayout swipeLayout;
    LinearLayout mainLinearLayout;
    private Spinner spinner_profile_module;
    private Spinner spinner_learningPlans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(in.learntech.ezae.R.layout.activity_my_achievements);
        Toolbar toolbar = (Toolbar) findViewById(in.learntech.ezae.R.id.toolbar);
        toolbar.setTitle("Dashboard");
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        spinner_profile_module = (Spinner)findViewById(in.learntech.ezae.R.id.spinner_profile_module) ;
        spinner_learningPlans = (Spinner)findViewById(in.learntech.ezae.R.id.spinner_learning_plans);
        mUserMgr = UserMgr.getInstance(this);
        ConstraintLayout mainLayout = (ConstraintLayout) this.findViewById(in.learntech.ezae.R.id.myachievements_layout);
        mainLinearLayout = (LinearLayout) mainLayout.findViewById(in.learntech.ezae.R.id.mainLayout);
        initViews();
        makeServiceCalls();
        swipeLayout = (SwipeRefreshLayout) findViewById(in.learntech.ezae.R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
    }
    private void initViews(){
        mScores = (TextView) findViewById(in.learntech.ezae.R.id.score);
        mProfileRank = (TextView) findViewById(in.learntech.ezae.R.id.rank);
        mPoints = (TextView) findViewById(in.learntech.ezae.R.id.points);
    }

    private void makeServiceCalls(){
        int loggedInUserSeq = mUserMgr.getLoggedInUserSeq();
        int loggedInUserCompanySeq = mUserMgr.getLoggedInUserCompanySeq();
        Object[] args = {loggedInUserSeq,loggedInUserCompanySeq};
        String achievementsCountUrl = MessageFormat.format(StringConstants.GET_MYACHIEVEMENT_COUNTS,args);
        String myBadgesURL = MessageFormat.format(StringConstants.GET_MYACHIEVEMENT_MY_BADGES,args);
        String getProfileAndModules = MessageFormat.format(StringConstants.GET_PROFILE_AND_MODULES,args);
        String getLearningPlans = MessageFormat.format(StringConstants.GET_ALL_LEARNING_PLANS,args);
        mAuthTask = new ServiceHandler(achievementsCountUrl,this, GET_MY_ACHIEVEMENTS,this);
        if(swipeLayout != null){
            mAuthTask.setShowProgress(!swipeLayout.isRefreshing());
        }
        mAuthTask.execute();
        mAuthTask = new ServiceHandler(myBadgesURL,this, GET_MY_ACHIEVEMENT_BADGES,this);
        if(swipeLayout != null){
            mAuthTask.setShowProgress(!swipeLayout.isRefreshing());
        }
        mAuthTask.execute();
        mAuthTask = new ServiceHandler(getProfileAndModules,this, GET_PROFILES_AND_MODULES,this);
        if(swipeLayout != null){
            mAuthTask.setShowProgress(!swipeLayout.isRefreshing());
        }
        mAuthTask.execute();
        mAuthTask = new ServiceHandler(getLearningPlans,this, GET_LEARNING_PLANS,this);
        if(swipeLayout != null){
            mAuthTask.setShowProgress(!swipeLayout.isRefreshing());
        }
        mAuthTask.execute();
    }

        private void populateProfileAndModules(JSONObject response)throws Exception{
            JSONArray profileAndModuleArr = response.getJSONArray("profilesAndModules");
            String[] spinnerArray = new String[profileAndModuleArr.length()];
            final HashMap<Integer,String> spinnerMap = new HashMap<Integer, String>();
            for (int i = 0; i < profileAndModuleArr.length(); i++)
            {
                JSONObject json = profileAndModuleArr.getJSONObject(i);
                spinnerMap.put(i,json.getString("id"));
                spinnerArray[i] = json.getString("name");
            }
            ArrayAdapter<String> adapter =new ArrayAdapter<String>(getApplicationContext(), in.learntech.ezae.R.layout.spinner_item, spinnerArray);
            adapter.setDropDownViewResource(in.learntech.ezae.R.layout.spinner_item);
            spinner_profile_module.setAdapter(adapter);
            spinner_profile_module.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
                public void onItemSelected(AdapterView<?> parent, View view, int pos,
                                           long id) {
                    ((TextView) view).setTextColor(Color.BLACK);
                    String selectedId = spinnerMap.get(pos);
                    LeaderBoardFragment itemFragment = LeaderBoardFragment.newInstance(selectedId,swipeLayout);
                    setFragment(itemFragment);
                }
                public void onNothingSelected(AdapterView<?> parent) {
                }
            });
    }

    private void populateLearningPlans(JSONObject response)throws Exception{
        JSONArray learningPlans = response.getJSONArray("learningplans");
        String[] spinnerArray = new String[learningPlans.length()];
        final HashMap<Integer,String> spinnerMap = new HashMap<Integer, String>();
        for (int i = 0; i < learningPlans.length(); i++)
        {
            JSONObject json = learningPlans.getJSONObject(i);
            spinnerMap.put(i,"score_"+json.getString("id"));
            spinnerArray[i] = json.getString("title");
        }
        ArrayAdapter<String> adapter =new ArrayAdapter<String>(getApplicationContext(), in.learntech.ezae.R.layout.spinner_item, spinnerArray);
        adapter.setDropDownViewResource(in.learntech.ezae.R.layout.spinner_item);
        spinner_learningPlans.setAdapter(adapter);
        spinner_learningPlans.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int pos,
                                       long id) {
                ((TextView) view).setTextColor(Color.BLACK);
                String selectedId = spinnerMap.get(pos);
                LeaderBoardFragment itemFragment = LeaderBoardFragment.newInstance(selectedId,swipeLayout);
                setMyScoreFragment(itemFragment);
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    protected void setFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(in.learntech.ezae.R.id.layout_leaderboard,fragment).commit();
    }

    protected void setMyScoreFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        fragmentTransaction.replace(in.learntech.ezae.R.id.layout_myscore,fragment).commit();
    }

    @Override
    public void processServiceResponse(JSONObject response) {
        boolean success = false;
        String message = null;
        try{
            success = response.getInt(SUCCESS) == 1 ? true : false;
            message = response.getString(MESSAGE);
            if(success){
                if(mCallName.equals(GET_MY_ACHIEVEMENTS)){
                    populateAchievementCounts(response);
                }else if(mCallName.equals(GET_MY_ACHIEVEMENT_BADGES)){
                    populateBadges(response);
                }else if(mCallName.equals(GET_PROFILES_AND_MODULES)){
                    populateProfileAndModules(response);
                }else if(mCallName.equals(GET_LEARNING_PLANS)){
                    populateLearningPlans(response);
                }
            }
        }catch (Exception e){
            message = "Error :- " + e.getMessage();
        }
        if(message != null && !message.equals("")){
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }
    private void populateAchievementCounts(JSONObject response)throws  Exception{
        JSONObject achievementData = response.getJSONObject(DASHBOARD_DATA);
        String totalScores = achievementData.getString("totalScores");
        JSONObject pendingTraingsJSON = achievementData.getJSONObject("pendingTrainings");
        String maxScore = pendingTraingsJSON.getString("maxScore");
        String profileRank = achievementData.getString("userRank");
        String points = achievementData.getString("points");

        mScores.setText(totalScores+"/" + maxScore);
        if(profileRank == "null"){
            profileRank = "0";
        }
        mProfileRank.setText(profileRank);
        mPoints.setText(points);
    }

    private void populateBadges(JSONObject response)throws Exception{
        JSONArray badgesJSONArray = response.getJSONArray(BADGES);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(this.LAYOUT_INFLATER_SERVICE);
        LinearLayout fragmentLayout = null;
        int count = 0;
        for (int i=0; i < badgesJSONArray.length(); i++) {
            JSONObject jsonObject = badgesJSONArray.getJSONObject(i);
            String badgeNameStr = jsonObject.getString("title");
            String badgeDetailStr = jsonObject.getString("detail");
            String badgeDateStr = jsonObject.getString("date");
            String imagePathStr = jsonObject.getString("imagepath");

            fragmentLayout = (LinearLayout)inflater.inflate(in.learntech.ezae.R.layout.my_achievements_badge_fragment,null);

            TextView badgeName = (TextView) fragmentLayout.findViewById(in.learntech.ezae.R.id.badgeName);
            badgeName.setText(badgeNameStr);

            TextView badgeDetails = (TextView) fragmentLayout.findViewById(in.learntech.ezae.R.id.badgeDetails);
            badgeDetails.setText(badgeDetailStr);

            TextView badgeDate = (TextView)fragmentLayout.findViewById(in.learntech.ezae.R.id.badgeDate);
            badgeDate.setText(badgeDateStr);

            ImageView badgeImageView = (ImageView)fragmentLayout.findViewById(in.learntech.ezae.R.id.badgeImage);
            loadImageCircleRequest(badgeImageView, StringConstants.WEB_URL  + imagePathStr);

            mainLinearLayout.addView(fragmentLayout);
        }
        if(badgesJSONArray.length() == 0){
            TextView noBadgesTextView = new TextView(this);
            noBadgesTextView.setPadding(10,10,10,10);
            noBadgesTextView.setText("No Badges Found");
            noBadgesTextView.setTextAlignment(ViewGroup.TEXT_ALIGNMENT_CENTER);
            mainLinearLayout.addView(noBadgesTextView);
        }
    }

    @Override
    public void setCallName(String call) {
        mCallName = call;
    }

    private void loadImageCircleRequest(ImageView img, String url) {
        Glide.with(this)
                .load(url)
                .transform(new ImageViewCircleTransform(this))
                .into(img);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    /**
     * Called when a swipe gesture triggers a refresh.
     */
    @Override
    public void onRefresh() {
        mainLinearLayout.removeViews(4, mainLinearLayout.getChildCount() - 4);
        makeServiceCalls();
    }

    @Override
    public void onListFragmentInteraction(LeaderboardModel item) {

    }
}