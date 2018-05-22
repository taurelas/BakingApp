package com.leadinsource.bakingapp.ui.recipe;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.leadinsource.bakingapp.R;
import com.leadinsource.bakingapp.model.Step;
import com.squareup.picasso.Picasso;

import timber.log.Timber;

/**
 * A fragment representing a single Step detail screen.
 */
public class StepDetailFragment extends Fragment {

    private Step recentStep;
    private SimpleExoPlayer exoPlayer;
    private RecipeActivityViewModel viewModel;
    private TextView textView;
    private SimpleExoPlayerView playerView;
    private ImageView imageView;
    private long playerPosition;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StepDetailFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Timber.d("ROTATION onCreate of StepDetailFragment %s", this);
        viewModel = ViewModelProviders.of(getActivity()).get(RecipeActivityViewModel.class);
        playerPosition = viewModel.getTime();

        Timber.d("ROTATION Got position %s from viewmodel", playerPosition);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.d("ROTATION onCreateView");
        View rootView = inflater.inflate(R.layout.step, container, false);

        textView = rootView.findViewById(R.id.step_description);
        playerView = rootView.findViewById(R.id.playerView);
        imageView = rootView.findViewById(R.id.imageView);

        viewModel.getCurrentStep().observe(getActivity(), new Observer<Step>() {
            @Override
            public void onChanged(@Nullable Step step) {
                Timber.d("Step changed");
                recentStep = step;
                if (step != null) {

                    if (textView != null) {
                        textView.setText(step.getDescription());
                    }

                    if (imageView != null) {
                        if(step.getThumbnailURL()!=null && step.getThumbnailURL().length()>0 && getContext()!=null) {
                            Picasso.get().load(step.getThumbnailURL()).into(imageView);
                        }
                    }

                    if (playerView != null) {
                        if (step.getVideoURL() != null && step.getVideoURL().length() > 0 && getContext() != null) {
                            initializePlayer(step);
                        } else {
                            playerView.setVisibility(View.GONE);
                            Timber.d("Setting player's visibility to gone");
                            releasePlayer();
                            // we used to reset time here but now we do it when we press next
                           // viewModel.resetTime();

                        }
                    } else {
                        Timber.d("PlayerView is null so releasing the player");
                        releasePlayer();
                    }

                } else {
                    Timber.d("Step is null");
                }
            }
        });

        return rootView;
    }

    private void initializePlayer(Step step) {
        releasePlayer();
        playerView.setVisibility(View.VISIBLE);
        Timber.d("initialize player, position is %s", playerPosition);
        TrackSelector trackSelector = new DefaultTrackSelector();
        LoadControl loadControl = new DefaultLoadControl();
        exoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
        playerView.setPlayer(exoPlayer);
        Uri videoUrl = Uri.parse(step.getVideoURL());
        String userAgent = Util.getUserAgent(getContext(), "BakingApp");
        MediaSource mediaSource = new ExtractorMediaSource(videoUrl, new DefaultDataSourceFactory(
                getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
        exoPlayer.prepare(mediaSource);
        exoPlayer.seekTo(playerPosition);
        Timber.d("%s: ROTATION Seeking to %s", this, viewModel.getTime());
        exoPlayer.setPlayWhenReady(true);

    }

    @Override
    public void onPause() {
        Timber.d("ROTATION onPause");
        super.onPause();
        if (exoPlayer!=null) {
            playerPosition = exoPlayer.getCurrentPosition();
            releasePlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (recentStep != null && exoPlayer == null) {
            initializePlayer(recentStep);
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // saving playerPosition
        Timber.d("ROTATION Saving player position: %s", playerPosition);
        viewModel.setCurrentTime(playerPosition);
    }

    /**
     * Necessary steps for release of the player
     */
    private void releasePlayer() {
        Timber.d("ROTATION Releasing the player");
        if (exoPlayer != null) {
            exoPlayer.setPlayWhenReady(false);
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }
    }

}