package in.learntech.ezae;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tubb.smrv.SwipeHorizontalMenuLayout;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.Date;

import in.learntech.ezae.Events.MainActivity;
import in.learntech.ezae.utils.DateUtil;
import in.learntech.ezae.utils.LayoutHelper;
import in.learntech.ezae.utils.StringConstants;
import in.learntech.ezae.Chatroom.ChatRoomChatActivity;
import in.learntech.ezae.Chatroom.ChatRoomModel;
import in.learntech.ezae.services.Interface.IServiceHandler;
import in.learntech.ezae.services.ServiceHandler;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link NotificationsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link NotificationsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationsFragment extends Fragment implements IServiceHandler{

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_USER_SEQ = "userSeq";
    private static final String ARG_COMPANY_SEQ = "companySeq";
    public static final String UN_NOMINATED_EVENT = "unNominatedEvent";
    public static final String CURRENTLY_ACTIVE_EVENT = "currentlyActiveEvent";
    private static final String NOMINATE_TRAINING = "nominateTraining";
    public static final String GET_NOTIFICATIONS = "getNotifications";
    public static final String DELETE_NOTIFICATION = "deleteNotification";
    private ServiceHandler mAuthTask = null;
    private static final String SUCCESS = "success";
    private static final String MESSAGE = "message";
    private int mUserSeq;
    private int mCompanySeq;
    private LinearLayout mChildItemsLayout;
    private LinearLayout mNotesLayout;
    private LayoutInflater mInflater;
    private LayoutHelper layoutHelper;
    private ViewGroup mContainer;
    private OnFragmentInteractionListener mListener;
    private String mCallName;
    private NotificationActivity parentActivity;
    public NotificationsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userSeq Parameter 1.
     * @param companySeq Parameter 2.
     * @return A new instance of fragment NotificationsFragment.
     */

    public static NotificationsFragment newInstance(int userSeq, int companySeq,NotificationActivity parent) {
        NotificationsFragment fragment = new NotificationsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_USER_SEQ, userSeq);
        args.putInt(ARG_COMPANY_SEQ, companySeq);
        fragment.setArguments(args);
        fragment.parentActivity = parent;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserSeq = getArguments().getInt(ARG_USER_SEQ);
            mCompanySeq = getArguments().getInt(ARG_COMPANY_SEQ);
        }
        layoutHelper =  new LayoutHelper(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mInflater = inflater;
        mContainer = container;
        //mFragmentLayout =  (LinearLayout) inflater.inflate(R.layout.dashboard_notification_fragment, container, false);
        ConstraintLayout mFragmentLayout =  (ConstraintLayout) inflater.inflate(in.learntech.ezae.R.layout.content_notification_child, container, false);
        mNotesLayout = (LinearLayout) mFragmentLayout.findViewById(in.learntech.ezae.R.id.notesLayout);
        executeGetNotificationCall();
        return mFragmentLayout;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    public void executeGetNotificationCall(){
        Object[] args = {mUserSeq,mCompanySeq};
        String notificationUrl = MessageFormat.format(StringConstants.GET_NOTIFICATIONS_NEW,args);
        mAuthTask = new ServiceHandler(notificationUrl,this, GET_NOTIFICATIONS,getActivity());
        if(parentActivity.swipeLayout != null){
            mAuthTask.setShowProgress(!parentActivity.swipeLayout.isRefreshing());
        }
        mAuthTask.execute();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void processServiceResponse(JSONObject response) {
        mAuthTask = null;
        //showProgress(false);
        boolean success = false;
        String message = null;
        try{
            success = response.getInt(SUCCESS) == 1 ? true : false;
            message = response.getString(MESSAGE);
            if(success){
                if (mCallName != null && mCallName.equals(NOMINATE_TRAINING)) {
                    Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                    startActivity(getActivity().getIntent());
                }else if (mCallName != null && mCallName.equals(GET_NOTIFICATIONS)) {
//                    JSONArray notificationJsonArr = response.getJSONArray("notifications");
//                    for (int i = 0; i < notificationJsonArr.length(); i++) {
//                        JSONObject jsonObject = notificationJsonArr.getJSONObject(i);
//                        int seq = jsonObject.getInt("seq");
//                        String notificationTitle = jsonObject.getString("title");
//                        String type = jsonObject.getString("type");
//                        String status = jsonObject.getString("status");
//                        String eventType = jsonObject.getString("eventtype");
//                        String from = jsonObject.getString("from");
//                        Date fromDate = DateUtil.stringToDate(from);
//                        from = DateUtil.dateToFormat(fromDate,DateUtil.format);
//                        notificationTitle  += "\n on " + from;
//                        String buttonTitle = "Nominate";
//                        LinearLayout childLayout = (LinearLayout) mInflater.inflate(R.layout.notifications_child_items, mContainer, false);
//                        TextView textView_nominated = (TextView)childLayout.findViewById(R.id.textView_nominated);
//                        ImageView imageView_notification = (ImageView)childLayout.findViewById(R.id.imageView_notifications);
//                        if (type.equals(CURRENTLY_ACTIVE_EVENT)) {
//                            if (eventType.equals("chatroom")) {
//                                buttonTitle = "Chatroom";
//                                layoutHelper.loadImage(imageView_notification,"icons8_communication_60");
//
//                            } else if (eventType.equals("classroom")) {
//                                buttonTitle = "Classroom";
//                            }
//                        }else{
//                            if(status.equals("unapproved")){
//                                buttonTitle = "Nominated" ;
//                            }
//                        }
//
//                        textView_nominated.setText(buttonTitle);
//                        textView_nominated.setVisibility(View.VISIBLE);
//                        TextView textView = (TextView) childLayout.findViewById(R.id.notification_title);
//                        textView.setText(notificationTitle);
//                        Button button = (Button) childLayout.findViewById(R.id.notification_button);
//                        ImageView imageView_button = (ImageView)childLayout.findViewById(R.id.imageView_chatroom);
//                        button.setText(buttonTitle);
//                        //if(!buttonTitle.equals("Nominated")){
//                            button.setOnClickListener(new startChat(seq, notificationTitle, null, buttonTitle,fromDate));
//                            imageView_button.setOnClickListener(new startChat(seq, notificationTitle, null, buttonTitle,fromDate));
//                        //}
//                        textView.setText(notificationTitle);
//                        mNotesLayout.addView(childLayout);
//                    }
                    populateNotifications(response);
                    if(parentActivity.swipeLayout != null) {
                        parentActivity.swipeLayout.setRefreshing(false);
                    }
                }else if(mCallName != null && mCallName.equals(DELETE_NOTIFICATION)){
                    int deletedSeq = response.getInt("seq");
                    SwipeHorizontalMenuLayout layout = (SwipeHorizontalMenuLayout) getActivity().findViewById(deletedSeq);
                    mNotesLayout.removeView(layout);
                    //LayoutHelper.showToast(getActivity(), "Removed Successfully");
                }
            }
        }catch (Exception e){
            message = "Error :- " + e.getMessage();
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private void populateNotifications(JSONObject response)throws Exception{
        JSONArray notificationJsonArr = response.getJSONArray("notifications");
        int unReadCount = 0;
        for (int i = 0; i < notificationJsonArr.length(); i++) {
            SwipeHorizontalMenuLayout childLayout = (SwipeHorizontalMenuLayout) mInflater.inflate(in.learntech.ezae.R.layout.notifications_child_items, mContainer, false);
            LinearLayout linearLayout = (LinearLayout)childLayout.findViewById(in.learntech.ezae.R.id.colorMask);
            TextView textView_nominated = (TextView)childLayout.findViewById(in.learntech.ezae.R.id.textView_nominated);
            ImageView imageView_button = (ImageView)childLayout.findViewById(in.learntech.ezae.R.id.imageView_chatroom);
            ImageView imageView_notification = (ImageView)childLayout.findViewById(in.learntech.ezae.R.id.imageView_notifications);
            JSONObject jsonObject = notificationJsonArr.getJSONObject(i);
            int seq = jsonObject.getInt("seq");
            childLayout.setId(seq);
            String notificationTitle = jsonObject.getString("title");
            int isRead = jsonObject.getInt("isread");

            String entityType = jsonObject.getString("entitytype");
            String notificationType = jsonObject.getString("notificationtype");
            int entitySeq = jsonObject.getInt("entityseq");
            String from = jsonObject.getString("startdate");
            int lpSeq = jsonObject.getInt("lpseq");
            Date fromDate = DateUtil.stringToDate(from);
            Button button = (Button) childLayout.findViewById(in.learntech.ezae.R.id.notification_button);
            String buttonTitle = "Nominate";
            if(entityType.equals("module")){
                if(!notificationType.equals("onEnrollment")){
                    button.setVisibility(View.GONE);
                    imageView_button.setVisibility(View.GONE);
                }
                 buttonTitle = "Module";
            }else if (entityType.equals("chatroom")
                    && !notificationType.equals("self_nomination")) {
                buttonTitle = "Chatroom";
                layoutHelper.loadImage(imageView_notification, "icons8_communication_60");
            } else if (entityType.equals("classroom")
                    && !notificationType.equals("self_nomination")) {
                buttonTitle = "Classroom";
            } else if(entityType.equals("badge")){
                buttonTitle = "Badge Allotted";
                layoutHelper.loadImage(imageView_notification, "icons8_prize_50");
            }
            if(notificationType.equals("nominated")) {
                 buttonTitle = "Nominated";
            }else if(notificationType.equals("reject")){
                buttonTitle = "Nomination Rejected";
                imageView_button.setVisibility(View.GONE);
            }
            button.setText(buttonTitle);
            button.setOnClickListener(new startChat(entitySeq, notificationTitle, null, buttonTitle,fromDate,lpSeq));
            imageView_button.setOnClickListener(new startChat(entitySeq, notificationTitle, null, buttonTitle,fromDate,lpSeq));
            textView_nominated.setText(buttonTitle);
            textView_nominated.setVisibility(View.VISIBLE);
            TextView textView = (TextView) childLayout.findViewById(in.learntech.ezae.R.id.notification_title);
            textView.setText(notificationTitle);
            if(isRead == 0){
                linearLayout.setBackground(ResourcesCompat.getDrawable(getActivity().getResources(), in.learntech.ezae.R.drawable.border_orange_right_backround, null));
                unReadCount++;
            }
            RelativeLayout deleteRL = (RelativeLayout) childLayout.findViewById(in.learntech.ezae.R.id.smMenuViewRight);
            deleteRL.setTag(in.learntech.ezae.R.string.notificationSeq, seq);
            mNotesLayout.addView(childLayout);
        }
        if(unReadCount > 0){
            executeMarkAsReadCall();
        }
    }

    private void executeMarkAsReadCall(){
        Object[] args = {mUserSeq,mCompanySeq};
        String notificationUrl = MessageFormat.format(StringConstants.MARK_AS_READ_NOTIFICATION,args);
        mAuthTask = new ServiceHandler(notificationUrl,this,getActivity());
        mAuthTask.setShowProgress(false);
        mAuthTask.execute();
    }

    class startChat implements View.OnClickListener{
        ChatRoomModel  model;
        String notificationType;
        Date eventFromDate;
        int lpSeq;
        startChat(int seq,String title,String imageUrl,String notType,Date fromDate,int learningPlanSeq){
            model = new ChatRoomModel(seq,title,imageUrl);
            notificationType = notType;
            eventFromDate = fromDate;
            lpSeq = learningPlanSeq;
        }
        @Override
        public void onClick(View view) {
            if(notificationType == "Chatroom") {
                Intent messageChatActivity = new Intent(getActivity(), ChatRoomChatActivity.class);
                messageChatActivity.putExtra("messageModel", model);
                startActivity(messageChatActivity);
                getActivity().overridePendingTransition(in.learntech.ezae.R.anim.firstactivity_enter, in.learntech.ezae.R.anim.firstactivity_exit);
            }else if(notificationType == "Classroom"){
                Intent intent = new Intent(getActivity(), MainActivity.class);
                intent.putExtra(StringConstants.EVENT_DATE,eventFromDate);
                startActivity(intent);
            }else if(notificationType == "Nominated"){
                Toast.makeText(getActivity(),"Training Already Nominated",Toast.LENGTH_LONG).show();
            }else if(notificationType == "Module"){
                int moduleSeq = model.getSeq();
                Intent intent = new Intent(getActivity(),UserTrainingActivity.class);
                intent.putExtra(StringConstants.LP_SEQ,lpSeq);
                intent.putExtra(StringConstants.MODULE_SEQ,moduleSeq);
                startActivity(intent);
            }else if(notificationType == "Badge Allotted"){
                Intent intent = new Intent(getActivity(), MyAchievements.class);
                startActivity(intent);
            }else if(notificationType == "Nomination Rejected"){

            }
            else {
                nominateTraining(model.getSeq());
            }
        }
    }


    @Override
    public void setCallName(String call) {
        mCallName = call;
    }


    public void nominateTraining(final int trainingSeq) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Nominate Training");
        builder.setMessage("Do you really want to Nominate for this Training?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                executeNominateTrainingCall(trainingSeq);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }


    private void executeNominateTrainingCall(int trainingSeq){
        Object[] args = {mUserSeq,mCompanySeq,trainingSeq,0};
        String notificationUrl = MessageFormat.format(StringConstants.NOMINATE_TRAINING,args);
        mAuthTask = new ServiceHandler(notificationUrl,this,NOMINATE_TRAINING,getActivity());
        mAuthTask.execute();
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void deleteNotification(final int noteSeq) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Remove Notification");
        builder.setMessage("Do you really want to remove this Notification?");
        builder.setPositiveButton("Remove", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                deleteAction(noteSeq);
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void deleteAction(int noteSeq){
        Object[] args = {mUserSeq,mCompanySeq,noteSeq};
        String deleteNoteURL = MessageFormat.format(StringConstants.DELETE_NOTIFICATION,args);
        mAuthTask = new ServiceHandler(deleteNoteURL,this, DELETE_NOTIFICATION,getActivity());
        mAuthTask.execute();
    }
}
