package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by dave on 10/11/16.
 */

public class GameOfLifeFragment extends Fragment {
    // constants for X & O
    private final static int ALIVE = 1;
    private final static int DEAD = 2;

    private TextView mTextView;
    // Game State
    private Cell[] mCells = new Cell[400];
    private int generation;
    @ColorInt
    private int aliveColor = Color.argb(255, 102, 255, 255);
    @ColorInt
    private int deadColor = Color.argb(255, 255, 102, 102);
    @ColorInt
    private  int backgroundColor = Color.argb(255,0,212, 180);

    private int[] mGrid = new int[400];
    // RecyclerView Stuff
    private RecyclerView mRecycler;
    private RecyclerView.Adapter<CellHolder> mAdapter = new CellAdapter();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game_of_life, container, false);
        // setup recycler view
        mRecycler = (RecyclerView) v.findViewById(R.id.reycler_tic_tac_toe);
        mRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 20));
        mRecycler.setAdapter(mAdapter);

        mTextView = (TextView) v.findViewById(R.id.textView);


        // just recreate activity when want to play again
        Button resetButton = (Button) v.findViewById(R.id.clone_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().recreate();
            }
        });

        return v;
    }

    private class CellHolder extends RecyclerView.ViewHolder {
        private Button mButton;
        private int mPosition;

        public CellHolder(LayoutInflater inflater, ViewGroup container) {
            super(inflater.inflate(R.layout.gof_cell, container, false));

            mButton = (Button)itemView.findViewById(R.id.ttt_button);
            // player makes a move when they click
            mButton.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View view) {
                                               if (mGrid[mPosition] == 0 || mGrid[mPosition] == 2) {
                                                   mGrid[mPosition] = ALIVE; // set move
                                                   mTextView.setText(mPosition + "ALIVE" + mGrid[mPosition]);
                                               } else if (mGrid[mPosition] == 1){
                                                   mGrid[mPosition] = DEAD; // set move
                                                   mTextView.setText(mPosition + "DEAD" + mGrid[mPosition]);
                                               }
                                               mAdapter.notifyItemChanged(mPosition); // reload ViewHolder
                                           }
                                       }
            );
        }

        public void bindPosition(int p) {
            mPosition = p;
        }
    }

    private class CellAdapter extends RecyclerView.Adapter<CellHolder> {
        @Override
        public void onBindViewHolder(CellHolder holder, int position) {
            // tell holder which place on grid it is representing
            holder.bindPosition(position);
            // actually change image displayed

            mTextView.setText(position + "ALIVE" + mGrid[position]);
            if (mGrid[position] == ALIVE) {
                holder.mButton.setBackgroundColor(aliveColor);
            } else if (mGrid[position] == DEAD) {
                holder.mButton.setBackgroundColor(deadColor);
            } else {
                holder.mButton.setBackgroundResource(R.drawable.empty);
            }
        }

        @Override
        public CellHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            return new CellHolder(inflater, parent);
        }

        @Override
        public int getItemCount() {
            return 400;
        }
    }
}
