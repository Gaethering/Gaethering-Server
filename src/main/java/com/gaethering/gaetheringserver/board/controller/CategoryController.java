package com.gaethering.gaetheringserver.board.controller;

import com.gaethering.gaetheringserver.board.dto.CategoryRequest;
import com.gaethering.gaetheringserver.board.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${api-prefix}")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/boards/category/add")
    public ResponseEntity<Void> addCategory (@RequestBody CategoryRequest request) {
        categoryService.addCategory(request);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
}
