package com.bolsadeideas.springboot.app.controllers;

import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import com.bolsadeideas.springboot.app.models.entity.Proveedor;
import com.bolsadeideas.springboot.app.models.service.IProveedorService;
import com.bolsadeideas.springboot.app.util.paginator.PageRender;

@Controller
@SessionAttributes("proveedores")
public class ProveedorController {
	
	
	@Autowired
	private IProveedorService proveedorService;

	
	@GetMapping(value = "/proveedores/ver/{id}")
	public String ver(@PathVariable(value = "id") Long id, Map<String, Object> model, RedirectAttributes flash) {

		Proveedor proveedor = proveedorService.findOne(id);
		if (proveedor == null) {
			flash.addFlashAttribute("error", "El proveedor no existe en la base de datos");
			return "redirect:/proveedores/listar";
		}

		model.put("proveedor", proveedor);
		model.put("titulo", "Detalle proveedor: " + proveedor.getNombre());
		return "proveedores/ver";
	}

	@RequestMapping(value = "/proveedores/Listar", method = RequestMethod.GET)
	public String listar(@RequestParam(name = "page", defaultValue = "0") int page, Model model) {

		Pageable pageRequest = new PageRequest(page, 4);

		Page<Proveedor> proveedores = proveedorService.findAll(pageRequest);

		PageRender<Proveedor> pageRender = new PageRender<Proveedor>("/proveedores/Listar", proveedores);
		model.addAttribute("titulo", "Listado de proveedores");
		model.addAttribute("proveedores", proveedores);
		model.addAttribute("page", pageRender);
		return "proveedores/Listar";
	}

	@RequestMapping(value = "/proveedores/form")
	public String crear(Map<String, Object> model) {

		Proveedor proveedor = new Proveedor();
		model.put("proveedor", proveedor);
		model.put("titulo", "Crear proveedor");
		return "proveedores/form";
	}
	@RequestMapping(value="/proveedores/form/{id}")
	public String editar(@PathVariable(value="id") Long id, Map<String, Object> model, RedirectAttributes flash) {
		
		Proveedor proveedor = null;
		
		if(id > 0) {
			proveedor = proveedorService.findOne(id);
			if(proveedor == null) {
				flash.addFlashAttribute("error", "El ID del proveedor no existe en la BBDD!");
				return "redirect:proveedores/Listar";
			}
		} else {
			flash.addFlashAttribute("error", "El ID del proveedor no puede ser cero!");
			return "redirect:/proveedores/Listar";
		}
		model.put("proveedor", proveedor);
		model.put("titulo", "Editar Proveedor");
		return "proveedores/form";
		
	}

		@RequestMapping(value = "/proveedores/form", method = RequestMethod.POST)
		public String guardar(@Valid Proveedor proveedor, BindingResult result, Model model, RedirectAttributes flash, SessionStatus status) {

			if (result.hasErrors()) {
				model.addAttribute("titulo", "Formulario de Proveedor");
				return "proveedores/form";
			
		}
		
		String mensajeFlash = (proveedor.getId() != null)? "Proveedor editado con éxito!" : "proveedor creado con éxito!";

		proveedorService.save(proveedor);
		status.setComplete();
		flash.addFlashAttribute("success", mensajeFlash);
		return "redirect:proveedores/Listar";
	}
		
		@RequestMapping(value="/proveedores/eliminar/{id}")
		public String eliminar(@PathVariable(value="id") Long id, RedirectAttributes flash) {

			if (id > 0) {

				Proveedor proveedor = proveedorService.findOne(id);

				proveedorService.delete(id);
				flash.addFlashAttribute("success", "Proveedor eliminado con éxito!");
				
					}
			
			return "redirect:/proveedores/Listar";
		}

	}


