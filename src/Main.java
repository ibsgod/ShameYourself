import com.dustinredmond.fxtrayicon.FXTrayIcon;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class Main extends Application
{
    ArrayList<String> procs = new ArrayList<String>();
    BorderPane bp = new BorderPane();
    ActivityToday today = new ActivityToday(bp, 900, 600);
    public void start(Stage primaryStage) throws Exception
    {
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(today);
        primaryStage.show();
        FXTrayIcon trayIcon = new FXTrayIcon(primaryStage, getClass().getResource("poo.png"));
        trayIcon.show();
        Timer timer = new Timer();
        timer.schedule(new TimerTask()
        {
            public void run() {
                Platform.runLater(() ->
                {
                    getProcesses();
                });
            }
        }, 0, 1000);
    }


    public static void main(String[] args)
    {
        launch(args);
    }

    public void getProcesses()
    {
        try
        {
            String line;
            Process p = Runtime.getRuntime().exec("powershell \"gps | where {$_.MainWindowTitle -and( $_.Description -ne '') } | select Description\n ");
            BufferedReader input =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));
            procs.clear();
            while ((line = input.readLine()) != null)
            {
                line = line.trim();
                if (!line.contains("Description") && !line.contains("-----------") && line.length() > 0)
                {
                    System.out.println(line.length() + line); //<-- Parse data here.
                    procs.add(line);
                }

            }
            input.close();
        }
        catch (Exception err)
        {
            err.printStackTrace();
        }
        today.update(procs);
    }
}
