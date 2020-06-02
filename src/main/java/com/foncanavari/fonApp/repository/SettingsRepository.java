package com.foncanavari.fonApp.repository;

import com.foncanavari.fonApp.model.Settings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingsRepository extends JpaRepository<Settings,Integer> {

}
