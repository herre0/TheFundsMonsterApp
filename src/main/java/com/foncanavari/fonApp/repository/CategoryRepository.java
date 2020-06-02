package com.foncanavari.fonApp.repository;

import com.foncanavari.fonApp.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Integer> {

}
