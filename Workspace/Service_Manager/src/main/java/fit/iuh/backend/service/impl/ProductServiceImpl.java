package fit.iuh.backend.service.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springdoc.api.OpenApiResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import fit.iuh.backend.controller.request.ImageRequest;
import fit.iuh.backend.controller.request.ProductCreationRequest;
import fit.iuh.backend.controller.request.ProductUpdateRequest;
import fit.iuh.backend.controller.response.ImageResponse;
import fit.iuh.backend.controller.response.ProductPageResponse;
import fit.iuh.backend.controller.response.ProductResponse;
import fit.iuh.backend.model.Category;
import fit.iuh.backend.model.Collection;
import fit.iuh.backend.model.Product;
import fit.iuh.backend.model.ProductImage;
import fit.iuh.backend.repository.CategoryRepository;
import fit.iuh.backend.repository.CollectionRepository;
import fit.iuh.backend.repository.ProductImageRepository;
import fit.iuh.backend.repository.ProductRepository;
import fit.iuh.backend.service.ProductService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j(topic = "PRODUCT-SERVICE")
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final CollectionRepository collectionRepository;
    private final ProductImageRepository productImageRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository,
            CollectionRepository collectionRepository, ProductImageRepository productImageRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.collectionRepository = collectionRepository;
        this.productImageRepository = productImageRepository;
    }

    @Override
    public ProductPageResponse getAllProducts(String keyword, String column, String direction, int page, int size) {
        String columnName = StringUtils.hasLength(column) ? column : "id";
        Sort.Direction sortDirection = Sort.Direction.DESC;

        if (StringUtils.hasLength(direction) && direction.equalsIgnoreCase("asc")) {
            sortDirection = Sort.Direction.ASC;
        }
        Sort.Order order = new Sort.Order(sortDirection, columnName);

        Pageable pageable = PageRequest.of(page, size, Sort.by(order));

        Page<Product> products;
        if (StringUtils.hasLength(keyword)) {
            keyword = "%" + keyword.toLowerCase() + "%";
            products = productRepository.searchByKeyword(keyword, pageable);
        } else {
            products = productRepository.findAll(pageable);
        }

        List<ProductResponse> productList = products.stream()
                .map(product -> {
                    List<ImageResponse> imageResponses = product.getImageSet() != null ? product.getImageSet().stream()
                            .filter(image -> image != null && image.getIsPrimary() != null && image.getIsPrimary())
                            .map(image -> new ImageResponse(image.getId(), image.getImageUrl(), image.getIsPrimary(),
                                    image.getSortOrder()))
                            .collect(Collectors.toList()) : new ArrayList<>();

                    ProductResponse.ProductResponseBuilder builder = ProductResponse.builder()
                            .productId(product.getId())
                            .name(product.getName())
                            .code(product.getCode())
                            .description(product.getDescription())
                            .stock(product.getQuantity())
                            .price(product.getPrice())
                            .status(product.getStatus())
                            .gender(product.getGender())
                            .material(product.getMaterial())
                            .goldKarat(product.getGoldKarat())
                            .color(product.getColor())
                            .brand(product.getBrand())
                            .size(product.getSize())
                            .viewCount(product.getViewCount())
                            .imageSet(imageResponses)
                            .createdAt(product.getCreatedAt())
                            .updatedAt(product.getUpdatedAt());

                    // Safe null checks for category and collection
                    if (product.getCategory() != null) {
                        builder.categoryId(product.getCategory().getId());
                    }

                    if (product.getCollection() != null) {
                        builder.collectionId(product.getCollection().getCollectionId());
                    }

                    return builder.build();
                })
                .toList();
        ProductPageResponse response = new ProductPageResponse();
        response.setPageNumber(page);
        response.setPageSize(size);
        response.setTotalElements(products.getTotalElements());
        response.setTotalPages(products.getTotalPages());
        response.setProducts(productList);
        return response;
    }

    @Override
    public ProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new OpenApiResourceNotFoundException("Product not found with id: " + id));

        List<ImageResponse> imageResponses = product.getImageSet() != null ? product.getImageSet().stream()
                .map(image -> new ImageResponse(image.getId(), image.getImageUrl(), image.getIsPrimary(),
                        image.getSortOrder()))
                .collect(Collectors.toList()) : new ArrayList<>();

        ProductResponse.ProductResponseBuilder builder = ProductResponse.builder()
                .productId(product.getId())
                .name(product.getName())
                .code(product.getCode())
                .description(product.getDescription())
                .stock(product.getQuantity())
                .price(product.getPrice())
                .status(product.getStatus())
                .gender(product.getGender())
                .material(product.getMaterial())
                .goldKarat(product.getGoldKarat())
                .color(product.getColor())
                .brand(product.getBrand())
                .size(product.getSize())
                .viewCount(product.getViewCount())
                .imageSet(imageResponses)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .reviews(product.getReviews());

        // Safe null checks for category and collection
        if (product.getCategory() != null) {
            builder.categoryId(product.getCategory().getId());
        }

        if (product.getCollection() != null) {
            builder.collectionId(product.getCollection().getCollectionId());
        }

        return builder.build();
    }

    @Override
    @Transactional
    public Long createProduct(ProductCreationRequest req) {
        log.info("Creating product: {}", req);

        Product product = new Product();
        product.setName(req.getName());
        product.setCode(req.getCode() != null ? req.getCode() : generateProductCode(req.getName()));
        product.setDescription(req.getDescription());
        product.setQuantity(req.getStock());
        product.setPrice(req.getPrice());
        product.setStatus(req.getStatus() != null ? req.getStatus() : "ACTIVE");
        product.setGender(req.getGender());
        product.setMaterial(req.getMaterial());
        product.setGoldKarat(req.getGoldKarat());
        product.setColor(req.getColor());
        product.setBrand(req.getBrand());
        product.setSize(req.getSize());
        product.setViewCount(req.getViewCount() != null ? req.getViewCount() : 0);

        Category category = categoryRepository.findById(req.getCategoryId())
                .orElseThrow(() -> new OpenApiResourceNotFoundException(
                        "Category not found with id: " + req.getCategoryId()));
        product.setCategory(category);

        Collection collection = collectionRepository.findById(req.getCollectionId())
                .orElseThrow(() -> new OpenApiResourceNotFoundException(
                        "Collection not found with id: " + req.getCollectionId()));
        product.setCollection(collection);

        LocalDateTime now = LocalDateTime.now();
        product.setCreatedAt(req.getCreatedAt() != null ? req.getCreatedAt() : now);
        product.setUpdatedAt(req.getUpdatedAt() != null ? req.getUpdatedAt() : now);

        productRepository.save(product);

        log.info("Saved product: {}", product);
        if (req.getImageSet() != null && !req.getImageSet().isEmpty()) {
            List<ProductImage> images = req.getImageSet().stream().map(imageRequest -> {
                ProductImage image = new ProductImage();
                image.setProduct(product);
                image.setImageUrl(imageRequest.getUrl());
                image.setIsPrimary(imageRequest.isThumbnail());
                image.setSortOrder(imageRequest.getSortOrder() != null ? imageRequest.getSortOrder() : 0);
                return image;
            }).toList();

            productImageRepository.saveAll(images);
            log.info("Saved images for product: {}", product.getId());
        }

        return product.getId();
    }

    @Override
    @Transactional
    public Long updateProduct(Long productId, ProductUpdateRequest req) {
        log.info("Updating product with id {}: {}", productId, req);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new OpenApiResourceNotFoundException("Product not found with id: " + productId));

        product.setName(req.getName());
        if (req.getCode() != null) {
            product.setCode(req.getCode());
        }
        product.setDescription(req.getDescription());
        product.setQuantity(req.getStock());
        product.setPrice(req.getPrice());
        if (req.getStatus() != null) {
            product.setStatus(req.getStatus());
        }
        product.setGender(req.getGender());
        product.setMaterial(req.getMaterial());
        product.setGoldKarat(req.getGoldKarat());
        product.setColor(req.getColor());
        product.setBrand(req.getBrand());
        if (req.getSize() != null) {
            product.setSize(req.getSize());
        }
        if (req.getViewCount() != null) {
            product.setViewCount(req.getViewCount());
        }

        if (!product.getCategory().getId().equals(req.getCategoryId())) {
            Category category = categoryRepository.findById(req.getCategoryId())
                    .orElseThrow(() -> new OpenApiResourceNotFoundException(
                            "Category not found with id: " + req.getCategoryId()));
            product.setCategory(category);
        }

        Collection collection = product.getCollection();
        if (collection == null) {
            if (req.getCollectionId() != null) {
                collection = collectionRepository.findById(req.getCollectionId())
                        .orElseThrow(() -> new OpenApiResourceNotFoundException(
                                "Collection not found with id: " + req.getCollectionId()));
                product.setCollection(collection);
            }
        }

        product.setUpdatedAt(LocalDateTime.now());

        productRepository.save(product);

        return product.getId();
    }

    @Override
    @Transactional
    public void deleteProduct(Long productId) {
        log.info("Deleting product with id: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new OpenApiResourceNotFoundException("Product not found with id: " + productId));

        List<ProductImage> images = productImageRepository.findByProduct_Id(productId);
        if (!images.isEmpty()) {
            productImageRepository.deleteAll(images);
            log.info("Deleted {} images associated with product {}", images.size(), productId);
        }

        productRepository.delete(product);
        log.info("Successfully deleted product with id: {}", productId);
    }

    @Override
    public ProductPageResponse getOutOfStockProducts(int page, int size) {
        int pageNo = 0;
        if (page > 0) {
            pageNo = page - 1;
        }

        Pageable pageable = PageRequest.of(pageNo, size);

        Page<Product> products = productRepository.findByQuantityEquals(0, pageable);
        List<ProductResponse> productList = products.stream()
                .map(product -> {
                    List<ImageResponse> imageResponses = product.getImageSet() != null ? product.getImageSet().stream()
                            .map(image -> new ImageResponse(image.getId(), image.getImageUrl(), image.getIsPrimary(),
                                    image.getSortOrder()))
                            .collect(Collectors.toList()) : new ArrayList<>();

                    ProductResponse.ProductResponseBuilder builder = ProductResponse.builder()
                            .productId(product.getId())
                            .name(product.getName())
                            .code(product.getCode())
                            .description(product.getDescription())
                            .stock(product.getQuantity())
                            .price(product.getPrice())
                            .status(product.getStatus())
                            .gender(product.getGender())
                            .material(product.getMaterial())
                            .goldKarat(product.getGoldKarat())
                            .color(product.getColor())
                            .brand(product.getBrand())
                            .size(product.getSize())
                            .viewCount(product.getViewCount())
                            .imageSet(imageResponses)
                            .createdAt(product.getCreatedAt())
                            .updatedAt(product.getUpdatedAt());

                    // Safe null checks for category and collection
                    if (product.getCategory() != null) {
                        builder.categoryId(product.getCategory().getId());
                    }

                    if (product.getCollection() != null) {
                        builder.collectionId(product.getCollection().getCollectionId());
                    }

                    return builder.build();
                })
                .toList();
        ProductPageResponse response = new ProductPageResponse();
        response.setPageNumber(page);
        response.setPageSize(size);
        response.setTotalElements(products.getTotalElements());
        response.setTotalPages(products.getTotalPages());
        response.setProducts(productList);
        return response;
    }

    @Override
    public ProductPageResponse getLowStockProducts(int threshold, int page, int size) {
        int pageNo = 0;
        if (page > 0) {
            pageNo = page - 1;
        }

        Pageable pageable = PageRequest.of(pageNo, size);
        Page<Product> products = productRepository.findByQuantityLessThanEqual(threshold, pageable);
        List<ProductResponse> productList = products.stream()
                .map(product -> {
                    List<ImageResponse> imageResponses = product.getImageSet() != null ? product.getImageSet().stream()
                            .map(image -> new ImageResponse(image.getId(), image.getImageUrl(), image.getIsPrimary(),
                                    image.getSortOrder()))
                            .collect(Collectors.toList()) : new ArrayList<>();

                    ProductResponse.ProductResponseBuilder builder = ProductResponse.builder()
                            .productId(product.getId())
                            .name(product.getName())
                            .code(product.getCode())
                            .description(product.getDescription())
                            .stock(product.getQuantity())
                            .price(product.getPrice())
                            .status(product.getStatus())
                            .gender(product.getGender())
                            .material(product.getMaterial())
                            .goldKarat(product.getGoldKarat())
                            .color(product.getColor())
                            .brand(product.getBrand())
                            .size(product.getSize())
                            .viewCount(product.getViewCount())
                            .imageSet(imageResponses)
                            .createdAt(product.getCreatedAt())
                            .updatedAt(product.getUpdatedAt());

                    // Safe null checks for category and collection
                    if (product.getCategory() != null) {
                        builder.categoryId(product.getCategory().getId());
                    }

                    if (product.getCollection() != null) {
                        builder.collectionId(product.getCollection().getCollectionId());
                    }

                    return builder.build();
                })
                .toList();
        ProductPageResponse response = new ProductPageResponse();
        response.setPageNumber(page);
        response.setPageSize(size);
        response.setTotalElements(products.getTotalElements());
        response.setTotalPages(products.getTotalPages());
        response.setProducts(productList);
        return response;
    }

    @Override
    @Transactional
    public Long updateProductStock(Long productId, int newStock) {
        log.info("Updating stock for product id: {} to {}", productId, newStock);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new OpenApiResourceNotFoundException("Product not found with id: " + productId));

        product.setQuantity(newStock);
        product.setUpdatedAt(LocalDateTime.now());

        Product updatedProduct = productRepository.save(product);
        log.info("Updated stock for product id: {}", productId);

        return updatedProduct.getId();
    }

    @Override
    @Transactional
    public void importProductsFromCSV(MultipartFile file) throws Exception {
        log.info("Starting product import from CSV");

        if (file.isEmpty()) {
            throw new IllegalArgumentException("CSV file is empty");
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            int lineCount = 0;

            line = br.readLine(); // Read the header line
            if (line == null) {
                throw new IllegalArgumentException("CSV file has no content");
            }

            while ((line = br.readLine()) != null) {
                lineCount++;
                try {
                    String[] data = line.split(",");

                    if (data.length < 12) { // Adjust based on the number of expected fields
                        throw new IllegalArgumentException("Invalid CSV format at line " + lineCount);
                    }

                    Product product = new Product();
                    product.setName(data[0].trim());
                    product.setDescription(data[1].trim());
                    product.setQuantity(Integer.parseInt(data[2].trim()));
                    product.setCode(generateProductCode(data[0].trim()));
                    product.setPrice(Double.parseDouble(data[3].trim()));
                    product.setGender(Integer.parseInt(data[4].trim()));
                    product.setMaterial(data[5].trim());
                    product.setGoldKarat(Integer.parseInt(data[6].trim()));
                    product.setColor(data[7].trim());
                    product.setBrand(data[8].trim());
                    product.setStatus("ACTIVE");
                    product.setViewCount(0);
                    product.setCreatedAt(LocalDateTime.now());
                    product.setUpdatedAt(LocalDateTime.now());

                    Category category = categoryRepository.findById(Long.parseLong(data[9].trim()))
                            .orElseThrow(() -> new OpenApiResourceNotFoundException(
                                    "Category not found: " + data[9].trim()));
                    product.setCategory(category);

                    Collection collection = collectionRepository.findById(Long.parseLong(data[10].trim()))
                            .orElseThrow(() -> new OpenApiResourceNotFoundException(
                                    "Collection not found: " + data[10].trim()));
                    product.setCollection(collection);

                    productRepository.save(product);
                    log.info("Imported product from line {}: {}", lineCount, product);

                    ProductImage productImage = new ProductImage();
                    productImage.setProduct(product);
                    productImage.setImageUrl(data[11].trim());
                    productImage.setIsPrimary(true);
                    productImage.setSortOrder(0);
                    productImageRepository.save(productImage);

                    log.info("Saved image for product at line {}: {}", lineCount);

                } catch (Exception e) {
                    log.error("Error processing line {}: {}", lineCount, line, e);
                    throw new RuntimeException("Error processing CSV file at line " + lineCount, e);
                }
            }
            log.info("Successfully imported {} products from CSV", lineCount);
        }
    }

    @Override
    @Transactional
    public List<Long> addImagesToProduct(Long productId, List<ImageRequest> imageRequests)
            throws OpenApiResourceNotFoundException {
        log.info("Adding {} images to product id: {}", imageRequests.size(), productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new OpenApiResourceNotFoundException("Product not found with id: " + productId));

        List<Long> savedImageIds = new ArrayList<>();

        for (ImageRequest imageRequest : imageRequests) {
            ProductImage productImage = new ProductImage();
            productImage.setProduct(product);
            productImage.setImageUrl(imageRequest.getUrl());
            productImage.setIsPrimary(imageRequest.isThumbnail());
            productImage.setSortOrder(imageRequest.getSortOrder() != null ? imageRequest.getSortOrder() : 0);

            ProductImage savedImage = productImageRepository.save(productImage);
            savedImageIds.add(savedImage.getId());
        }

        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);

        log.info("Added {} images to product id: {}", savedImageIds.size(), productId);

        return savedImageIds;
    }

    @Override
    @Transactional
    public void deleteImageFromProduct(Long productId, Long imageId) throws OpenApiResourceNotFoundException {
        log.info("Deleting image id: {} from product id: {}", imageId, productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new OpenApiResourceNotFoundException("Product not found with id: " + productId));

        ProductImage productImage = productImageRepository.findById(imageId)
                .orElseThrow(() -> new OpenApiResourceNotFoundException("Image not found with id: " + imageId));

        if (!productImage.getProduct().getId().equals(productId)) {
            throw new IllegalArgumentException("Image does not belong to the specified product");
        }

        productImageRepository.delete(productImage);

        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);

        log.info("Deleted image id: {} from product id: {}", imageId, productId);
    }

    // Helper method to generate product code from name
    private String generateProductCode(String name) {
        if (name == null || name.isEmpty()) {
            return "PRD" + System.currentTimeMillis();
        }

        // Create code from first letters of each word + timestamp
        StringBuilder code = new StringBuilder("JEC-");
        String[] words = name.split("\\s+");
        for (String word : words) {
            if (!word.isEmpty()) {
                code.append(Character.toUpperCase(word.charAt(0)));
            }
        }

        // Add timestamp to ensure uniqueness
        code.append("-").append(System.currentTimeMillis() % 10000);

        return code.toString();
    }
}