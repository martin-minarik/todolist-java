package myproject.todo_client;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import myproject.todo_client.ClientEndPoints.TodoRecordClient;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Log4j2
public class MainViewController
{
    @FXML
    private TextField entrySearch;

    @FXML
    private TableView<TodoRecord> tableView;

    @FXML
    private TableColumn<TodoRecord, Date> tableColumnDueTo;

    @FXML
    private TableColumn<TodoRecord, String> tableColumnStatus;

    @FXML
    private TableColumn<TodoRecord, String> tableColumnTitle;

    @FXML
    private Label remainingTimeLabel;

    private ObservableList<TodoRecord> records;

    private ScheduledExecutorService executorService;
    private Duration remainingTime;


    @FXML
    public void initialize()
    {

        tableColumnTitle.setCellValueFactory(new PropertyValueFactory<TodoRecord, String>("title"));
        tableColumnStatus.setCellValueFactory(new PropertyValueFactory<TodoRecord, String>("status"));
        tableColumnDueTo.setCellValueFactory(new PropertyValueFactory<TodoRecord, Date>("dueTo"));

        tableView.setRowFactory(tv ->
        {
            TableRow<TodoRecord> row = new TableRow<>();
            row.setOnMouseClicked(event ->
            {
                if (!row.isEmpty() && event.getButton() == MouseButton.PRIMARY
                        && event.getClickCount() == 2)
                {

                    TodoRecord clickedRow = row.getItem();
                    try
                    {
                        handleEditRow(clickedRow);
                    } catch (IOException e)
                    {
                        throw new RuntimeException(e);
                    }
                }
            });

            return row;
        });


        records = FXCollections.observableArrayList();
        tableView.setItems(records);

        updateTable();


    }

    public void handleEntrySearch(ActionEvent actionEvent) throws IOException
    {
        log.info("Fetching rows filtered by substring '{}' in title", entrySearch.getText());
        CompletableFuture.supplyAsync(
                        () -> EndPointProvider.getClient(TodoRecordClient.class).getTodoRecords(entrySearch.getText()))
                .thenAccept(records ->
                {
                    this.records.clear();
                    this.records.addAll(records);
                });
    }

    private void createEditWindow(EditViewController controller) throws IOException
    {
        Locale locale = Locale.of(Constants.language);

        ResourceBundle resourceBundle = ResourceBundle.getBundle("Texts", locale);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("edit_view.fxml"), resourceBundle);
        loader.setController(controller);
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = new Stage();
        stage.setTitle("Edit");
        stage.setScene(scene);

        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
    }

    public void handleButtonNew(ActionEvent actionEvent) throws IOException
    {
        log.info("New record");
        EditViewController controller = new EditViewController();
        controller.setPreInitializeData(null, this::updateTable);
        createEditWindow(controller);
    }

    public void handleButtonClear(ActionEvent actionEvent) throws IOException
    {
        log.info("Clearing of all rows");
        EndPointProvider.getClient(TodoRecordClient.class).removeTodoRecords();
        updateTable();
    }

    private void handleEditRow(TodoRecord item) throws IOException
    {
        EditViewController controller = new EditViewController();
        controller.setPreInitializeData(item, this::updateTable);

        createEditWindow(controller);
    }

    private void updateTable()
    {
        log.info("Fetching all records from server");

        CompletableFuture.supplyAsync(
                () -> EndPointProvider.getClient(TodoRecordClient.class).getTodoRecords()).thenAccept(records ->
        {
            this.records.clear();
            this.records.addAll(records);
            initRemainingTimeExecutor();
        });


    }

    private void initRemainingTimeExecutor()
    {
        if (executorService != null && !executorService.isShutdown())
            executorService.shutdown();

        this.remainingTime = this.getSmallestRemainingTime();

        if (remainingTime == null)
        {
            Platform.runLater(new Runnable()
            {
                @Override
                public void run()
                {
                    remainingTimeLabel.setText("0:00:00");
                }
            });
            return;
        }


        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(() ->
        {
            long seconds = remainingTime.getSeconds();
            String formatted = String.format(
                    "%d:%02d:%02d",
                    seconds / 3600,
                    (seconds % 3600) / 60,
                    (seconds % 60));

            Platform.runLater(new Runnable()
            {
                @Override
                public void run()
                {
                    remainingTimeLabel.setText(formatted);
                }
            });

            remainingTime = remainingTime.minusSeconds(1);
            if (remainingTime.getSeconds() < 0)
            {
                Platform.runLater(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        initRemainingTimeExecutor();
                    }
                });
            }
        }, 0, 1, TimeUnit.SECONDS);

    }

    private Duration getSmallestRemainingTime()
    {
        long currentTime = System.currentTimeMillis();
        OptionalLong smallestDifference = records.stream()
                .filter(obj -> !Objects.equals(obj.getStatus(), "Done"))
                .mapToLong(obj -> obj.getDueTo().getTime() - currentTime)
                .filter(difference -> difference > 0)
                .min();

        return smallestDifference.isPresent()
                ? Duration.ofMillis(smallestDifference.getAsLong()) : null;
    }

}