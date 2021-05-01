import com.dustinredmond.fxtrayicon.FXTrayIcon;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class Main extends Application
{
    ArrayList<String> procs = new ArrayList<String>();
    BorderPane bp = new BorderPane();
    ActivityToday today;

    public void start(Stage primaryStage) throws Exception
    {
        today = new ActivityToday(bp, 650, 600, primaryStage);
        if (!Info.file.createNewFile())
        {
            Info.readJSON(Info.file);
        }

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
                    try
                    {
                        today.update(procs);
                        Info.updateJSON(Info.file);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                });
            }
        }, 0, 30000);
    }


    public static void main(String[] args)
    {
        launch(args);
    }

    public void getProcesses()
    {
        System.out.println("SDf");
        if (LocalDate.now().getYear() > Integer.parseInt(Info.currDate.substring(4, 8)) || LocalDate.now().getMonthValue() > Integer.parseInt(Info.currDate.substring(2, 4)) || LocalDate.now().getDayOfMonth() > Integer.parseInt(Info.currDate.substring(0, 2)))
        {
            Info.currDate = String.format("%02d", LocalDate.now().getDayOfMonth()) + String.format("%02d", LocalDate.now().getMonthValue()) + LocalDate.now().getYear();
            Info.todayProg.clear();
        }
        Thread t = new Thread(() ->
        {
            try
            {
                String line;
                Process p = Runtime.getRuntime().exec("powershell \"gps | where {$_.MainWindowTitle -and( $_.Description -ne '') } | select Description\n ");
                BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                procs.clear();
                while ((line = input.readLine()) != null)
                {
                    line = line.trim();
                    if (!line.contains("Description") && !line.contains("-----------") && line.length() > 0)
                    {
                        procs.add(line);
                    }

                }
                String command = "";
                ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", command);
                pb.redirectErrorStream();
                for (int i = 0; i < Info.addList.size(); i++)
                {
                    command = "tasklist /v /fo list /fi \"imagename eq  " + Info.addList.get(i) + "*\"| find /i  \"image name:\"";
                    System.out.println(command);
                    pb.command("cmd.exe", "/c", command);
                    p = pb.start();
                    p.waitFor();
                    input = new BufferedReader(new InputStreamReader(p.getInputStream()));
                    line = input.readLine();
                    System.out.println(line);
                    if (line != null && line.contains("Image Name:") && !line.contains("N/A"))
                    {
                        procs.add(line.trim().split(" ")[line.trim().split(" ").length - 1].substring(0, line.trim().split(" ")[line.trim().split(" ").length - 1].length() - 4));
                    }
                }
                input.close();
            }
            catch (Exception err)
            {
                err.printStackTrace();
            }
        });
        t.start();
    }


}
