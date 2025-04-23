/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.clientapp;

import javax.swing.*;
import java.io.*;
import java.net.*;

public class ClientApp extends JFrame {
    private JTextArea area;
    private JTextField input;
    private JButton sendBtn, fileBtn;
    private Socket socket;
    private DataOutputStream dos;

    public ClientApp() {
        setTitle("Client - Chat & File");
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        area = new JTextArea();
        area.setEditable(false);
        input = new JTextField();
        sendBtn = new JButton("Kirim");
        fileBtn = new JButton("Upload File");

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.add(input);
        panel.add(sendBtn);
        panel.add(fileBtn);

        add(new JScrollPane(area), "Center");
        add(panel, "South");
        setVisible(true);

        setupConnection();
        setupActions();
    }

    private void setupConnection() {
        try {
            socket = new Socket("localhost", 5000); // GANTI 'localhost' dengan IP server jika pakai 2 komputer
            dos = new DataOutputStream(socket.getOutputStream());
            area.append("Terhubung ke server.\n");
        } catch (IOException e) {
            area.append("Gagal konek ke server.\n");
        }
    }

    private void setupActions() {
        sendBtn.addActionListener(e -> {
            try {
                String text = input.getText();
                dos.writeUTF(text);
                area.append("Me: " + text + "\n");
                input.setText("");
            } catch (IOException ex) {
                area.append("Gagal kirim pesan.\n");
            }
        });

        fileBtn.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                try {
                    dos.writeUTF("FILE:" + file.getName());
                    FileInputStream fis = new FileInputStream(file);
                    byte[] buffer = new byte[4096];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) > 0) {
                        dos.write(buffer, 0, bytesRead);
                    }
                    fis.close();
                    area.append("File dikirim: " + file.getName() + "\n");
                } catch (IOException ex) {
                    area.append("Gagal kirim file.\n");
                }
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ClientApp::new);
    }
}
