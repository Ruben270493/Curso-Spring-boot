package com.bolsadeideas.springboot.webflux.models.dao;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.bolsadeideas.springboot.webflux.models.documents.Producto;

public interface ProductoDao extends ReactiveMongoRepository<Producto, String> {}