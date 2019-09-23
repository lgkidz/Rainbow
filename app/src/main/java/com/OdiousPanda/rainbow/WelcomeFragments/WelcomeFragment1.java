package com.OdiousPanda.rainbow.WelcomeFragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.OdiousPanda.rainbow.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class WelcomeFragment1 extends Fragment {


    public WelcomeFragment1() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.greeting_slide, container, false);
    }

}
