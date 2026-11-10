package com.dev.demo_service.grpc;

import com.dev.demo_service.entity.Product;
import com.dev.demo_service.service.ProductService;
import com.dev.demo_service.stubs.ProductRequest;
import com.dev.demo_service.stubs.ProductResponse;
import com.dev.demo_service.stubs.ProductServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.grpc.server.service.GrpcService;
import org.springframework.web.server.ResponseStatusException;

@GrpcService
public class ProductServiceImpl extends ProductServiceGrpc.ProductServiceImplBase {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ProductService productService;

    public ProductServiceImpl(ProductService productService) {
        this.productService = productService;
    }

    @Override
    public void getProductDetails(ProductRequest request, StreamObserver<ProductResponse> responseObserver) {
        logger.info("Received gRPC request for product ID: {}", request.getId());
        Product product;
        try {
            product = productService.findById(request.getId());
        } catch (ResponseStatusException exception) {
            logger.error("Error fetching product with ID {}: {}", request.getId(), exception.getReason());
            if (exception.getStatusCode().value() == 404) {
                responseObserver.onError(Status.NOT_FOUND
                        .withDescription(exception.getReason())
                        .asRuntimeException());
                return;
            }
            throw exception;
        }

        ProductResponse response = ProductResponse.newBuilder()
                .setId(product.getId())
                .setName(product.getName())
                .setDescription(product.getDescription())
                .setPrice(String.valueOf(product.getPrice()))
                .setQuantity(product.getQuantity())
                .build();

        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

}