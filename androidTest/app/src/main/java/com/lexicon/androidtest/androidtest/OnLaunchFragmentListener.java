package com.lexicon.androidtest.androidtest;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public interface OnLaunchFragmentListener {
    <T extends Fragment> void onLaunchFragment(Class<T> c, Bundle args, boolean createNewFragment);
    void onPopFragmentBackstack(String name, int flags);
}
