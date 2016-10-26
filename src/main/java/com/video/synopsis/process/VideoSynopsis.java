package com.video.synopsis.process;

import com.video.synopsis.utils.PropUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VideoSynopsis {

    private static final Logger _log = LoggerFactory.getLogger(VideoSynopsis.class);

    static {
        System.setProperty("hadoop.home.dir", "D:\\apps\\hadoop\\hadoop-2.6.0");
    }

    @Autowired
    private PropUtils propUtils;

    /**
     * @param videoName 原始视频文件的路径
     * @return
     */
    public String process(String videoName) {

        SparkConf sparkConf = new SparkConf();
        sparkConf.setAppName("VideoSynopsis");
        sparkConf.setMaster("spark://192.168.1.131:7077");
        String[] jars = new String[1];
        jars[0] = "/home/benedict/Downloads/VideoSynopsis.jar";
        sparkConf.setJars(jars);
        JavaSparkContext ctx = new JavaSparkContext(sparkConf);

        //"E:/video_synopsis/"
        String basePath = propUtils.getProperty("base.path");
        String backgroundPath = basePath.concat("background/");//差分出来的背景的保存路径
        String imagePath = basePath.concat("images");//差分出的含有动态目标的图片的保存路径
        String videoPath = basePath.concat("video"); //最终视频文件的保存路径

        _log.info(basePath);
        _log.info(videoName);
        _log.info(backgroundPath);
        _log.info(imagePath);
        _log.info(videoPath);

        // /opt/data/video_synopsis/source/3.avi
        BackgroundSubtraction bs = new BackgroundSubtraction();
        ImageToVideo it = new ImageToVideo();
        bs.getVideoFrames(videoName, imagePath, backgroundPath);//将视频差分成帧并保存图片
        String resultPath = it.generatedAimVideo(imagePath, backgroundPath, videoPath);//将差分的图片重新合成视频

        ctx.stop();
        return resultPath;
    }
}