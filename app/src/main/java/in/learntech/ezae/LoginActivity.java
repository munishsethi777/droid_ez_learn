package in.learntech.ezae;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
//import com.google.android.gms.gcm.GoogleCloudMessaging;
//import com.google.android.gms.iid.InstanceID;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.MessageFormat;

import in.learntech.ezae.utils.LayoutHelper;
import in.learntech.ezae.BusinessObjects.User;
import in.learntech.ezae.Managers.UserMgr;
import in.learntech.ezae.messages.MessageChatActivity;
import in.learntech.ezae.messages.MessageModel;
import in.learntech.ezae.services.Interface.IServiceHandler;
import in.learntech.ezae.services.ServiceHandler;
import in.learntech.ezae.utils.PreferencesUtil;
import in.learntech.ezae.utils.StringConstants;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener,IServiceHandler {
    View bg;
    //Views
    private EditText mUsernameView;
    private EditText mPasswordView;
    private String mGcmid;
    private ServiceHandler mAuthTask = null;
    private UserMgr mUserMgr;
    private PreferencesUtil mPreferencesUtil;
    private int mLoggedUserInSeq;
    private String mCallName;
    private boolean hasTokenUpdated;
    private static int mIsFcm = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(in.learntech.ezae.R.layout.login_layout);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle("Sign In");
        }
        bg = findViewById(in.learntech.ezae.R.id.activity_loginsignup_style12);
        mPasswordView = (EditText) findViewById(in.learntech.ezae.R.id.password_view);
        mUsernameView = (EditText) findViewById(in.learntech.ezae.R.id.username_view);
        mUserMgr = UserMgr.getInstance(this);
        mLoggedUserInSeq = mUserMgr.getLoggedInUserSeq();
        User loggedInUser = mUserMgr.getUserByUserSeq(mLoggedUserInSeq);
        mPreferencesUtil = PreferencesUtil.getInstance(getApplicationContext());
        hasTokenUpdated = mPreferencesUtil.hasTokenUpdated();
        if(mLoggedUserInSeq > 0 && loggedInUser != null){
            updateFCMToken();
            goToDashboardActivity();
        }
        String url = BuildConfig.IMAGE_URL + "login-signup/style-12/Login-Register-12-960.jpg";

        Glide.with(this)
                .load(in.learntech.ezae.R.drawable.ecommerce17_color)
                .thumbnail(0.01f)
                .centerCrop()
                .into(new SimpleTarget<GlideDrawable>() {
                    @Override
                    public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                        bg.setBackground(resource);
                    }
                });
    }

    private void updateFCMToken(){
        if(!hasTokenUpdated) {
            try {
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (!task.isSuccessful()) {
                                    return;
                                }
                                // Get new Instance ID token
                                String token = task.getResult().getToken();
                                mGcmid = token;
                                updateFCMTokenCall();
                            }
                        });

            } catch (Exception e) {
                LayoutHelper.showToast(this, e.getMessage());
            }
        }
    }

    private String UPDATE_FCM_TOKEN_CALL = "updateFCMTokenCall";

    private void updateFCMTokenCall(){
        try {
            Object[] args = {mLoggedUserInSeq, mGcmid,mIsFcm};
            String updateTokenUrl = MessageFormat.format(StringConstants.UPDATE_FCM_TOKEN, args);
            mAuthTask = new ServiceHandler(updateTokenUrl,this, UPDATE_FCM_TOKEN_CALL,this);
            mAuthTask.execute();
        }catch (Exception e){
            LayoutHelper.showToast(this,e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(in.learntech.ezae.R.menu.loginsignup_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case in.learntech.ezae.R.id.action_search:
                Toast.makeText(this, "action search clicked!", Toast.LENGTH_SHORT).show();
                break;
            case in.learntech.ezae.R.id.action_settings:
                Toast.makeText(this, "action setting clicked!", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case in.learntech.ezae.R.id.txtForgotPassword:
                Toast.makeText(this, "Forgot Password clicked!", Toast.LENGTH_SHORT).show();
                break;
            case in.learntech.ezae.R.id.btnSignIn:
                loginWithGCMID();
                //Toast.makeText(this, "Sign In button clicked!", Toast.LENGTH_SHORT).show();
                //createUrl();
                break;
            default:
                break;
        }
    }

    private void createUrl(){
        try {
            //{"seq":400,"userSeq":15,"learningPlanSeq":0,"isTimeUp":false,"questionsResponse":{"1017":3521,"1018":3524}}
            JSONObject progressJson = new JSONObject();
            progressJson.put("seq", 400);
            progressJson.put("userSeq", 15);
            progressJson.put("learningPlanSeq", 0);
            progressJson.put("isTimeUp", false);
            JSONObject questionJson = new JSONObject();
            questionJson.put("1017", 3521);
            questionJson.put("1018", 3524);
            progressJson.put("questionsResponse", questionJson);
            String jsonArrString = progressJson.toString();
            jsonArrString = URLEncoder.encode(jsonArrString, "UTF-8");
            Toast.makeText(this,  jsonArrString, Toast.LENGTH_SHORT).show();
        }catch (Exception e){
            Toast.makeText(this,  e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void processServiceResponse(JSONObject response){
        mAuthTask = null;
        //showProgress(false);
        boolean success = false;
        String message = "Logged in successfully";
        try{
            success = response.getInt("success") == 1 ? true : false;
            message = response.getString("message");
            if(success){
                if(mCallName == UPDATE_FCM_TOKEN_CALL){
                    mPreferencesUtil.setHasTokenUpdated();
                }else{
                    mUserMgr.saveUserFromResponse(response);
                    goToDashboardActivity();
                }
            }
        }catch (Exception e){
            message = "Error :- " + e.getMessage();
        }
        LayoutHelper.showToast(this,message);
    }

    @Override
    public void setCallName(String call) {
        mCallName = call;
    }

    public void loginWithGCMID() {
      // boolean isUserAlreadyExists = mUserMgr.isUserExistsWithUsername
        //        (mUsernameView.getText().toString());
       // if(isUserAlreadyExists){
         //  attemptLogin();
        //}else {
//            new AsyncTask<Void, Void, String>() {
//               // GoogleCloudMessaging gcm;
//                String regid;
//
//                @Override
//                protected String doInBackground(Void... params) {
//                    //try {
//                     //   if (gcm == null) {
//                     //       gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
//                     //   }
//                      //  InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
//                       // regid = instanceID.getToken("834648747026",
//                               // GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
//                        // Persist the regID - no need to register again.
//                        //storeRegistrationId(getContext(), regid);
//                    //} //catch (IOException ex) {
//
//                    //    String message = ex.getMessage();
//
//                   // }"
//                    try{
//                            regid = FirebaseInstanceId.getInstance().getToken("834648747026",null);
//                    }catch (IOException ex){
//                        String message = ex.getMessage();
//                    }
//
//                    return regid;
//                }
//
//                @Override
//                protected void onPostExecute(String regId) {
//                    mGcmid = regId;
//                    attemptLogin();
//                }
//            }.execute(null, null, null);
            FirebaseInstanceId.getInstance().getInstanceId()
                    .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (!task.isSuccessful()) {
                                return;
                            }
                            // Get new Instance ID token
                            String token = task.getResult().getToken();
                            mGcmid = token;
                            attemptLogin();
                            // Log and toast
                        }
                    });

       // }
    }

    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUsernameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
         //   mPasswordView.setError(getString(R.string.error_field_required));
          //  focusView = mPasswordView;
         //   cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(in.learntech.ezae.R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            //            //            // perform the user login attempt.
            //            //            //username = "ETC_321";
            //            //            //password = "9656525263";
            try {
                username = URLEncoder.encode(username, "UTF-8");
                password = URLEncoder.encode(password, "UTF-8");
                Object[] args = {username, password, mGcmid,mIsFcm};
                String loginUrl = MessageFormat.format(StringConstants.LOGIN_URL, args);
                mAuthTask = new ServiceHandler(loginUrl, this, this);
                mAuthTask.execute();
            }catch (Exception e){
                LayoutHelper.showToast(this,e.getMessage());
            }
        }
    }



    private void goToDashboardActivity(){
        boolean isNotificationStateOn = mPreferencesUtil.isNotificationState();
        Intent intent = new Intent(this,DashboardActivity.class);
        if(isNotificationStateOn){
            Object data[] = mPreferencesUtil.getNotificationData();
            String entityType = data[1].toString();
            Integer entitySeq = Integer.parseInt(data[0].toString());
            if(entityType.equals("module")) {
                intent = new Intent(this,UserTrainingActivity.class);
                intent.putExtra(StringConstants.LP_SEQ,0);
                intent.putExtra(StringConstants.MODULE_SEQ,entitySeq);
            }else if(entityType.equals("badge")){
                intent = new Intent(this,MyAchievements.class);
            }else{
                intent = new Intent(this,MessageChatActivity.class);
                String fromUserName = data[2].toString();
                MessageModel mm = new MessageModel();
                mm.setChattingUser(fromUserName);
                mm.setChattingUserSeq(entitySeq);
                mm.setChattingUserType(entityType);
                intent.putExtra("messageModel",mm);
            }
            mPreferencesUtil.resetNotificationData();
        }
        startActivity(intent);
        finish();


    }
}
