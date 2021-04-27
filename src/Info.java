import netscape.javascript.JSObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.ls.LSOutput;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class Info
{
    static JSONObject mainJSON = new JSONObject();
    static HashMap<String, HashMap<String, Integer>> progToDates = new HashMap<>();
    static HashMap<String, HashMap<String, Integer>> datesToProg = new HashMap<>();
    static HashMap<String, HashMap<String, Integer>> progToHours = new HashMap<>();
    static HashMap<String, Integer> todayProg = new HashMap<>();
    static String home = System.getProperty("user.home") + "/Documents/shamefuldata.json";
    static File file = new File(home);
    static ArrayList<String> ignoreList = new ArrayList<String>();
    static String currDate = String.format("%02d", LocalDate.now().getDayOfMonth()) + String.format("%02d", LocalDate.now().getMonthValue()) + LocalDate.now().getYear();
    public static void readJSON(File file) throws Exception
    {
        try
        {
            FileReader fileReader;
            BufferedReader br = null;
            fileReader = new FileReader(file);
            br = new BufferedReader(fileReader);
            String s = br.readLine();
            if (s == null)
            {
                return;
            }
            JSONObject mj = new JSONObject(s);
            JSONArray jignore = mj.getJSONArray("ignoreList");
            ignoreList.clear();
            for (int i = 0; i < jignore.length(); i++)
            {
                ignoreList.add(jignore.getString(i));
            }
            currDate = mj.getString("currDate");
            JSONObject jtodayProg = mj.getJSONObject("todayProg");
            todayProg.clear();
            for (String prog: jtodayProg.keySet())
            {
                todayProg.put(prog, jtodayProg.getInt(prog));
            }
            JSONObject jprogToDates = mj.getJSONObject("progToDates");
            progToDates.clear();
            for (String prog: jprogToDates.keySet())
            {
                HashMap<String, Integer> temp = new HashMap<>();
                for (String dates: jprogToDates.getJSONObject(prog).keySet())
                {
                    temp.put(dates, jprogToDates.getJSONObject(prog).getInt(dates));
                }
                progToDates.put(prog, temp);
            }
            JSONObject jdatesToProg = mj.getJSONObject("datesToProg");
            datesToProg.clear();
            for (String dates: jdatesToProg.keySet())
            {
                HashMap<String, Integer> temp = new HashMap<>();
                for (String prog: jdatesToProg.getJSONObject(dates).keySet())
                {
                    temp.put(prog, jdatesToProg.getJSONObject(dates).getInt(prog));
                }
                datesToProg.put(dates, temp);
            }
            JSONObject jprogToHours = mj.getJSONObject("progToHours");
            progToHours.clear();
            for (String prog: jprogToHours.keySet())
            {
                HashMap<String, Integer> temp = new HashMap<>();
                for (String hours: jprogToHours.getJSONObject(prog).keySet())
                {
                    temp.put(hours, jprogToHours.getJSONObject(prog).getInt(hours));
                }
                progToHours.put(prog, temp);
            }
        }
        catch(Exception e)
        {
        }
    }

    public static void updateJSON(File file) throws Exception
    {
        //ADD TODAY ACTIVITY STUFF
        FileWriter fileWriter;
        BufferedWriter bw = null;
        fileWriter = new FileWriter(file, false);
        bw = new BufferedWriter(fileWriter);
        mainJSON.put("currDate", currDate);
        JSONArray jignore = new JSONArray();
        jignore.putAll(ignoreList);
        mainJSON.put("ignoreList", jignore);
        JSONObject jtodayProg = new JSONObject();
        for (String prog: todayProg.keySet())
        {
            jtodayProg.put(prog, todayProg.get(prog)-1);
        }
        mainJSON.put("todayProg", jtodayProg);
        JSONObject jprogToDates = new JSONObject();
        for (String prog : progToDates.keySet())
        {
            JSONObject item = new JSONObject();
            for (String dates: progToDates.get(prog).keySet())
            {
                item.put(dates, progToDates.get(prog).get(dates));
            }
            jprogToDates.put(prog, item);
        }
        mainJSON.put("progToDates", jprogToDates);
        JSONObject jdatesToProg = new JSONObject();
        for (String dates : datesToProg.keySet())
        {
            JSONObject item = new JSONObject();
            for (String prog: datesToProg.get(dates).keySet())
            {
                item.put(prog, datesToProg.get(dates).get(prog));
            }
            jdatesToProg.put(dates, item);
        }
        mainJSON.put("datesToProg", jdatesToProg);
        JSONObject jprogToHours = new JSONObject();
        for (String prog : progToHours.keySet())
        {
            JSONObject item = new JSONObject();
            for (String hours: progToHours.get(prog).keySet())
            {
                item.put(hours, progToHours.get(prog).get(hours));
            }
            jprogToHours.put(prog, item);
        }
        mainJSON.put("progToHours", jprogToHours);
        bw.write(mainJSON.toString());
        bw.close();
    }
}
