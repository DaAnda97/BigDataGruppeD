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
import java.util.regex.Pattern;

public class HostnameCount {
    public static class TokenizerMapper extends Mapper<Object, Text, Text, IntWritable> {

        private final static IntWritable one = new IntWritable(1);
        private static final Pattern IP_PATTERN = Pattern.compile(
                "^(([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");


        // value provides one line, map gets called multiple times
        public void map(Object key, Text value, Context context) throws IOException, InterruptedException {
            String line = value.toString();

            LogInfo logInfo = new LogInfo();
            logInfo.parse(line);

            String host = "";
            if (validateIp(logInfo.getHost())){
                host = "Ip";
            } else {
                host = "ResolvedHostname";
            }

            context.write(new Text(host), one);
        }

        public static boolean validateIp(final String ip) {
            return IP_PATTERN.matcher(ip).matches();
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
        Job job = Job.getInstance(conf, "hostname count");
        job.setJarByClass(HostnameCount.class);
        job.setMapperClass(HostnameCount.TokenizerMapper.class);
        job.setCombinerClass(HostnameCount.IntSumReducer.class);
        job.setReducerClass(HostnameCount.IntSumReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);
        FileInputFormat.addInputPath(job, new Path(inputFile));
        FileOutputFormat.setOutputPath(job, new Path(outputPath));
        System.exit(job.waitForCompletion(true) ? 0 : 1);
    }
}
