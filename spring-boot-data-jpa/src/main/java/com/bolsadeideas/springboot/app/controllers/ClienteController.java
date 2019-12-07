package com.bolsadeideas.springboot.app.controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestWrapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bolsadeideas.springboot.app.models.entity.Cliente;
import com.bolsadeideas.springboot.app.models.service.IClienteService;
import com.bolsadeideas.springboot.app.models.service.IUploadFileService;
import com.bolsadeideas.springboot.app.util.paginator.PageRender;
import com.bolsadeideas.springboot.app.view.xml.ClienteList;

@Controller
@SessionAttributes("cliente")
public class ClienteController {
	
	protected final Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	private IClienteService clienteService;
	
	@Autowired
	private IUploadFileService uploadFileService;
	
	@Autowired
	private MessageSource messageSource;
	
	@Secured("ROLE_USER")
	@GetMapping(value="/uploads/{filename:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String filename) {
		Resource recurso = null;
		try {
			recurso = uploadFileService.load(filename);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachemente; filename=\""+recurso.getFilename()+"\"").body(recurso);
	}
	
	@Secured("ROLE_USER")
	@GetMapping(value = "/ver/{id}")
	public String ver(@PathVariable(value = "id") Long id, Model model, RedirectAttributes flash, Locale locale) {
		Cliente cliente = clienteService.fetchByIdWithFacturas(id);
		
		if (null == cliente) {
			flash.addFlashAttribute("error", messageSource.getMessage("text.cliente.notexist", null, locale));
			return "redirect:/listar";
		}
		
		model.addAttribute("cliente",cliente);
		model.addAttribute("titulo", messageSource.getMessage("text.cliente.detail", null, locale) + cliente.getNombre());
		
		return "ver";
	}
	
	@ResponseBody
	@GetMapping(value = "/listar-rest")
	public ClienteList listarRest() {
		return new ClienteList(clienteService.findAll());
	}
	
	@GetMapping(value = {"/listar","/"})
	public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model, Authentication authentication, HttpServletRequest request, Locale locale) {
		
		if (null != authentication)
			logger.info("Tu username es: " + authentication.getName());
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		if (null != authentication)
			logger.info("(Utilizando de forma estatica SecurityContextHolder) Tu username es: " + auth.getName());
		
		if (hasRole("ROLE_ADMIN"))
			logger.info("Hola "+ auth.getName() + ", tienes acceso.");
		else
			logger.info("Hola "+ auth.getName() + ", NO tienes acceso.");
		
		SecurityContextHolderAwareRequestWrapper securityContext = new SecurityContextHolderAwareRequestWrapper(request, "ROLE_");
		
		if (securityContext.isUserInRole("ADMIN"))
			logger.info("Hola "+ auth.getName() + ", tienes acceso (Con SecurityContextHolderAwareRequestWrapper).");
		else
			logger.info("Hola "+ auth.getName() + ", NO tienes acceso. (Con SecurityContextHolderAwareRequestWrapper)");
		
		if (request.isUserInRole("ROLE_ADMIN"))
			logger.info("Hola "+ auth.getName() + ", tienes acceso (Con Request).");
		else
			logger.info("Hola "+ auth.getName() + ", NO tienes acceso. (Con Request)");
		
		
		Pageable pageRequest = PageRequest.of(page,4);
		
		Page<Cliente> clientes = clienteService.findAll(pageRequest);
		PageRender<Cliente> pageRender = new PageRender<>("/listar",clientes);
		
		model.addAttribute("titulo", messageSource.getMessage("text.cliente.listar.titulo", null, locale));
		model.addAttribute("clientes", clientes);
		model.addAttribute("page", pageRender);
		
		return "listar";
		
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping(value = "/form")
	public String crear(Model model, Locale locale) {
		Cliente cliente = new Cliente();
		model.addAttribute("cliente", cliente);
		model.addAttribute("titulo", messageSource.getMessage("text.cliente.clientform", null, locale));
		return "form";
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping(value = "/form/{id}")
	public String editar(@PathVariable(value = "id") Long id, Model model, RedirectAttributes flash, Locale locale) {
		Cliente cliente = null;
		
		if (id > 0) {
			cliente = clienteService.findOne(id);
			if (null == cliente) {
				flash.addFlashAttribute("error", messageSource.getMessage("text.cliente.notexist", null, locale));
				return "redirect:/listar";
			}
		} else {
			flash.addFlashAttribute("error", messageSource.getMessage("text.cliente.notcero", null, locale));
			return "redirect:/listar";
		}
		
		model.addAttribute("cliente", cliente);
		model.addAttribute("titulo", messageSource.getMessage("text.cliente.editcostumer", null, locale));
		
		return "form";
	}
	
	@PostMapping(value = "/form")
	public String guardar(@Valid Cliente cliente, BindingResult result, Model model, @RequestParam("file") MultipartFile foto, RedirectAttributes flash, SessionStatus status, Locale locale) {
		if (result.hasErrors()) {
			model.addAttribute("titulo", messageSource.getMessage("text.cliente.clientform", null, locale));
			return "form";
		}
		
		if (!foto.isEmpty()) {
			if (cliente.getId() != null && cliente.getId() > 0 && cliente.getFoto() != null && cliente.getFoto().length() > 0) {
				uploadFileService.delete(cliente.getFoto());
			}
			
			String uniqueFilename = null;
			
			try {
				uniqueFilename = uploadFileService.copy(foto);
			} catch (IOException e) {
				e.printStackTrace();
			}
			flash.addFlashAttribute("info", messageSource.getMessage("text.client.theimage", null, locale)+"'" + uniqueFilename + "' "+messageSource.getMessage("text.client.uploaded", null, locale));
			cliente.setFoto(uniqueFilename);
		}
		
		String mensajeFlash = (cliente.getId() != null) ? messageSource.getMessage("text.client.editok", null, locale) : messageSource.getMessage("text.client.createdok", null, locale);
		clienteService.save(cliente);
		status.setComplete();
		flash.addFlashAttribute("success", mensajeFlash);
		return "redirect:listar";
	}
	
	@Secured("ROLE_ADMIN")
	@GetMapping(value = "/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash, Locale locale) {
		if (id > 0) {
			Cliente cliente = clienteService.findOne(id);
			clienteService.delete(id);
			flash.addFlashAttribute("success", messageSource.getMessage("text.client.deletedok", null, locale));
			if (uploadFileService.delete(cliente.getFoto())) {
				flash.addFlashAttribute("info", messageSource.getMessage("text.client.costumerimage", null, locale)+cliente.getNombre()+" "+messageSource.getMessage("text.cliente.delimageok", null, locale));
			}
		}
		return "redirect:/listar";
	}
	
	private boolean hasRole(String role) {
		
		SecurityContext context = SecurityContextHolder.getContext();
		
		if (null == context)
			return false;
		
		Authentication auth = context.getAuthentication();
		
		if (null == auth)
			return false;
		
		Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
		
		return authorities.contains(new SimpleGrantedAuthority(role));
		
		/*for (GrantedAuthority authority : authorities) {
			
			if (role.equals(authority.getAuthority()))
				return true;
			
		}
		
		return false;*/
		
	}
	
}
