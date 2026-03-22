package com.healthcare.api.service.impl;

import com.healthcare.api.dto.request.UserCreationRequest;
import com.healthcare.api.dto.request.UserUpdateRequest;
import com.healthcare.api.dto.response.PageResponse;
import com.healthcare.api.dto.response.UserResponse;
import com.healthcare.api.entity.Doctor;
import com.healthcare.api.entity.Patient;
import com.healthcare.api.entity.Role;
import com.healthcare.api.entity.User;
import com.healthcare.api.exception.AppException;
import com.healthcare.api.exception.ErrorCode;
import com.healthcare.api.mapper.DoctorMapper;
import com.healthcare.api.mapper.PatientMapper;
import com.healthcare.api.mapper.UserMapper;
import com.healthcare.api.repository.DoctorRepository;
import com.healthcare.api.repository.PatientRepository;
import com.healthcare.api.repository.RoleRepository;
import com.healthcare.api.repository.UserRepository;
import com.healthcare.api.service.UserService;
import com.healthcare.api.utils.security.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final DoctorMapper doctorMapper;
    private final PatientMapper patientMapper;

    @Override
    @Transactional
    public UserResponse createUser(UserCreationRequest request){
        if(userRepository.existsByEmail(request.getEmail())){
            throw new AppException(ErrorCode.USER_EXISTED);
        }

        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setStatus("ACTIVE");
        user.setRole(role);

        User savedUser = userRepository.save(user);

        if(role.getName().toUpperCase().contains("DOCTOR")){
            Doctor doctor = doctorMapper.toDoctor(request);
            doctor.setUser(savedUser);
            doctorRepository.save(doctor);
        } else if(role.getName().toUpperCase().contains("PATIENT")){
            Patient patient = patientMapper.toPatient(request);
            patient.setUser(savedUser);
            patientRepository.save(patient);
        }

        return mapToUserResponse(savedUser);
    }

    @Override
    public UserResponse getMyInfo() {
        String email = SecurityUtils.getCurrentUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return mapToUserResponse(user);
    }

    @Override
    public PageResponse<UserResponse> getUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<User> userPage = userRepository.findAll(pageable);

        List<UserResponse> userResponses = userPage.getContent().stream()
                .map(this::mapToUserResponse)
                .toList();

        return PageResponse.<UserResponse>builder()
                .currentPage(page)
                .totalPages(userPage.getTotalPages())
                .pageSize(userPage.getSize())
                .totalElements(userPage.getTotalElements())
                .data(userResponses)
                .build();
    }

    @Override
    public UserResponse getUser(String id){
        User user = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        return mapToUserResponse(user);
    }

    @Override
    @Transactional
    public UserResponse updateUser(String id, UserUpdateRequest request){
        User user = userRepository.findById(UUID.fromString(id))
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        userMapper.updateUser(user, request);

        if(request.getPassword() != null &&  !request.getPassword().isEmpty()){
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        User savedUser = userRepository.save(user);

        return mapToUserResponse(savedUser);
    }

    @Override
    public void deleteUser(String id){
        if(!userRepository.existsById(UUID.fromString(id))){
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        userRepository.deleteById(UUID.fromString(id));
    }

    private UserResponse mapToUserResponse(User user){
        UserResponse response = userMapper.toUserResponse(user);

        if (user.getRole().getName().toUpperCase().contains("DOCTOR")) {
            doctorRepository.findByUserId(user.getId()).ifPresent(response::setProfile);
        } else if (user.getRole().getName().toUpperCase().contains("PATIENT")) {
            patientRepository.findByUserId(user.getId()).ifPresent(response::setProfile);
        }
        return response;
    }
}
