package org.example;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Main {

    private static List<String> words = new ArrayList();

    public static void main(String[] args) {

        JFrame jFrame = new JFrame("Server");
        jFrame.setSize(500,500);
        jFrame.setVisible(true);
        jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        Main main = new Main();
        main.readFile("src/main/resources/słowa.docx");
        main.startServer();

    }
    public void readFile(String filename)
    {
        try {
            FileInputStream fiss = new FileInputStream(filename);
            XWPFDocument document = new XWPFDocument(fiss);

            StringBuilder textBuilder = new StringBuilder();
            for(XWPFParagraph para : document.getParagraphs())
            {
                textBuilder.append(para.getText()).append(" ");
            }
            document.close();

            String text = textBuilder.toString();
            String[] splitWords = text.split("\\s+"); //odzielenie po spacji
            for(String word : splitWords)
            {
                words.add(word);
            }

        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void startServer()
    {
        try(ServerSocket serverSocket = new ServerSocket(5000))
        {
            while(true)
            {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Client connected: " + clientSocket.getInetAddress());
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(),true);

                ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

                final int[] index = {0};

                DateTimeFormatter dtff = DateTimeFormatter.ofPattern("HH:mm:ss");

                scheduler.scheduleAtFixedRate(() ->
                {
                    LocalDateTime aldd = LocalDateTime.now();
                    String word = words.get(index[0]);
                    out.println(dtff.format(aldd) + " " + word);
                    index[0]++;
                }, 0, 5, TimeUnit.SECONDS);
            }
        }catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private static class ClientHandler extends Thread
    {
        private final Socket clientSocket;
        private final Main mainInstance;

        public ClientHandler(Socket socket, Main mainInstance)
        {
            this.clientSocket = socket;
            this.mainInstance = mainInstance;
        }

    }

}