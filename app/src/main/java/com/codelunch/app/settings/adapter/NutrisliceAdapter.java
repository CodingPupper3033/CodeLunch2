package com.codelunch.app.settings.adapter;

import androidx.recyclerview.widget.RecyclerView;

public abstract class NutrisliceAdapter<VH extends androidx.recyclerview.widget.RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {
    public abstract void delete(int pos);
}
