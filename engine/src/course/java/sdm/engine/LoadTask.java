package course.java.sdm.engine;

import generatedClasses.SuperDuperMarketDescriptor;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.util.function.Consumer;

public class LoadTask extends Task<Boolean> {

    private String filePath;
    private final SuperXML superXML;

    private final int SLEEP_TIME = 10;

    public LoadTask(String filePath,SuperXML superXML) {
        this.filePath = filePath;
        this.superXML = superXML;
    }

    @Override
    protected Boolean call() throws Exception {
        try {
            updateMessage("Fetching file...");
            sleepForAWhile(SLEEP_TIME);
            if (superXML.load(this.filePath)) {
                updateProgress(10,100);
                updateMessage("Validating file...");
                superXML.validateXML();
                for(int i=10; i<=100;i++) {
                    sleepForAWhile(SLEEP_TIME);
                    updateProgress(i, 100);
                }
                updateMessage("File Loaded Successfully!");
            }
        }
        catch (Exception e){
            updateMessage(e.getMessage());
  //          return Boolean.FALSE;
            //   tasks.clear()
 //           updateProgress(10,10);
            throw e;
  //          this.cancel();
        }
        return Boolean.TRUE;
    }

    public static void sleepForAWhile(long sleepTime) {
        if (sleepTime != 0) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException ignored) {

            }
        }
    }


}
