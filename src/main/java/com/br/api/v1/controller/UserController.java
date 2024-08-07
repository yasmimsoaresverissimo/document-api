package com.br.api.v1.controller;

import com.br.domain.repository.spec.TemplateSpec;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.br.api.v1.model.input.UserActiveModelInput;
import com.br.api.v1.model.input.UserModelInput;
import com.br.api.v1.mapper.DepartamentoModelMapper;
import com.br.api.v1.mapper.UserEditModelMapperBack;
import com.br.api.v1.mapper.UserModelMapper;
import com.br.api.v1.mapper.UserModelMapperBack;
import com.br.api.v1.model.*;
import com.br.domain.model.Departamento;
import com.br.domain.model.User;
import com.br.domain.service.DepartamentoService;
import com.br.domain.service.UserService;

import io.swagger.annotations.*;

@Api(tags = "User")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/v1/user")
public class UserController {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserModelMapper userModelMapper;
	
	@Autowired
	private UserModelMapperBack userModelMapperBack;
	
	@Autowired
	private UserEditModelMapperBack userEditModelMapperBack;

	@Autowired
	private DepartamentoService departmentService;

	@Autowired
	private DepartamentoModelMapper departmentModelMapper;
	
	@ApiOperation("Retorna uma lista de usuários.")
    @ApiResponses({
        @ApiResponse(code = 200, message = "Usuários listados sucesso."),
        @ApiResponse(code = 500, message = "Ocorreu um erro interno.")
    })
	
	
	@PostMapping("/cadastrar")
	public ResponseEntity<UserModel> cadastrar(@RequestBody @Valid UserModelInput userModelInput) {
		User user = userModelMapperBack.toModel(userModelInput);
		UserModel userModel = userModelMapper.toModel(userService.save(user));
		return ResponseEntity.status(HttpStatus.CREATED).body(userModel);
	}

	@GetMapping("/listar")
	public ResponseEntity<Page<UserDepartmentModel>> getUsers(TemplateSpec.UserSpec spec,
	                                                          @PageableDefault(page = 0, size = 5) Pageable pageable) {
	    Page<User> usersPage = userService.findAll(spec, Pageable.unpaged());
	    List<UserDepartmentModel> userDepartmentModels = new ArrayList<>();
	    for (User user : usersPage.getContent()) {
	        UserModel userModel = userModelMapper.toModel(user);
	        
	        // Busca o departamento localmente usando o DepartmentService
	        Departamento departamento = departmentService.findById(user.getDepartmentId());
	        DepartmentListModel departmentModel = departmentModelMapper.toModelList(departamento);

	        UserDepartmentModel userDepartmentModel = new UserDepartmentModel();
	        userDepartmentModel.setUserModel(userModel);
	        userDepartmentModel.setDepartment(departmentModel);
	        userDepartmentModels.add(userDepartmentModel);
	    }
	    return ResponseEntity.ok().body(new PageImpl<>(userDepartmentModels, usersPage.getPageable(), usersPage.getTotalElements()));
	}


	@GetMapping("/department/{dapartmentId}")
	public ResponseEntity<List<UserModel>> getListar(@PathVariable (name = "dapartmentId") UUID dapartmentId) {
		return ResponseEntity.status(HttpStatus.OK).body(userModelMapper.toCollectionModel(userService.buscarUsuariosDoDepartamento(dapartmentId)));
	}

	//@PreAuthorize("hasAnyRole('ROLE_ESTAGIARIO')")
	@GetMapping("/buscar/{id}")
	public ResponseEntity<User> getUser(@PathVariable(name = "id") UUID id) {
		User user = userService.findById(id);
		return ResponseEntity.status(HttpStatus.OK).body(user);
	}

	//@PreAuthorize("hasAnyRole('ROLE_ESTAGIARIO')")
	@GetMapping("/buscar/{matricula}/matricula")
	public ResponseEntity<UserMatriculaModel> getUser(@PathVariable(name = "matricula") String matricula) {
		User user = userService.findByMatricula(matricula);
		UserMatriculaModel userMatriculaModel = 
					userModelMapper.toModelMatricula(user);
			return ResponseEntity.status(HttpStatus.OK).body(userMatriculaModel);
	}

	@PatchMapping("/ativar-desativar/{id}")
    public ResponseEntity<UserModel> activateUser(@RequestBody UserActiveModelInput userActiveModelInput,
																  @PathVariable(name = "id") UUID id ) {
		return ResponseEntity.status(HttpStatus.CREATED).body(
				userModelMapper.toModel(userService.activaUser(id, userActiveModelInput.isActive())));
 	}

	//@PreAuthorize("hasAnyRole('ROLE_GESTOR')")
    @PutMapping("/desativar/{id}")
    public ResponseEntity<UserModel> deactivateUser( @RequestBody UserModelInput userModelInput, 
    		@PathVariable (name = "id") UUID id) {
	    User user = userService.findById(id);
		userEditModelMapperBack.copyToDomainObject(userModelInput, user);
		return ResponseEntity.status(HttpStatus.CREATED).body(userModelMapper.toModel(userService.deactivateUser(id)));
	}
    
    @GetMapping("/filtro")
    public ResponseEntity<Page<User>> getFiltro(
            @RequestParam(value = "matricula", required = false) String matricula,
            @RequestParam(value = "nome", required = false) String nome,
            @RequestParam(value = "departmentId", required = false) UUID departmentId,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {

        // Chama o serviço de filtro com os parâmetros fornecidos
        Page<User> result = userService.Filtro(matricula, nome, departmentId, pageable);
        
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

}
