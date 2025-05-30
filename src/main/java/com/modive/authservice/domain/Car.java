package com.modive.authservice.domain;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
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

    @Getter
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name="uuid2", strategy = "uuid2")
    @Column(name = "car_id", columnDefinition = "VARCHAR(36)")
    private String carId;

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
                .createDateTime(LocalDateTime.now())
                .build();
    }
}
