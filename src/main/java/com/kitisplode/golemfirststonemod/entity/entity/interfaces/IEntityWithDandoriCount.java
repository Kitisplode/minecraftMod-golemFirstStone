package com.kitisplode.golemfirststonemod.entity.entity.interfaces;

public interface IEntityWithDandoriCount
{
    void recountDandori();
    void setRecountDandori();

    int getTotalDandoriCount();
    int getDandoriCountBlue();
    int getDandoriCountRed();
    int getDandoriCountYellow();
    int getDandoriCountIron();
    int getDandoriCountSnow();
    int getDandoriCountFirstStone();
    int getDandoriCountFirstOak();
    int getDandoriCountFirstBrick();
    int getDandoriCountFirstDiorite();
}
