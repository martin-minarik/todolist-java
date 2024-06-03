package myproject.todo_client.ClientEndPoints;


import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import myproject.todo_client.TodoRecord;

import java.util.List;

public interface TodoRecordClient
{



    @GET
    @Path("todorecords")
    @Produces(MediaType.APPLICATION_JSON)
    List<TodoRecord> getTodoRecords();

    @GET
    @Path("todorecords")
    @QueryParam("search")
    @Produces(MediaType.APPLICATION_JSON)
    List<TodoRecord> getTodoRecords(@QueryParam("search") String search);

    @POST
    @Path("/todorecord")
    @Consumes(MediaType.APPLICATION_JSON)
    Long createTodoRecord(TodoRecord todoRecord);

    @PUT
    @Path("/todorecord")
    @Consumes(MediaType.APPLICATION_JSON)
    void updateTodoRecord(TodoRecord todoRecord);

    @DELETE
    @Path("/todorecords")
    void removeTodoRecords();

    @DELETE
    @Path("/todorecord/{id}")
    void removeTodoRecord(@PathParam("id") Long id);


}
