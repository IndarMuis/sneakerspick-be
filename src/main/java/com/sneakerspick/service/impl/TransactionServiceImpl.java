package com.sneakerspick.service.impl;

import com.sneakerspick.domain.*;
import com.sneakerspick.dto.request.CheckoutRequest;
import com.sneakerspick.dto.request.TransactionSearchRequest;
import com.sneakerspick.dto.response.CheckoutResponse;
import com.sneakerspick.dto.response.ItemCheckoutResponse;
import com.sneakerspick.dto.response.ProductResponse;
import com.sneakerspick.dto.response.TransactionResponse;
import com.sneakerspick.enums.PaymentType;
import com.sneakerspick.enums.TransactionStatus;
import com.sneakerspick.repository.ProductRepository;
import com.sneakerspick.repository.TransactionItemRepository;
import com.sneakerspick.repository.TransactionRepository;
import com.sneakerspick.repository.UserRepository;
import com.sneakerspick.service.TransactionService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionItemRepository transactionItemRepository;
    private final ProductRepository productRepository;
    private final ValidationService validationService;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CheckoutResponse checkout(CheckoutRequest request) {
        validationService.validate(request);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = (String) auth.getPrincipal();
        User user = userRepository.findUserByUsername(username).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found"));

        Transaction transaction = new Transaction();
        transaction.setAddress(request.getAddress());
        transaction.setShippingPrice(request.getShippingPrice());
        transaction.setTotalPrice(request.getTotalPrice());
        transaction.setPaymentType(
                request.getPaymentType() != null ? request.getPaymentType() : PaymentType.MANUAL
        );
        transaction.setTransactionStatus(
                request.getTransactionStatus() != null ? request.getTransactionStatus() : TransactionStatus.PENDING
        );
        transaction.setUser(user);

        log.info("SAVE TRANSACTION");
        Transaction transactionSave = transactionRepository.save(transaction);

        List<ItemCheckoutResponse> items = new ArrayList<>();
        request.getItems().forEach((item) -> {
            log.info("SAVE TRANSACTION ITEM");
            TransactionItem transactionItem = new TransactionItem();
            Product product = productRepository.findById(item.getProductId()).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "product not found")
            );
            transactionItem.setUser(user);
            transactionItem.setProduct(product);
            transactionItem.setQuantity(item.getQuantity());
            transactionItem.setTransaction(transaction);
            TransactionItem saveItems = transactionItemRepository.save(transactionItem);

            items.add(toItemCheckoutResponse(saveItems));
        });

        return CheckoutResponse.builder()
                .id(transactionSave.getId())
                .userId(user.getId())
                .address(transactionSave.getAddress())
                .shippingPrice(transactionSave.getShippingPrice())
                .totalPrice(transactionSave.getTotalPrice())
                .status(transactionSave.getTransactionStatus())
                .items(items)
                .build();
    }

    @Override
    public Page<TransactionResponse> getAllTransaction(TransactionSearchRequest request) {
        Specification<Transaction> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (Objects.nonNull(request.getId())) {
                predicates.add(builder.equal(root.get("id"), request.getId()));
            }

            if (Objects.nonNull(request.getStatus())) {
                predicates.add(builder.equal(root.get("transaction_status"), request.getStatus()));
            }

            return query.where(predicates.toArray(new Predicate[]{})).getRestriction();
        };

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<Transaction> transactions = transactionRepository.findAll(specification, pageable);

        if (transactions.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "data not found");
        } else {
            List<TransactionResponse> transactionResponses = transactions.getContent().stream().map(this::toTransactionResponse).toList();
            return new PageImpl<>(transactionResponses, pageable, transactions.getTotalElements());
        }
    }

    private TransactionResponse toTransactionResponse(Transaction transaction) {
        List<ItemCheckoutResponse> itemCheckoutResponses = transaction.getTransactionItems().stream().map(this::toItemCheckoutResponse).toList();
        return TransactionResponse.builder()
                .id(transaction.getId())
                .userId(transaction.getUser().getId())
                .address(transaction.getAddress())
                .shippingPrice(transaction.getShippingPrice())
                .totalPrice(transaction.getTotalPrice())
                .transactionStatus(transaction.getTransactionStatus())
                .paymentType(transaction.getPaymentType())
                .items(itemCheckoutResponses)
                .build();
    }

    private ItemCheckoutResponse toItemCheckoutResponse(TransactionItem item) {
        ProductResponse product = ProductResponse.builder()
                .id(item.getProduct().getId())
                .name(item.getProduct().getName())
                .price(item.getProduct().getPrice())
                .tags(item.getProduct().getTags())
                .description(item.getProduct().getDescription())
                .category(item.getProduct().getProductCategory().getName())
                .galleries(
                        item.getProduct().getGalleries().stream().map(ProductGallery::getUrl).toList()
                )
                .build();
        return ItemCheckoutResponse.builder()
                .id(item.getId())
                .userId(item.getUser().getId())
                .product(product)
                .quantity(item.getQuantity())
                .transactionId(item.getTransaction().getId())
                .build();

    }

}
