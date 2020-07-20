/*
 *  Copyright (c) 2018 Gabriele Corso
 *
 *  Distributed under the MIT software license, see the accompanying
 *  file LICENSE or http://www.opensource.org/licenses/mit-license.php.
 */

package com.gcorso.academy.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gcorso.academy.objects.Course;
import com.gcorso.academy.layout.FitDoughnut;
import com.gcorso.academy.R;

import java.util.List;

import static com.gcorso.academy.Preferences.CoursesColors;

public class CoursesProgGridAdapter extends BaseAdapter {

    private List<Course> courses;

    public CoursesProgGridAdapter(List<Course> courses) {
        this.courses = courses;
    }

    @Override
    public int getCount() {
        return courses.size();
    }

    @Override
    public Object getItem(int position) {
        return courses.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.courseprog_item, parent, false);

        TextView tvCourseTitle = view.findViewById(R.id.tvtitle);
        tvCourseTitle.setText(courses.get(position).getTitle());

        FitDoughnut dCourse = view.findViewById(R.id.doughnut);
        TextView tvCoursePerc = view.findViewById(R.id.tvpercentage);

        dCourse.setColorPrimary(Color.parseColor(CoursesColors[position]));
        dCourse.animateSetPercent((float) courses.get(position).getPercentageProgress());
        String t = courses.get(position).getPercentageProgress() + "%";
        tvCoursePerc.setText(t);

        tvCourseTitle.setTextColor(Color.parseColor(CoursesColors[position]));
        tvCoursePerc.setTextColor(Color.parseColor(CoursesColors[position]));

        return view;
    }
}