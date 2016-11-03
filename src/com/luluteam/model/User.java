package com.luluteam.model;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by guan on 11/2/16.
 */
public class User {
    private String id;
    private String MAX_STRING = "1000000";
    private int periodsize;


    private int recordcount = 0;

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

    public int getRecordcount() {
        return recordcount;
    }

    //=================================================

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

        recordcount++;

        String periodid = record[2];
        double max;
        double min;

        if (record[4].equals("") && record[5].equals("")) {
            //System.out.println("本次读数和上次读书均没有，舍弃记录。" + record[0] + "\t" + record[2] + "\t" + record[3]);
            return;
        }

        if (record[4].equals("") && !record[5].equals("")) {//缺失本次读数，此处补全了
            record[4] = record[5];
        }
        if (!record[4].equals("") && record[5].equals("")) {//缺失上次读数，此处补全了
            record[5] = record[4];
        }

        max = Double.valueOf(record[4]);
        min = Double.valueOf(record[5]);


        if (periods.containsKey(periodid)) {
            periods.get(periodid).add(periodid, max, min);
        } else {
            periods.put(periodid, new Period(periodid, max, min));
        }
    }

    public String printPeriod() {
        complementPeriod();

        StringBuilder sb = new StringBuilder();
        DecimalFormat df = new DecimalFormat("#.00");
        double value;

        sb.append(id + ",");
        for (int i = 1; i <= 12; i++) {
            if (periods.containsKey(Integer.toString(i))) {

                value = periods.get(Integer.toString(i)).getPeriodValue();
                sb.append(df.format(value) + ",");//保留两位小数

                if (value<0.0)
                {
                    System.out.println("有负数 id: "+id+"\tmonth:"+i);
                }

            } else {
                sb.append(",");
            }
        }

        sb.append("\n");

        return sb.toString();
    }


    /**
     * 补全空缺月用电总量
     */
    private void complementPeriod() {
        for (int i = 2; i <= 11; i++) {
            if (!periods.containsKey(Integer.toString(i))) {

                if (periods.containsKey(Integer.toString(i - 1)) && periods.containsKey(Integer.toString(i + 1))) {
                    String i_periodid = Integer.toString(i);
                    double i_min = periods.get(Integer.toString(i - 1)).max;
                    double i_max = periods.get(Integer.toString(i + 1)).min;
                    periods.put(i_periodid, new Period(i_periodid, i_max, i_min));
                }
                i++;
            }
        }
    }

    /**
     *
     */
    public class Period {
        String id;
        double max;
        double min;

        private int count = 0;

        public Period(String id, double max, double min) {
            this.id = id;
            this.max = max;
            this.min = min;
            count = 0;
        }

        public void add(String id, double max, double min) {
            if (!this.id.equals(id)) {
                System.out.println("记录与周期集合不符，插入失败：" + id + "\t" + max + "\t" + min);
                return;
            }

            this.max = max > this.max ? max : this.max;

            this.min = min < this.min ? min : this.min;

            count++;

        }

        public double getPeriodValue() {
            return max - min;
        }

    }


}
