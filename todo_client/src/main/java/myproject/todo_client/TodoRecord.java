package myproject.todo_client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TodoRecord
{
    private Long id;

    private String title;

    private String status;

    private String description;

    private Timestamp created;

    private Timestamp dueTo;
}
