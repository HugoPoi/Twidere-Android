/*
 * 				Twidere - Twitter client for Android
 * 
 *  Copyright (C) 2012-2014 Mariotaku Lee <mariotaku.lee@gmail.com>
 * 
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 * 
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 * 
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mariotaku.twidere.activity.support;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ThemedAppCompatDelegateFactory;
import android.support.v7.widget.ActionBarContainer;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.Window;

import org.mariotaku.twidere.BuildConfig;
import org.mariotaku.twidere.Constants;
import org.mariotaku.twidere.R;
import org.mariotaku.twidere.activity.iface.IAppCompatActivity;
import org.mariotaku.twidere.activity.iface.IThemedActivity;
import org.mariotaku.twidere.util.StrictModeUtils;
import org.mariotaku.twidere.util.ThemeUtils;
import org.mariotaku.twidere.util.Utils;
import org.mariotaku.twidere.view.ShapedImageView.ShapeStyle;

public abstract class ThemedAppCompatActivity extends AppCompatActivity implements Constants,
        IThemedActivity, IAppCompatActivity {

    // Data fields
    private int mCurrentThemeColor;
    private int mCurrentThemeBackgroundAlpha;
    @ShapeStyle
    private int mProfileImageStyle;
    private String mCurrentThemeBackgroundOption;
    private String mCurrentThemeFontFamily;

    private ThemedAppCompatDelegateFactory.ThemedAppCompatDelegate mDelegate;
    private Toolbar mToolbar;

    @Override
    public String getCurrentThemeFontFamily() {
        return mCurrentThemeFontFamily;
    }

    @Override
    public int getCurrentThemeBackgroundAlpha() {
        return mCurrentThemeBackgroundAlpha;
    }

    @Override
    public String getCurrentThemeBackgroundOption() {
        return mCurrentThemeBackgroundOption;
    }

    @Override
    public int getCurrentThemeColor() {
        return mCurrentThemeColor;
    }

    @Override
    public int getThemeBackgroundAlpha() {
        return ThemeUtils.getUserThemeBackgroundAlpha(this);
    }

    @Override
    public String getThemeBackgroundOption() {
        return ThemeUtils.getThemeBackgroundOption(this);
    }

    @Override
    public String getThemeFontFamily() {
        return ThemeUtils.getThemeFontFamily(this);
    }

    @Override
    @ShapeStyle
    public int getCurrentProfileImageStyle() {
        return mProfileImageStyle;
    }

    @Override
    public final void restart() {
        Utils.restartActivity(this);
    }

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            StrictModeUtils.detectAllVmPolicy();
            StrictModeUtils.detectAllThreadPolicy();
        }
        super.onCreate(savedInstanceState);
        ThemeUtils.applyToolbarItemColor(this, getActionBarToolbar(), getCurrentThemeColor());
    }

    @Override
    public void onSupportActionModeStarted(@NonNull android.support.v7.view.ActionMode mode) {
        super.onSupportActionModeStarted(mode);
        ThemeUtils.applySupportActionModeColor(mode, getCurrentThemeColor(),
                getThemeBackgroundOption(), true);
        ThemeUtils.applySupportActionModeItemColor(mode, getCurrentThemeColor());
    }

    @NonNull
    @Override
    public ThemedAppCompatDelegateFactory.ThemedAppCompatDelegate getDelegate() {
        if (mDelegate != null) return mDelegate;
        return mDelegate = ThemedAppCompatDelegateFactory.create(this, this);
    }

    @Override
    protected void onApplyThemeResource(@NonNull Resources.Theme theme, int resId, boolean first) {
        mCurrentThemeColor = getThemeColor();
        mCurrentThemeBackgroundAlpha = getThemeBackgroundAlpha();
        mProfileImageStyle = Utils.getProfileImageStyle(this);
        mCurrentThemeBackgroundOption = getThemeBackgroundOption();
        mCurrentThemeFontFamily = getThemeFontFamily();
        super.onApplyThemeResource(theme, resId, first);
        final Window window = getWindow();
        if (shouldApplyWindowBackground()) {
            ThemeUtils.applyWindowBackground(this, window, mCurrentThemeBackgroundOption,
                    mCurrentThemeBackgroundAlpha);
        }
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        final Window window = getWindow();
        final Toolbar actionBarToolbar = (Toolbar) window.findViewById(R.id.action_bar);
        final ActionBarContainer actionBarContainer = (ActionBarContainer) window.findViewById(R.id.action_bar_container);
        ThemeUtils.applyActionBarBackground(actionBarContainer, this, mCurrentThemeColor,
                mCurrentThemeBackgroundOption, true);
        ThemeUtils.applyToolbarItemColor(this, actionBarToolbar, mCurrentThemeColor);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        final boolean result = super.onPrepareOptionsMenu(menu);
        final Window window = getWindow();
        final Toolbar actionBarToolbar = (Toolbar) window.findViewById(R.id.action_bar);
        ThemeUtils.applyToolbarItemColor(this, actionBarToolbar, mCurrentThemeColor);
        return result;
    }

    @Override
    public void setSupportActionBar(Toolbar toolbar) {
        super.setSupportActionBar(toolbar);
        mToolbar = toolbar;
        ThemeUtils.applyToolbarItemColor(this, toolbar, mCurrentThemeColor);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        ThemeUtils.fixNightMode(getResources(), newConfig);
        super.onConfigurationChanged(newConfig);
    }

    @Nullable
    public final Toolbar peekActionBarToolbar() {
        return mToolbar;
    }

    @Nullable
    public final Toolbar getActionBarToolbar() {
        if (mToolbar != null) return mToolbar;
        final View actionBarView = getWindow().findViewById(android.support.v7.appcompat.R.id.action_bar);
        if (actionBarView instanceof Toolbar) {
            return (Toolbar) actionBarView;
        }
        return null;
    }

    protected boolean shouldApplyWindowBackground() {
        return true;
    }

}
