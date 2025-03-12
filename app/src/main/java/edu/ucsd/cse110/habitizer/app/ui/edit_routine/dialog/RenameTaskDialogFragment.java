package edu.ucsd.cse110.habitizer.app.ui.edit_routine.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import edu.ucsd.cse110.habitizer.app.MainViewModel;
import edu.ucsd.cse110.habitizer.app.databinding.FragmentDialogRenameTaskBinding;
import edu.ucsd.cse110.habitizer.app.ui.edit_routine.EditRoutineAdapter;
import edu.ucsd.cse110.habitizer.lib.domain.Task;

public class RenameTaskDialogFragment extends DialogFragment {

    private FragmentDialogRenameTaskBinding view;
    private MainViewModel activityModel;
    private Task currentTask;
    private EditRoutineAdapter adapter;

    public RenameTaskDialogFragment(Task currentTask, EditRoutineAdapter adapter) {
        this.currentTask = currentTask;
        this.adapter = adapter;
    }

    public static RenameTaskDialogFragment newInstance(Task task, EditRoutineAdapter parentAdapter) {
        var fragment = new RenameTaskDialogFragment(task, parentAdapter);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        this.view = FragmentDialogRenameTaskBinding.inflate(getLayoutInflater());

        return new AlertDialog.Builder(getActivity())
                .setTitle("Rename Task")
                .setMessage("Please provide the new name of this task.")
                .setView(view.getRoot())
                .setPositiveButton("Rename", this::onPositiveButtonClick)
                .setNegativeButton("Cancel", this::onNegativeButtonClick)
                .create();
    }

    private void onPositiveButtonClick(DialogInterface dialog, int which) {
        var newTaskName = view.taskNameEditText.getText().toString();
        activityModel.updateTaskName(currentTask.getId(), newTaskName);
        dialog.dismiss();
    }



    private void onNegativeButtonClick(DialogInterface dialog, int which) {
        dialog.cancel();
    }
}
