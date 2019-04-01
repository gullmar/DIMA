package com.dima.emmeggi95.jaycaves.me;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.dima.emmeggi95.jaycaves.me.entities.ChartAlbumsAdapter;
import com.dima.emmeggi95.jaycaves.me.view_models.GenresViewModel;

import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ChartsFragment extends Fragment {

    RecyclerView chartsRecyclerView;
    RecyclerView.Adapter chartsAdapter;
    RecyclerView.LayoutManager chartsLayoutManager;

    Spinner genresSpinner;
    Spinner yearsSpinner;

    GenresViewModel genresViewModel;
    List<String> years;

    List<String> genres;

    int FIRST_YEAR = 1950;

    String selectedGenre;
    String selectedYear;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_charts, container, false);

        // Set the recycler view
        chartsRecyclerView = view.findViewById(R.id.charts_recycler_view);

        chartsLayoutManager = new LinearLayoutManager(getActivity());

        // Connect to the database and download data without filters

        // Temp...
        List<Album> albumList = new ArrayList<>();
        albumList.add(new Album("Album #1", "1995", 4.17, "Artist ABC", "Rock", ""));
        albumList.add(new Album("Album #2", "1995", 4.17, "Artist ABC", "Rock", ""));
        albumList.add(new Album("Album #3", "1995", 4.17, "Artist ABC", "Rock", ""));
        chartsAdapter = new ChartAlbumsAdapter(getActivity(), albumList);

        chartsRecyclerView.setLayoutManager(chartsLayoutManager);
        chartsRecyclerView.setAdapter(chartsAdapter);

        // Get spinners
        genresSpinner = view.findViewById(R.id.genre_spinner);
        yearsSpinner = view.findViewById(R.id.year_spinner);

        // Get genres from ViewModel
        genresViewModel = ViewModelProviders.of(getActivity()).get(GenresViewModel.class);
        genres = new ArrayList<>();

        // Set genres to spinner
        final Observer genresObserver = new Observer<List<String>>() {
            @Override
            public void onChanged(@Nullable List<String> genresList) {
                genres.clear();
                genres.add(getResources().getString(R.string.all_genres));
                genres.addAll(genresList);
                ArrayAdapter<String> genresArray = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, genres);
                genresSpinner.setAdapter(genresArray);
            }
        };
        genresViewModel.getData().observe(this, genresObserver);

        // Generate years
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        years = new ArrayList<>();
        years.add(getResources().getString(R.string.all_years));
        for(int i = currentYear; i>=FIRST_YEAR; i--){
            years.add(String.valueOf(i));
        }

        // Set years to spinner
        ArrayAdapter<String> yearsArray = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, years);
        yearsSpinner.setAdapter(yearsArray);

        // Set listener on both spinners
        genresSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Bisognerebbe inserire un loading per la connessione al database
                selectedGenre = parent.getItemAtPosition(position).toString();
                // Connect to database to retrieve the updated list
                if(selectedGenre.equals(getResources().getString(R.string.all_genres))){
                    // don't filter by genre
                } else {
                    // filter by selected genre
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        yearsSpinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedYear = parent.getItemAtPosition(position).toString();
                // Connect to database to retrieve the updated list
                if(selectedYear.equals(getResources().getString(R.string.all_years))){
                    // don't filter by genre
                } else {
                    // filter by selected genre
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return view;
    }
}
