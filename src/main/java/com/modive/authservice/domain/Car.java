package com.modive.authservice.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "CARS")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long carId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false)
    private String number;
    
    @Column(nullable = false)
    private boolean active;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createDateTime;

    public static Car of(final User user, final String number) {
        return Car.builder()
                .user(user)
                .number(number)
                .active(true)
                .build();
    }
}
