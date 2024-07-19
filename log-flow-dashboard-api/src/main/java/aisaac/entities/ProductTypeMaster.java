package aisaac.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_type_master")
public class ProductTypeMaster {
	
	  @Id
	  @GeneratedValue(strategy = GenerationType.IDENTITY)
	  @Column(name = "product_type_id", unique = true, nullable = false)
	  private Long productTypeId;
	
	  @Column(name = "product_type", nullable = false, length = 255)
	  private String productType;

      @Column(name = "description", nullable = false, length = 255)
      private String description;
	    
      @Temporal(TemporalType.TIMESTAMP)
      @Column(name = "created_date", nullable = false, length = 19)
      private LocalDateTime createdDate;

      @Temporal(TemporalType.TIMESTAMP)
      @Column(name = "last_modified_date", nullable = false, length = 19)
      private LocalDateTime lastModifiedDate;
      
      @Column(name = "deleted")
      private boolean deleted;
}
