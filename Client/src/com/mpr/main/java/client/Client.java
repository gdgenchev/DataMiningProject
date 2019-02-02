package com.mpr.main.java.client;

import com.communication.senddata.Msg;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

import static com.communication.senddata.MsgType.*;
import static com.mpr.main.java.client.Constants.LOG_FILE_NAME;
import static com.mpr.main.java.client.Constants.RECEIVED_DIR;

public class Client {
    private Socket socket;
    private ObjectOutputStream objectOutputStream;
    private ObjectInputStream objectInputStream;
    private static final Logger logger = LogManager.getLogger("Client Logger");

    public Client(String host, int port) {
        try {
            this.socket = new Socket(host, port);
            this.objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            this.objectInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            logger.warn("Error while constructing the client: " + e.getMessage());
        }
    }
    private static double getMinSupp(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter value [0.0-1.0]");
        while (true) {
            try {
                double minSupp = Double.parseDouble(scanner.nextLine());
                if (minSupp >= 0.0 && minSupp <= 1.0) {
                    return minSupp;
                }
                System.out.println("Wrong input! Number not between 0-1");
            } catch (NumberFormatException e) {
                System.out.println("Wrong input! Please enter floating value 0-1");
            }
        }
    }

    private void sendMinSupp(double minSupp) throws IOException {
        objectOutputStream.writeObject(new Msg(minSupp, MIN_SUPP));
    }

    private void sendLogFile() throws IOException {
        String line;
        BufferedReader reader = new BufferedReader(new FileReader (LOG_FILE_NAME));
        reader.readLine(); //skip the first line
        while ((line = reader.readLine()) != null) {
            objectOutputStream.writeObject(new Msg(line, LINE));
        }
        objectOutputStream.writeObject(new Msg(null, CLIENT_FINISHED));
    }

    public static void main(String[] args) {
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        Client client = new Client(host, port);
        double minSupp = getMinSupp();
        try {
            client.sendMinSupp(minSupp);
            client.sendLogFile();
            client.receiveResult();
        } catch (IOException e) {
            logger.warn("Error while sending data to server: " + e.getMessage());
        }
    }

    private void receiveResult() {
        try {
            Msg msg;
            while ((msg = (Msg) objectInputStream.readObject()) != null
                    && !msg.getType().equals(SERVER_FINISHED)) {
                if (msg.getType().equals(FILE_NAME)) {
                    String filename = (String) msg.getData();
                    new File(RECEIVED_DIR).mkdir();
                    PrintWriter pr = new PrintWriter(RECEIVED_DIR + filename);
                    while ((msg = (Msg) objectInputStream.readObject()) != null
                            && !msg.getType().equals(FINISHED_FILE)) {
                        if (msg.getType().equals(LINE)) {
                            pr.println((String) msg.getData());
                        }
                    }
                    pr.close();
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            logger.warn("Error while receiving message from server: " + e.getMessage());
        }
    }
}
