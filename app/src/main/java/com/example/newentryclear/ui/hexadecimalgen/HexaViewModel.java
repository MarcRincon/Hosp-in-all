package com.example.newentryclear.ui.hexadecimalgen;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HexaViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HexaViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("");
    }

    public LiveData<String> getText() {
        return mText;
    }
}