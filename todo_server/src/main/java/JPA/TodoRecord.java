package JPA;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;


@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@Entity
public class TodoRecord
{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String title;

    private String status;

    private String description;

    private Timestamp created;

    private Timestamp dueTo;
}



