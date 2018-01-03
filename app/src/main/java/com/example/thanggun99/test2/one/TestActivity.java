package com.example.thanggun99.test2.one;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;

import com.example.thanggun99.test2.R;

import java.util.WeakHashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import se.emilsjolander.stickylistheaders.ExpandableStickyListHeadersListView;

public class TestActivity extends FragmentActivity {

    @BindView(R.id.expand_list)
    ExpandableStickyListHeadersListView expandListView;
   /* @BindView(R.id.web_view)
    WebView webView;*/

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        TestBaseAdapter testBaseAdapter = new TestBaseAdapter(this);
        expandListView.setAdapter(testBaseAdapter);
        expandListView.setAnimExecutor(new AnimationExecutor());
        expandListView.setOnHeaderClickListener((l, header, itemPosition, headerId, currentlySticky) -> {
            if (expandListView.isHeaderCollapsed(headerId)) {
                expandListView.expand(headerId);
            } else {
                expandListView.collapse(headerId);
            }
        });
    }


    class AnimationExecutor implements ExpandableStickyListHeadersListView.IAnimationExecutor {
        WeakHashMap<View, Integer> mOriginalViewHeightPool = new WeakHashMap();

        @Override
        public void executeAnim(final View target, final int animType) {
            if (ExpandableStickyListHeadersListView.ANIMATION_EXPAND == animType && target.getVisibility() == View.VISIBLE) {
                return;
            }
            if (ExpandableStickyListHeadersListView.ANIMATION_COLLAPSE == animType && target.getVisibility() != View.VISIBLE) {
                return;
            }
            if (mOriginalViewHeightPool.get(target) == null) {
                mOriginalViewHeightPool.put(target, target.getHeight());
            }
            final int viewHeight = mOriginalViewHeightPool.get(target);
            float animStartY = animType == ExpandableStickyListHeadersListView.ANIMATION_EXPAND ? 0f : viewHeight;
            float animEndY = animType == ExpandableStickyListHeadersListView.ANIMATION_EXPAND ? viewHeight : 0f;
            final ViewGroup.LayoutParams lp = target.getLayoutParams();
            ValueAnimator animator = ValueAnimator.ofFloat(animStartY, animEndY);
            animator.setDuration(200);
            target.setVisibility(View.VISIBLE);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    if (animType == ExpandableStickyListHeadersListView.ANIMATION_EXPAND) {
                        target.setVisibility(View.VISIBLE);
                    } else {
                        target.setVisibility(View.GONE);
                    }
                    target.getLayoutParams().height = viewHeight;
                }

                @Override
                public void onAnimationCancel(Animator animator) {

                }

                @Override
                public void onAnimationRepeat(Animator animator) {

                }
            });
            animator.addUpdateListener(valueAnimator -> {
                lp.height = ((Float) valueAnimator.getAnimatedValue()).intValue();
                target.setLayoutParams(lp);
                target.requestLayout();
            });
            animator.start();
        }
    }

}
