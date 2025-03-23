package com.example.demo.controller;

import com.example.demo.model.Brand;
import com.example.demo.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequestMapping("/api/brands")
public class  BrandController {
    @Autowired
    private BrandService brandService;

    @GetMapping
    public List<Brand> getAllBrands() {
        return brandService.getAllBrands();
    }

    @PostMapping
    public Brand createBrand(@RequestBody Brand brand) {
        return brandService.createBrand(brand);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Brand> updateBrand(@PathVariable Integer id, @RequestBody Brand brand) {
        Optional<Brand> existingBrand = brandService.getBrandById(id);
        if (!existingBrand.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Brand updatedBrand = existingBrand.get();
        updatedBrand.setName(brand.getName());
        brandService.createBrand(updatedBrand); // Lưu lại thương hiệu đã cập nhật

        return ResponseEntity.ok(updatedBrand);
    }


    @DeleteMapping("/{id}")
    public void deleteBrand(@PathVariable Integer id) {
        brandService.deleteBrand(id);
    }
    @GetMapping("/categories")
    public ResponseEntity<List<Brand>> getAllCategories() {
        return ResponseEntity.ok(brandService.getAllBrands());
    }

}

