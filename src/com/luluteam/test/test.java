package com.luluteam.test;

import java.util.DoubleSummaryStatistics;

/**
 * Created by guan on 11/2/16.
 */
public class test {

    public static void main(String[] args)
    {
        String s="12,1,2,,,";
        String[] ss=s.split(",",6);

        for (String tmp:ss)
        {
//            System.out.println(tmp+"Double:"+ Double.valueOf(tmp));
            System.out.println(tmp);
        }
    }

}
