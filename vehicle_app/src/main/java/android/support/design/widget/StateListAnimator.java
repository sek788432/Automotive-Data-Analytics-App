package android.support.design.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.support.annotation.RestrictTo;
import android.util.StateSet;
import java.util.ArrayList;

@RestrictTo({RestrictTo.Scope.LIBRARY_GROUP})
public final class StateListAnimator {
    private final Animator.AnimatorListener animationListener = new AnimatorListenerAdapter() {
        public void onAnimationEnd(Animator animator) {
            if (StateListAnimator.this.runningAnimator == animator) {
                StateListAnimator.this.runningAnimator = null;
            }
        }
    };
    private Tuple lastMatch = null;
    ValueAnimator runningAnimator = null;
    private final ArrayList<Tuple> tuples = new ArrayList<>();

    public void addState(int[] specs, ValueAnimator animator) {
        Tuple tuple = new Tuple(specs, animator);
        animator.addListener(this.animationListener);
        this.tuples.add(tuple);
    }

    public void setState(int[] state) {
        Tuple match = null;
        int count = this.tuples.size();
        int i = 0;
        while (true) {
            if (i >= count) {
                break;
            }
            Tuple tuple = this.tuples.get(i);
            if (StateSet.stateSetMatches(tuple.specs, state)) {
                match = tuple;
                break;
            }
            i++;
        }
        if (match != this.lastMatch) {
            if (this.lastMatch != null) {
                cancel();
            }
            this.lastMatch = match;
            if (match != null) {
                start(match);
            }
        }
    }

    private void start(Tuple match) {
        this.runningAnimator = match.animator;
        this.runningAnimator.start();
    }

    private void cancel() {
        if (this.runningAnimator != null) {
            this.runningAnimator.cancel();
            this.runningAnimator = null;
        }
    }

    public void jumpToCurrentState() {
        if (this.runningAnimator != null) {
            this.runningAnimator.end();
            this.runningAnimator = null;
        }
    }

    static class Tuple {
        final ValueAnimator animator;
        final int[] specs;

        Tuple(int[] specs2, ValueAnimator animator2) {
            this.specs = specs2;
            this.animator = animator2;
        }
    }
}
