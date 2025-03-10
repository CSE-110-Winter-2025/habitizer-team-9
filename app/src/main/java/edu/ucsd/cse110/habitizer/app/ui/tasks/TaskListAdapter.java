package edu.ucsd.cse110.habitizer.app.ui.tasks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.habitizer.app.R;
import edu.ucsd.cse110.habitizer.app.databinding.ListItemTaskBinding; //?? for tasks?
import edu.ucsd.cse110.habitizer.app.ui.routines.RoutinesFragment;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.habitizer.app.databinding.FragmentTasksBinding;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import java.util.*;

public class TaskListAdapter extends ArrayAdapter<Task> {
    private boolean routineCompleted = false;
    Routine taskRoutine;
    public TaskListAdapter(Context context, List<Task> tasks, Routine r) {
        super(context, 0, new ArrayList<>(tasks));
        taskRoutine = r;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        var task = getItem(position);
        assert task != null;

        ListItemTaskBinding binding;
        if (convertView != null) {
            binding = ListItemTaskBinding.bind(convertView);
        } else {
            var layoutInflater = LayoutInflater.from(getContext());
            binding = ListItemTaskBinding.inflate(layoutInflater, parent, false);
        }

        binding.taskName.setText(task.getName());
        binding.checkBox.setEnabled(!routineCompleted);
        binding.checkBox.setOnClickListener(v -> {
            if(routineCompleted) return;
            task.checkOff();
            if(task.getIsCheckedOff()) {
                int timeBetween = taskRoutine.routineTimer.getTaskTime();

                if (timeBetween < 60){ // plus 5 rounds up
                    binding.taskTimestamp.setText("" + (5 * (timeBetween/5) + 5) + "s");

                }
                else{ // plus 1 rounds up
                    binding.taskTimestamp.setText("" + (timeBetween/60 + 1) + "m");
                }


                // resets old elapsed time
                taskRoutine.routineTimer.oldElapsedTime = taskRoutine.routineTimer.getElapsedTimeInSeconds();

            }else {
                binding.taskTimestamp.setText("Task Incomplete");
            }
            checkAllTasksCompleted();
        });

        return binding.getRoot();
    }

    // Method iterating all tasks to check for completion
    private void checkAllTasksCompleted(){
        if(routineCompleted) return;
        boolean allChecked = true;
        for (int i = 0; i < getCount(); i++) {
            Task task = getItem(i);
            if (task != null && !task.getIsCheckedOff()) {
                allChecked = false;
                break;
            }
        }

        if(allChecked){
            routineCompleted = true;
            taskRoutine.routineTimer.stop();
            long totalTime = taskRoutine.routineTimer.getElapsedTimeInSeconds();
            // plus one for round up
            String message = "All tasks completed. Total time taken: " + (totalTime / 60 + 1) + "m";
            notifyCompletion(message);
        }
    }

    private void notifyCompletion(String message){
        if (completionListener != null){
            completionListener.onRoutineCompleted(message);
        }
    }

    public interface CompletionListener {
        void onRoutineCompleted(String message);
    }

    private CompletionListener completionListener;

    public void setCompletionListener(CompletionListener listener) {
        this.completionListener = listener;
    }

}
