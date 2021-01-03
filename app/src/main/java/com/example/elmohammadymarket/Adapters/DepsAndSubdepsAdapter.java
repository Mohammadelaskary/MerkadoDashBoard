package com.example.elmohammadymarket.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.elmohammadymarket.R;

import java.util.HashMap;
import java.util.List;

public class DepsAndSubdepsAdapter extends BaseExpandableListAdapter {
    List<String> deps;
    HashMap<String, List<String>> depsAndSubdeps;
    Context context;

    public DepsAndSubdepsAdapter(List<String> deps, HashMap<String, List<String>> depsAndSubdeps, Context context) {
        this.deps = deps;
        this.depsAndSubdeps = depsAndSubdeps;
        this.context = context;
    }

    @Override
    public int getGroupCount() {
        return deps.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return depsAndSubdeps.get(deps.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return deps.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return depsAndSubdeps.get(deps.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        String deps = (String) getGroup(i);
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.department_name, null);
        }
        TextView textView = view.findViewById(R.id.dep_name);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "arabic_font.ttf");
        textView.setTypeface(face);
        textView.setText(deps);

        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        String subdeps = (String) getChild(i, i1);
        if (view == null) {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.subdepartment_name, null);
        }

        TextView textView = view.findViewById(R.id.subdep_name);
        Typeface face = Typeface.createFromAsset(context.getAssets(), "arabic_font.ttf");
        textView.setTypeface(face);
        textView.setText(subdeps);

        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
