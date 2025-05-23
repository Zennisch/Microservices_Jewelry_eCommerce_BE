package com.iuh.edu.fit.BEJewelry.Architecture.controller;

import com.iuh.edu.fit.BEJewelry.Architecture.domain.Role;
import com.iuh.edu.fit.BEJewelry.Architecture.domain.response.ResultPaginationDTO;
import com.iuh.edu.fit.BEJewelry.Architecture.service.RoleService;
import com.iuh.edu.fit.BEJewelry.Architecture.util.annotation.ApiMessage;
import com.iuh.edu.fit.BEJewelry.Architecture.util.error.IdInvalidException;
import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class RoleController {
    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    @GetMapping("/roles/{id}")
    @ApiMessage("Fetch role by id")
    public ResponseEntity<Role> getById(@PathVariable("id") long id) throws IdInvalidException {
        Role role = this.roleService.fetchById(id);
        if (role == null) {
            throw new IdInvalidException("Role với id = " + id + " không tồn tại");
        }
        return ResponseEntity.ok().body(role);
    }

    @GetMapping("/roles")
    @ApiMessage("Fetch roles")
    public ResponseEntity<ResultPaginationDTO> getPermissions(@Filter Specification<Role> spec, Pageable pageable) {
        return ResponseEntity.ok(this.roleService.getRoles(spec, pageable));
    }

    @PostMapping("/roles")
    @ApiMessage("Create a role")
    public ResponseEntity<Role> create(@Valid @RequestBody Role r) throws IdInvalidException {
        if (this.roleService.existByName(r.getName())) {
            throw new IdInvalidException("Role với name = " + r.getName() + " đã tồn tại");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(this.roleService.create(r));
    }

    @PutMapping("/roles")
    @ApiMessage("Update a role")
    public ResponseEntity<Role> update(@Valid @RequestBody Role r) throws IdInvalidException {
        if (this.roleService.fetchById(r.getId()) == null) {
            throw new IdInvalidException("Role với id = " + r.getId() + " không tồn tại");
        }
        return ResponseEntity.ok().body(this.roleService.update(r));
    }

    @DeleteMapping("/roles/{id}")
    @ApiMessage("Delete a role")
    public ResponseEntity<Void> delete(@PathVariable("id") long id) throws IdInvalidException {
        if (this.roleService.fetchById(id) == null) {
            throw new IdInvalidException("Role với id = " + id + " không tồn tại");
        }
        this.roleService.delete(id);
        return ResponseEntity.ok().body(null);
    }
}