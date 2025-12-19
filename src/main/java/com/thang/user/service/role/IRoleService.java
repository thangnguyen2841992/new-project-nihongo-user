package com.thang.user.service.role;


import com.thang.user.model.entity.Role;

import java.util.List;
import java.util.Optional;

public interface IRoleService {
    List<Role> getRoles();
    void addRole(Role role);
    Optional<Role> findByRoleName(String name);
}
