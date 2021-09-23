package com.fatihbaser.edusharedemo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.fatihbaser.edusharedemo.activities.FilterActivity;
import com.fatihbaser.edusharedemo.databinding.FragmentFiltersBinding;


public class FiltersFragment extends Fragment {
    private FragmentFiltersBinding binding;
    public FiltersFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFiltersBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.cardViewPs4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Elektronik ve mimarlık");
            }
        });

        binding.cardViewXBOX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Dil ve Edebiyat");
            }
        });

        binding.cardViewNINTENDO.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Sanat");
            }
        });

        binding.cardViewPC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Fen Bilimleri");
            }
        });

        return view;
    }

    private void goToFilterActivity(String category) {
        Intent intent = new Intent(getContext(), FilterActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }
}