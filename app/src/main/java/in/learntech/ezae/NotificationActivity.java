package in.learntech.ezae;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import in.learntech.ezae.Managers.UserMgr;

public class NotificationActivity extends AppCompatActivity implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private UserMgr mUserMgr;
    Toolbar mToolbar;
    private int userSeq;
    private int companySeq;
    public SwipeRefreshLayout swipeLayout;
    NotificationsFragment mFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(in.learntech.ezae.R.layout.activity_notification);
        mToolbar = (Toolbar) findViewById(in.learntech.ezae.R.id.toolbar);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Notifications");
        }
        mUserMgr = UserMgr.getInstance(this);
        userSeq = mUserMgr.getLoggedInUserSeq();
        companySeq = mUserMgr.getLoggedInUserCompanySeq();
        mFragment = NotificationsFragment.newInstance(userSeq,companySeq,this);
        getFragmentManager().beginTransaction().replace(in.learntech.ezae.R.id.notificationLayout,mFragment).commit();
        swipeLayout = (SwipeRefreshLayout) findViewById(in.learntech.ezae.R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
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
        NotificationsFragment mFragment = NotificationsFragment.newInstance(userSeq,companySeq,this);
        getFragmentManager().beginTransaction().replace(in.learntech.ezae.R.id.notificationLayout,mFragment).commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case in.learntech.ezae.R.id.smMenuViewRight:
                int seq = (int) view.getTag(in.learntech.ezae.R.string.notificationSeq);
                mFragment.deleteNotification(seq);
                break;
            default:
                break;
        }
    }
}
