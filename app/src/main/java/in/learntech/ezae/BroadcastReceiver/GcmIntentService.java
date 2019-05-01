package in.learntech.ezae.BroadcastReceiver;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Date;

import in.learntech.ezae.LoginActivity;
import in.learntech.ezae.MyAchievements;
import in.learntech.ezae.NotificationActivity;
import in.learntech.ezae.UserTrainingActivity;
import in.learntech.ezae.utils.DateUtil;
import in.learntech.ezae.utils.PreferencesUtil;
import in.learntech.ezae.utils.StringConstants;
import in.learntech.ezae.Managers.UserMgr;
import in.learntech.ezae.R;
import in.learntech.ezae.messages.MessageChatActivity;
import in.learntech.ezae.messages.MessageModel;


public class GcmIntentService extends IntentService {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    public GcmIntentService() {
        super("GcmIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        PreferencesUtil preferencesUtil = PreferencesUtil.getInstance(getApplicationContext());
        mNotificationManager = (NotificationManager)
            this.getSystemService(Context.NOTIFICATION_SERVICE);
        String jsonString = intent.getExtras().getString("message");
        String title = "Right";
        String description = "";
        String action = "";
        Intent newIntent = new Intent(this,LoginActivity.class);
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            title = jsonObject.getString("title");
            description = jsonObject.getString("description");
            Integer entitySeq = jsonObject.getInt("entitySeq");
            String entityType = jsonObject.getString("entityType");

            UserMgr mUserManager = UserMgr.getInstance(getApplicationContext());
            if(!mUserManager.isUserLoggedIn()){
                preferencesUtil.setNotificationState(true);
                preferencesUtil.setNotificationData(jsonObject);
            }else{
                if(entityType.equals("module")){
                    String lpSeq = jsonObject.getString("lpSeq");
                    newIntent = new Intent(this,UserTrainingActivity.class);
                    int learningPlanSeq = 0;
                    if(lpSeq != null && !lpSeq.isEmpty() && !lpSeq .equals("null")){
                        learningPlanSeq = Integer.parseInt(lpSeq);
                    }
                    newIntent.putExtra(StringConstants.LP_SEQ,learningPlanSeq);
                    newIntent.putExtra(StringConstants.MODULE_SEQ,entitySeq);
                }else if(entityType.equals("badge")){
                    newIntent = new Intent(this,MyAchievements.class);
                } else if(entityType.equals("chatroom") || entityType.equals("classroom") ){
                    newIntent = new Intent(this,NotificationActivity.class);
                }
                else{
                    String currentActivity = preferencesUtil.getCurrentActivityName();
                    if(currentActivity != null && currentActivity.equals(StringConstants.MESSAGE_CHAT_ACTIVITY)){
                        MessageChatActivity activity = (MessageChatActivity) PreferencesUtil.getCurrentActivity();
                        JSONArray jsonArray = new JSONArray();
                        JSONObject messageJson = new JSONObject();
                        messageJson.put("seq",0);
                        messageJson.put("dated", DateUtil.dateToString(new Date()));
                        messageJson.put("messagetext",jsonObject.getString("description"));
                        messageJson.put("fromuserseq",entitySeq);
                        jsonArray.put(messageJson);
                        activity.addReceivedMessage(jsonArray);
                        return;
                    }
                    newIntent = new Intent(this,MessageChatActivity.class);
                    String fromUserName = jsonObject.getString("fromUserName");
                    MessageModel mm = new MessageModel();
                    mm.setChattingUser(fromUserName);
                    mm.setChattingUserSeq(entitySeq);
                    mm.setChattingUserType(entityType);
                    newIntent.putExtra("messageModel",mm);
                }
            }
        }catch (Exception e){
            Log.e("GcmIntentService",e.getMessage());
        }

        newIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                newIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_learntech);
        NotificationCompat.Builder mBuilder =
            new NotificationCompat.Builder(this)
                    //.setColor(Color.parseColor("#ff5031"))
                    .setSmallIcon(R.mipmap.ic_launcher_learntech)
                    .setLargeIcon(icon)
                    .setContentTitle(title)
                    .setContentText(description)
                    .setSound(uri);

        mBuilder.setContentIntent(contentIntent);
        Notification n = mBuilder.build();
        mNotificationManager.notify(NOTIFICATION_ID, n);

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }
}

