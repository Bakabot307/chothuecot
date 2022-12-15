package com.shopMe.quangcao.role;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RoleService {

    @Autowired
    RoleRepository roleRepository;

    public Role getRoleByName(String name){
       return roleRepository.findByName(name);
    }

    public Role save(Role role){
        return roleRepository.save(role);
    }
}
