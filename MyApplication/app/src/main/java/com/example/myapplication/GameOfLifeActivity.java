package com.example.myapplication;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

public class GameOfLifeActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new GameOfLifeFragment();
    }
}
