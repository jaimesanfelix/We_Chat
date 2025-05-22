module com.fct.we_chat {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.sql;
    requires javafx.base;

    opens com.fct.we_chat.Models to javafx.base;

    opens com.fct.we_chat to javafx.fxml;
    exports com.fct.we_chat;
}