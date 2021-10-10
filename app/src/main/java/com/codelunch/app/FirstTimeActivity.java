package com.codelunch.app;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

public class FirstTimeActivity extends AppCompatActivity {

    private ImageView logoImageView;
    private int longAnimationDuration;
    private TextView welcomeMessageTextView;
    private Button buttonGetStarted;
    private ImageView nutriLogoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_time);

        LinkedList<View> queue = new LinkedList<>();
        longAnimationDuration = getResources().getInteger(android.R.integer.config_longAnimTime);

        // Welcome Message
        welcomeMessageTextView = findViewById(R.id.welcomeMessageTextView);
        queue.add(welcomeMessageTextView);

        // Logo
        logoImageView = findViewById(R.id.logoView);
        queue.add(logoImageView);

        //Button
        buttonGetStarted = findViewById(R.id.buttonGetStarted);
        queue.add(buttonGetStarted);
        // TODO BUTTON GOES TO MENU EXPLAINING SCHOOLS

        // Logo
        nutriLogoImageView = findViewById(R.id.nutrisliceLogo);
        queue.add(nutriLogoImageView);

        animateFadeIn(queue, longAnimationDuration, longAnimationDuration);
    }

    public void animateFadeIn(Queue<View> views, int startDelay, int duration) {
        Queue<AnimationHolder> queue = new LinkedList<>();
        for (View item : views) {
            queue.add(new AnimationHolder(item, startDelay, duration));
        }
        animateFadeIn(queue);
    }

    public void animateFadeIn(Queue<AnimationHolder> holders) {
        try {
            animateFadeIn(holders.remove(), new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    animateFadeIn(holders);
                }
            });
        } catch (NoSuchElementException e) {
            // Done
        }
    }

    public void animateFadeIn(AnimationHolder holder, Animator.AnimatorListener listener) {
        animateFadeIn(holder.view, holder.duration, holder.startDelay, listener);
    }

    public void animateFadeIn(View view, int duration, int startDelay, Animator.AnimatorListener listener) {
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);

        view.animate()
                .setStartDelay(startDelay)
                .alpha(1f)
                .setDuration(duration)
                .setListener(listener);
    }

    private class AnimationHolder {
        View view;
        int startDelay;
        int duration;

        public AnimationHolder(View view, int startDelay, int duration) {
            this.view = view;
            this.startDelay = startDelay;
            this.duration = duration;
        }
    }
}