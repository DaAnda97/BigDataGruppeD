package com.bda.mapreduce;

import com.bda.mapreduce.job.HostnameCount;
import com.bda.mapreduce.job.HoursCount;
import com.bda.mapreduce.job.ResponseLengthCount;
import com.bda.mapreduce.job.StatusCodeCount;

public class Main {

    public static void main(String[] args) throws Exception {

        switch(args[0]){
            case "HoursCount":
                HoursCount.run(args[1], args[2]);
                break;
            case "ResponseLengthCount":
                ResponseLengthCount.run(args[1], args[2]);
                break;
            case "HostnameCount":
                HostnameCount.run(args[1], args[2]);
                break;
            case "StatusCodeCount":
                StatusCodeCount.run(args[1], args[2]);
                break;
            default:
                System.out.println(args[0] + " is not a available Job. You can choose between "
                        + "HoursCount "
                        + "ResponseLengthCount"
                        + "HostnameCount"
                        + "and StatusCodeCount");
        }
    }

}
