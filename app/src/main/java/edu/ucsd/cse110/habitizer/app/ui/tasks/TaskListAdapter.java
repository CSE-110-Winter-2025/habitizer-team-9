//package edu.ucsd.cse110.habitizer.app.ui.tasks;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//
//import androidx.annotation.NonNull;
//
//import edu.ucsd.cse110.habitizer.app.databinding.FragmentTasksBinding;
//import edu.ucsd.cse110.habitizer.lib.domain.Routine;
//import edu.ucsd.cse110.habitizer.lib.domain.Task;
//
//import java.util.*;
//
//public class TaskListAdapter extends ArrayAdapter<Task> {
//    public TaskListAdapter(Context context, List<Task> tasks){
//        super(context, 0, new ArrayList<>(tasks));
//    }
//
//    @NonNull
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent){
//        var task = getItem(position);
//        assert task != null;
//
//        FragmentTasksBinding binding;
//        if(convertView != null){
//            binding = FragmentTasksBinding.bind(convertView);
//        }else{
//            var layoutInflater = LayoutInflater.from(getContext());
//            binding = FragmentTasksBinding.inflate(layoutInflater, parent, false);
//        }
//
//        // add implementation for task list
//
//        return binding.getRoot();
//    }
//
//
//}
