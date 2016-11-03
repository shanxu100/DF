package com.luluteam.main;

import com.luluteam.model.AllUsers;
import com.luluteam.utils.Deviation;
import com.luluteam.utils.ReadFromFile;
import com.luluteam.utils.WriteToFile;

import java.util.*;

/**
 * Created by guan on 11/1/16.
 */
public class Statistics {

    public static String DIR = "/home/guan/documents/lesson/";
    private static String ALL_USER_YONGDIAN_DATA_FILE = DIR + "all_user_yongdian_data_2015";
    private static String ALL_USER_YONGDIAN_DATA_FILE_NEW = DIR + "all_user_yongdian_data_2015_new";

    private static String user8639948250_FILE=DIR+"8639948250user";

    private static String TEST_FILE = DIR + "test";
    private static String TRAIN_FILE = DIR + "train";
    private static List<String> ALL_USER_YONGDIAN_DATA_LIST = new ArrayList<>();
    private static List<String> TRAIN_LIST = new ArrayList<>();


    //确定窃电用户的数据
    int QD_COUNT = 0;
    private Map<String, ArrayList<String>> QD_USER = new HashMap<>();//<用户，用电量>
    private AllUsers QD_AllUsers;


    //不确定用户数据
    int NOT_SURE_COUNT = 0;
    private Map<String, ArrayList<String>> NOT_SURE_USER = new HashMap<>();//<用户，用电量>
    private AllUsers NOT_SURE_AllUsers;


    //flag
    private static String QD_FLAG = "1";
    private static String NOT_SURE_FLAG = "0";

    public static void main(String[] args) {

        Statistics statistics = new Statistics();


        ALL_USER_YONGDIAN_DATA_LIST = ReadFromFile.readFileByLines(ALL_USER_YONGDIAN_DATA_FILE_NEW);

        TRAIN_LIST = ReadFromFile.readFileByLines(TRAIN_FILE);
        statistics.from_List_to_Map(TRAIN_LIST);

        //按月统计训练集中的每个人的月用电量
        statistics.statisticAsMonth();


    }

    /**
     * 把训练集的数据转换成Map存储
     *
     * @param list
     * @return
     */
    private void from_List_to_Map(List<String> list) {

        String[] tmp;
        for (String s : list) {
            tmp = s.split(",");
            if (tmp[1].equals(QD_FLAG)) {
                QD_USER.put(tmp[0], new ArrayList<String>());
                QD_COUNT++;
            } else {
                NOT_SURE_USER.put(tmp[0], new ArrayList<String>());
                NOT_SURE_COUNT++;
            }

        }

    }

    /**
     * 将训练集中的数据进行分类，分别记录窃电用户的 “用电量” 和 不确定用户的 “用电量”
     *
     * @param list
     */
    private void classifyTrain(List<String> list) {

        String[] tmp;
        for (String s : list) {
            tmp = s.split(",", 7);

            if (QD_USER.containsKey(tmp[0]))//如果这个用户是“窃电用户”
            {
                QD_USER.get(tmp[0]).add(tmp[4]);
                continue;
            }

            if (NOT_SURE_USER.containsKey(tmp[0])) {
                NOT_SURE_USER.get(tmp[0]).add(tmp[4]);
                continue;
            }

        }

    }

    private void classifyTrain2(List<String> list) {
        String[] tmp;

        for (String s : list) {
            tmp = s.split(",", 7);

            //recordTest(tmp);


            if (QD_USER.containsKey(tmp[0]))//如果这个用户是“窃电用户”
            {
                QD_AllUsers.addUser(tmp);
                continue;
            }

            if (NOT_SURE_USER.containsKey(tmp[0])) {
                NOT_SURE_AllUsers.addUser(tmp);
                continue;
            }

        }

    }

    /**
     * 按月统计训练集中的每个人的月用电量
     */
    public void statisticAsMonth() {
        QD_AllUsers = new AllUsers(QD_COUNT);
        NOT_SURE_AllUsers = new AllUsers(NOT_SURE_COUNT);
        classifyTrain2(ALL_USER_YONGDIAN_DATA_LIST);

        QD_AllUsers.toPrint("QD_AsMonth");
        NOT_SURE_AllUsers.toPrint("NOT_SURE_AsMonth");

    }

    /**
     * 计算方差的入口
     */
    public void statisticVariance() {
        classifyTrain(ALL_USER_YONGDIAN_DATA_LIST);

        System.out.println("训练集的数量：" + TRAIN_LIST.size());
        System.out.println("窃电用户的数量：" + QD_USER.size());
        System.out.println("不确定用户的数量：" + NOT_SURE_USER.size());

        StringBuilder sb1 = new StringBuilder();
        sb1.append("CONS_NO;variance;sum\n");
        System.out.println("每个窃电用户的数据：");
        for (String s : QD_USER.keySet()) {
            ArrayList<String> list = QD_USER.get(s);
            double variance = countVariance(list);
            double sum = countSum(list);
            sb1.append(s + ";" + variance + ";" + sum + "\n");
        }
        WriteToFile.writeToFile("QD_USER", sb1.toString(),false);


        StringBuilder sb2 = new StringBuilder();
        sb2.append("CONS_NO;variance;sum\n");
        System.out.println("每个不确定的用户的数据：");
        for (String s : NOT_SURE_USER.keySet()) {
            ArrayList<String> list = NOT_SURE_USER.get(s);
            double variance = countVariance(list);
            double sum = countSum(list);
            sb2.append(s + ";" + variance + ";" + sum + "\n");
        }
        WriteToFile.writeToFile("NOT_SURE_USER", sb2.toString(),false);
    }

    /**
     * 计算方差
     *
     * @param list
     * @return
     */
    private double countVariance(List<String> list) {
        double[] values = new double[list.size()];

        for (int i = 0; i < list.size(); i++) {
            values[i] = Double.valueOf(list.get(i));
        }

        return Deviation.ComputeVariance2(values);
    }

    /**
     * 计算和
     *
     * @param list
     * @return
     */
    private double countSum(List<String> list) {
        double sum = 0;
        for (String s : list) {
            sum += Double.valueOf(s);
        }
        return sum;
    }

    private void recordTest(String[] record)
    {
        if (record[4].equals("")&&!record[5].equals(""))
        {
            System.out.println("只有上次读数，没有本次读数："+ record[0]+"\t"+record[2]+"\t"+record[3]);
        }
    }

}
