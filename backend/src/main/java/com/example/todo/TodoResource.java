package com.example.todo;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/todos")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class TodoResource {

    @GET
    public List<Todo> list() {
        return Todo.listAllOrdered();
    }

    @GET
    @Path("/{id}")
    public Todo get(@PathParam("id") Long id) {
        Todo todo = Todo.findById(id);
        if (todo == null) throw new NotFoundException();
        return todo;
    }

    @POST
    @Transactional
    public Response create(Todo todo) {
        todo.id = null; // クライアントから id が送られても無視する
        todo.persist();
        return Response.status(Response.Status.CREATED).entity(todo).build();
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Todo update(@PathParam("id") Long id, Todo updated) {
        Todo todo = Todo.findById(id);
        if (todo == null) throw new NotFoundException();
        todo.title = updated.title;
        todo.completed = updated.completed;
        return todo;
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response delete(@PathParam("id") Long id) {
        boolean deleted = Todo.deleteById(id);
        if (!deleted) throw new NotFoundException();
        return Response.noContent().build();
    }
}
