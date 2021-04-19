/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example.services;

import com.example.PersistenceManager;
import com.example.models.Producto;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.codehaus.jettison.json.JSONObject;

/**
 *
 * @author dlpol
 */

@Path("/productos")
@Produces(MediaType.APPLICATION_JSON)
public class ProductoService {
    
    @PersistenceContext(unitName = "CompetitorsPU")
    EntityManager entityManager;
    
    @PostConstruct
    public void init() {
        try {
            entityManager = PersistenceManager.getInstance().getEntityManagerFactory().createEntityManager();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @GET
    @Path("/get-productos")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllProductos() {
        Query q = entityManager.createQuery("select u from Producto u order by u.name ASC");
        List<Producto> productos = q.getResultList();
        return Response.status(200).header("Access-Control-Allow-Origin","*").entity(productos).build();

    }
    
    @POST
    @Path("/add-producto")
    @Produces(MediaType.APPLICATION_JSON)
    public Response createProducto(Producto producto) {
        JSONObject rta = new JSONObject();
        Producto productoTmp = new Producto();
        productoTmp.setDescription(producto.getDescription());
        productoTmp.setId(producto.getId());
        productoTmp.setName(productoTmp.getName());
        productoTmp.setPrice(producto.getPrice());
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(productoTmp);
            entityManager.getTransaction().commit();
            entityManager.refresh(productoTmp);
            rta.put("producto_id", productoTmp.getId());
        } 
        catch (Throwable t) {
            t.printStackTrace();
            if (entityManager.getTransaction().isActive()) {
                entityManager.getTransaction().rollback();
            }
            productoTmp = null;
        } 
        finally {
            entityManager.clear();
            entityManager.close();
        }
        return Response.status(200).header("Access-Control-Allow-Origin","*").entity(rta).build();
    }
    
}
