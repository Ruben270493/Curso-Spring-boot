package com.bolsadeideas.springboot.di.app.models.service;

//@Primary
//@Component("miServicioSimple")
public class MiServicio implements IServicio{
	
	@Override
	public String operacion() {
		return "Ejecutando algún proceso importante simple...";
	}
	
}
