package com.enviro.assessment.junior.paballo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "investors")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Investor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String firstName;

    @Email(message = "Invalid email format")
    private String email;

    @Column(nullable = false)
    private String lastName;

    @Column(nullable = false)
    private LocalDate birthDate;

    @CreationTimestamp
    private LocalDateTime creationDate;

    @UpdateTimestamp
    private LocalDateTime updatedDate;

    @OneToMany(mappedBy = "investor", cascade = CascadeType.ALL)
    private List<Product> products;

}
