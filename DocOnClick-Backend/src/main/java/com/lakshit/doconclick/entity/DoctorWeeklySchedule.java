package com.lakshit.doconclick.entity;

import com.lakshit.doconclick.enums.CustomDayOfWeek;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "doctor_weekly_schedules", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"doctor_id", "day_of_week"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorWeeklySchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false)
    private Doctor doctor;

    @Enumerated(EnumType.STRING)
    @Column(name = "day_of_week", nullable = false)
    private CustomDayOfWeek dayOfWeek;

    @Column(nullable = false)
    private boolean isAvailable = true;

    @OneToMany(mappedBy = "weeklySchedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WeeklyTimeSlot> timeSlots = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}