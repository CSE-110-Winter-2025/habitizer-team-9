package edu.ucsd.cse110.habitizer.app.ui.tasks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

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


    public TaskListAdapter(Context context, List<Task> tasks) {
        super(context, 0, new ArrayList<>(tasks));
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

        binding.checkBox.setOnClickListener(v -> {
            task.checkOff();
            if(task.getIsCheckedOff())
                binding.taskTimestamp.setText("Task Checked Off");
            else
                binding.taskTimestamp.setText("Task Incomplete");
        });

        return binding.getRoot();
    }


}
