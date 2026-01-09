package com.royalhalalmeats.royalmeats.model;



import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class MeatItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Double pricePerLB;

}

