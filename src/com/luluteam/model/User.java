package com.luluteam.model;

import com.luluteam.utils.Deviation;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
     * 重点
     * 为用户添加用电记录
     * 在此处做一些统计
     *
     * @param record
     */
    public void addRecord(String[] record) {

        recordcount++;

        String periodid = record[2];
        double now_num;
        double last_num;
        double sumOfDay;
        boolean isModified = false;

        if (record[4].equals("") && record[5].equals("")) {
            //System.out.println("本次读数和上次读书均没有，舍弃记录。" + record[0] + "\t" + record[2] + "\t" + record[3]);
            return;
        }

        if (record[4].equals("") && !record[5].equals("")) {//缺失本次读数，此处补全了
            record[4] = record[5];
            record[6] = "0";
            isModified = true;
        }
        if (!record[4].equals("") && record[5].equals("")) {//缺失上次读数，此处补全了
            record[5] = record[4];
            record[6] = "0";
            isModified = true;
        }

        now_num = Double.valueOf(record[4]);
        last_num = Double.valueOf(record[5]);

        if (record[6].equals("")) {
            record[6] = Double.toString(now_num - last_num);
        }


        sumOfDay = Double.valueOf(record[6]);

        if (periods.containsKey(periodid)) {
            periods.get(periodid).add(periodid, now_num, last_num, sumOfDay, isModified);
        } else {
            periods.put(periodid, new Period(periodid, now_num, last_num, sumOfDay, isModified));
        }
    }

    public String printPeriod() {
        complementPeriod();

        StringBuilder sb = new StringBuilder();
        DecimalFormat df = new DecimalFormat("#0.00");
        double sumOfPeriod;
        double avgOfPeriod;
        double devOfPeriod;

        Period period;

        sb.append(id + ",");
        for (int i = 1; i <= 12; i++) {
            if (periods.containsKey(Integer.toString(i))) {

                period = periods.get(Integer.toString(i));
                sumOfPeriod = period.getValueAsSum();
                avgOfPeriod = period.getValueAsAvg();
                devOfPeriod = period.getValueAsDeviation();
                sb.append(df.format(sumOfPeriod) + ",");//保留两位小数
                sb.append(df.format(avgOfPeriod) + ",");//保留两位小数
                sb.append(df.format(devOfPeriod) + ",");//保留两位小数

                if (sumOfPeriod < 0.0) {
                    System.out.println("有负数 id: " + id + "\tmonth:" + i);
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
                    double i_start = periods.get(Integer.toString(i - 1)).start;
                    double i_end = periods.get(Integer.toString(i + 1)).end;
                    periods.put(i_periodid, new Period(i_periodid, i_end, i_start, 0, true));
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
        double start;
        double end;

        double sumOfPeriod;

        private int count;
        private int unmodifiedCount;

        private List<Double> unmodifiedSumOfDay;

        public Period(String id, double now_num, double last_num, double sumOfDay, boolean isModified) {
            this.id = id;
            this.max = now_num;
            this.min = last_num;

            this.start = last_num;

            sumOfPeriod = 0;

            count = 0;
            unmodifiedCount = 0;
            unmodifiedSumOfDay = new ArrayList<>();

            add(id, now_num, last_num, sumOfDay, isModified);

        }

        public void add(String id, double now_num, double last_num, double sumOfDay, boolean isModified) {
            if (!this.id.equals(id)) {
                System.out.println("记录与周期集合不符，插入失败：" + id + "\t" + now_num + "\t" + last_num);
                return;
            }

            this.max = now_num > this.max ? now_num : this.max;
            this.min = last_num < this.min ? last_num : this.min;

            this.end = now_num;//记录末尾读数

            sumOfPeriod += sumOfDay;//以第二种方式记录总用电量

            count++;

            if (!isModified) {
                unmodifiedSumOfDay.add(sumOfDay);
                unmodifiedCount++;
            }

        }

        public double getValueAsSum() {
            double value;
            if ((value = max - min) > 0) {
                return value;
            } else {
                return sumOfPeriod;
            }
        }

        public double getValueAsAvg() {
            if(unmodifiedCount==0)
            {
                return 0;
            }
            double sum = 0;
            for (Double d : unmodifiedSumOfDay) {
                sum += d;
            }
            return sum / unmodifiedCount;
        }

        public double getValueAsDeviation() {

            if(unmodifiedCount==0)
            {
                return 0;
            }
            return Deviation.ComputeVariance2(unmodifiedSumOfDay.toArray());
        }

    }


}
