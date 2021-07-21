package com.github.ct.cart.infrastructure.repositories;


import com.github.ct.cart.domain.projecttions.Cart;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

@RestResource
public interface CartRepository extends JpaRepository<Cart, String> {

  // @RestResource(exported = false)
  // Page<Cart> findAll(Pageable pageable);

  @ApiOperation("find by name")
  Page<Cart> findByCustomerName(
      @ApiParam(value = "客戶") @Param("customerName") String customerName, Pageable pageable);
}
