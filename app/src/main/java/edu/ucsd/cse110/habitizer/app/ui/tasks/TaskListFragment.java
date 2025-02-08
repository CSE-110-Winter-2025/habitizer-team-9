package edu.ucsd.cse110.habitizer.app.ui.tasks;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.habitizer.app.MainViewModel;
import edu.ucsd.cse110.habitizer.app.databinding.FragmentRoutinesBinding;
import edu.ucsd.cse110.habitizer.app.databinding.FragmentTasksBinding;
import edu.ucsd.cse110.habitizer.app.ui.routines.RoutinesFragment;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;
import edu.ucsd.cse110.habitizer.lib.domain.Task;

public class TaskListFragment extends Fragment {
    private MainViewModel activityModel;
    private FragmentTasksBinding view;
//    private TaskListAdapter adapter;

    private Routine routine;

    public TaskListFragment(Routine routine){
        this.routine = routine;
    }

    public static TaskListFragment newInstance(Routine routine){
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

//        this.adapter = new TaskListAdapter(requireContext(), List.of());
//        activityModel.getMap().observe(map -> {
//            adapter.clear();
//            assert map != null;
//            assert routine != null;
//            adapter.addAll(new ArrayList<Task>(map.get(routine)));
//            adapter.notifyDataSetChanged();
//
//        });


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = FragmentTasksBinding.inflate(inflater, container, false);
        view.routineName.setText(routine.getName());
        //IMPLEMENT WHEN TASK IS IMPLEMENTED
        // view.taskList.setAdapter(adapter);

        return view.getRoot();
    }



}
