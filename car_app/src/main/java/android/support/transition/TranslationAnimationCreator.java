package android.support.transition;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.view.View;

class TranslationAnimationCreator {
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r0v1, resolved type: java.lang.Object} */
    /* JADX DEBUG: Multi-variable search result rejected for TypeSearchVarInfo{r12v0, resolved type: int[]} */
    /* JADX WARNING: Multi-variable type inference failed */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    static android.animation.Animator createAnimation(android.view.View r18, android.support.transition.TransitionValues r19, int r20, int r21, float r22, float r23, float r24, float r25, android.animation.TimeInterpolator r26) {
        /*
            r7 = r18
            r8 = r19
            float r10 = r18.getTranslationX()
            float r11 = r18.getTranslationY()
            android.view.View r0 = r8.view
            int r1 = android.support.transition.R.id.transition_position
            java.lang.Object r0 = r0.getTag(r1)
            r12 = r0
            int[] r12 = (int[]) r12
            r0 = 1
            r1 = 0
            if (r12 == 0) goto L_0x002a
            r2 = r12[r1]
            int r2 = r2 - r20
            float r2 = (float) r2
            float r2 = r2 + r10
            r3 = r12[r0]
            int r3 = r3 - r21
            float r3 = (float) r3
            float r3 = r3 + r11
            r6 = r2
            r5 = r3
            goto L_0x002e
        L_0x002a:
            r6 = r22
            r5 = r23
        L_0x002e:
            float r2 = r6 - r10
            int r2 = java.lang.Math.round(r2)
            int r13 = r20 + r2
            float r2 = r5 - r11
            int r2 = java.lang.Math.round(r2)
            int r14 = r21 + r2
            r7.setTranslationX(r6)
            r7.setTranslationY(r5)
            int r2 = (r6 > r24 ? 1 : (r6 == r24 ? 0 : -1))
            if (r2 != 0) goto L_0x004e
            int r2 = (r5 > r25 ? 1 : (r5 == r25 ? 0 : -1))
            if (r2 != 0) goto L_0x004e
            r0 = 0
            return r0
        L_0x004e:
            r2 = 2
            android.animation.PropertyValuesHolder[] r3 = new android.animation.PropertyValuesHolder[r2]
            android.util.Property r4 = android.view.View.TRANSLATION_X
            float[] r9 = new float[r2]
            r9[r1] = r6
            r9[r0] = r24
            android.animation.PropertyValuesHolder r4 = android.animation.PropertyValuesHolder.ofFloat(r4, r9)
            r3[r1] = r4
            android.util.Property r4 = android.view.View.TRANSLATION_Y
            float[] r2 = new float[r2]
            r2[r1] = r5
            r2[r0] = r25
            android.animation.PropertyValuesHolder r1 = android.animation.PropertyValuesHolder.ofFloat(r4, r2)
            r3[r0] = r1
            android.animation.ObjectAnimator r9 = android.animation.ObjectAnimator.ofPropertyValuesHolder(r7, r3)
            android.support.transition.TranslationAnimationCreator$TransitionPositionListener r15 = new android.support.transition.TranslationAnimationCreator$TransitionPositionListener
            android.view.View r2 = r8.view
            r0 = r15
            r1 = r18
            r3 = r13
            r4 = r14
            r16 = r5
            r5 = r10
            r17 = r6
            r6 = r11
            r0.<init>(r1, r2, r3, r4, r5, r6)
            r9.addListener(r0)
            android.support.transition.AnimatorUtils.addPauseListener(r9, r0)
            r1 = r26
            r9.setInterpolator(r1)
            return r9
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.transition.TranslationAnimationCreator.createAnimation(android.view.View, android.support.transition.TransitionValues, int, int, float, float, float, float, android.animation.TimeInterpolator):android.animation.Animator");
    }

    private static class TransitionPositionListener extends AnimatorListenerAdapter {
        private final View mMovingView;
        private float mPausedX;
        private float mPausedY;
        private final int mStartX;
        private final int mStartY;
        private final float mTerminalX;
        private final float mTerminalY;
        private int[] mTransitionPosition = ((int[]) this.mViewInHierarchy.getTag(R.id.transition_position));
        private final View mViewInHierarchy;

        TransitionPositionListener(View movingView, View viewInHierarchy, int startX, int startY, float terminalX, float terminalY) {
            this.mMovingView = movingView;
            this.mViewInHierarchy = viewInHierarchy;
            this.mStartX = startX - Math.round(this.mMovingView.getTranslationX());
            this.mStartY = startY - Math.round(this.mMovingView.getTranslationY());
            this.mTerminalX = terminalX;
            this.mTerminalY = terminalY;
            if (this.mTransitionPosition != null) {
                this.mViewInHierarchy.setTag(R.id.transition_position, (Object) null);
            }
        }

        public void onAnimationCancel(Animator animation) {
            if (this.mTransitionPosition == null) {
                this.mTransitionPosition = new int[2];
            }
            this.mTransitionPosition[0] = Math.round(((float) this.mStartX) + this.mMovingView.getTranslationX());
            this.mTransitionPosition[1] = Math.round(((float) this.mStartY) + this.mMovingView.getTranslationY());
            this.mViewInHierarchy.setTag(R.id.transition_position, this.mTransitionPosition);
        }

        public void onAnimationEnd(Animator animator) {
            this.mMovingView.setTranslationX(this.mTerminalX);
            this.mMovingView.setTranslationY(this.mTerminalY);
        }

        public void onAnimationPause(Animator animator) {
            this.mPausedX = this.mMovingView.getTranslationX();
            this.mPausedY = this.mMovingView.getTranslationY();
            this.mMovingView.setTranslationX(this.mTerminalX);
            this.mMovingView.setTranslationY(this.mTerminalY);
        }

        public void onAnimationResume(Animator animator) {
            this.mMovingView.setTranslationX(this.mPausedX);
            this.mMovingView.setTranslationY(this.mPausedY);
        }
    }

    private TranslationAnimationCreator() {
    }
}
