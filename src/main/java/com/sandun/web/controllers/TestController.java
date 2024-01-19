package com.sandun.web.controllers;

import com.sandun.web.dto.ReqResponse;
import com.sandun.web.dto.User;
import com.sandun.web.entities.Test;
import com.sandun.web.util.HibernateUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.ext.Provider;
import org.hibernate.Session;
import org.hibernate.Transaction;

@Path("/test")
public class TestController {
    @POST
    public String index() {
        System.out.println("A Request!!");
//        System.out.println(response.getId());
        return "{\"response\":\"Success\"}";
    }
}
