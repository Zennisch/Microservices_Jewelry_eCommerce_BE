package fit.iuh.backend.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import fit.iuh.backend.controller.request.ImageRequest;
import fit.iuh.backend.controller.request.ProductCreationRequest;
import fit.iuh.backend.controller.request.ProductUpdateRequest;
import fit.iuh.backend.controller.response.ProductPageResponse;
import fit.iuh.backend.controller.response.ProductResponse;

public interface ProductService {
    ProductPageResponse getAllProducts(String keyword , String column, String direction, int page, int size);
    Long createProduct(ProductCreationRequest product);
    ProductResponse getProductById(Long id);
    Long updateProduct(Long productId, ProductUpdateRequest req);
    void deleteProduct(Long id);

    ProductPageResponse getOutOfStockProducts(int page, int size);
    ProductPageResponse getLowStockProducts(int threshold,int page, int size);
    Long updateProductStock(Long productId, int newStock);
    void importProductsFromCSV(MultipartFile file) throws Exception;
    List<Long> addImagesToProduct(Long productId, List<ImageRequest> imageRequests);
    void deleteImageFromProduct(Long productId, Long imageId);
}