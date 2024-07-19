package aisaac.entities;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import aisaac.util.CustomDateTimeSerializer;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_authority_matrix")
public class UserAuthorityMatrix {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "rec_id", unique = true, nullable = false)
	private Long recId;
	
	@Column(name = "user_id", nullable = false)
	private Long userId;
	
	@Column(name = "authority_name", nullable = false, length = 255)
	private String authorityName;
	
	@Column(name = "module_name", nullable = false, length = 255)
	private String moduleName;
	
	@Column(name = "enabled", nullable = false)
	private Boolean enabled;
	
	@JsonSerialize(using = CustomDateTimeSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_date", length = 19)
    private LocalDateTime createdDate;

    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_date", length = 19)
    private LocalDateTime updatedDate;
    
    @Column(name = "created_by_user_id", nullable = true)
    private Long createdByUserId;
    
    @Column(name = "updated_by_user_id", nullable = true)
    private Long updatedByUserId;
    
}
