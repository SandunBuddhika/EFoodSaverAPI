package com.sandun.web.middleware;

import com.sandun.web.entities.User;
import com.sandun.web.service.UserService;
import com.sandun.web.util.JWTUtil;
import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.net.URI;


@Provider
@Priority(1)
public class JwtValidationFilter implements ContainerRequestFilter {
    @Inject
    JWTUtil jwtUtil;
    @Inject
    UserService userService;
    @Context
    HttpServletRequest request;

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {
        System.out.println(request);
        String header = request.getHeader("authorization");
        String token = "";
        if (header != null) {
            token = header.replace("Bearer", "");

        }
        System.out.println(token);
        String[] paths = request.getServletPath().split("/");
        String path = paths[paths.length - 1];
        if (path.equals("insert") || path.equals("auth") || path.equals("verification") || path.equals("resendVerificationCode")) {
            return;
        } else {
            if (token != null && !token.isBlank()) {
                try {
                    User user = userService.getById(Long.parseLong(jwtUtil.getUserIdFromToken(token)));
                    if (jwtUtil.validateToken(token, user) && user.getStatus() != 0) {
                        return;
                    } else {
                        containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("{\"response\",\"Unauthoried\"}").build());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                containerRequestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("{\"response\",\"Unauthoried\"}").build());
            }
        }

    }
}
