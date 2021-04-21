package com.example.demo.shareddomain.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class CartInfoDto {
  private String cartNumber;
  private String customer;
  private Integer amount;
  private String createdBy;
  private LocalDateTime createdDate;
  private String lastModifiedBy;
  private LocalDateTime lastModifiedDate;
}
