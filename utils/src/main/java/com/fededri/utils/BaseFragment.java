package com.fededri.utils;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * Created by Federico Torres on 9/10/2017.
 */

public class BaseFragment extends Fragment {
    public BaseActivity mActivity;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity		=	(BaseActivity) this.getActivity();
    }


    public void showDialog(String message){
        ((BaseActivity)getActivity()).showDialog(message,getContext(),null);
    }

    public void showDialog(String message, DialogInterface.OnClickListener listener){
        ((BaseActivity)getActivity()).showDialog(message,getContext(),listener);
    }

    public void showDialog(String message, DialogInterface.OnClickListener okListener, DialogInterface.OnClickListener negativeListener){
        ((BaseActivity)getActivity()).showDialog(message,getContext(),okListener,negativeListener);
    }

    public void showProgressDialog(){
        ( (BaseActivity) getActivity()).showProgressDialog();
    }


    public void dismissProgressDialog(){
        ((BaseActivity) getActivity()).dismissProgressDialog();
    }

}
