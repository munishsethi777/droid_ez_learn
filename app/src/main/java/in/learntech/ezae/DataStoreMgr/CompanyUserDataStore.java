package in.learntech.ezae.DataStoreMgr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import java.text.MessageFormat;
import java.util.ArrayList;

import in.learntech.ezae.BusinessObjects.CompanyUser;

/**
 * Created by baljeetgaheer on 31/10/17.
 */

public class CompanyUserDataStore {
    private Context mContext;
    private DBUtil mDBUtil;

    public static final String COLUMN_SEQ = "id";
    public static final String COLUMN_USER_SEQ = "seq";
    public static final String COLUMN_USER_TYPE = "type";
    public static final String COLUMN_NAME = "username";
    public static final String COLUMN_IMAGE_NAME = "imagename";
    public static final String COLUMN_COMPANY_SEQ = "companyseq";
    public static final String COLUMN_FULLNAME = "fullname";

    public static final String TABLE_NAME = "companyusers";

    public static final String CREATE_TABLE = "create table " + TABLE_NAME
            + "(" + COLUMN_SEQ + " INTEGER PRIMARY KEY, "
            + COLUMN_USER_SEQ + " TEXT, "
            + COLUMN_USER_TYPE + " TEXT, "
            + COLUMN_NAME + " TEXT, "
            + COLUMN_IMAGE_NAME + " TEXT, "
            + COLUMN_COMPANY_SEQ + " TEXT ,"
            + COLUMN_FULLNAME + " TEXT )";
    public static final String FIND_BY_COMPANY_SEQ = "Select * from companyusers where "
            + COLUMN_COMPANY_SEQ + "= {0,number,#} ";

    public CompanyUserDataStore(Context context) {
        mContext = context;
        mDBUtil = DBUtil.getInstance(mContext);
    }

    public long save(CompanyUser companyUser){
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_SEQ, companyUser.getSeq());
        values.put(COLUMN_USER_TYPE, companyUser.getType());
        values.put(COLUMN_NAME, companyUser.getUserName());
        values.put(COLUMN_IMAGE_NAME, companyUser.getImageName());
        values.put(COLUMN_COMPANY_SEQ, companyUser.getCompanySeq());
        values.put(COLUMN_FULLNAME,companyUser.getFullName());
        int seq = companyUser.getId();
        return mDBUtil.addOrUpdateUser(this.TABLE_NAME, values, String.valueOf(seq));
    }


    public ArrayList<CompanyUser> getCompanyUsersByCompanySeq(int companySeq) {
        Integer[] args = {companySeq};
        String query = MessageFormat.format(FIND_BY_COMPANY_SEQ, args);
        Cursor c = mDBUtil.executeQuery(query);
        ArrayList<CompanyUser> companyUserList = CursorToList(c);
        return companyUserList;
    }


    private ArrayList<CompanyUser> CursorToList(Cursor c) {
        ArrayList<CompanyUser> companyUserList = new ArrayList<CompanyUser>();
        if (c.moveToFirst()) {
            do {
                CompanyUser companyUser = populateObject(c);
                companyUserList.add(companyUser);
            } while (c.moveToNext());
        }
        c.close();
        return companyUserList;
    }

    public boolean deleteAll(){
        boolean flag = mDBUtil.deleteAll(TABLE_NAME);
        return flag;
    }

    private CompanyUser populateObject(Cursor c) {
        int id = c.getInt(0);
        int userSeq = c.getInt(1);
        String type = c.getString(2);
        String userName = c.getString(3);
        String imageName = c.getString(4);
        int companySeq = c.getInt(5);
        String fullName = c.getString(6);

        CompanyUser companyUser = new CompanyUser();
        companyUser.setId(id);
        companyUser.setSeq(userSeq);
        companyUser.setType(type);
        companyUser.setUserName(userName);
        companyUser.setImageName(imageName);
        companyUser.setCompanySeq(companySeq);
        companyUser.setFullName(fullName);
        return companyUser;
    }
}