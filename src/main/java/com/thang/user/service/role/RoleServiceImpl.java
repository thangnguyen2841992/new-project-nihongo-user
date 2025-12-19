package com.thang.user.service.role;

import com.thang.user.model.entity.Role;
import com.thang.user.repository.IRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl implements IRoleService{
    @Autowired
    private IRoleRepository roleRepository;

    @Override
    public List<Role> getRoles() {
        return this.roleRepository.findAll();
    }

    @Override
    public void addRole(Role role) {
        this.roleRepository.save(role);
    }

    @Override
    public Optional<Role> findByRoleName(String name) {
        return this.roleRepository.findByRoleName(name);
    }
}
