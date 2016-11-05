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
        double sum;

        if (record[4].equals("") && record[5].equals("")) {
            //System.out.println("本次读数和上次读书均没有，舍弃记录。" + record[0] + "\t" + record[2] + "\t" + record[3]);
            return;
        }

        if (record[4].equals("") && !record[5].equals("")) {//缺失本次读数，此处补全了
            record[4] = record[5];
            record[6] = "0";
        }
        if (!record[4].equals("") && record[5].equals("")) {//缺失上次读数，此处补全了
            record[5] = record[4];
            record[6] = "0";
        }

        now_num = Double.valueOf(record[4]);
        last_num = Double.valueOf(record[5]);

        if (record[6].equals("")) {
            record[6] = Double.toString(now_num - last_num);
        }


        sum = Double.valueOf(record[6]);

        if (periods.containsKey(periodid)) {
            periods.get(periodid).add(periodid, now_num, last_num, sum);
        } else {
            periods.put(periodid, new Period(periodid, now_num, last_num));
        }
    }

    public String printPeriod() {
        complementPeriod();

        StringBuilder sb = new StringBuilder();
        DecimalFormat df = new DecimalFormat("#0.00");
        double value;

        sb.append(id + ",");
        for (int i = 1; i <= 12; i++) {
            if (periods.containsKey(Integer.toString(i))) {

                value = periods.get(Integer.toString(i)).getPeriodValue();
                sb.append(df.format(value) + ",");//保留两位小数

                if (value < 0.0) {
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
                    periods.put(i_periodid, new Period(i_periodid, i_end, i_start));
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

        double sumAsPeriod;

        private int count = 0;

        public Period(String id, double now_num, double last_num) {
            this.id = id;
            this.max = now_num;
            this.min = last_num;

            this.start = last_num;

            sumAsPeriod = 0;

            count = 0;
        }

        public void add(String id, double now_num, double last_num, double sum) {
            if (!this.id.equals(id)) {
                System.out.println("记录与周期集合不符，插入失败：" + id + "\t" + now_num + "\t" + last_num);
                return;
            }

            this.max = now_num > this.max ? now_num : this.max;
            this.min = last_num < this.min ? last_num : this.min;

            this.end = now_num;//记录末尾读数

            sumAsPeriod += sum;//以第二种方式记录总用电量

            count++;

        }

        public double getPeriodValue() {
           double value;
            if ((value=max-min)>0)
            {
                return value;
            }else {
                return sumAsPeriod;
            }
        }

    }


}
