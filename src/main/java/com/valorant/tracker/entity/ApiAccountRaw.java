package com.valorant.tracker.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode; // <-- 1. ДОБАВЛЕН ИМПОРТ
import org.hibernate.type.SqlTypes;           // <-- 2. ДОБАВЛЕН ИМПОРТ
import java.time.*;

@Entity
@Table(name = "api_accounts_raw")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiAccountRaw {

    @Id
    private String puuid;

    private String region;
    private String name;
    private String tag;

    @JdbcTypeCode(SqlTypes.JSON) // <-- 3. ДОБАВЛЕНА АННОТАЦИЯ
    @Column(columnDefinition = "jsonb")
    private String payload;

    private OffsetDateTime fetchedAt;
}
