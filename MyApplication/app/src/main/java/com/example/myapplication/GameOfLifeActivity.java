package com.example.myapplication;

import android.content.Context;
import android.content.Intent;

import androidx.fragment.app.Fragment;

public class GameOfLifeActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment() {
        return new GameOfLifeFragment();
    }
    
    public Intent cloneFragment(Context context, Cell[] cells){
        Intent intent = new Intent(context, GameOfLifeActivity.class);
        intent.putExtra("CELLS_EXTRA", cells);
        return intent;
    }
}
