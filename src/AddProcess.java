import javafx.application.Platform;
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
import java.util.Timer;
import java.util.TimerTask;

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
        vb.setPadding(new Insets(15, 80, 20, 80));
        vb.setAlignment(Pos.CENTER);
        submit.setOnAction(e ->
        {
            System.out.println("Sdffse");

            Platform.runLater(() ->
            {
                errlbl.setText("Looking for windows...");
            });



            Thread t = new Thread(() ->
            {
                String line;
                Process p = null;
                try
                {
                    if (tf.getText().length() > 4)
                    {
                        String command = "tasklist /v /fo list /fi \"imagename eq  " + tf.getText().trim().substring(0, tf.getText().length() - 4) + "*\"| find /i  \"window title:\"";
                        ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", command);
                        pb.redirectErrorStream();
                        p = pb.start();
                        System.out.println(command);
                        System.out.println(p);
                        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                        String program = "";
                        while ((line = input.readLine()) != null)
                        {
                            line = line.trim();
                            System.out.println(line);
                            if (line.contains("Window Title:") && !line.contains("N/A"))
                            {
                                program = line.substring(14, line.length());
                            }
                        }
                        String finalProgram = program;
                        String process = tf.getText().trim().substring(0, tf.getText().length() - 4).toLowerCase();
                        Platform.runLater(() ->
                        {
                            boolean valid = false;
                            if (finalProgram.length() > 0)
                            {
                                errlbl.setText("Found window \"" + finalProgram + "\".");
                                valid = true;
                            }
                            else
                            {
                                errlbl.setText("Could not find window \"" + finalProgram + "\".");
                            }
                            if (valid && Info.ignoreList.contains(process))
                            {
                                errlbl.setText(errlbl.getText() + "\n" + process + " is no longer ignored.");
                                Info.ignoreList.remove(process);
                                valid = false;
                            }
                            if (valid && (Info.todayProg.containsKey(process) || Info.addList.contains(process)))
                            {
                                errlbl.setText(errlbl.getText() + "\n" + process + " already in list.");
                                Info.ignoreList.remove(process);
                                valid = false;
                            }
                            if (valid)
                            {
                                errlbl.setText(errlbl.getText() + "\n" + process + " added to list.");
                                Info.addList.add(process);
                            }

                        });
                    }
                }
                catch (IOException ioException)
                {
                    ioException.printStackTrace();
                }
            });
            t.start();
        });

    }
}
