package com.thang.user.service.role;


import com.thang.user.model.entity.Role;

import java.util.List;

public interface IRoleService {
    List<Role> getRoles();
    Role addRole(Role role);
}
