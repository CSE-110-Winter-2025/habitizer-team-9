package edu.ucsd.cse110.habitizer.app.ui.routines;

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
import edu.ucsd.cse110.habitizer.lib.domain.Routine;

public class RoutinesFragment extends Fragment {
    private MainViewModel activityModel;
    private FragmentRoutinesBinding view;
    private RoutineListAdapter adapter;

    public RoutinesFragment() {}

    public static RoutinesFragment newInstance(){
        RoutinesFragment fragment = new RoutinesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        var modelOwner = requireActivity();
        var modelFactory = ViewModelProvider.Factory.from(MainViewModel.initializer);
        var modelProvider = new ViewModelProvider(modelOwner, modelFactory);
        this.activityModel = modelProvider.get(MainViewModel.class);

        this.adapter = new RoutineListAdapter(requireContext(), List.of(new Routine("Morning Routine"), new Routine("Evening Routine")));
        activityModel.getRoutines().observe(routines -> {
            adapter.clear();
            adapter.addAll(new ArrayList<>(routines));
            adapter.notifyDataSetChanged();
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = FragmentRoutinesBinding.inflate(inflater, container, false);
        view.routineList.setAdapter(adapter);

        return view.getRoot();
    }

}
