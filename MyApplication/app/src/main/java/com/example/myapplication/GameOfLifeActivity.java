package com.example.myapplication;

import androidx.fragment.app.Fragment;

public class GameOfLifeActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new GameOfLifeFragment();
    }
}
