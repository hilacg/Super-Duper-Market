package course.java.sdm.engine;

import javafx.concurrent.Task;

import java.io.FileReader;


public class LoadTask extends Task<Boolean> {

    private final FileReader filePath;
    private final SuperXML superXML;
    private Engine engine;

    private final int SLEEP_TIME = 10;

    public LoadTask(FileReader filePath, SuperXML superXML, Engine engine) {
        this.filePath = filePath;
        this.superXML = superXML;
        this.engine = engine;
    }

    @Override
    protected Boolean call() throws Exception {
   /*    try {
            updateProgress(10,100);
            updateMessage("Fetching file...");
            sleepForAWhile(SLEEP_TIME);
            if (superXML.load(this.filePath)) {
                updateProgress(20,100);
                updateMessage("Validating file...");
                superXML.validateXML();
                for(int i=20; i<=100;i++) {
                    sleepForAWhile(SLEEP_TIME);
                    updateProgress(i, 100);
                }
                updateMessage("File Loaded Successfully!");
            }
        }
        catch (Exception e){
            updateMessage(e.getMessage());
            updateProgress(0,100);
            this.cancel();
            return Boolean.FALSE;
        }*/
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
