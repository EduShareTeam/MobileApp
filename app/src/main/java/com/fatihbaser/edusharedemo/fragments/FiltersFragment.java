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

        binding.fenbilimleri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Fen bilimleri");
            }
        });

        binding.eItimbilimleri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Egitim bilimleri");
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
                goToFilterActivity("Yabanci diller");
            }
        });
        binding.mimarlK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Mimarlik");
            }
        });
        binding.teknolojivemuhendislkik.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Teknoloji ve Muhendislik");
            }
        });
        binding.guzelsanatlar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Guzel Sanatlar");
            }
        });
        binding.iktisat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Iktisadi bilimler");
            }
        });
        binding.sporbilimleri.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("Spor bilimleri");
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