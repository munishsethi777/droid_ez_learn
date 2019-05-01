package in.learntech.ezae;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import in.learntech.ezae.utils.StringConstants;
import in.learntech.ezae.Managers.UserMgr;

public class MyTrainings extends AppCompatActivity implements View.OnClickListener{
    private static final String[] pageTitle = {"LEARNING PLANS","MY MODULES"};
    private int mUserSeq;
    private int mCompanySeq;
    private UserMgr mUserMgr;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(in.learntech.ezae.R.layout.activity_my_trainings);
        Toolbar toolbar = (Toolbar) findViewById(in.learntech.ezae.R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(true);
            actionBar.setTitle("My Trainings");
        }
        mUserMgr = UserMgr.getInstance(this);
        mUserSeq = mUserMgr.getLoggedInUserSeq();
        mCompanySeq = mUserMgr.getLoggedInUserCompanySeq();
        TabLayout tabLayout = (TabLayout) findViewById(in.learntech.ezae.R.id.tab_layout_loginsignup4);
        ViewPager viewPager = (ViewPager) findViewById(in.learntech.ezae.R.id.viewpager_loginsignup4);
        MyTrainingsAdapter adapter = new MyTrainingsAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onClick(View view) {
        boolean isReattempted = (boolean)view.getTag(in.learntech.ezae.R.string.isreattemtped) ;
        switch (view.getId()){
            case in.learntech.ezae.R.id.button_moduleLaunch:
                if(isReattempted){
                    confirmReattempt(view);
                }else{
                    goUserTrainingActivity(view);
                }
                break;
            case in.learntech.ezae.R.id.imageView_launch:
                if(isReattempted){
                    confirmReattempt(view);
                }else{
                    goUserTrainingActivity(view);
                }
                break;
            default:
                break;
        }
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }



    public class MyTrainingsAdapter extends FragmentPagerAdapter{
        private List<Fragment> fragments;

        public MyTrainingsAdapter(FragmentManager fm){
            super(fm);
            this.fragments = new ArrayList<>();
            Fragment moduleFragment = MyTrainings_MyModulesFragment.newInstance(mUserSeq,mCompanySeq);
            Fragment lpFragment = MyTrainings_LearningPlansFragment.newInstance(mUserSeq,mCompanySeq);
            fragments.add(lpFragment);
            fragments.add(moduleFragment);
        }
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int arrayPos) {
            return pageTitle[arrayPos];
        }
    }

    private void goUserTrainingActivity(View view){
        boolean isReattempted = (boolean)view.getTag(in.learntech.ezae.R.string.isreattemtped) ;
        int lpSeq = (int)view.getTag(in.learntech.ezae.R.string.lp_seq);
        int moduleSeq = (int)view.getTag(in.learntech.ezae.R.string.module_seq);
        Intent intent = new Intent(this,UserTrainingActivity.class);
        intent.putExtra(StringConstants.LP_SEQ,lpSeq);
        intent.putExtra(StringConstants.MODULE_SEQ,moduleSeq);
        intent.putExtra(StringConstants.IS_REATTEMPTED,isReattempted);
        startActivity(intent);
    }

    private void confirmReattempt(final View view){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Confirm Re-attempted");
            builder.setMessage("Do you really want to re-attempt this Training?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    goUserTrainingActivity(view);
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

}
