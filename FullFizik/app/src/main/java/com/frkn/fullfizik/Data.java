package com.frkn.fullfizik;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by frkn on 21.06.2017.
 */

public class Data {

    public static ArrayList<ActivityStyle13Model> chapterList = new ArrayList<>();
    public static ArrayList<ActivityStyle13Model> testList = new ArrayList<>();
    public static ArrayList<ActivityStyle13Model> specialList = new ArrayList<>();

    public enum Account { BASIC, STANDART, PREMIUM, VIP };
    public static JSONObject inceptionJson = null;
    public static int chaptersCount, testsCount, specialsCount;

    public static Account accountType = Account.BASIC;
    public static int BUFFER_SIZE = 1024;

    public static ArrayList<ActivityStyle13Model> getList(int id) {
        switch (id) {
            case 0:
                return testList;
            case 1:
                return chapterList;
            case 2:
                return specialList;
            default:
                return chapterList;
        }
    }

}
