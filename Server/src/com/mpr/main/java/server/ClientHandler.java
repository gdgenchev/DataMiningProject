package com.mpr.main.java.server;

import ca.pfv.spmf.algorithms.frequentpatterns.relim.AlgoRelim;
import com.communication.senddata.Msg;
import com.communication.senddata.MsgType;
import com.mpr.main.java.server.event.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import static com.communication.senddata.MsgType.*;
import static com.mpr.main.java.server.event.SupportedEvents.SUPPORTED_EVENTS;

public class ClientHandler implements Runnable {
    private static final Logger logger = LogManager.getLogger("ClientHandler Logger");
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private Map<String, PrintWriter> writersForName;

    private String folderName = "client-" + ThreadLocalRandom.current().nextInt(0, 100);

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;
            inputStream = new ObjectInputStream(socket.getInputStream());
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            new File(folderName).mkdir();
            writersForName = new HashMap<>();
            for (String name : SUPPORTED_EVENTS.keySet()) {
                writersForName.put(name, new PrintWriter(folderName + File.separator + name));
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.warn("Error initializing accepted client socket: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            processClientInput();
        } finally {
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void processClientInput() {
        Msg msg;
        double minSupp = 0.0;
        String line;
        try {
            while ((msg = (Msg) inputStream.readObject()) != null
                    && msg.getType() != CLIENT_FINISHED) {
                if (msg.getType().equals(MsgType.LINE)) {
                    line = (String) msg.getData();
                    parseEventAndWriteToFile(line);
                } else if (msg.getType().equals(MsgType.MIN_SUPP)) {
                    minSupp = (double) msg.getData();
                }
            }
            for (PrintWriter writer : writersForName.values()) {
                writer.close();
            }
            //Run the algorithm for each file
            for (String fileName : SUPPORTED_EVENTS.keySet()) {
                String inputFilePath = folderName + File.separator + fileName;
                String outputFilePath = folderName + File.separator + fileName + "-out";
                AlgoRelim algoRelim = new AlgoRelim();
                algoRelim.runAlgorithm(minSupp, inputFilePath, outputFilePath);
                Files.delete(Paths.get(inputFilePath));
                File file = new File(outputFilePath);
                if (file.length() > 0) {
                    sendResultToClient(fileName, file);
                }
                Files.delete(Paths.get(outputFilePath));
            }
            outputStream.writeObject(new Msg(null, SERVER_FINISHED));
            new File(folderName).delete();
        } catch (IOException | ClassNotFoundException e) {
            logger.warn("Error from processClientInput: " + e.getMessage());
        }
    }

    private void sendResultToClient(String fileName, File file) throws IOException {
        String line;
        outputStream.writeObject(new Msg(fileName, FILE_NAME));
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            while ((line = br.readLine()) != null) {
                EventType type = SUPPORTED_EVENTS.get(fileName);
                if (type != null) {
                    String toSend;
                    Event event = null;
                    switch (type) {
                        case ONE_USER_ONE_ITEM:
                            event = new OneUserOneItemEvent();
                            break;
                        case TWO_USERS_ONE_ITEM:
                            event = new TwoUsersOneItemEvent();
                            break;
                    }
                    toSend = event.decode(line, fileName);
                    if (toSend != null) {
                        outputStream.writeObject(new Msg(toSend, LINE));
                    }
                }
            }
        }
        outputStream.writeObject(new Msg(null, FINISHED_FILE));
    }

    private void parseEventAndWriteToFile(String line) {
        String[] fields = line.split(",");
        if (fields.length == 8) {
            String context = fields[2];
            String name = fields[4];
            String description = fields[5];
            String ip = fields[7];
            EventType type = SUPPORTED_EVENTS.get(name);
            if (type != null) {
                Event event = null;
                switch (type) {
                    case ONE_USER_ONE_ITEM:
                        event = new OneUserOneItemEvent(context, name, ip, description);
                        break;
                    case TWO_USERS_ONE_ITEM:
                        event = new TwoUsersOneItemEvent(context, name, ip, description);
                        break;
                }
                event.writeToFile(folderName, writersForName.get(name));
            }
        }
    }
}
