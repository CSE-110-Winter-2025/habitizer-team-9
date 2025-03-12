package edu.ucsd.cse110.habitizer.app.ui.edit_routine;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

import edu.ucsd.cse110.habitizer.app.databinding.ListItemEditRoutineBinding;
import edu.ucsd.cse110.habitizer.app.ui.edit_routine.dialog.RenameTaskDialogFragment;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.Task;

public class EditRoutineAdapter extends ArrayAdapter<Task> {
    Routine taskRoutine;
    private Fragment fragment = null;

    public EditRoutineAdapter(Context context, List<Task> tasks, Routine r, Fragment fragment) {
        super(context, 0, new ArrayList<>(tasks));
        this.fragment = fragment;
        taskRoutine = r;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        var task = getItem(position);
        assert task != null;

        ListItemEditRoutineBinding binding;
        if (convertView != null) {
            binding = ListItemEditRoutineBinding.bind(convertView);
        } else {
            var layoutInflater = LayoutInflater.from(getContext());
            binding = ListItemEditRoutineBinding.inflate(layoutInflater, parent, false);
        }

        binding.taskName.setText(task.getName());

        binding.taskRenameButton.setOnClickListener(v -> {
            var dialogFragment = RenameTaskDialogFragment.newInstance(task, this);
            dialogFragment.show(fragment.getParentFragmentManager(), "RenameTaskDialogFragment");
        });

        binding.taskReorderDown.setOnClickListener(v -> {
            ((EditRoutineFragment)fragment).activityModel.moveTaskDown(taskRoutine, task);
        });

        binding.taskReorderUp.setOnClickListener(v -> {
            Log.d("Adapter", "Press is being noticed");
            ((EditRoutineFragment)fragment).activityModel.moveTaskUp(taskRoutine, task);
        });

        binding.taskDeleteButton.setOnClickListener(v -> {
            new AlertDialog.Builder(fragment.getContext())
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to delete this task?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        ((EditRoutineFragment) fragment).activityModel.deleteTask(task.getId());
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        return binding.getRoot();
    }



}
