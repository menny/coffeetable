package net.evendanan.coffeetable;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 */
public class LalaFragment extends Fragment {

    private final PrankStateMachine mPrankStateMachine = new PrankStateMachine();
    private TextView mCenterArea;

    public LalaFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lala, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.prank_top_area).setOnClickListener(v -> onErelaTapped(TapArea.Top));
        view.findViewById(R.id.prank_bottom_area).setOnClickListener(v -> onErelaTapped(TapArea.Bottom));
        mCenterArea = view.findViewById(R.id.prank_center_area);
        mCenterArea.setOnClickListener(v -> onErelaTapped(TapArea.Center));
    }

    @Override
    public void onStart() {
        super.onStart();
        mCenterArea.setText(mPrankStateMachine.reset().centerText);
    }

    private void onErelaTapped(TapArea tapArea) {
        final PrankUiState prankUiState = mPrankStateMachine.erelaTapped(tapArea);

        mCenterArea.setText(prankUiState.centerText);

        if (prankUiState.wonGame) {
            /*
            act=android.intent.action.MAIN cat=[android.intent.category.HOME] flg=0x13200000 cmp=com.nook.partner/.FacadeLauncherActivity
             */
            Intent nookIntent = new Intent(Intent.ACTION_MAIN);
            nookIntent.addCategory(Intent.CATEGORY_HOME);
            nookIntent.setClassName("com.nook.partner", "com.nook.partner.FacadeLauncherActivity");
            nookIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(nookIntent);
        }
    }

    private static final PrankStep STEP_ONE = new StaticPrankStep(TapArea.Top, R.string.prank_text_1,
            new StaticPrankStep(TapArea.Bottom, R.string.prank_text_2,
                    new StaticPrankStep(TapArea.Bottom, R.string.prank_text_3,
                            new StaticPrankStep(TapArea.Top, R.string.prank_text_4,
                                    new RandomTapPrankStep(R.string.prank_text_5)))));

    private static PrankStep LOAD_NOOK = new PrankStep() {
        @Override
        public PrankStep erelaTapped(TapArea area) {
            return this;
        }

        @Override
        public int getStepStringRes() {
            return R.string.loading_nook;
        }
    };

    private static PrankStep WRONG_TAP = new StaticPrankStep(TapArea.Center, R.string.prank_text_failure, STEP_ONE);
    private static PrankStep WRONG_GUESS_TAP = new StaticPrankStep(TapArea.Center, R.string.prank_text_failure_guess, STEP_ONE);
    private static PrankStep RIGHT_GUESS_TAP = new StaticPrankStep(TapArea.Center, R.string.prank_text_success, LOAD_NOOK);


    private static class PrankUiState {
        @StringRes
        final int centerText;
        final boolean wonGame;

        private PrankUiState(int centerText, boolean wonGame) {
            this.centerText = centerText;
            this.wonGame = wonGame;
        }
    }

    private static class PrankStateMachine {
        private PrankStep mCurrentStep = STEP_ONE;

        PrankUiState reset() {
            mCurrentStep = STEP_ONE;
            return new PrankUiState(mCurrentStep.getStepStringRes(), false);
        }

        PrankUiState erelaTapped(TapArea area) {
            mCurrentStep = mCurrentStep.erelaTapped(area);

            return new PrankUiState(mCurrentStep.getStepStringRes(), mCurrentStep == LOAD_NOOK);
        }
    }

    private enum TapArea {
        Top,
        Bottom,
        Center
    }

    private interface PrankStep {
        PrankStep erelaTapped(TapArea area);

        @StringRes
        int getStepStringRes();
    }

    private static class StaticPrankStep implements PrankStep {

        private final TapArea mDesiredTap;
        @StringRes
        private final int mStepString;
        private final PrankStep mNextStep;

        private StaticPrankStep(TapArea desiredTap, int stepString, PrankStep nextStep) {
            mDesiredTap = desiredTap;
            mStepString = stepString;
            mNextStep = nextStep;
        }

        @Override
        public PrankStep erelaTapped(TapArea area) {
            if (area.equals(mDesiredTap)) {
                return mNextStep;
            } else {
                return WRONG_TAP;
            }
        }

        @Override
        public int getStepStringRes() {
            return mStepString;
        }
    }

    private static class RandomTapPrankStep implements PrankStep {
        private final Random mRandom = new Random();
        @StringRes
        private final int mStepString;

        private RandomTapPrankStep(@StringRes int stepString) {
            mStepString = stepString;
        }

        @Override
        public PrankStep erelaTapped(TapArea area) {
            final TapArea desiredArea = mRandom.nextBoolean() ? TapArea.Top : TapArea.Bottom;
            if (area.equals(desiredArea)) {
                return RIGHT_GUESS_TAP;
            } else {
                return WRONG_GUESS_TAP;
            }
        }

        @Override
        public int getStepStringRes() {
            return mStepString;
        }
    }
}
