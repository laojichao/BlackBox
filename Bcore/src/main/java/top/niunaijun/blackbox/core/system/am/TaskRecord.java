package top.niunaijun.blackbox.core.system.am;

import android.content.Intent;

import java.util.LinkedList;
import java.util.List;

/**
 * Represents a task (back stack) within the virtual environment's activity management.
 * Virtualizes the Android task concept, grouping related {@link ActivityRecord} instances
 * by task affinity and maintaining their ordering.
 *
 * <p>Used by {@link ActivityStack} to track which activities belong to the same task,
 * enabling correct handling of launch modes (singleTask, singleInstance) and back navigation.</p>
 */
public class TaskRecord {
    /** The Android task ID assigned by the host system. */
    public int id;
    /** The virtual user ID this task belongs to. */
    public int userId;
    /** The task affinity string, typically derived from the activity's package or manifest setting. */
    public String taskAffinity;
    /** The original intent that created this task (used for task-to-front comparison). */
    public Intent rootIntent;
    /** Ordered list of activities in this task, bottom-to-top. */
    public final List<ActivityRecord> activities = new LinkedList<>();

    /**
     * Constructs a new TaskRecord with the given ID, user, and affinity.
     *
     * @param id          the Android task ID
     * @param userId      the virtual user ID
     * @param taskAffinity the task affinity string
     */
    public TaskRecord(int id, int userId, String taskAffinity) {
        this.id = id;
        this.userId = userId;
        this.taskAffinity = taskAffinity;
    }

    /**
     * Checks whether all activities in this task are finished, indicating a new task should be
     * created instead of reusing this one.
     *
     * @return true if all activities are finished or the task is empty
     */
    public boolean needNewTask() {
        for (ActivityRecord activity : activities) {
            if (!activity.finished) {
                return false;
            }
        }
        return true;
    }

    /**
     * Adds an activity to the top of this task.
     *
     * @param record the activity record to add
     */
    public void addTopActivity(ActivityRecord record) {
        activities.add(record);
    }

    /**
     * Removes an activity from this task.
     *
     * @param record the activity record to remove
     */
    public void removeActivity(ActivityRecord record) {
        activities.remove(record);
    }

    /**
     * Returns the topmost non-finished activity in this task, searching from the top down.
     *
     * @return the top activity record, or null if all activities are finished
     */
    public ActivityRecord getTopActivityRecord() {
        for (int i = activities.size() - 1; i >= 0; i--) {
            ActivityRecord activityRecord = activities.get(i);
            if (!activityRecord.finished) {
                return activityRecord;
            }
        }
        return null;
    }
}
