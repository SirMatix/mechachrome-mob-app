package com.example.mechachromemobileapp.Models;

/**
 *  Day Class
 *
 *  holds information about day in timetable,
 *  timeframe strings represent each time block,
 *  and order is user to differentiate between
 *  days of the week
 */
public class Day {

    private String name, group, mode;
    private String timeframe1, timeframe2, timeframe3, timeframe4, timeframe5, timeframe6;
    private int order;

    public Day() {
    }

    public String getName() {
        return name;
    }

    public String getGroup() {
        return group;
    }

    public String getMode() {
        return mode;
    }

    public String getTimeframe1() {
        return timeframe1;
    }

    public String getTimeframe2() {
        return timeframe2;
    }

    public String getTimeframe3() {
        return timeframe3;
    }

    public String getTimeframe4() {
        return timeframe4;
    }

    public String getTimeframe5() {
        return timeframe5;
    }

    public String getTimeframe6() {
        return timeframe6;
    }

    public int getOrder() {
        return order;
    }
}


