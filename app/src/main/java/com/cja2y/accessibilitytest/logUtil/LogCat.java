package com.cja2y.accessibilitytest.logUtil;

import android.support.annotation.CheckResult;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * @author Jack Tony
 * @date 2015/12/4
 */
public class LogCat {

    private static final List<String> DEFAULT_COMMAND = new ArrayList<>();

    static {
        DEFAULT_COMMAND.add("logcat");
    }

    private List<String> commandLine;

    private static LogCat mInstance = null;

    public static LogCat getInstance() {
        if (mInstance == null) {
            mInstance = new LogCat();
        }
        return mInstance;
    }

    private LogCat() {
        commandLine = new ArrayList<>(DEFAULT_COMMAND);
    }

    public LogCat options(Options options) {
        commandLine.add(LogParser.parse(options));
        return this;
    }

    public LogCat recentLines(@IntRange(from = 0) int lineSize) {
        commandLine.add("-t");
        commandLine.add(String.valueOf(lineSize));
        return this;
    }

    /**
     * @param tag
     */
    @CheckResult
    public LogCat filter(@NonNull String tag) {
        return filter(tag, Level.VERBOSE); //
    }


    @CheckResult
    public LogCat filter(@Nullable String tag, Level lev) {
        String levStr = LogParser.parse(lev);

        if (!TextUtils.isEmpty(tag)) {
            commandLine.add(tag.trim() + ":" + levStr);
           // Log.d("ddd", "filter: " + commandLine.get(commandLine.size()-1));
            commandLine.add("*:S");
        } else {
            commandLine.add("*:" + levStr);
        }
        return this;
    }

    @CheckResult
    public LogCat withTime() {
        commandLine.add("-v");
        commandLine.add("time");
        return this;
    }

    @CheckResult
    public LogCat clear() {
        commandLine.add("-c");
        return this;
    }

    public Process commit() {
        Process exec = null;
        try {
            exec = Runtime.getRuntime().exec(commandLine.toArray(new String[this.commandLine.size()]));
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            commandLine = new ArrayList<>(DEFAULT_COMMAND);
        }
        return exec;
    }

}
