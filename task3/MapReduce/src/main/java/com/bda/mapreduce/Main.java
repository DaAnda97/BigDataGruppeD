package com.bda.mapreduce;

public class Main {

    public static void main(String[] args) throws Exception {

        switch(args[0]){
            case "HoursCount":
                HoursCount.run(args[1], args[2]);
                break;
            default:
                System.out.println(args[0] + " is not a available Job. You can choose between HoursCount");
        }
    }

}
