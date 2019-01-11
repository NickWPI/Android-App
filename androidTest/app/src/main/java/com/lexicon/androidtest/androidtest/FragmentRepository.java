package com.lexicon.androidtest.androidtest;

import android.support.v4.app.Fragment;

import java.util.HashMap;

public class FragmentRepository {
    private static FragmentRepository m_Instance = new FragmentRepository();

    private HashMap<String, Fragment> m_Fragments;

    private FragmentRepository() {
        m_Fragments = new HashMap<String, Fragment>();
    }

    public void addFragment(String name, Fragment fragment) {
        m_Fragments.put(name, fragment);
    }

    public Fragment getFragment(String name) {
        return m_Fragments.get(name);
    }

    public boolean hasFragment(String name) {
        return m_Fragments.containsKey(name);
    }

    public void removeFragment(String name) {
        m_Fragments.remove(name);
    }

    public static FragmentRepository getInstance() {
        return m_Instance;
    }
}
