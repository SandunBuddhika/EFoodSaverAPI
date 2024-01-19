package com.sandun.web.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sandun.web.dto.SignUpAuth;
import com.sandun.web.dto.User;
import com.sandun.web.entities.address.Address;
import com.sandun.web.entities.address.Location;
import com.sandun.web.service.LocationService;
import com.sandun.web.service.UserService;
import com.sandun.web.util.JWTUtil;
import com.sandun.web.util.JavaMailUnit;
import jakarta.inject.Inject;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Path("/user")
public class UserController {
    @Inject
    UserService userService;
    @Inject
    LocationService locationService;

    @Path("/insert")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response insert(SignUpAuth user) {
        System.out.println(user);
        Map<String, Object> errorMessages = new HashMap<>();
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<SignUpAuth>> violations = validator.validate(user);
            if (!violations.isEmpty()) {
                for (ConstraintViolation<SignUpAuth> error : violations) {
                    errorMessages.put(error.getPropertyPath().toString(), error.getMessage());
                }
                return Response.status(Response.Status.OK).entity(errorMessages).build();
            } else {
                List<Object> insertResult = userService.insert(user);
                if (insertResult.size() != 3) {
                    errorMessages.put("response", "Email or Contact No Already Exist");
                    return Response.status(Response.Status.OK).entity(errorMessages).build();
                }
                String regex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(user.getEmailOrCno());
                if (matcher.matches()) {
                    errorMessages.put("type", "Email");
                } else {
                    errorMessages.put("type", "Mobile");
                }
                com.sandun.web.entities.User u = ((com.sandun.web.entities.User) insertResult.get(2));
                ObjectNode responseU = createUResponseObj(u);
                new Thread(() -> {
                    JavaMailUnit mailUnit = new JavaMailUnit();
                    mailUnit.emailVerificationProcessMail(u.getEmailOrCno(), (String) insertResult.get(1));
                }).start();
                errorMessages.put("data", responseU);
                errorMessages.put("response", "Success");
                System.out.println(errorMessages);
                return Response.status(Response.Status.OK).entity(errorMessages).build();
            }
        } catch (Exception e) {
            errorMessages.put("response", "Something Went Wrong Please Try Again Later");
            return Response.status(Response.Status.OK).entity(errorMessages).build();
        }
    }

    @Path("/auth")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response auth(User user) {
        System.out.println(user);
        Map<String, Object> errorMessages = new HashMap<>();
        try (ValidatorFactory factory = Validation.buildDefaultValidatorFactory()) {
            Validator validator = factory.getValidator();
            Set<ConstraintViolation<User>> violations = validator.validate(user);
            if (!violations.isEmpty()) {
                for (ConstraintViolation<User> error : violations) {
                    errorMessages.put(error.getPropertyPath().toString(), error.getMessage());
                }
                System.out.println(errorMessages);
                return Response.status(Response.Status.OK).entity(errorMessages).build();
            } else {
                com.sandun.web.entities.User user2 = userService.signIn(user.getEmailOrCno(), user.getPassword(), user.getType());
                System.out.println(user2);
                if (user2 == null && user2.getUserType().equals("User")) {
                    if (user.getIsGoogle()) {
                        if (userService.insert(new SignUpAuth(user.getfName(), user.getlName(), user.getEmailOrCno(), user.getPassword(), user.getImg())).get(0).equals("false")) {
                            errorMessages.put("Error!", "Something Went Wrong Please Try Again Later");
                            return Response.status(Response.Status.OK).entity(errorMessages).build();
                        }
                        errorMessages.put("response", "Success");
                        user2 = userService.signIn(user.getEmailOrCno(), user.getPassword(), user.getType());
                    } else {
                        errorMessages.put("Error!", "Details Are Invalid!!");
                        return Response.status(Response.Status.OK).entity(errorMessages).build();
                    }
                } else {
                    if (user2.getStatus() == 0) {
                        String regex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Z|a-z]{2,}$";
                        Pattern pattern = Pattern.compile(regex);
                        Matcher matcher = pattern.matcher(user.getEmailOrCno());
                        if (matcher.matches()) {
                            errorMessages.put("type", "Email");
                        } else {
                            errorMessages.put("type", "Mobile");
                        }
                        errorMessages.put("response", "Please Verify Your Account");
                    } else {
                        errorMessages.put("response", "Success");
                    }
                }
                ObjectNode responseU = createUResponseObj(user2);
                errorMessages.put("data", responseU);

                JWTUtil jwtUtil = new JWTUtil();
                String token = jwtUtil.generateAccessToken(user2);
                errorMessages.put("token", token);

                return Response.status(Response.Status.OK).entity(errorMessages).build();
            }
        } catch (Exception e) {
            errorMessages.put("response", "Something Went Wrong Please Try Again Later");
            return Response.status(Response.Status.OK).entity(errorMessages).build();
        }
    }

    private ObjectNode createUResponseObj(com.sandun.web.entities.User user2) {
        ObjectNode responseU = new ObjectNode(JsonNodeFactory.instance);
        responseU.put("id", String.valueOf(user2.getId()));
        responseU.put("fName", user2.getfName());
        responseU.put("lName", user2.getlName());
        responseU.put("emailOrCno", user2.getEmailOrCno());
        responseU.put("userType", user2.getUserType().getName());
        responseU.put("status", String.valueOf(user2.getStatus()));
        return responseU;
    }

    @Path("/verification")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response checkVerification(JsonNode obj) {
        System.out.println(obj);
        Map<String, String> message = new HashMap<>();
        com.sandun.web.entities.User user = userService.checkVerification(obj.get("id").asLong(), obj.get("code").asText());
        if (user != null) {
            message.put("response", "Success");
            user.setStatus(1);
            userService.update(user);
            return Response.status(Response.Status.OK).entity(message).build();
        } else {
            message.put("response", "Provided OTP Code is Invalid, Try Again Later!");
            return Response.status(Response.Status.OK).entity(message).build();
        }
    }

    @Path("/resendVerificationCode")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response resendVerificationCode(JsonNode obj) {
        System.out.println(obj);
        Map<String, String> message = new HashMap<>();
        if (obj.get("id") != null) {
            com.sandun.web.entities.User user = userService.getById(obj.get("id").asLong());
            if (user != null) {
                String vCode = UUID.randomUUID().toString().replaceAll("[a-zA-Z-]", "");
                if (vCode.length() > 6) {
                    vCode = vCode.substring(0, 5);
                }
                String finalVCode = vCode;
                JavaMailUnit mailUnit = new JavaMailUnit();
                mailUnit.emailVerificationProcessMail(user.getEmailOrCno(), finalVCode);
                user.setVerificationCode(vCode);
                System.out.println(userService.update(user));
                message.put("response", "Success");
                return Response.status(Response.Status.OK).entity(message).build();
            } else {
                message.put("response", "Something Went Wrong, Try Again Later!");
                return Response.status(Response.Status.OK).entity(message).build();
            }
        } else {
            message.put("response", "Something Went Wrong, Try Again Later!");
            return Response.status(Response.Status.OK).entity(message).build();
        }
    }

    @Path("/getAllDetails")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getAllUserDetails(JsonNode obj) {
        Map<String, Object> response = new HashMap<>();
        if (obj != null && obj.get("id").asLong() > 0) {
            com.sandun.web.entities.User user = userService.getById(obj.get("id").asLong());
            ObjectNode node = new ObjectNode(JsonNodeFactory.instance);
            node.put("fName", user.getfName());
            node.put("lName", user.getlName());
            node.put("em_or_cno", user.getEmailOrCno());
            Address address = user.getAddress();
            if (address != null) {
                node.put("add_line_1", address.getLine1());
                node.put("add_line_2", address.getLine2());
                Location location = address.getLocation();
                if (location != null) {
                    node.put("city", location.getCity().getName());
                    node.put("district", location.getDistrict().getName());
                    node.put("province", location.getProvince().getName());
                }
            }
            response.put("response", "Success");
            response.put("data", node);
            return Response.status(Response.Status.OK).entity(response).build();
        } else {
            response.put("response", "Please Log In");
            return Response.status(Response.Status.OK).entity(response).build();
        }
    }

    @Path("/update")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(JsonNode obj) {
        Map<String, Object> response = new HashMap<>();
        if (obj != null && obj.get("id").asLong() > 0) {
            com.sandun.web.entities.User user = userService.getById(obj.get("id").asLong());
            String city = obj.get("city").asText();
            String district = obj.get("district").asText();
            String province = obj.get("province").asText();
            Location location = locationService.getLocation(city, district, province);
            if (location != null) {
                String fName = obj.get("fName").asText();
                String lName = obj.get("lName").asText();
                String addLine1 = obj.get("addLine1").asText();
                String addLine2 = obj.get("addLine2").asText();
                user.setfName(fName);
                user.setlName(lName);
                Address address = user.getAddress();
                if (address == null) {
                    address = new Address();
                }
                address.setLine1(addLine1);
                address.setLine2(addLine2);
                address.setLocation(location);
                user.setAddress(address);
                address.setUser(user);
                userService.update(user);
                response.put("response", "Success");
            } else {
                response.put("response", "Please Select A Valid Location");
            }
            return Response.status(Response.Status.OK).entity(response).build();
        } else {
            response.put("response", "Please Log In");
            return Response.status(Response.Status.OK).entity(response).build();
        }
    }

    @Path("/setLocation")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setLocation(JsonNode obj) {
        Map<String, Object> response = new HashMap<>();
        if (obj != null && obj.get("id").asLong() > 0) {
            com.sandun.web.entities.User user = userService.getById(obj.get("id").asLong());
            Address address = user.getAddress();
            if (address == null) {
                response.put("response", "Please Finished Filling Your Details First, Check Your Profile");
            } else {
                address.setLatitude(obj.get("latitude").asDouble());
                address.setlLongitude(obj.get("longitude").asDouble());
                user.setAddress(address);
                address.setUser(user);
                userService.update(user);
                response.put("response", "Success");
            }
        } else {
            response.put("response", "Please Log In");
        }
        return Response.status(Response.Status.OK).entity(response).build();
    }

    @Path("/check")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response check() {
        return Response.status(Response.Status.OK).entity("{\"response\",\"Success\"}").build();
    }

}

