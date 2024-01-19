package com.sandun.web.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;

@Path("fb-call-back")
public class FbCallBackController {
    @GET
    public void index(@Context HttpServletRequest request){
        System.out.println("Request!!");
        System.out.println(request);
    }
}
