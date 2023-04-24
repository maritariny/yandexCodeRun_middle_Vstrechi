package ru.maritariny;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        int n = Integer.parseInt(reader.readLine());
        final String appoint = "APPOINT";
        final String print = "PRINT";

        HashMap<Integer,  ArrayList<HashMap<Date[], String[]>>> bookedDate = new HashMap<>();

        for (int i = 0; i < n; i++) {
            String[] str = reader.readLine().split(" ");

            if (str[0].equals(appoint)) {
                Integer day = Integer.parseInt(str[1]);
                String time = str[2];
                int duration = Integer.parseInt(str[3]);
                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                Date dateOne = format.parse(time);
                Date dateTwo = new Date(dateOne.getTime() + (duration - 1) * 60 * 1000);

                int k = Integer.parseInt(str[4]);
                String[] names = new String[k];
                for (int j = 5; j < (5 + k); j++) {
                    names[j-5] = str[j];
                }

                HashMap<Date[], String[]> bookedTime = new HashMap<>();
                Date[] times = new Date[2];
                times[0] = dateOne;
                times[1] = dateTwo;
                bookedTime.put(times, names);
                List<String> failNames = new ArrayList();
                boolean result = putMeet(bookedDate, day, bookedTime, failNames);
                if (result) {
                    System.out.println("OK");
                } else {
                    System.out.println("FAIL");
                    for (int j = 0; j < failNames.size(); j++) {
                        System.out.print(failNames.get(j));
                        if (j != failNames.size()) {
                            System.out.print(" ");
                        }
                    }
                    System.out.println();
                }
            } else if (str[0].equals(print)) {
                int day = Integer.parseInt(str[1]);
                String name = str[2];
                print(day, name, bookedDate);
            } else {
                break;
            }
        }
        reader.close();
    }

    public static void print (Integer day, String name,
                              HashMap<Integer,  ArrayList<HashMap<Date[], String[]>>> bookedDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        if (bookedDate.containsKey(day)) {
            Map<Date, String> toPrint = new TreeMap<>();

            ArrayList meetsOfDay = bookedDate.get(day);
            for (Object meetObject : meetsOfDay) {
                HashMap<Date[], String[]> meet = (HashMap<Date[], String[]>) meetObject;
                for (Map.Entry<Date[], String[]> entryMeet : meet.entrySet()) {
                    Date[] curTimes = entryMeet.getKey();
                    String[] curNames = entryMeet.getValue();

                    StringBuilder str = new StringBuilder();
                    StringBuilder names = new StringBuilder();
                    boolean hasMeet = false;
                    for (String curName : curNames) {
                        if (!names.isEmpty()) {
                            names.append(" ");
                        }
                        names.append(curName);
                        if (curName.equals(name)) {
                            hasMeet = true;
                        }
                    }
                    if (hasMeet && !name.isEmpty()) {
                        // 12:30 30 andrey alex
                        str.append(dateFormat.format(curTimes[0]));
                        str.append(" ");
                        long elapsedms = curTimes[1].getTime() - curTimes[0].getTime();
                        long duration = TimeUnit.MINUTES.convert(elapsedms, TimeUnit.MILLISECONDS) + 1;
                        str.append(duration);
                        str.append(" ");
                        str.append(names.toString());
                        toPrint.put(curTimes[0], str.toString());
                    }
                }
            }

            if (!toPrint.isEmpty()) {
                for (Map.Entry<Date, String> entry : toPrint.entrySet()) {
                    System.out.println(entry.getValue());
                }
            }
        }

    }
    public static boolean putMeet(HashMap<Integer,  ArrayList<HashMap<Date[], String[]>>> bookedDate,
                                  Integer day,
                                  HashMap<Date[], String[]> bookedTime,
                                  List<String> failNames) {
        // Если у кого-то из участников получается две встречи в один и тот же момент времени
        boolean result = true;

        for (Map.Entry<Date[], String[]> entry : bookedTime.entrySet()) {
            if (bookedDate.containsKey(day)) {
                ArrayList meetsOfDay = bookedDate.get(day);
                Date[] times = entry.getKey();
                String[] names = entry.getValue();
                for (String name : names) {
                    for (Object meetObject : meetsOfDay) {
                        HashMap<Date[], String[]> meet = (HashMap<Date[], String[]>) meetObject;
                        Date[] curTimes = null;
                        String[] curNames = null;
                        for (Map.Entry<Date[], String[]> entryMeet : meet.entrySet()) {
                            curTimes = entryMeet.getKey();
                            curNames = entryMeet.getValue();
                        }
                        for (String curName : curNames) {
                            if (name.equals(curName)) {
                                if (!((curTimes[0].after(times[1])) || (times[0].after(curTimes[1])))) {
                                    result = false;
                                    if (!failNames.contains(name)) {
                                        failNames.add(name);
                                    }
                                }
                            }
                        }
                    }
                }
                if (result) {
                    meetsOfDay.add(bookedTime);
                    bookedDate.put(day, meetsOfDay);
                }
            } else {
                ArrayList<HashMap<Date[], String[]>> arr = new ArrayList<HashMap<Date[], String[]>>();
                arr.add(bookedTime);
                bookedDate.put(day, arr);
            }
        }

        return result;
    }
}

