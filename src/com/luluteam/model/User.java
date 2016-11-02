package com.luluteam.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by guan on 11/2/16.
 */
public class User {
    private String id;
    private int periodsize;
    private Map<String, Period> periods;//<周期id，每个周期具体的数据>

    public Map<String, Period> getPeriods() {
        return periods;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getPeriodsize() {
        return periodsize;
    }

    public void setPeriodsize(int periodsize) {
        this.periodsize = periodsize;
    }

    public User(String[] record) {
        this.id = record[0];
        periods = new HashMap<>();
        addRecord(record);

    }

    /**
     * 为用户添加用电记录
     * 在此处做一些统计
     *
     * @param record
     */
    public void addRecord(String[] record) {


        String periodid = record[2];
        double max;
        double min;

        if (record[4].equals("")) {
            max = Double.MIN_VALUE;
        } else {
            max = Double.valueOf(record[4]);
        }

        if (record[5].equals("")) {
            min = Double.MAX_VALUE;
        } else {
            min = Double.valueOf(record[5]);
        }

        if (periods.containsKey(periodid)) {
            periods.get(periodid).add(periodid, max, min);
        } else {
            periods.put(periodid, new Period(periodid, max, min));
        }
    }

    /**
     *
     */
    public class Period {
        String id;
        double max;
        double min;

        public Period(String id, double max, double min) {
            this.id = id;
            this.max = max;
            this.min = min;
        }

        public void add(String id, double max, double min) {
            if (!this.id.equals(id)) {
                System.out.println("记录与周期集合不符，插入失败：" + id + "\t" + max + "\t" + min);
            }

            this.max = max > this.max ? max : this.max;

            this.min = min < this.min ? min : this.min;

        }

        public double getPeriodValue() {
            return max - min;
        }

    }


}
