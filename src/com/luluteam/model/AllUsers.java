package com.luluteam.model;

import com.luluteam.utils.WriteToFile;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by guan on 11/2/16.
 */
public class AllUsers {
    Map<String, User> map;

    public AllUsers(int num_of_users) {
        map = new HashMap<>(num_of_users);
    }

    public void addUser(String[] record) {
        if (map.containsKey(record[0])) {
            map.get(record[0]).addRecord(record);
        } else {
            map.put(record[0], new User(record));
        }
    }


    public void toPrint(String filename) {
        StringBuilder sb;
        for (String s : map.keySet()) {
//            System.out.println("用户："+s+"每个周期的数据：");
            sb=new StringBuilder();
            sb.append(s+":");
            Map<String, User.Period> periods = map.get(s).getPeriods();

            for (int i = 1; i <= 12; i++) {
                if (periods.containsKey(Integer.toString(i))) {
                    sb.append(periods.get(Integer.toString(i)).getPeriodValue() + ":");
                } else {
                    sb.append(":");
                }
            }
            System.out.println();
            sb.append("\n");
            WriteToFile.writeToSD(filename,sb.toString(),true);
        }
    }

}
