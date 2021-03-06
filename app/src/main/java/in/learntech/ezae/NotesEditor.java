package in.learntech.ezae;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.text.MessageFormat;

import in.learntech.ezae.utils.LayoutHelper;
import in.learntech.ezae.utils.StringConstants;
import in.learntech.ezae.Managers.UserMgr;
import in.learntech.ezae.services.Interface.IServiceHandler;
import in.learntech.ezae.services.ServiceHandler;

public class NotesEditor extends AppCompatActivity implements View.OnClickListener,IServiceHandler{

    private int mNoteSeq;
    private ServiceHandler mAuthTask = null;
    private static final String SUCCESS = "success";
    private static final String MESSAGE = "message";
    private UserMgr mUserMgr;
    private String mCallName;
    public static final String GET_NOTE_DETAILS_CALL = "getNoteDetailsCall";
    public static final String SAVE_NOTE_CALL = "saveNoteCall";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(in.learntech.ezae.R.layout.activity_notes_editor);
        Toolbar toolbar = (Toolbar) findViewById(in.learntech.ezae.R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mUserMgr = UserMgr.getInstance(this);
        loadNotes();
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this,NotesActivity.class);
        startActivity(intent);
        overridePendingTransition(in.learntech.ezae.R.anim.secondactivity_enter, in.learntech.ezae.R.anim.secondactivity_exit);
        finish();
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case in.learntech.ezae.R.id.saveNotes:
                saveNotes();
                break;
            default:
                break;
        }
    }
    private void loadNotes(){
        int userSeq = mUserMgr.getLoggedInUserSeq();
        int companySeq = mUserMgr.getLoggedInUserCompanySeq();
        int noteSeq = getIntent().getIntExtra(StringConstants.NOTE_SEQ,0);
        if(noteSeq > 0) {
            Object[] args = {userSeq, companySeq, noteSeq};
            String loadNotesDetailsURL = MessageFormat.format(StringConstants.GET_NOTES_DETAILS, args);
            mAuthTask = new ServiceHandler(loadNotesDetailsURL, this, GET_NOTE_DETAILS_CALL, this);
            mAuthTask.execute();
        }else{
            EditText notesDetails = (EditText) findViewById(in.learntech.ezae.R.id.noteDetails);
            notesDetails.setText("");
        }
    }
    private void saveNotes(){
        try {
            int userSeq = mUserMgr.getLoggedInUserSeq();
            int companySeq = mUserMgr.getLoggedInUserCompanySeq();
            int noteSeq = getIntent().getIntExtra(StringConstants.NOTE_SEQ, 0);
            EditText notesDetailsView = (EditText) findViewById(in.learntech.ezae.R.id.noteDetails);
            String noteDetails = notesDetailsView.getText().toString();
            noteDetails = URLEncoder.encode(noteDetails, "UTF-8");
            Object[] args = {userSeq, companySeq, noteSeq, noteDetails};
            String saveNotesURL = MessageFormat.format(StringConstants.SAVE_NOTES_DETAILS, args);
            mAuthTask = new ServiceHandler(saveNotesURL, this, SAVE_NOTE_CALL, this);
            mAuthTask.execute();

        }catch(Exception e){

        }
    }
    @Override
    public void processServiceResponse(JSONObject response) {
        mAuthTask = null;
        boolean success = false;
        String message = null;
        try{
            success = response.getInt(SUCCESS) == 1 ? true : false;
            message = response.getString(MESSAGE);
            if(success){
                if(mCallName.equals(GET_NOTE_DETAILS_CALL)) {
                    JSONObject jsonObject = response.getJSONObject("notes");
                    int noteSeq = jsonObject.getInt("seq");
                    String noteDetails = jsonObject.getString("details");
                    EditText notesDetails = (EditText) findViewById(in.learntech.ezae.R.id.noteDetails);
                    notesDetails.setText(noteDetails);
                }else if(mCallName.equals(SAVE_NOTE_CALL)){
                    LayoutHelper.showToast(this,message);
                    onBackPressed();
                    overridePendingTransition(in.learntech.ezae.R.anim.secondactivity_enter, in.learntech.ezae.R.anim.secondactivity_exit);
                }
            }
        }catch (Exception e){

            message = "Error :- " + e.getMessage();
        }
        if(message != null && !message.equals("")){
            //Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void setCallName(String call) {
        mCallName = call;
    }

}
