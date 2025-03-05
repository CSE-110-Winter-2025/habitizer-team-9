package edu.ucsd.cse110.habitizer.app.ui.tasks;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.habitizer.app.MainViewModel;
import edu.ucsd.cse110.habitizer.app.R;
import edu.ucsd.cse110.habitizer.app.databinding.FragmentTasksBinding;
import edu.ucsd.cse110.habitizer.app.ui.routines.RoutinesFragment;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.RoutineTimer;
import edu.ucsd.cse110.habitizer.lib.domain.Task;
import edu.ucsd.cse110.habitizer.app.ui.tasks.TaskListAdapter;

public class TaskListFragment extends Fragment {
    private MainViewModel activityModel;
    private FragmentTasksBinding view;
    private ToggleButton mockModeToggle;
    private Button advanceTimeButton;
    private TaskListAdapter adapter;

    private TextView timerTextView;
    private Routine routine;

    public TaskListFragment(Routine routine) {
        this.routine = routine;
    }

    public static TaskListFragment newInstance(Routine routine) {
        TaskListFragment fragment = new TaskListFragment(routine);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);

        // Initialize the timer
        var routineTimer = new RoutineTimer(secondsElapsed -> {
            if (timerTextView != null) {
                int minutes = secondsElapsed / 60; // Convert seconds to minutes
                String output = String.valueOf(minutes) + " m / ";
                if(routine.getGoalTime() != 0){
                    output = output + routine.getGoalTime() + " m";
                }else{
                    output = output +  "-";
                }

                final String finalOutput = output;
                requireActivity().runOnUiThread(() ->

                        timerTextView.setText(finalOutput) // Display minutes
                );
            }
        });

        activityModel.setRoutineTimer(routineTimer);
        routine.routineTimer = routineTimer;

        this.adapter = new TaskListAdapter(requireContext(), List.of(), routine);
        activityModel.getMap().observe(map -> {
            adapter.clear();
            assert map != null;
            assert routine != null;
            for(Task task : map.get(routine)){
                if(task.getIsCheckedOff())
                    task.checkOff();
            }
            adapter.addAll(new ArrayList<Task>(map.get(routine)));
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = FragmentTasksBinding.inflate(inflater, container, false);

        // Bind views
        timerTextView = view.timerTextView;
        mockModeToggle = view.mockModeToggle;
        advanceTimeButton = view.advanceTimeButton;
        Button endRoutineButton = view.endRoutineButton;


        // Set routine name
        view.routineName.setText(routine.getName());

        // Toggle mock mode
        mockModeToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            advanceTimeButton.setEnabled(isChecked);
            activityModel.toggleMockMode(isChecked);
        });

        // Advance time button
        advanceTimeButton.setOnClickListener(v -> activityModel.advanceMockTime());

        // Initially disable advance time button if mock mode is off
        advanceTimeButton.setEnabled(mockModeToggle.isChecked());
        endRoutineButton.setOnClickListener(v -> endRoutineView(endRoutineButton, activityModel.endRoutine()));


        // Start the timer
        activityModel.getRoutineTimer().start();
        //IMPLEMENT WHEN TASK IS IMPLEMENTED <<
        view.taskList.setAdapter(adapter);

        // Set up completion listener
        adapter.setCompletionListener(message -> requireActivity().runOnUiThread(() -> {
            showCompletionDialog(message);
        }));



        return view.getRoot();
    }

    // Dialog after completing all tasks
    private void showCompletionDialog(String message) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Routine Completed")
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();

        // Disable the "End Routine" button when routine ends automatically
        Button endRoutineButton = view.endRoutineButton;
        if (endRoutineButton != null) {
            endRoutineButton.setEnabled(false);
            endRoutineButton.setText("Routine Ended");
            endRoutineButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), android.R.color.darker_gray));
        }
    }

    public void endRoutineView(Button endRoutineButton, String message) {
        // Disable the button
        endRoutineButton.setEnabled(false);
        endRoutineButton.setText("Routine Ended");

        // Grey out the button
        endRoutineButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), android.R.color.darker_gray));

        // Show pop-up for time summary
        showCompletionDialog(message);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        activityModel.getRoutineTimer().stop(); // Stop the timer to prevent leaks
        view = null;
    }
}
