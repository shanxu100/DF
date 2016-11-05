package com.luluteam.main;

import com.luluteam.model.AllUsers;
import com.luluteam.utils.ReadFromFile;

import java.util.*;

/**
 * Created by guan on 11/1/16.
 */
public class Statistics {

    public static String DIR = "/home/guan/documents/lesson/";
//    public static String DIR = "/Users/lianglitu/Documents/lesson/";

    private static String ALL_USER_YONGDIAN_DATA_FILE = DIR + "all_user_yongdian_data_2015";
    private static String ALL_USER_YONGDIAN_DATA_FILE_NEW = DIR + "all_user_yongdian_data_2015_new";

    private static String user_FILE = DIR + "9683587354user";

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



    private void classifyTrain(List<String> list) {
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
        classifyTrain(ALL_USER_YONGDIAN_DATA_LIST);

        QD_AllUsers.toPrint("QD_AsMonth");
        NOT_SURE_AllUsers.toPrint("NOT_SURE_AsMonth");

    }



    private void recordTest(String[] record) {
//        if (record[4].equals("") && !record[5].equals("")) {
//            System.out.println("只有上次读数，没有本次读数：" + record[0] + "\t" + record[2] + "\t" + record[3]);
//        }
//
        try {
//            if (!record[4].equals("")&&!record[5].equals("")&&record[6].equals(""))
//            {
//                System.out.println("只有读数，没有日用电量："+record[0] + "\t" + record[2] + "\t" + record[3]);
//            }
            if (record[4].equals("")&&record[5].equals("")&&!record[6].equals(""))
            {
                System.out.println("只有日用电量,没有读数："+record[0] + "\t" + record[2] + "\t" + record[3]);
            }

        }catch (Exception e)
        {
            System.out.println("异常"+record[0]+"\t"+record[2]+"\t"+record[3]);
            e.printStackTrace();
        }

    }

}
