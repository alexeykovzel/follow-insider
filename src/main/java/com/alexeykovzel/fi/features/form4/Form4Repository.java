package com.alexeykovzel.fi.features.form4;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface Form4Repository extends JpaRepository<Form4, String> {

    @Query("SELECT f.accessionNo FROM Form4 f")
    Collection<String> findAllAccessionNumbers();
}
