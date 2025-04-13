package com.example.demo.controller;

import com.example.demo.model.Brand;
import com.example.demo.model.Category;
import com.example.demo.model.Product;
import com.example.demo.repository.BrandRepository;
import com.example.demo.repository.CategoryRepository;
import com.example.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@CrossOrigin(origins = "http://127.0.0.1:5500") // Cho phép frontend gọi API
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final String UPLOAD_DIR = "uploads/";

    @Autowired
    private ProductService productService;
    @Autowired
    private BrandRepository brandRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    // 📌 API lấy sản phẩm theo ID
    @GetMapping
    public ResponseEntity<List<Product>> getProducts(
            @RequestParam(required = false) Integer category_id,
            @RequestParam(required = false, defaultValue = "false") boolean isAdmin) {

        List<Product> products;

        if (isAdmin) {
            products = productService.getAllProductsIncludingBlocked(); // ✅ Đảm bảo lấy sản phẩm bị khóa
        } else {
            products = productService.getActiveProducts();
        }

        return ResponseEntity.ok(products);
    }


    // 📌 API Thêm sản phẩm với ảnh
    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Product> createProduct(
            @RequestParam("name") String name,
            @RequestParam("brand_id") Integer brandId,
            @RequestParam("category_id") Integer categoryId,
            @RequestParam("price") double price,
            @RequestParam("stock") int stock,
            @RequestParam("description") String description,
            @RequestParam(value = "image", required = false) MultipartFile imageFile) throws IOException {

        // Kiểm tra brand và category có tồn tại không
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new RuntimeException("Brand not found"));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        // Tạo sản phẩm mới
        Product product = new Product();
        product.setName(name);
        product.setBrand(brand);
        product.setCategory(category);
        product.setPrice(price);
        product.setStock(stock);
        product.setDescription(description);

        // Nếu có ảnh, lưu vào thư mục và cập nhật đường dẫn
        if (imageFile != null && !imageFile.isEmpty()) {
            product.setImageUrl(saveImage(imageFile));
        }

        Product savedProduct = productService.createProduct(product);
        return ResponseEntity.ok(savedProduct);
    }

    // 📌 API cập nhật sản phẩm
    @PutMapping(value = "/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestParam("name") String name,
            @RequestParam("brand_id") Integer brandId,
            @RequestParam("category_id") Integer categoryId,
            @RequestParam("price") double price,
            @RequestParam("stock") int stock,
            @RequestParam("blocked") boolean blocked,
            @RequestParam("description") String description,
            @RequestParam(value = "image", required = false) MultipartFile image) throws IOException {

        Product product = productService.getProductById(id).orElse(null);
        if (product == null) return ResponseEntity.notFound().build();

        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new RuntimeException("Brand not found"));
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        product.setName(name);
        product.setBrand(brand);
        product.setCategory(category);
        product.setPrice(price);
        product.setStock(stock);
        product.setBlocked(blocked);
        product.setDescription(description);

        if (image != null && !image.isEmpty()) {
            product.setImageUrl(saveImage(image));
        }

        productService.updateProduct(id, product);
        return ResponseEntity.ok(product);
    }

    // 📌 API xóa sản phẩm
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    // 📌 API lấy danh sách danh mục
    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryRepository.findAll());
    }

    // 📌 API lấy danh mục theo ID
    @GetMapping("/categories/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Integer id) {
        return categoryRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 📌 API lấy danh sách thương hiệu
    @GetMapping("/brands")
    public ResponseEntity<List<Brand>> getAllBrands() {
        return ResponseEntity.ok(brandRepository.findAll());
    }

    // 📌 API lấy thương hiệu theo ID
    @GetMapping("/brands/{id}")
    public ResponseEntity<Brand> getBrandById(@PathVariable Integer id) {
        return brandRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 📌 API hiển thị ảnh sản phẩm
    @GetMapping("/uploads/{filename}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(UPLOAD_DIR).resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, Files.probeContentType(filePath))
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 📌 Hàm lưu ảnh vào thư mục uploads/
    private String saveImage(MultipartFile file) throws IOException {
        if (!Files.exists(Paths.get(UPLOAD_DIR))) {
            Files.createDirectories(Paths.get(UPLOAD_DIR));
        }

        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path targetLocation = Paths.get(UPLOAD_DIR).resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        return "/api/products/uploads/" + fileName; // Trả về đường dẫn ảnh để frontend dùng
    }
    @PutMapping("/{id}/toggle-block")
    public ResponseEntity<Product> toggleBlockProduct(@PathVariable Long id) {
        Product product = productService.getProductById(id).orElse(null);
        if (product == null) return ResponseEntity.notFound().build();

        product.setBlocked(!product.getBlocked()); // Đảo trạng thái khóa/mở khóa
        productService.updateProduct(id, product);

        return ResponseEntity.ok(product);
    }
}
