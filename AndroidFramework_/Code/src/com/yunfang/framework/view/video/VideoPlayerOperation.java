package com.yunfang.framework.view.video;

import java.io.IOException;

/**
 * @ClassName: VideoPlayerOperation
 * @Description: 视频播放器操作接口
 * @author 贺隽
 * @date 2015-7-8 19:33:21
 * 
 */
public interface VideoPlayerOperation {

	boolean isPlaying();

	int getCurrentPosition();

	void seekPosition(int position);

	void stopPlay();

	void playVideo(String path) throws IllegalArgumentException, SecurityException, IllegalStateException, IOException;

	void pausedPlay();

	void resumePlay();

}
