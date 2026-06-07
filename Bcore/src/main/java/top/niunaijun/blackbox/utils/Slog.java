/*
 * Copyright (C) 2006 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package top.niunaijun.blackbox.utils;

import android.util.Log;

/**
 * A thin logging wrapper around Android's {@link Log} class. Provides convenience methods
 * for all standard log levels (VERBOSE, DEBUG, INFO, WARN, ERROR) with optional
 * {@link Throwable} stack trace appending. Also exposes a raw
 * {@link #println(int, String, String)} method for custom priority levels.
 *
 * <p>This class mirrors the system-level {@code android.util.Slog} API surface for use
 * in environments where the system logging API is unavailable.
 */
public final class Slog {
    /** System log buffer ID. */
    public static final int LOG_ID_SYSTEM = 3;

    private Slog() {
    }

    /**
     * Logs a verbose message.
     *
     * @param tag the log tag for identifying the source
     * @param msg the message to log
     * @return the number of bytes written
     */
    public static int v(String tag, String msg) {
        return Log.println(Log.VERBOSE, tag, msg);
    }

    /**
     * Logs a verbose message with an associated throwable stack trace.
     *
     * @param tag the log tag for identifying the source
     * @param msg the message to log
     * @param tr the throwable whose stack trace will be appended
     * @return the number of bytes written
     */
    public static int v(String tag, String msg, Throwable tr) {
        return Log.println(Log.VERBOSE, tag,
                msg + '\n' + Log.getStackTraceString(tr));
    }

    /**
     * Logs a debug message.
     *
     * @param tag the log tag for identifying the source
     * @param msg the message to log
     * @return the number of bytes written
     */
    public static int d(String tag, String msg) {
        return Log.println(Log.DEBUG, tag, msg);
    }


    /**
     * Logs a debug message with an associated throwable stack trace.
     *
     * @param tag the log tag for identifying the source
     * @param msg the message to log
     * @param tr the throwable whose stack trace will be appended
     * @return the number of bytes written
     */
    public static int d(String tag, String msg, Throwable tr) {
        return Log.println(Log.DEBUG, tag,
                msg + '\n' + Log.getStackTraceString(tr));
    }

    /**
     * Logs an info message.
     *
     * @param tag the log tag for identifying the source
     * @param msg the message to log
     * @return the number of bytes written
     */
    public static int i(String tag, String msg) {
        return Log.println(Log.INFO, tag, msg);
    }

    /**
     * Logs an info message with an associated throwable stack trace.
     *
     * @param tag the log tag for identifying the source
     * @param msg the message to log
     * @param tr the throwable whose stack trace will be appended
     * @return the number of bytes written
     */
    public static int i(String tag, String msg, Throwable tr) {
        return Log.println(Log.INFO, tag,
                msg + '\n' + Log.getStackTraceString(tr));
    }

    /**
     * Logs a warning message.
     *
     * @param tag the log tag for identifying the source
     * @param msg the message to log
     * @return the number of bytes written
     */
    public static int w(String tag, String msg) {
        return Log.println(Log.WARN, tag, msg);
    }


    /**
     * Logs a warning message with an associated throwable stack trace.
     *
     * @param tag the log tag for identifying the source
     * @param msg the message to log
     * @param tr the throwable whose stack trace will be appended
     * @return the number of bytes written
     */
    public static int w(String tag, String msg, Throwable tr) {
        return Log.println(Log.WARN, tag,
                msg + '\n' + Log.getStackTraceString(tr));
    }

    /**
     * Logs a warning with only a throwable stack trace (no message).
     *
     * @param tag the log tag for identifying the source
     * @param tr the throwable whose stack trace will be logged
     * @return the number of bytes written
     */
    public static int w(String tag, Throwable tr) {
        return Log.println(Log.WARN, tag, Log.getStackTraceString(tr));
    }

    /**
     * Logs an error message.
     *
     * @param tag the log tag for identifying the source
     * @param msg the message to log
     * @return the number of bytes written
     */
    public static int e(String tag, String msg) {
        return Log.println(Log.ERROR, tag, msg);
    }


    /**
     * Logs an error message with an associated throwable stack trace.
     *
     * @param tag the log tag for identifying the source
     * @param msg the message to log
     * @param tr the throwable whose stack trace will be appended
     * @return the number of bytes written
     */
    public static int e(String tag, String msg, Throwable tr) {
        return Log.println(Log.ERROR, tag,
                msg + '\n' + Log.getStackTraceString(tr));
    }

    /**
     * Logs a message at the specified priority level.
     *
     * @param priority the log priority level (e.g., {@link Log#DEBUG}, {@link Log#ERROR})
     * @param tag the log tag for identifying the source
     * @param msg the message to log
     * @return the number of bytes written
     */
    public static int println(int priority, String tag, String msg) {
        return Log.println(priority, tag, msg);
    }
}

