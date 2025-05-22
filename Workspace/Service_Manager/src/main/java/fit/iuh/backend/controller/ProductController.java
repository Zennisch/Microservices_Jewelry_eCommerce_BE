package fit.iuh.backend.controller;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import fit.iuh.backend.controller.request.ImageRequest;
import fit.iuh.backend.controller.request.ProductCreationRequest;
import fit.iuh.backend.controller.request.ProductUpdateRequest;
import fit.iuh.backend.controller.response.ProductResponse;
import fit.iuh.backend.service.ProductService;
import io.micrometer.core.instrument.binder.logging.LogbackMetrics;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;


    @Autowired
    public ProductController(ProductService productService, LogbackMetrics logbackMetrics) {
        this.productService = productService;
    }

    @Operation(
            summary = "Get all products",
            description = "Retrieves a page of all products"
    )
    @GetMapping
    public Map<String, Object> getAllProducts(@RequestParam(required = false) String keyword,
                                                        @RequestParam(required = false) String column,
                                                        @RequestParam(required = false) String direction,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "12")int size) {

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "product list");
        result.put("data", productService.getAllProducts(keyword,column,direction,page,size));

        return result;
    }

    @Operation(
            summary = "Create a new product",
            description = "Creates a new product and stores it in the database."
    )
    @PostMapping("/add")
    public ResponseEntity<Object> createProduct(@RequestBody ProductCreationRequest product) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message", "User created successfully");
        result.put("data", productService.createProduct(product));

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @Operation(
            summary = "Get product by ID",
            description = "Retrieves a product by its unique ID."
    )
    @GetMapping("/{id}")
    public Map<String, Object> getProductById(@PathVariable Long id) {
        ProductResponse productDetail = productService.getProductById(id);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "Product details");
        result.put("data", productDetail);

        return result;

    }
    @Operation(
            summary = "Update product",
            description = "Updates the details of an existing product by its ID."
    )
    @PutMapping("/{id}/update")
    public ResponseEntity<Object> updateProduct(@PathVariable Long id, @RequestBody ProductUpdateRequest product) {

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "User updated successfully");
        result.put("data", productService.updateProduct(id,product));

        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @Operation(
            summary = "Delete product",
            description = "Deletes a product by its ID."
    )
    @DeleteMapping("/{id}/delete")
    public ResponseEntity<Object> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "User deleted successfully");
        result.put("data",null );
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(
            summary = "Get out of stock products",
            description = "Retrieves a list of products with out of stock"
    )
    @GetMapping("/out-of-stock")
    public Map<String, Object> getOutOfStockProduct(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "20")int size) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "product list");
        result.put("data", productService.getOutOfStockProducts(page,size));
        return result;
    }


    @Operation(
            summary = "Get low stock products",
            description = "Retrieves a list of products with stock below the specified threshold."
    )
    @GetMapping("/low-stock")
    public Map<String, Object> getLowStockProducts(@RequestParam int threshold,
                                                   @RequestParam(defaultValue = "0") int page,
                                                   @RequestParam(defaultValue = "20")int size) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "product list");
        result.put("data", productService.getLowStockProducts(threshold,page,size));
        return result;
    }

    @Operation(
            summary = "Update stock products",
            description = "Update stock"
    )
    @PatchMapping("/{productId}/update-stock")
    public ResponseEntity<Object> updateProductStock(@PathVariable Long productId, @RequestParam int newStock) {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "User updated successfully");
        result.put("data", productService.updateProductStock(productId,newStock));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(
            summary = "Import products from CSV",
            description = "Imports a list of products from a CSV file and saves them into the database."
    )
    @PostMapping("/import")
    public ResponseEntity<Object> importProductsFromCSV(@RequestParam("file") MultipartFile file) {
        try {
            productService.importProductsFromCSV(file);
            return ResponseEntity.ok("Products imported successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error importing products: " + e.getMessage());
        }
    }

    @Operation(summary = "Add images to product", description = "Adds multiple new images to a specific product")
    @PostMapping("/{id}/images/add-images")
    public ResponseEntity<Object> addImagesToProduct(@PathVariable Long id,
                                                     @RequestBody List<ImageRequest> imageRequests) {

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.CREATED.value());
        result.put("message", "User created successfully");
        result.put("data",  productService.addImagesToProduct(id, imageRequests));

        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @Operation(summary = "Delete image from product", description = "Deletes an image from a specific product")
    @DeleteMapping("/{productId}/images/{imageId}/delete")
    public ResponseEntity<Object> deleteImageFromProduct(@PathVariable Long productId,
                                                         @PathVariable Long imageId) {
        productService.deleteImageFromProduct(productId,imageId);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("status", HttpStatus.OK.value());
        result.put("message", "User deleted successfully");
        result.put("data",null );
        return new ResponseEntity<>(result, HttpStatus.OK);

    }
}
