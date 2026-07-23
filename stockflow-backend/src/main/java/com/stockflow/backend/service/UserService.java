package com.stockflow.backend.service;

import com.stockflow.backend.dto.user.UserRequest;
import com.stockflow.backend.dto.user.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    Page<UserResponse> getAllUsersPaginated(Pageable pageable);
    Page<UserResponse> searchUsers(String keyword, Pageable pageable);
    List<UserResponse> getAllUsers();
    UserResponse getUserById(Long id);
    UserResponse createUser(UserRequest request);
    UserResponse updateUser(Long id, UserRequest request);
    void deleteUser(Long id);
}
