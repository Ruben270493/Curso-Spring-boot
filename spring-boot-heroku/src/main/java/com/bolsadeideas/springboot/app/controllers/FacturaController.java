package com.bolsadeideas.springboot.app.controllers;

import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bolsadeideas.springboot.app.models.entity.Cliente;
import com.bolsadeideas.springboot.app.models.entity.Factura;
import com.bolsadeideas.springboot.app.models.entity.ItemFactura;
import com.bolsadeideas.springboot.app.models.entity.Producto;
import com.bolsadeideas.springboot.app.models.service.IClienteService;

@Controller
@Secured("ROLE_ADMIN")
@RequestMapping("/factura")
@SessionAttributes("factura")
public class FacturaController {
	
	@Autowired
	private IClienteService clienteService;
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private MessageSource messageSource;
	
	@GetMapping("/ver/{id}")
	public String ver(@PathVariable(value = "id") Long id, Model model, RedirectAttributes flash, Locale locale) {
		
		Factura factura = clienteService.fetchFacturaByIdWithClienteWithItemFacturaWithProducto(id); 
		//clienteService.findFacturaById(id);
		
		if (null == factura) {
			flash.addFlashAttribute("error", messageSource.getMessage("text.invoice.noexist", null, locale));
			return "redirect:/listar";
		}
		
		model.addAttribute("factura",factura);
		model.addAttribute("titulo", messageSource.getMessage("text.invoice.name", null, locale)+": "+factura.getDescripcion());
		
		return "factura/ver";
	}
	
	@GetMapping("/form/{clienteId}")
	public String crear(@PathVariable(value = "clienteId") Long clienteId, Model model, RedirectAttributes flash, Locale locale) {
		Cliente cliente = clienteService.findOne(clienteId);
		
		if (null == cliente) {
			flash.addFlashAttribute("error", messageSource.getMessage("text.cliente.notexist", null, locale));
			return "redirect:/listar";
		}
		
		Factura factura = new Factura();
		factura.setCliente(cliente);
		
		model.addAttribute("factura", factura);
		model.addAttribute("titulo", messageSource.getMessage("text.invoice.create", null, locale));
		
		return "factura/form";
	}
	
	@GetMapping(value = "/cargar-productos/{term}", produces = {"application/json"})
	public @ResponseBody List<Producto> cargarProducto(@PathVariable String term) {
		return clienteService.findByNombre(term);
	}
	
	@PostMapping("/form")
	public String guardar(@Valid Factura factura, BindingResult result, @RequestParam(name = "item_id[]", required = false) Long[] itemId, 
			@RequestParam(name = "cantidad[]", required = false) Integer[] cantidad, RedirectAttributes flash, SessionStatus status, Model model, Locale locale) {
		
		if (result.hasErrors()) {
			model.addAttribute("titulo", messageSource.getMessage("text.invoice.create", null, locale));
			return "factura/form";
		}
		
		if (itemId == null || itemId.length == 0) {
			model.addAttribute("titulo", messageSource.getMessage("text.invoice.create", null, locale));
			model.addAttribute("error", messageSource.getMessage("text.invoice.notlines", null, locale));
			return "factura/form";
		}
		
		for (int i = 0; i < itemId.length; i++) {
			
			Producto producto = clienteService.findProductoById(itemId[i]);
			ItemFactura linea = new ItemFactura();
			linea.setCantidad(cantidad[i]);
			linea.setProducto(producto);
			factura.addItemFactura(linea);
			
			log.info("ID: " + itemId[i].toString() + ", "+ messageSource.getMessage("text.invoice.quantity", null, locale) +": " + cantidad[i].toString());
			
		}
		
		clienteService.saveFactura(factura);
		status.setComplete();
		
		flash.addFlashAttribute("success", messageSource.getMessage("text.invoice.ok", null, locale));
		
		return "redirect:/ver/" + factura.getCliente().getId();
		
	}
	
	@GetMapping("/eliminar/{id}")
	public String eliminar(@PathVariable(value = "id") Long id, RedirectAttributes flash, Locale locale) {
		Factura factura = clienteService.findFacturaById(id);
		
		if (null != factura) {
			
			clienteService.deleteFactura(id);
			flash.addFlashAttribute("success", messageSource.getMessage("text.invoice.deletedok", null, locale));
			
			return "redirect:/ver/"+factura.getCliente().getId();
			
		}
		
		flash.addFlashAttribute("error", messageSource.getMessage("text.invoice.noexist", null, locale));
		
		return "redirect:/listar";
	}
}
