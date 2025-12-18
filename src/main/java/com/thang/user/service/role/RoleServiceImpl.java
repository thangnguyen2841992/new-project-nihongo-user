package com.thang.user.service.role;

import com.thang.user.model.entity.Role;
import com.thang.user.repository.IRoleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements IRoleService{

    private final IRoleRepository roleRepository;

    public RoleServiceImpl(IRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    public List<Role> getRoles() {
        return this.roleRepository.findAll();
    }

    @Override
    public Role addRole(Role role) {
        return this.roleRepository.save(role);
    }
}
