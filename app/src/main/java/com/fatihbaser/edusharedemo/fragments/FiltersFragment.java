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
    public FiltersFragment() {
        // Required empty public constructor
    }
    //TODO: DINAMIK YAPP
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        com.fatihbaser.edusharedemo.databinding.
                FragmentFiltersBinding binding = FragmentFiltersBinding.
                inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.fenbilimleri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Fen Bilimleri");
            }
        });

        binding.eItimbilimleri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Eğitim Bilimleri");
            }
        });

        binding.dilveedebiyat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Dil ve Edebiyat");
            }
        });

        binding.yabancDiller.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Yabancı Diller");
            }
        });
        binding.mimarliK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Mimarlık");
            }
        });
        binding.teknolojivemuhendislkik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Mühendislik");
            }
        });
        binding.guzelsanatlar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Güzel Sanatlar");
            }
        });
        binding.iktisat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("İktisadi Bilimler");
            }
        });
        binding.sporbilimleri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Spor Bilimleri");
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