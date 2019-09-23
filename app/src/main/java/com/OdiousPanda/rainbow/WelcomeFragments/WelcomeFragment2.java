package com.OdiousPanda.rainbow.WelcomeFragments;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.OdiousPanda.rainbow.R;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 */
public class WelcomeFragment2 extends Fragment {

    public WelcomeFragment2() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.welcome_slide_video, container, false);
        VideoView videoView = v.findViewById(R.id.videoView);
        Uri uri = Uri.parse("android.resource://" + Objects.requireNonNull(getActivity()).getPackageName() + "/" + R.raw.slide2);
        videoView.setVideoURI(uri);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
        videoView.start();
        return v;
    }

}
