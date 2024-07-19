package aisaac.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_master")
public class ProductMaster {
	
	  @Id
	  @GeneratedValue(strategy = GenerationType.IDENTITY)
	  @Column(name = "product_id", unique = true, nullable = false)
	  private Long productId;
	
	  @Column(name = "product_vendor", nullable = false, length = 255)
	  private String productVendor;
	  
	  @Column(name = "product_name", nullable = false, length = 255)
	  private String productName;

      @Column(name = "product_type_id", nullable = false, length = 11)
      private Long productTypeId;
      
      @Column(name = "deleted")
      private boolean deleted;
      
      @Transient
      private String productType;
}
