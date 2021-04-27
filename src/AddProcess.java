import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class AddProcess extends Scene
{
    BorderPane bp;
    Stage stage;
    double width;
    double height;

    public AddProcess(Stage stage, Parent root, double width, double height)
    {
        super(root, width, height);
        this.stage = stage;
        this.width = width;
        this.height = height;
        bp = (BorderPane) root;
        start();
    }

    public void start()
    {
        bp.requestFocus();
        Label addlbl = new Label("Process file name (Can be found in task manager)");
        TextField tf = new TextField();
        tf.setPromptText("someprogram.exe");
        Button submit = new Button("Add");
        Label errlbl = new Label();
        VBox vb = new VBox(tf, errlbl, submit);
        bp.setCenter(vb);
        HBox hb = new HBox(addlbl);
        hb.setAlignment(Pos.CENTER);
        bp.setTop(hb);
        addlbl.setAlignment(Pos.CENTER);
        addlbl.setFont(new Font(16));
        vb.setSpacing(10);
        vb.setPadding(new Insets(15, 100, 20, 100));
        vb.setAlignment(Pos.CENTER);
        submit.setOnAction(e ->
        {
            errlbl.setText("sdf");

            String line;
//            Process p = null;
            try
            {

                String command = "tasklist /v /fo list /fi \"imagename eq  " + tf.getText().trim() + "*\"| find /i  \"window title:\"";
//                p = Runtime.getRuntime().exec(command);
                ProcessBuilder p = new ProcessBuilder();
                p.command(command);
                p.redirectErrorStream();
                System.out.println(p);
                BufferedReader input =
                        new BufferedReader(new InputStreamReader(p.start().getInputStream()));

                System.out.println(command);
                System.out.println(input.readLine());
                while ((line = input.readLine()) != null)
                {
                    System.out.println(1);
                    line = line.trim();
                    System.out.println(line);

                }
                System.out.println("sdf");
            }
            catch (IOException ioException)
            {
                ioException.printStackTrace();
            }
        });

    }
}
