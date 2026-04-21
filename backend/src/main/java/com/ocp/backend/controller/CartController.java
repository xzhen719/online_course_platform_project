package com.ocp.backend.controller;

import com.ocp.backend.dto.response.CartDto;
import com.ocp.backend.dto.response.CartItemDto;
import com.ocp.backend.entity.Cart;
import com.ocp.backend.entity.CartItem;
import com.ocp.backend.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    // 1. Get My Cart
    // GET /api/cart
    @GetMapping

    // JWT token (filter -> SecurityContextHolder) -> userDetails -> email -> user
    // -> cart
    public ResponseEntity<CartDto> getMyCart(@AuthenticationPrincipal UserDetails userDetails) {
        // A. Get entity from service
        String email = userDetails.getUsername();
        Cart cart = cartService.getUserCart(email);

        // B. Convert Entity to DTO (To avoid infinite loop)
        CartDto cartDto = convertToDto(cart);
        return ResponseEntity.ok(cartDto);

    }

    // 2. Add to Cart
    // POST /api/cart/add/{courseId}
    @PostMapping("/add/{courseId}")
    public ResponseEntity<String> addToCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long courseId) {

        cartService.addToCart(userDetails.getUsername(), courseId);
        return ResponseEntity.ok("add to cart successfully!");
    }

    // 3. Remove from Cart
    // DELETE /api/cart/remove/{cartItemId}
    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<String> removeFromCart(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long cartItemId) {
        cartService.removeFromCart(userDetails.getUsername(), cartItemId);
        return ResponseEntity.ok("remove from cart successfully!");
    }

    // --- Helper Method: Converter ---
    private CartDto convertToDto(Cart cart) {
        // Convert List<CartItem> -> List<CartItemDto>
        List<CartItemDto> itemDtoList = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            CartItemDto itemDto = new CartItemDto();
            itemDto.setId(cartItem.getId());
            itemDto.setCourseId(cartItem.getCourse().getId());
            itemDto.setCourseName(cartItem.getCourse().getName());
            itemDto.setPrice(cartItem.getCourse().getPrice());
            itemDto.setImageUrl(cartItem.getCourse().getImageUrl());

            itemDtoList.add(itemDto);
        }

        // Calculate Total Amount
        double totalPrice = 0.0;
        // TODO: Sum up the price of all items and set it to dto.setTotalPrice(...)

        for (CartItemDto cartItemDto : itemDtoList) {
            if (cartItemDto.getPrice() != null) {
                totalPrice += cartItemDto.getPrice();
            }
        }
        // Hint: You can use a loop or stream to sum 'itemDto.getPrice()'
        CartDto cartDto = new CartDto();
        cartDto.setId(cart.getId());
        cartDto.setItems(itemDtoList);
        cartDto.setTotalPrice(totalPrice);

        return cartDto;
    }
}
