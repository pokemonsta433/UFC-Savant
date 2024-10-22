package com.example.ufcsavant.model;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class FighterService {
    private List<String> allNames = new ArrayList<>();

    @PostConstruct
    public void init() {
        loadFighterNames();
    }

    private void loadFighterNames(){


    }

    public List<String> getAllNames() {
        return allNames;
    }

}
