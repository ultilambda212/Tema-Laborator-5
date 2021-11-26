package demo;

import java.util.Comparator;

public class SortTaskByTitle implements Comparator<TaskModel> {

    public int compare(TaskModel a, TaskModel b) {
        return a.getTitle().compareTo(b.getTitle());
    }
}
