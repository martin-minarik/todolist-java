module myproject.todo_client {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.apache.cxf.rs.client;
    requires com.fasterxml.jackson.module.jakarta.xmlbind;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.jakarta.rs.json;
    requires jakarta.xml.bind;
    requires java.sql;

    requires org.apache.logging.log4j;

    requires jakarta.ws.rs;


    requires lombok;

    opens myproject.todo_client to javafx.fxml;
    exports myproject.todo_client;
}