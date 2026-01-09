package com.royalhalalmeats.royalmeats.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.royalhalalmeats.royalmeats.model.MeatItem;

public interface MeatItemRepository extends JpaRepository<MeatItem, Long> {}
