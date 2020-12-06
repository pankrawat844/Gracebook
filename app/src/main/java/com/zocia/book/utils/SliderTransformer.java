package com.zocia.book.utils;

import android.view.View;

import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2.PageTransformer;

import kotlin.jvm.internal.Intrinsics;

import org.jetbrains.annotations.NotNull;

public final class SliderTransformer implements ViewPager.PageTransformer {
    private static final float DEFAULT_TRANSLATION_X = 0.0F;
    private static final float DEFAULT_TRANSLATION_FACTOR = 1.2F;
    private static final float SCALE_FACTOR = 0.14F;
    private static final float DEFAULT_SCALE = 1.0F;
    private static final float ALPHA_FACTOR = 0.3F;
    private static final float DEFAULT_ALPHA = 1.0F;
    private final int offscreenPageLimit;

    public SliderTransformer(int offscreenPageLimit) {
        this.offscreenPageLimit = offscreenPageLimit;
    }

    public void transformPage(@NotNull View page, float position) {
        Intrinsics.checkParameterIsNotNull(page, "page");
        ViewCompat.setElevation(page, -Math.abs(position));
        float scaleFactor = -0.14F * position + 1.0F;
        float alphaFactor = -0.3F * position + 1.0F;
        if (position <= 0.0F) {
            page.setTranslationX(0.0F);
            page.setScaleX(1.0F);
            page.setScaleY(1.0F);
            page.setAlpha(1.0F + position);
        } else if (position <= (float) (this.offscreenPageLimit - 1)) {
            page.setScaleX(scaleFactor);
            page.setScaleY(scaleFactor);
            page.setTranslationX(-((float) page.getWidth() / 1.2F) * position);
            page.setAlpha(alphaFactor);
        } else {
            page.setTranslationX(0.0F);
            page.setScaleX(1.0F);
            page.setScaleY(1.0F);
            page.setAlpha(1.0F);
        }

    }


}
