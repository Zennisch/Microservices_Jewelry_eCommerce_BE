package fit.iuh.backend.model;

import fit.iuh.backend.common.UserActivityStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@Entity
@Table(name = "user_activities")
public class UserActivity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Đổi từ activityId thành id

    @ManyToOne
    @JoinColumn(name = "user_id") // Giữ nguyên snake_case cho tên cột
    private User user;

    private String type;

    @ElementCollection
    @CollectionTable(name = "activity_data")
    @MapKeyColumn(name = "data_key")
    @Column(name = "data_value")
    private Map<String, String> data = new HashMap<>();

    private String ipAddress;
    private String userAgent;
    private LocalDateTime timeStamp;

    @Enumerated(EnumType.STRING)
    private UserActivityStatus status;
}