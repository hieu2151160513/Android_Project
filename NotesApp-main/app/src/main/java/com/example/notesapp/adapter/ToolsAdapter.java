package com.example.notesapp.adapter;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.notesapp.R;
import com.example.notesapp.ToolsListener;
import com.example.notesapp.ViewOnClick;
import com.example.notesapp.model.ToolsItem;
import com.example.notesapp.view.activity.ToolsViewHolder;

import java.util.List;

public class ToolsAdapter extends RecyclerView.Adapter<ToolsViewHolder> {

    private List<ToolsItem> paintList;
    private int selected = -1;
    private ToolsListener toolsListener;

    public ToolsAdapter(List<ToolsItem> paintList, ToolsListener toolsListener) {
        this.paintList = paintList;
        this.toolsListener = toolsListener;
    }

    @NonNull
    @Override
    public ToolsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.paint_tools_item, parent, false);

        return new ToolsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToolsViewHolder holder, int position) {

        holder.name.setText(paintList.get(position).getName());
        holder.icon.setImageResource(paintList.get(position).getRscID());

        holder.setViewOnClick(new ViewOnClick() {
            @Override
            public void onClick(int pos) {
                selected = pos;
                toolsListener.onSelected(paintList.get(pos).getName());
                notifyDataSetChanged();
            }
        });

        if (selected == position) {
            holder.name.setTypeface(holder.name.getTypeface(), Typeface.BOLD);
        } else {
            holder.name.setTypeface(Typeface.DEFAULT);
        }

    }

    @Override
    public int getItemCount() {
        return paintList.size();
    }
}
