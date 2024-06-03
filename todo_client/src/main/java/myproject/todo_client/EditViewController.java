package myproject.todo_client;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import myproject.todo_client.ClientEndPoints.TodoRecordClient;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Log4j2
public class EditViewController
{
    public final String[] statusChoices = {"To do", "In progress", "Done"};

    @FXML
    private TextField textFieldTitle;

    @FXML
    private ChoiceBox<String> choiceBoxStatus;

    @FXML
    private Label labelCreated;

    @FXML
    private DatePicker datePickerDate;

    @FXML
    private Spinner<Integer> spinnerHours;

    @FXML
    private Spinner<Integer> spinnerMinutes;

    private TodoRecord todoRecord;

    private Runnable callback;

    public void setPreInitializeData(TodoRecord todoRecord, Runnable callback)
    {
        this.todoRecord = todoRecord;
        this.callback = callback;
    }

    @FXML
    public void initialize()
    {
        choiceBoxStatus.getItems().addAll(statusChoices);

        SpinnerValueFactory<Integer> hoursValueFactory
                = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 24, 0);

        SpinnerValueFactory<Integer> minutesValueFactory
                = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 60, 0);


        if (todoRecord == null)
        {
            choiceBoxStatus.setValue(statusChoices[0]);
            labelCreated.setText("");
            datePickerDate.setValue(LocalDate.now());
        } else
        {
            textFieldTitle.setText(todoRecord.getTitle());
            choiceBoxStatus.setValue(todoRecord.getStatus());
            labelCreated.setText(todoRecord.getCreated().toString());
            datePickerDate.setValue(todoRecord.getDueTo().toLocalDateTime().toLocalDate());
            hoursValueFactory.setValue(todoRecord.getDueTo().toLocalDateTime().getHour());
            minutesValueFactory.setValue(todoRecord.getDueTo().toLocalDateTime().getMinute());
        }

        this.spinnerHours.setValueFactory(hoursValueFactory);
        this.spinnerMinutes.setValueFactory(minutesValueFactory);
    }

    private boolean checkFields()
    {
        return !textFieldTitle.getText().isBlank();
    }


    public void handleButtonDelete(ActionEvent actionEvent) throws IOException
    {
        if (todoRecord != null)
        {
            log.info("Deleting record");
            EndPointProvider.getClient(TodoRecordClient.class).removeTodoRecord(todoRecord.getId());
            callback.run();
        }


        Stage stage = (Stage) textFieldTitle.getScene().getWindow();
        stage.close();
    }


    public void handleButtonSave(ActionEvent actionEvent) throws IOException
    {
        if (!checkFields())
            return;

        log.info("Saving record");

        String title = textFieldTitle.getText();
        String status = choiceBoxStatus.getValue();
        Timestamp now = java.sql.Timestamp.valueOf(LocalDateTime.now());
        Timestamp dueTo = java.sql.Timestamp.valueOf(LocalDateTime.of(datePickerDate.getValue(),
                LocalTime.of(spinnerHours.getValue(), spinnerMinutes.getValue())));


        if (todoRecord == null)
        {
            TodoRecord todoRecord = new TodoRecord(
                    null,
                    title,
                    status,
                    "",
                    now,
                    dueTo
            );

            Long id = EndPointProvider.getClient(TodoRecordClient.class).createTodoRecord(todoRecord);
        } else
        {
            this.todoRecord.setTitle(title);
            this.todoRecord.setStatus(status);
            this.todoRecord.setDueTo(dueTo);
            EndPointProvider.getClient(TodoRecordClient.class).updateTodoRecord(todoRecord);
        }

        callback.run();

        Stage stage = (Stage) textFieldTitle.getScene().getWindow();
        stage.close();
    }


}