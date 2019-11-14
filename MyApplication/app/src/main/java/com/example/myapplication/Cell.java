package com.example.myapplication;

import androidx.annotation.ColorInt;

import java.io.Serializable;

public class Cell implements Serializable {
    private static final int LIFESPAN = 10;
    private static final int COLORCHANGESPEED = 20;
    private int age;
    private int status = 0;
    @ColorInt
    private int aliveColor;
    @ColorInt
    private int deadColor;

    public Cell (int aliveColor, int deadColor){
        this.aliveColor = aliveColor;
        this.deadColor = deadColor;
    }

    public void setAliveColor(int aliveColor){this.aliveColor = aliveColor; }

    public void setDeadColor(int deadColor){this.deadColor = deadColor; }

    public void setAlive() {this.status = 1; }
    public void setDead() {this.status = 0; }

    public void invert() {this.status = (this.status == 1) ? 0 : 1;}

    /*public void setStatus(boolean newStatus) {
        this.status = newStatus;
    }*/

    public int getStatus() {
        return this.status;
    }

    public void resetAge() {
        this.age = 0;
    }

    /*public void age() {
        if (this.age >= 10) {
            this.status = false;
        } else {
            ++this.age;
        }

    }*/

    public void setAge(int a) {
        this.age = a;
    }

    public int getAge() {
        return this.age;
    }
}
