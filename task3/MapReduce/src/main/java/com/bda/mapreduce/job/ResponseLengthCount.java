package com.bda.mapreduce.job;

import com.bda.mapreduce.model.LogInfo;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

public class ResponseLengthCount {

    public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable> {

        private final static IntWritable one = new IntWritable(1);

        // value provides one line, map gets called multiple times
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();

            LogInfo logInfo = new LogInfo();
            logInfo.parse(line);

            String responseLength = logInfo.getResponseLength();
            String lengthClass = getLengthClass(responseLength);

            context.write(new Text(lengthClass), one);
        }

        private String getLengthClass(String responseLengthString) {

            if (responseLengthString.equals("-")){
                return "-";
            }

            int responseLength = Integer.parseInt(responseLengthString);

            if(responseLength == 0){
                return "0";
            }
            if(responseLength < 10){
                return "0-9";
            }
            if(responseLength < 100){
                return "10-99";
            }
            if(responseLength < 1000){
                return "100-999";
            }
            if(responseLength < 10000){
                return "1.000-9.999";
            }
            if(responseLength < 100000){
                return "10.000-99.999";
            }
            if(responseLength < 1000000){
                return "100.000-999.999";
            }
            else {
                return "> 1.000.000";
            }
        }
    }

    public static class IntSumReducer extends Reducer<Text,IntWritable,Text,IntWritable> {
        private IntWritable result = new IntWritable();

        public void reduce(Text key, Iterable<IntWritable> values, Context context) throws IOException, InterruptedException {
            int sum = 0;
            for (IntWritable val : values) {
                sum += val.get();
            }
            result.set(sum);
            context.write(key, result);
        }
    }


    public static void run(String inputFile, String outputPath) throws Exception {
        Configuration conf = new Configuration();
        Job job = Job.getInstance(conf, "hours count");
        job.setJarByClass(ResponseLengthCount.class);
        job.setMapperClass(ResponseLengthCount.TokenizerMapper.class);
        job.setCombinerClass(ResponseLengthCount.IntSumReducer.class);
        job.setReducerClass(ResponseLengthCount.IntSumReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(inputFile));
        FileOutputFormat.setOutputPath(job, new Path(outputPath));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }

}
