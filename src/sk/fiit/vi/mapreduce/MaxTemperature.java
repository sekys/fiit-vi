package sk.fiit.vi.mapreduce;

/**
 * Created by Seky on 28. 9. 2014.
 */

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

public class MaxTemperature extends Configured implements Tool {

    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new MaxTemperature(), args);
        System.exit(res);
        // HadoopApprovals.verifyMapReduce(new WordCountMapper(), new WordCountReducer(), 0, "cat cat dog");
    }

    @Override
    public int run(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: MaxTemperature <input path> <output path>");
            return -1;
        }

        // When implementing tool
        Configuration conf = this.getConf();
        //conf.set("mapreduce.map.java.opts", "-agentlib:jdwp=transport=dt_socket,server=n,address=Seky-PC:5000,suspend=y");

        // Create job
        Job job = new Job(conf);
        job.setJarByClass(MaxTemperature.class);
        job.setJobName("Parse people");

        FileInputFormat.addInputPath(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));

        job.setMapperClass(MaxTemperatureMapper.class);
        job.setReducerClass(MaxTemperatureReducer.class);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        // Execute job and return status
        return job.waitForCompletion(true) ? 0 : 1;
    }
}
