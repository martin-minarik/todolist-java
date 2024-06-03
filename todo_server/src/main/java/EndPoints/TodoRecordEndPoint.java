package EndPoints;

import JPA.TodoRecord;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.stream.Collectors;

@Path("/")
public class TodoRecordEndPoint
{
    @Inject
    EntityManager em;


    @GET
    @Path("/todorecords")
    @QueryParam("search")
    @Produces(MediaType.APPLICATION_JSON)
    public List<TodoRecord> getTodoRecords(@QueryParam("search") String search)
    {
        TypedQuery<TodoRecord> query =
                em.createQuery("SELECT todorecord FROM TodoRecord AS todorecord", TodoRecord.class);

        List<TodoRecord> records = query.getResultList();

        if(search == null || search.isBlank())
            return records;

        return records.stream()
                .filter(record -> record.getTitle().contains(search))
                .collect(Collectors.toList());
    }


    @GET
    @Path("/todorecord/{id}")
    public Response getTodoRecord(@PathParam("id") Long id)
    {
        TodoRecord result = em.find(TodoRecord.class, id);
        if (result == null)
        {
            return Response.status(Response.Status.NOT_FOUND).type(MediaType.TEXT_PLAIN)
                    .entity("JPA.TodoRecord with id = " + id + " not found").build();
        }
        return Response.ok(result, MediaType.APPLICATION_JSON).build();
    }

    @Transactional
    @POST
    @Path("/todorecord")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.TEXT_PLAIN)
    public Long createTodoRecord(TodoRecord todoRecord)
    {
        em.persist(todoRecord);
        return todoRecord.getId();
    }

    @Transactional
    @PUT
    @Path("/todorecord")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateTodoRecord(TodoRecord todoRecord)
    {
        TodoRecord result = em.find(TodoRecord.class, todoRecord.getId());
        if (result == null)
        {
            return Response.status(Response.Status.NOT_FOUND).type(MediaType.TEXT_PLAIN)
                    .entity("JPA.TodoRecord with id = " + todoRecord.getId() + " not found").build();
        }
        result.setTitle(todoRecord.getTitle());
        result.setDescription(todoRecord.getDescription());
        result.setStatus(todoRecord.getStatus());
        result.setCreated(todoRecord.getCreated());
        result.setDueTo(todoRecord.getDueTo());
        return Response.ok().build();
    }

    @Transactional
    @DELETE
    @Path("/todorecords")
    public Response deleteTodoRecords()
    {
        em.createQuery("DELETE FROM TodoRecord").executeUpdate();

        return Response.ok().build();
    }

    @Transactional
    @DELETE
    @Path("/todorecord/{id}")
    public Response deleteTodoRecord(@PathParam("id") Long id)
    {
        TodoRecord result = em.find(TodoRecord.class, id);
        if (result == null)
        {
            return Response.status(Response.Status.NOT_FOUND).type(MediaType.TEXT_PLAIN)
                    .entity("JPA.TodoRecord with id = " + id + " not found").build();
        }
        em.remove(result);
        return Response.ok().build();
    }
}
