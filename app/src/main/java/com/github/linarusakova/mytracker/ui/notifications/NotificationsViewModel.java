package com.github.linarusakova.mytracker.ui.notifications;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.github.linarusakova.mytracker.R;

import kotlinx.coroutines.CoroutineScope;

public class NotificationsViewModel extends ViewModel {


    private final MutableLiveData<String> mText;
    CharSequence text;


    public NotificationsViewModel() {

        mText = new MutableLiveData<>();
//        text= getCloseable(R.string.under_consrtuction);
//        mText.setValue((String) text);


    }
//
//    public LiveData<String> getText() {
//        return mText;
//    }
}