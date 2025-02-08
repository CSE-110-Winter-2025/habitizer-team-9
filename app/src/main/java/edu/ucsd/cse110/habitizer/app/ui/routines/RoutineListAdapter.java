package edu.ucsd.cse110.habitizer.app.ui.routines;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import edu.ucsd.cse110.habitizer.app.databinding.ListItemRoutineBinding;
import edu.ucsd.cse110.habitizer.lib.domain.Routine;

public class RoutineListAdapter extends ArrayAdapter<Routine> {
    public RoutineListAdapter(Context context, List<Routine> routines) {
        super(context, 0, new ArrayList<>(routines));
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        var routine = getItem(position);
        assert routine != null;

        ListItemRoutineBinding binding;
        if (convertView != null) {
            binding = ListItemRoutineBinding.bind(convertView);
        } else {
            var layoutInflater = LayoutInflater.from(getContext());
            binding = ListItemRoutineBinding.inflate(layoutInflater, parent, false);
        }

        binding.editRoutine.setText(routine.getName());

        return binding.getRoot();
    }
}
