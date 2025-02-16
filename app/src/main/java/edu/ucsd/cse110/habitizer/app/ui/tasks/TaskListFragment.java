package edu.ucsd.cse110.habitizer.app.ui.tasks;

import android.app.AlertDialog;
import android.os.Bundle;
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
    private RoutineTimer routineTimer;
    private TextView timerTextView;
    private ToggleButton mockModeToggle;
    private Button advanceTimeButton;
    private TaskListAdapter adapter;

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
        routineTimer = new RoutineTimer(secondsElapsed -> {
            if (timerTextView != null) {
                int minutes = secondsElapsed / 60; // Convert seconds to minutes
                requireActivity().runOnUiThread(() ->
                        timerTextView.setText(String.valueOf(minutes) + "m") // Display minutes
                );
            }
        });

        routine.routineTimer = this.routineTimer;

        this.adapter = new TaskListAdapter(requireContext(), List.of(), routine);
        activityModel.getMap().observe(map -> {
            adapter.clear();
            assert map != null;
            assert routine != null;
            adapter.addAll(new ArrayList<Task>(map.get(routine)));
            adapter.notifyDataSetChanged();

        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = FragmentTasksBinding.inflate(inflater, container, false);

        // Bind views
        Button backButton = view.backButton;
        timerTextView = view.timerTextView;
        mockModeToggle = view.mockModeToggle;
        advanceTimeButton = view.advanceTimeButton;
        Button endRoutineButton = view.endRoutineButton;


        // Set routine name
        view.routineName.setText(routine.getName());

        // Toggle mock mode
        mockModeToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                routineTimer.enableMockMode(); // Enable mock mode
                advanceTimeButton.setEnabled(true); // Enable advance time button
            } else {
                routineTimer.disableMockMode(); // Disable mock mode
                advanceTimeButton.setEnabled(false); // Disable advance time button
            }
        });

        // Advance time button
        advanceTimeButton.setOnClickListener(v -> {
            try {
                routineTimer.advanceMockTime(30); // Advance time by 30 seconds
            } catch (IllegalStateException e) {
                timerTextView.setText("Enable Mock Mode First!");
            }
        });

        // Initially disable advance time button if mock mode is off
        advanceTimeButton.setEnabled(mockModeToggle.isChecked());
        backButton.setOnClickListener(v -> goBackToHome());
        endRoutineButton.setOnClickListener(v -> endRoutine(endRoutineButton));


        // Start the timer
        routineTimer.start();
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
    }




    @Override
    public void onDestroyView() {
        super.onDestroyView();
        routineTimer.stop(); // Stop the timer to prevent leaks
        view = null;
    }

    private void endRoutine(Button endRoutineButton) {
        routineTimer.stop();

        // Calculate the total time
        long totalTime = (long) Math.ceil(routineTimer.getElapsedTimeInSeconds() / 60.0);
        String message = "Routine Ended. Total time taken: " + totalTime + "m";

        // Disable the button
        endRoutineButton.setEnabled(false);

        // Grey out the button
        endRoutineButton.setBackgroundTintList(ContextCompat.getColorStateList(requireContext(), android.R.color.darker_gray));

        // Show pop-up for time summary
        showCompletionDialog(message);
    }

    private void goBackToHome() {
        if (routineTimer != null) {
            routineTimer.stop(); // Stop the timer
        }

        // Navigate back to the home screen (RoutinesFragment)
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new RoutinesFragment()) // Ensure this ID exists in activity_main.xml
                .addToBackStack(null) // Allows user to navigate back
                .commit();
    }

}
