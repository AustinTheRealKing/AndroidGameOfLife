package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.ColorInt;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.TextView;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dave on 10/11/16.
 */

public class GameOfLifeFragment extends Fragment {
    private final static String TAG = "GAME_O_LIFE";
    // constants for X & O
    private final static int ALIVE = 1;
    private final static int DEAD = 0;

    private final static int PLAYING = 1;
    private final static int WAITING = 0;

    private String mSaveFileLocation;

    private TextView mTextView;
    private Button mColorOneButton;
    private Button mColorTwoButton;
    private Button mColorThreeButton;
    // Game State
    private Cell[] mCells = new Cell[400];
    private int generation;
    @ColorInt
    private int aliveColor = Color.argb(255, 102, 255, 255);
    @ColorInt
    private int deadColor = Color.argb(1, 1, 1, 0);
    @ColorInt
    private  int mColorOne = Color.argb(255 ,244,67, 54);
    @ColorInt
    private  int mColorTwo = Color.argb(255,0,188, 212);
    @ColorInt
    private  int mColorThree = Color.argb(255,228,142, 255);

    // RecyclerView Stuff
    private RecyclerView mRecycler;
    private RecyclerView.Adapter<CellHolder> mAdapter = new CellAdapter();
    private final Handler gameHandler = new Handler();
    private int playState = WAITING;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_game_of_life, container, false);
        // setup recycler view
        //get local file location for saving and loading
        mSaveFileLocation = getContext().getFilesDir().getPath() + "/save_file.txt";
        //create cell array
        for (int i = 0; i < 400; i++)
        {
            mCells[i] = new Cell(aliveColor, deadColor);
        }
        
        Intent intent = getActivity().getIntent();

        if (intent.getExtras() != null) {
            if (intent.getExtras().containsKey("CELLS_EXTRA")) {
                mCells = (Cell[])intent.getSerializableExtra("CELLS_EXTRA");
            }
        }

        mRecycler = (RecyclerView) v.findViewById(R.id.reycler_tic_tac_toe);
        mRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 20));
        mRecycler.setAdapter(mAdapter);

        mTextView = (TextView) v.findViewById(R.id.textView);

        // just recreate activity when want to play again
        final Button startButton = (Button) v.findViewById(R.id.start_button);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if we are currently waiting then start
                if (playState == WAITING)
                {
                    startButton.setText("Stop");
                    startGameLoop();
                }
                //if we are running the game then pause it
                else if (playState == PLAYING) {
                    startButton.setText("Start");
                    stopGameLoop();
                }
            }
        });
        //setting up some button listeners
        Button openButt = (Button) v.findViewById(R.id.open_button);
        openButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFromFile();
            }
        });

        mColorOneButton = (Button) v.findViewById(R.id.color_one);
        mColorOneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecycler.setBackgroundColor(mColorOne);
            }
        });

        mColorTwoButton = (Button) v.findViewById(R.id.color_two);
        mColorTwoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecycler.setBackgroundColor(mColorTwo);
            }
        });

        mColorThreeButton = (Button) v.findViewById(R.id.color_three);
        mColorThreeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mRecycler.setBackgroundColor(mColorThree);
            }
        });


        // just recreate activity when want to play again
        Button resetButton = (Button) v.findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().recreate();
            }
        });
        
        Button saveButton = (Button) v.findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    FileOutputStream fos= new FileOutputStream(mSaveFileLocation);
                    ObjectOutputStream oos= new ObjectOutputStream(fos);
                    oos.writeObject(mCells);
                    oos.close();
                    fos.close();
                }catch(IOException ioe){
                    ioe.printStackTrace();
                }
            }
        });
        
        Button cloneButton = (Button) v.findViewById(R.id.clone_button);
        cloneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GameOfLifeActivity gameActivity = new GameOfLifeActivity();
                Intent intent = gameActivity.cloneFragment(getActivity(), mCells);
                startActivity(intent);
            }
        });
        return v;
    }

    //method for loading cell array from file
    private void loadFromFile()
    {
        try {
            //create input stream
            FileInputStream fis = new FileInputStream(mSaveFileLocation);
            //wrap object stream around it
            ObjectInputStream is = new ObjectInputStream(fis);
            //put the file into the cell array
            mCells = (Cell[]) is.readObject();
            is.close();
            fis.close();
            for (int i = 0; i < 400; i++)
            {
                //make sure the map updates
                mAdapter.notifyItemChanged(i); // reload ViewHolder
            }

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //used to begin the game infinite looping
    private void startGameLoop()
    {
        //set the "framerate" for the game
        final int delay = 1000;
        gameHandler.postDelayed(new Runnable(){
            public void run(){
                //assigns this callback to be run every second then has it loop in a second
                gameLoop();
                gameHandler.postDelayed(this, delay);
            }
        }, delay);
        //change the state
        playState = PLAYING;
    }

    //used to stop looping game
    private void stopGameLoop()
    {
        //removes the callback so it stops looping
        gameHandler.removeCallbacksAndMessages(null);
        playState = WAITING;
    }

    //this is the actual game loop itself
    private void gameLoop()
    {
        //array to hold what needs to be swapped. we did it like this to make sure we dont change the grid while we are still processing it
        List<Integer> inverts = new ArrayList<>();
        int count;
        //for each cell
        for (int i = 0; i < mAdapter.getItemCount(); i++)
        {
            //get number of alive neighbors
            count = getNumAliveNeighbors(i);
            //if its alive
            if (mCells[i].getStatus() == ALIVE)
            {
                //check if it should invert
                if (count != 2 && count != 3)
                {
                    inverts.add(i);
                }
            } else if (mCells[i].getStatus() != ALIVE)
            {
                //do the same but with different requirements
                if (count == 3)
                {
                    inverts.add(i);
                }
            }
        }

        //actually invert all the cells that need it and update the display
        for (int index: inverts) {
            mCells[index].invert();
            mAdapter.notifyItemChanged(index); // reload ViewHolder
        }

    }

    //converts an (X,Y) position for a 2d array to a 1d index
    private int xyToI(int x, int y) {
        //all these if statements are to make sure it wraps correctly
        if (x >= 20)
        {
            x -= 20;
        }
        if (y >= 20)
        {
            y -= 20;
        }

        if (x< 0)
        {
            x += 20;
        }

        if (y < 0)
        {
            y += 20;
        }
        return x + 20 * y;
    }

    private int getNumAliveNeighbors(int i)
    {
        //get the indexes of the neighbors
        int[] neighbors = getNeighborIndicies(i);
        int count = 0;
        for (int j = 0; j < 8; j++)
        {
            if (mCells[neighbors[j]].getStatus() == ALIVE)
            {
                count++;
            }
        }
        return count;
    }

    private int[] getNeighborIndicies(int i)
    {
        int index = 0;
        int[] neighbors = new int[8];
        //convert the 1d position of this cell to a 2d position
        int myX = i % 20;
        int myY = i / 20;
        for (int x = -1; x < 2; x++)
        {
            for (int y = -1; y < 2; y++)
            {
                //iterate over the 9 cell (3X3) area around the target, ignoring itself
                if (x == 0 && y == 0)
                {
                    continue;
                } else {
                    //we had to convert to 2d coordinates to fix wrapping
                    neighbors[index] =  xyToI(myX + x, myY + y);
                    index++;
                }
            }
        }
        return neighbors;
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
                                               mCells[mPosition].invert();
                                               Log.d(TAG, ""+mPosition+": "+getNumAliveNeighbors(mPosition));
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

            if (mCells[position].getStatus() == ALIVE) {
                holder.mButton.setBackgroundColor(aliveColor);
                Animation mAnimation = new AlphaAnimation(1, 0);
                mAnimation.setDuration(200);
                mAnimation.setInterpolator(new LinearInterpolator());
                mAnimation.setRepeatCount(Animation.INFINITE);
                mAnimation.setRepeatMode(Animation.REVERSE);
                holder.mButton.startAnimation(mAnimation);
            } else if (mCells[position].getStatus() == DEAD) {
                holder.mButton.setBackgroundColor(R.drawable.empty);
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
