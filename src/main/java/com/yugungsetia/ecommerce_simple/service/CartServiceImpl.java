package com.yugungsetia.ecommerce_simple.service;

import com.yugungsetia.ecommerce_simple.common.errors.BadRequestException;
import com.yugungsetia.ecommerce_simple.common.errors.ForbiddenAccessException;
import com.yugungsetia.ecommerce_simple.common.errors.ResourceNotFoundException;
import com.yugungsetia.ecommerce_simple.entity.Cart;
import com.yugungsetia.ecommerce_simple.entity.CartItem;
import com.yugungsetia.ecommerce_simple.entity.Product;
import com.yugungsetia.ecommerce_simple.model.CartItemResponse;
import com.yugungsetia.ecommerce_simple.repository.CartItemRepository;
import com.yugungsetia.ecommerce_simple.repository.CartRepository;
import com.yugungsetia.ecommerce_simple.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    @Override
    @Transactional
    public void addItemToCart(Long userId, Long productId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = Cart
                            .builder()
                            .userId(userId)
                            .build();
                    return cartRepository.save(newCart);
                });

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product dengan id " + productId + " tidak ditemukan"));

        if (product.getUserId().equals(userId)) {
            throw new BadRequestException("Produk tidak bisa ditambahkan ke keranjang belanja karena produk tersebut milik anda");
        }

        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartIdAndProductId(cart.getCartId(), productId);

        if (existingItemOpt.isPresent()) {
            CartItem existingItem = existingItemOpt.get();
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItemRepository.save(existingItem);

        } else {
            CartItem newItem = CartItem
                    .builder()
                    .cartId(cart.getCartId())
                    .productId(productId)
                    .quantity(quantity)
                    .price(product.getPrice())
                    .build();

            cartItemRepository.save(newItem);
        }

    }

    @Override
    public void updateCartItemQuantity(Long userId, Long productId, int quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart tidak ditemukan untuk user dengan id " + userId));

        Optional<CartItem> existingItemOpt = cartItemRepository.findByCartIdAndProductId(cart.getCartId(), productId);

        if (existingItemOpt.isEmpty()) {
            throw new ResourceNotFoundException("Product " + productId + " belum di tambahkan ke dalam Cart");
        }

        CartItem item = existingItemOpt.get();
        if (!item.getCartId().equals(cart.getCartId())) {
            throw new ForbiddenAccessException("Cart item bukan milik anda");
        }

        if (quantity <= 0) {
            cartItemRepository.deleteById(item.getCartItemId());
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }
    }

    @Transactional
    @Override
    public void removeItemFromCart(Long userId, Long cartItemId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart tidak ditemukan untuk user dengan id " + userId));

        Optional<CartItem> existingItemOpt = cartItemRepository.findById(cartItemId);

        if (existingItemOpt.isEmpty()) {
            throw new ResourceNotFoundException("Cart item tidak ditemukan");
        }

        CartItem item = existingItemOpt.get();
        if (!item.getCartId().equals(cart.getCartId())) {
            throw new ForbiddenAccessException("Cart item bukan milik anda");
        }

        cartItemRepository.deleteById(cartItemId);

    }

    @Transactional
    @Override
    public void clearCart(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart tidak ditemukan untuk user dengan id " + userId));

        cartItemRepository.deleteAllByCartId(cart.getCartId());
    }

    @Override
    public List<CartItemResponse> getCartItems(Long userId) {
        List<CartItem> cartItems = cartItemRepository.getUserCartItems(userId);
        if (cartItems.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> productIds = cartItems.stream()
                .map(CartItem::getProductId)
                .toList();

        List<Product> products = productRepository.findAllById(productIds);

        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getProductId, Function.identity()));

        return cartItems.stream()
                .map(cartItem -> {
                    Product product = productMap.get(cartItem.getProductId());
                    if (product == null) {
                        throw new ResourceNotFoundException("Product tidak ditemukan dengan id " + cartItem.getProductId());
                    }
                    return CartItemResponse.fromCartItemAndProduct(cartItem, product);
                })
                .toList();
    }
}
