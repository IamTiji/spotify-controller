package com.tiji.spotify_controller.util;

import com.google.gson.annotations.JsonAdapter;
import net.minecraft.Util;

@JsonAdapter(InterpolatedTimeAdapter.class)
public class InterpolatedTime {
    protected long interpolateStart;
    protected long startTime;

    public InterpolatedTime(long startTime) {
        resetTime(startTime);
    }

    public long getInterpolatedTime() {
        long elapsed = Util.getMillis() - interpolateStart;
        return startTime + elapsed;
    }

    public void resetTime(long startTime) {
        this.interpolateStart = Util.getMillis();
        this.startTime = startTime;
    }

    public static InterpolatedTime nonProgressing(long time) {
        return new InterpolatedTime(time) {
            @Override
            public long getInterpolatedTime() {
                return startTime;
            }
        };
    }

    public static InterpolatedTime nonProgressing() {
        return nonProgressing(0);
    }

    public static InterpolatedTime optionalProgression(long time, boolean progressing) {
        return progressing ? new InterpolatedTime(time) : nonProgressing(time);
    }
}