package edu.ucsd.cse110.habitizer.app.ui.edit_routine;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.habitizer.app.MainViewModel;
import edu.ucsd.cse110.habitizer.app.R;
import edu.ucsd.cse110.habitizer.app.databinding.ListItemEditRoutineBinding;
import edu.ucsd.cse110.habitizer.app.ui.edit_routine.dialog.AddTaskDialogFragment;
import edu.ucsd.cse110.habitizer.app.ui.edit_routine.dialog.ChangeGoalTimeDialogFragment;
import edu.ucsd.cse110.habitizer.app.ui.edit_routine.dialog.RenameRoutineDialogFragment;
import edu.ucsd.cse110.habitizer.app.ui.edit_routine.dialog.RenameTaskDialogFragment;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;

import edu.ucsd.cse110.habitizer.app.databinding.FragmentEditRoutineBinding;
import edu.ucsd.cse110.habitizer.lib.domain.Task;

public class EditRoutineFragment extends Fragment {

    private MainViewModel activityModel;
    private FragmentEditRoutineBinding view;
    private EditRoutineAdapter adapter;
    private Routine routine;

    public EditRoutineFragment(Routine routine) { this.routine = routine; }

    public static EditRoutineFragment newInstance(Routine routine) {
        EditRoutineFragment fragment = new EditRoutineFragment(routine);
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

        this.adapter = new EditRoutineAdapter(requireContext(), List.of(), routine, this);
        activityModel.getMap().observe(map -> {
            adapter.clear();
            List<Task> tasks = map.get(routine);
            if (tasks == null) {
                tasks = new ArrayList<>();
            }
//            assert map != null;
//            assert routine != null;
            adapter.addAll(new ArrayList<Task>(map.get(routine)));
            adapter.notifyDataSetChanged();
        });

    }
  
    private void updateGoalTimeDisplay(long newGoalTime) {
        String goalTimeText = "Goal Time: " + (newGoalTime == 0 ? "-" : newGoalTime + " m");
        view.goalTimePreview.setText(goalTimeText);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState)
    {
        this.view = FragmentEditRoutineBinding.inflate(inflater, container, false);

        view.routineName.setText(routine.getName());
        view.taskList.setAdapter(adapter);
        String goalTimeText = "Goal Time: " + (routine.getGoalTime() == 0 ? "-" : routine.getGoalTime() + " m");
        view.goalTimePreview.setText(goalTimeText);

        // Implement add task
        view.addTaskButton.setOnClickListener(v -> {
            var dialogFragment = AddTaskDialogFragment.newInstance(routine);

            dialogFragment.show(getParentFragmentManager(), "AddTaskDialogFragment");
        });

        // Rename Routine
        view.routineName.setOnClickListener(v -> {
            var dialogFragment = RenameRoutineDialogFragment.newInstance(routine);
            dialogFragment.setRenameRoutineListener(newName -> {
                view.routineName.setText(routine.getName());
            });
            dialogFragment.show(getParentFragmentManager(), "RenameRoutineDialogFragment");
        });

        view.changeGoalTime.setOnClickListener(v -> {
            var dialogFragment = ChangeGoalTimeDialogFragment.newInstance(routine);
            dialogFragment.setOnGoalTimeUpdatedListener(newGoalTime -> {
                routine.setGoalTime(newGoalTime);
                updateGoalTimeDisplay(newGoalTime);
            });
            dialogFragment.show(getParentFragmentManager(), "ChangeGoalTimeDialogFragment");

        });

        return view.getRoot();
    }
}
