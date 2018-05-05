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

import timber.log.Timber;

/**
 * A fragment representing a single Step detail screen.

 */
public class StepDetailFragment extends Fragment {

    private Step step;
    private SimpleExoPlayer exoPlayer;
    private RecipeActivityViewModel viewModel;
    private TextView textView;
    private SimpleExoPlayerView playerView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public StepDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = ViewModelProviders.of(getActivity()).get(RecipeActivityViewModel.class);

   /*     if (getArguments().containsKey(EXTRA_STEP)) {
            step = getArguments().getParcelable(EXTRA_STEP);
            Timber.d("Creating Fragment for %s", step.getShortDescription());
        }*/
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.step, container, false);

        textView = rootView.findViewById(R.id.step_description);
        playerView = rootView.findViewById(R.id.playerView);

        viewModel.getCurrentStep().observe(getActivity(), new Observer<Step>() {
            @Override
            public void onChanged(@Nullable Step step) {
                if (step != null) {

                    if(textView !=null) {
                        textView.setText(step.getDescription());
                    }

                    if(playerView!=null) {
                        if(step.getVideoURL()!=null && step.getVideoURL().length()>0 && getContext()!=null) {
                            TrackSelector trackSelector = new DefaultTrackSelector();
                            LoadControl loadControl = new DefaultLoadControl();
                            exoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
                            playerView.setPlayer(exoPlayer);
                            Uri videoUrl = Uri.parse(step.getVideoURL());
                            String userAgent = Util.getUserAgent(getContext(), "BakingApp");
                            MediaSource mediaSource = new ExtractorMediaSource(videoUrl, new DefaultDataSourceFactory(
                                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
                            exoPlayer.prepare(mediaSource);
                            exoPlayer.setPlayWhenReady(true);
                        } else {
                            playerView.setVisibility(View.GONE);
                            Timber.d("Setting visibility to gone");
                        }
                    }

                } else {
                    Timber.d("Step is null");
                }
            }
        });

        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (exoPlayer!=null) {
            exoPlayer.stop();
            exoPlayer.release();
            exoPlayer = null;
        }
    }
}
